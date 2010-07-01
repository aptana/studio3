package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSTypeWalker;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.ParameterElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.ReturnTypeElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
import com.aptana.editor.js.sdoc.model.ParamTag;
import com.aptana.editor.js.sdoc.model.ReturnTag;
import com.aptana.editor.js.sdoc.model.Tag;
import com.aptana.editor.js.sdoc.model.TagType;
import com.aptana.editor.js.sdoc.model.Type;
import com.aptana.editor.js.sdoc.model.TypeTag;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;

public class JSFileIndexingParticipant implements IFileStoreIndexingParticipant
{
	private JSIndexWriter _indexWriter;
	
	/**
	 * JSFileIndexingParticipant
	 */
	public JSFileIndexingParticipant()
	{
		this._indexWriter = new JSIndexWriter();
	}
	
	/**
	 * applyDocumentation
	 * 
	 * @param function
	 * @param block
	 */
	protected void applyDocumentation(FunctionElement function, DocumentationBlock block)
	{
		if (block != null)
		{
			// apply description
			function.setDescription(block.getText());
			
			// apply parameters
			for (Tag tag : block.getTags(TagType.PARAM))
			{
				ParamTag paramTag = (ParamTag) tag;
				ParameterElement parameter = new ParameterElement();
				
				parameter.setName(paramTag.getName());
				parameter.setDescription(paramTag.getText());
				parameter.setUsage(paramTag.getUsage().getName());
				
				for (Type type : paramTag.getTypes())
				{
					parameter.addType(type.toSource());
				}
				
				function.addParameter(parameter);
			}
			
			// apply return types
			for (Tag tag : block.getTags(TagType.RETURN))
			{
				ReturnTag returnTag = (ReturnTag) tag;
				
				for (Type type : returnTag.getTypes())
				{
					ReturnTypeElement returnType = new ReturnTypeElement();
					
					returnType.setType(type.toSource());
					returnType.setDescription(returnTag.getText());
					
					function.addReturnType(returnType);
				}
			}
		}
	}
	
	/**
	 * applyDocumentation
	 * 
	 * @param property
	 * @param block
	 */
	protected void applyDocumentation(PropertyElement property, DocumentationBlock block)
	{
		if (block != null)
		{
			// apply description
			property.setDescription(block.getText());
			
			// apply types
			for (Tag tag : block.getTags(TagType.TYPE))
			{
				TypeTag typeTag = (TypeTag) tag;
				
				for (Type type : typeTag.getTypes())
				{
					ReturnTypeElement returnType = new ReturnTypeElement();
					
					returnType.setType(type.toSource());
					returnType.setDescription(typeTag.getText());
					
					property.addType(returnType);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size());

		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				return;
			}

			try
			{
				if (file == null)
				{
					continue;
				}

				sub.subTask(file.getName());

				try
				{
					// grab the source of the file we're going to parse
					String source = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(-1)));

					// minor optimization when creating a new empty file
					if (source != null && source.length() > 0)
					{
						// create parser and associated parse state
						IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IJSParserConstants.LANGUAGE);

						if (pool != null)
						{
							IParser parser = pool.checkOut();

							// apply the source to the parse state and parse
							ParseState parseState = new ParseState();
							parseState.setEditState(source, source, 0, 0);
							parser.parse(parseState);

							pool.checkIn(parser);

							// process results
							this.processParseResults(index, file, parseState.getParseResult());
						}
					}
				}
				catch (CoreException e)
				{
					Activator.logError(e.getMessage(), e);
				}
				catch (Throwable e)
				{
					Activator.logError(e.getMessage(), e);
				}
			}
			finally
			{
				sub.worked(1);
			}
		}

		monitor.done();
	}

	/**
	 * processParseResults
	 * 
	 * @param index
	 * @param file
	 * @param parseState
	 */
	private void processParseResults(Index index, IFileStore file, IParseNode ast)
	{
		URI location = file.toURI();
		Scope<JSNode> globals = ((JSParseRootNode) ast).getGlobalScope();

		if (globals != null)
		{
			TypeElement type = new TypeElement();
			
			type.setName("Window");
			
			for (String symbol : globals.getLocalSymbolNames())
			{
				List<JSNode> nodes = globals.getSymbol(symbol);

				if (nodes != null && nodes.size() > 0)
				{
					// TODO: We may want to process all nodes and potentially
					// create a new type that is the union of all types. For
					// now we grab the last definition
					JSNode node = nodes.get(nodes.size() - 1);
					
					if (node instanceof JSFunctionNode)
					{
						FunctionElement function = new FunctionElement();
						
						function.setName(symbol);
						
						this.applyDocumentation(function, node.getDocumentation());
						
						type.addProperty(function);
					}
					else
					{
						PropertyElement property = new PropertyElement();
						DocumentationBlock block = node.getDocumentation();
						
						property.setName(symbol);
						
						if (block != null)
						{
							this.applyDocumentation(property, node.getDocumentation());
						}
						else
						{
							JSTypeWalker walker = new JSTypeWalker(globals, index);
							
							node.accept(walker);
							
							for (String propertyType : walker.getTypes())
							{
								ReturnTypeElement returnType = new ReturnTypeElement();
								
								returnType.setType(propertyType);
								
								property.addType(returnType);
							}
						}
						
						type.addProperty(property);
					}
				}
			}
			
			this._indexWriter.writeType(index, type, location);
		}
	}
}
