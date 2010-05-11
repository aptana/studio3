package com.aptana.editor.js.contentassist.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.js.Activator;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

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
		for (IFile file : files)
		{
			if (isJSFile(file))
			{
				try
				{
					IParseNode ast;

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
						ast = parser.parse(parseState);

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
		}
	}

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
					return true;
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
					stream.close();
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
		Queue<IParseNode> queue = new LinkedList<IParseNode>();

		queue.add(ast);

		while (queue.size() > 0)
		{
			IParseNode node = queue.remove();

			// process functions
			if (node instanceof JSFunctionNode)
			{
				JSFunctionNode function = (JSFunctionNode) node;
				String name = function.getName();

				if (name != null && name.length() > 0)
				{
					index.addEntry(JSIndexConstants.FUNCTION, name, file.getProjectRelativePath().toPortableString());
				}

			}

			// add children for processing
			IParseNode[] children = node.getChildren();

			if (children != null && children.length > 0)
			{
				queue.addAll(Arrays.asList(children));
			}
		}
	}
}
