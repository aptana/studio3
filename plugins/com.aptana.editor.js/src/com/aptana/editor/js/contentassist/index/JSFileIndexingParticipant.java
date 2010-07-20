package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSSymbolCollector;
import com.aptana.editor.js.contentassist.JSTypeWalker;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
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
	 * getGlobals
	 * 
	 * @param root
	 * @return
	 */
	protected Scope<JSNode> getGlobals(IParseNode root)
	{
		Scope<JSNode> result = null;
		
		if (root instanceof JSParseRootNode)
		{
			JSSymbolCollector s = new JSSymbolCollector();
			
			((JSParseRootNode) root).accept(s);
			
			result = s.getScope();
		}
		
		return result;
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
		Scope<JSNode> globals = this.getGlobals(ast);
		
		if (globals != null)
		{
			TypeElement type = new TypeElement();
			
			// set type
			type.setName("Window"); //$NON-NLS-1$
			
			// add properties
			for (PropertyElement property : JSTypeWalker.getScopeProperties(globals, index, location))
			{
				type.addProperty(property);
			}
			
			this._indexWriter.writeType(index, type, location);
			
			// TODO: process assignments
//			for (JSNode assignment : globals.getAssignments())
//			{
//				
//			}
		}
	}
}
