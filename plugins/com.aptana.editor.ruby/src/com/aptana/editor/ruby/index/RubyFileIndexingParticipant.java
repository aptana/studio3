package com.aptana.editor.ruby.index;

import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserResult;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.ruby.Activator;
import com.aptana.editor.ruby.parsing.ISourceElementRequestor;
import com.aptana.editor.ruby.parsing.RubySourceParser;
import com.aptana.editor.ruby.parsing.SourceElementVisitor;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;

public class RubyFileIndexingParticipant implements IFileStoreIndexingParticipant
{

	@Override
	public void index(Set<IFileStore> files, final Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 100);
		for (final IFileStore store : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}
			indexFileStore(index, store, sub.newChild(100));
		}
	}

	private void indexFileStore(final Index index, IFileStore store, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		if (store == null)
		{
			return;
		}
		try
		{
			sub.subTask(store.toString());

			// grab the source of the file we're going to parse
			String source = IOUtil.read(store.openInputStream(EFS.NONE, sub.newChild(20)));

			// minor optimization when creating a new empty file
			if (source == null || source.length() <= 0)
			{
				return;
			}
			
			RubySourceParser sourceParser = new RubySourceParser();
			ParserResult result = sourceParser.parse(store.getName(), source);
			sub.worked(50);
			Node root = result.getAST();
			ISourceElementRequestor builder = new RubySourceIndexer(index, store.toURI());
			SourceElementVisitor visitor = new SourceElementVisitor(builder);
			visitor.acceptNode(root);
		}
		catch (Throwable e)
		{
			Activator.log(e);
		}
		finally
		{
			sub.done();
		}
	}

}
