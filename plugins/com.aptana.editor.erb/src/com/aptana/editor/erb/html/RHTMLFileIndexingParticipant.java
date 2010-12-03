package com.aptana.editor.erb.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.erb.IERBConstants;
import com.aptana.editor.html.contentassist.index.HTMLFileIndexingParticipant;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.ruby.RubyEditorPlugin;
import com.aptana.editor.ruby.index.RubyFileIndexingParticipant;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

public class RHTMLFileIndexingParticipant extends AbstractFileIndexingParticipant
{

	public void index(Set<IFileStore> files, final Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 100);
		for (final IFileStore store : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}
			Thread.yield(); // be nice to other threads, let them get in before each file...
			indexFileStore(index, store, sub.newChild(100));
		}
	}

	private void indexFileStore(final Index index, IFileStore store, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			if (store == null)
			{
				return;
			}

			sub.subTask(index.getRelativeDocumentPath(store.toURI()).toString());

			removeTasks(store, sub.newChild(10));

			// grab the source of the file we're going to parse
			String fileContents = IOUtil.read(store.openInputStream(EFS.NONE, sub.newChild(20)));
			indexSource(index, fileContents, store, sub.newChild(70));
		}
		catch (Throwable e)
		{
			RubyEditorPlugin.log(e);
		}
		finally
		{
			sub.done();
		}
	}

	private void indexSource(final Index index, String fileContents, IFileStore store, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			// minor optimization when creating a new empty file
			if (fileContents == null || fileContents.trim().length() <= 0)
			{
				return;
			}
			HTMLParseState parseState = new HTMLParseState();
			parseState.setEditState(fileContents, null, 0, 0);

			IParseNode parseNode = ParserPoolFactory.parse(IERBConstants.LANGUAGE_ERB, parseState);
			sub.worked(20);

			HTMLFileIndexingParticipant part = new HTMLFileIndexingParticipant();
			part.walkAST(index, store, fileContents, parseNode, sub.newChild(30));

			// TODO Grab the ruby code only, replace rest with whitespace. Then parse and index that too!
			String rubyContents = replaceNonRubyCodeWithWhitespace(fileContents);
			sub.worked(5);
			RubyFileIndexingParticipant rfip = new RubyFileIndexingParticipant();
			rfip.indexSource(index, rubyContents, store, sub.newChild(45));
		}
		finally
		{
			sub.done();
		}
	}

	public static String replaceNonRubyCodeWithWhitespace(String source)
	{
		List<String> code = getRubyCodeChunks(source);
		if (code == null || code.size() == 0)
		{
			return fillWithWhitespace(source);
		}

		StringBuilder buffer = new StringBuilder();
		int endOfLastFragment = 0;
		boolean dontIncludeSemicolon = false;
		for (String codeFragment : code)
		{
			int beginningOfCurrentFragment = source.indexOf(codeFragment, endOfLastFragment); // find index of current
			// piece of code,
			// start looking after last piece of
			// code
			// replace from end of last code piece to beginning of next with
			// spaces for any non-whitespace characters in between
			if (codeFragment.startsWith("#")) //$NON-NLS-1$
			{
				codeFragment = fillWithWhitespace(codeFragment);
				dontIncludeSemicolon = true;
			}
			String portion = source.substring(endOfLastFragment, beginningOfCurrentFragment);
			for (int j = 0; j < portion.length(); j++)
			{
				char chr = portion.charAt(j);
				if (Character.isWhitespace(chr))
				{
					buffer.append(chr);
				}
				else
				{
					if (j != 0 && chr == '>' && portion.charAt(j - 1) == '%')
					{
						if (dontIncludeSemicolon)
						{
							buffer.append(' ');
							dontIncludeSemicolon = false;
						}
						else
							buffer.append(';');
					}
					else
					{
						buffer.append(' ');
					}
				}
			}

			buffer.append(codeFragment); // now add in code piece
			endOfLastFragment = beginningOfCurrentFragment + codeFragment.length(); // now search from end of
			// current fragment
		}
		return buffer.toString();
	}

	/**
	 * Takes a string and replaces all non-whitespace content with space characters (retains any existing whitespace in
	 * place).
	 * 
	 * @param source
	 * @return
	 */
	private static String fillWithWhitespace(String source)
	{
		StringBuilder buffer = new StringBuilder();
		for (int j = 0; j < source.length(); j++)
		{
			char chr = source.charAt(j);
			if (Character.isWhitespace(chr))
			{
				buffer.append(chr);
			}
			else
			{
				buffer.append(' ');
			}
		}
		return buffer.toString();
	}

	private static List<String> getRubyCodeChunks(String stringContents)
	{
		List<String> code = new ArrayList<String>();
		String[] pieces = stringContents.split("(<%%)|(%%>)|(<%=)|(<%)|(\\-?%>)"); //$NON-NLS-1$
		for (int i = 0; i < pieces.length; i++)
		{
			if ((i % 2) == 1)
			{
				code.add(pieces[i]);
			}
		}
		return code;
	}

}
