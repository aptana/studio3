package com.aptana.editor.js.index;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSFunctionNode;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.util.IOUtil;

public class JSFileIndexingParticipant implements IFileIndexingParticipant
{
	private static final String JS_EXTENSION = "js"; //$NON-NLS-1$
	
	/**
	 * JSFileIndexingParticipant
	 */
	public JSFileIndexingParticipant()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void index(Set<IFile> files, Index index, IProgressMonitor monitor)
	{
		for (IFile file : files)
		{
			String fileExtension = file.getFileExtension();
			
			if (JS_EXTENSION.equalsIgnoreCase(fileExtension))
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
					e.printStackTrace();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
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
					index.addEntry(IndexConstants.FUNCTION, name, file.getProjectRelativePath().toPortableString());
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
