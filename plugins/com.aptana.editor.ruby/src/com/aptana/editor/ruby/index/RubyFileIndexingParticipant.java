package com.aptana.editor.ruby.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.IParserPool;
import com.aptana.editor.common.ParserPoolFactory;
import com.aptana.editor.ruby.Activator;
import com.aptana.editor.ruby.IRubyConstants;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.editor.ruby.parsing.ast.RubyImport;
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class RubyFileIndexingParticipant implements IFileIndexingParticipant
{

	private static final String RUBY_EXTENSION = "rb"; //$NON-NLS-1$

	@Override
	public void index(Set<IFile> files, Index index, IProgressMonitor monitor)
	{
		for (IFile file : files)
		{
			if (isRubyFile(file))
			{
				try
				{
					// grab the source of the file we're going to parse
					String source = IOUtil.read(file.getContents());

					// minor optimization when creating a new empty file
					if (source != null && source.length() > 0)
					{
						IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IRubyParserConstants.LANGUAGE);
						IParser parser = pool.checkOut();
						
						// create parser and associated parse state
						ParseState parseState = new ParseState();
						
						// apply the source to the parse state
						parseState.setEditState(source, source, 0, 0);

						// parse and grab the result
						IParseNode ast = parser.parse(parseState);
						pool.checkIn(parser);
						
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

	private boolean isRubyFile(IFile file)
	{
		InputStream stream = null;
		IContentTypeManager manager = Platform.getContentTypeManager();
		try
		{
			stream = file.getContents();
			IContentType[] types = manager.findContentTypesFor(stream, file.getName());
			for (IContentType type : types)
			{
				if (type.getId().equals(IRubyConstants.CONTENT_TYPE_RUBY)
						|| type.getId().equals(IRubyConstants.CONTENT_TYPE_RUBY_AMBIGUOUS))
					return true;
			}
		}
		catch (Exception e)
		{
			Activator.log(e);
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

		return RUBY_EXTENSION.equalsIgnoreCase(file.getFileExtension());
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
					// FIXME This isn't checking loadpaths in any sort of way. Requires may be relative to loadpaths,
					// which may be the file's parent, the project root, the working dir, std library, gems, etc!
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
