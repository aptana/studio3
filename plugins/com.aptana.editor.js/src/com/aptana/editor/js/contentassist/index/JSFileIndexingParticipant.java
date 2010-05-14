package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSPrimitiveNode;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

public class JSFileIndexingParticipant implements IFileIndexingParticipant
{
	private static final String JS_EXTENSION = "js"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void index(Set<IFile> files, Index index, IProgressMonitor monitor)
	{
		monitor = SubMonitor.convert(monitor, files.size());
		for (IFile file : files)
		{
			if (monitor.isCanceled())
				return;
			try
			{
				if (file == null || !isJSFile(file))
				{
					continue;
				}
				monitor.subTask(file.getLocation().toPortableString());
				try
				{
					// create parser and associated parse state
					JSParser parser = new JSParser();
					ParseState parseState = new ParseState();

					// grab the source of the file we're going to parse
					String source = IOUtil.read(file.getContents());

					// minor optimization when creating a new empty file
					if (source != null && source.length() > 0)
					{
						// apply the source to the parse state
						parseState.setEditState(source, source, 0, 0);

						// parse and grab the result
						IParseNode ast = parser.parse(parseState);

						// now walk the parse tree
						this.walkAST(index, file, ast);
					}
				}
				catch (CoreException e)
				{
					Activator.logError(e.getMessage(), e);
				}
				catch (Exception e)
				{
					Activator.logError(e.getMessage(), e);
				}
			}
			finally
			{
				monitor.worked(1);
			}
		}
		monitor.done();
	}

	/**
	 * isJSFile
	 * 
	 * @param file
	 * @return
	 */
	private boolean isJSFile(IFile file)
	{
		InputStream stream = null;
		IContentTypeManager manager = Platform.getContentTypeManager();

		try
		{
			stream = file.getContents();

			IContentType[] types = manager.findContentTypesFor(stream, file.getName());

			for (IContentType type : types)
			{
				if (type.getId().equals(IJSConstants.CONTENT_TYPE_JS))
				{
					return true;
				}
			}
		}
		catch (Exception e)
		{
			Activator.logError(e.getMessage(), e);
		}
		finally
		{
			try
			{
				if (stream != null)
				{
					stream.close();
				}
			}
			catch (IOException e)
			{
				// ignore
			}
		}

		return JS_EXTENSION.equalsIgnoreCase(file.getFileExtension());
	}

	/**
	 * walkAST
	 * 
	 * @param index
	 * @param file
	 * @param ast
	 */
	private void walkAST(Index index, IFile file, IParseNode ast)
	{
		for (String name : this.getGlobalFunctions(ast))
		{
			System.out.println(name + "()");
			index.addEntry(JSIndexConstants.FUNCTION, name, file.getProjectRelativePath().toPortableString());
		}
		for (String varName : this.getGlobalDeclarations(ast))
		{
			System.out.println(varName);
		}
	}

	/**
	 * getGlobalDeclarations
	 * 
	 * @param ast
	 * @return
	 */
	private List<String> getGlobalDeclarations(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();

		this.processXPath(
			"/var/declaration/identifier[position() = 1 and count(following-sibling::function) = 0]",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSPrimitiveNode)
					{
						result.add(item.toString());
					}
				}
			}
		);

		return result;
	}

	/**
	 * getGlobalFunctions
	 * 
	 * @param ast
	 * @return
	 */
	private List<String> getGlobalFunctions(IParseNode ast)
	{
		final List<String> result = new LinkedList<String>();

		this.processXPath(
			"/function[string-length(@name) > 0]",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSFunctionNode)
					{
						JSFunctionNode function = (JSFunctionNode) item;
						
						result.add(function.getName());
					}
				}
			}
		);

		this.processXPath(
			"/var/declaration/identifier[count(following-sibling::function) > 0]",
			ast,
			new ItemProcessor() {
				public void process(Object item)
				{
					if (item instanceof JSPrimitiveNode)
					{
						result.add(item.toString());
					}
				}
			}
		);

		return result;
	}

	/**
	 * processXPath
	 * 
	 * @param expression
	 * @param node
	 * @param processor
	 */
	private void processXPath(String expression, IParseNode node, ItemProcessor processor)
	{
		if (expression != null && expression.length() > 0 && node != null && processor != null)
		{
			try
			{
				XPath xpath = new ParseNodeXPath(expression);
				Object list = xpath.evaluate(node);
	
				if (list instanceof List<?>)
				{
					List<?> items = (List<?>) list;
	
					for (Object item : items)
					{
						processor.process(item);
					}
				}
			}
			catch (JaxenException e)
			{
				e.printStackTrace();
			}
		}
	}
}
