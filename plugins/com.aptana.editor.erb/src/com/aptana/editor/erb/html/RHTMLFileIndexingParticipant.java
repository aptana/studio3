package com.aptana.editor.erb.html;

import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.jrubyparser.ast.CommentNode;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.tasks.TaskTag;
import com.aptana.editor.erb.IERBConstants;
import com.aptana.editor.html.contentassist.index.HTMLFileIndexingParticipant;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.ruby.RubyEditorPlugin;
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

			// minor optimization when creating a new empty file
			if (fileContents != null && fileContents.trim().length() > 0)
			{
				HTMLParseState parseState = new HTMLParseState();
				parseState.setEditState(fileContents, null, 0, 0);

				IParseNode parseNode = ParserPoolFactory.parse(IERBConstants.LANGUAGE_ERB, parseState);
				sub.worked(50);

				HTMLFileIndexingParticipant part = new HTMLFileIndexingParticipant();
				part.walkAST(index, store, fileContents, parseNode, sub.newChild(20));

				// TODO Grab the ruby code only, replace rest with whitespace. Then parse and index that too!

			}
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
}
