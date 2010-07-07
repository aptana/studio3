package com.aptana.editor.js.contentassist.index;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSASTQueryHelper;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;
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
		if (Platform.inDevelopmentMode())
		{
			URI location = file.toURI();
			Scope<JSNode> globals = ((JSParseRootNode) ast).getGlobalScope();

			for (String symbol : globals.getLocalSymbolNames())
			{
				List<JSNode> nodes = globals.getSymbol(symbol);
				String category = JSIndexConstants.VARIABLE;

				for (JSNode node : nodes)
				{
					DocumentationBlock block = node.getDocumentation();

					if (block != null)
					{
						// System.out.println("Found block for " + symbol + "\n" + block.toSource());
					}

					if (node instanceof JSFunctionNode)
					{
						category = JSIndexConstants.FUNCTION;
						break;
					}
				}

				index.addEntry(category, symbol, location);
			}
		}
		else
		{
			this.walkAST(index, file, ast);
		}
	}

	/**
	 * walkAST
	 * 
	 * @param index
	 * @param file
	 * @param ast
	 */
	private void walkAST(Index index, IFileStore file, IParseNode ast)
	{
		JSASTQueryHelper astHelper = new JSASTQueryHelper();
		URI location = file.toURI();

		for (String name : astHelper.getChildFunctions(ast))
		{
			index.addEntry(JSIndexConstants.FUNCTION, name, location);
		}
		for (String varName : astHelper.getChildVarNonFunctions(ast))
		{
			index.addEntry(JSIndexConstants.VARIABLE, varName, location);
		}
		// for (String varName : astHelper.getAccidentalGlobals(ast))
		// {
		// System.out.println("accidental global: " + varName);
		// index.addEntry(JSIndexConstants.VARIABLE, varName, location);
		// }
	}
}
