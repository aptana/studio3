package com.aptana.editor.ruby.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserResult;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.ruby.Activator;
import com.aptana.editor.ruby.IRubyConstants;
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
		SubMonitor sub = SubMonitor.convert(monitor, files.size());
		try
		{
			RubySourceParser sourceParser = new RubySourceParser();
			for (final IFileStore store : files)
			{
				if (sub.isCanceled())
				{
					throw new CoreException(Status.CANCEL_STATUS);
				}
				try
				{
					if (store == null)
					{
						continue;
					}
					sub.subTask(store.toString());
					if (!isRubyFile(store))
					{
						continue;
					}

					// grab the source of the file we're going to parse
					String source = IOUtil.read(store.openInputStream(EFS.NONE, monitor));

					// minor optimization when creating a new empty file
					if (source != null && source.length() > 0)
					{
						ParserResult result = sourceParser.parse(store.getName(), source);
						Node root = result.getAST();
						ISourceElementRequestor builder = new RubySourceIndexer(index, store.toURI().getPath());
						SourceElementVisitor visitor = new SourceElementVisitor(builder);
						visitor.acceptNode(root);
					}
				}
				catch (Throwable e)
				{
					Activator.log(e);
				}
				finally
				{
					sub.worked(1);
				}
			}
		}
		finally
		{
			sub.done();
		}
	}

	private boolean isRubyFile(IFileStore file)
	{
		// Try a faster way, just check filename/extension against ruby content type
		IContentTypeManager manager = Platform.getContentTypeManager();
		IContentType rubyType = manager.getContentType(IRubyConstants.CONTENT_TYPE_RUBY);
		if (rubyType != null)
		{
			if (rubyType.isAssociatedWith(file.getName()))
			{
				return true;
			}
		}

		// Ok, now try slower way where we actually use a stream and grab all content types for file.
		InputStream stream = null;
		try
		{
			stream = file.openInputStream(EFS.NONE, new NullProgressMonitor());
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
			// TODO This can often be caused by permissions issues. We should probably just ignore them
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

		return false;
	}
}
