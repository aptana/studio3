package com.aptana.editor.ruby.index;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.ruby.Activator;
import com.aptana.editor.ruby.parsing.RubyParser;
import com.aptana.editor.ruby.parsing.ast.RubyImport;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.util.IOUtil;

public class RubyFileIndexingParticipant implements IFileIndexingParticipant
{

	private static final String RUBY_EXTENSION = "rb"; //$NON-NLS-1$

	@Override
	public void index(Set<IFile> files, Index index, IProgressMonitor monitor)
	{
		String extension;
		for (IFile file : files)
		{
			extension = file.getFileExtension();

			if (RUBY_EXTENSION.equalsIgnoreCase(extension))
			{
				try
				{
					// create parser and associated parse state
					RubyParser parser = new RubyParser();
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
						walkAST(index, file, ast);
					}
				}
				catch (CoreException e)
				{
					Activator.log(e);
				}
				catch (Exception e)
				{
					Activator.log(e);
				}
			}
		}
	}

	private void walkAST(Index index, IFile file, IParseNode ast)
	{
		Queue<IParseNode> queue = new LinkedList<IParseNode>();
		queue.add(ast);

		IParseNode node;
		while (queue.size() > 0)
		{
			node = queue.remove();

			// process functions
			if (node instanceof RubyImport)
			{
				RubyImport require = (RubyImport) node;
				String name = require.getName();
				if (name != null)
				{
					IFile requireFile = file.getParent().getFile(new Path(name));
					if (requireFile.exists())
					{
						addIndex(index, file, IRubyIndexConstants.REQUIRE, requireFile.getProjectRelativePath()
								.toPortableString());
					}
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

	private static void addIndex(Index index, IFile file, String category, String word)
	{
		index.addEntry(category, word, file.getProjectRelativePath().toPortableString());
	}
}
