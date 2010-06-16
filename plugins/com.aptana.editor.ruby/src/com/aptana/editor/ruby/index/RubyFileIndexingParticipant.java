package com.aptana.editor.ruby.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
import com.aptana.index.core.IFileIndexingParticipant;
import com.aptana.index.core.Index;

public class RubyFileIndexingParticipant implements IFileIndexingParticipant
{

	private static final String RUBY_EXTENSION = "rb"; //$NON-NLS-1$

	@Override
	public void index(Set<IFileStore> files, final Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size());
		for (final IFileStore store : files)
		{
			if (sub.isCanceled())
				throw new CoreException(Status.CANCEL_STATUS);
			if (store == null)
				continue;
			sub.subTask(store.getName());
			if (!isRubyFile(store))
				continue;
			try
			{
				// grab the source of the file we're going to parse
				String source = IOUtil.read(store.openInputStream(EFS.NONE, monitor));

				// minor optimization when creating a new empty file
				if (source != null && source.length() > 0)
				{

					RubySourceParser sourceParser = new RubySourceParser();
					ParserResult result = sourceParser.parse(store.getName(), source);
					Node root = result.getAST();
					ISourceElementRequestor builder = new RubySourceIndexer(index, getFilePath(store));
					SourceElementVisitor visitor = new SourceElementVisitor(builder);
					visitor.acceptNode(root);
				}
			}
			catch (Exception e)
			{
				Activator.log(e);
			}
			sub.worked(1);
		}
	}

	public static String getFilePath(final IFileStore store)
	{
		// HACK Detect when it's a core stub and change the reported name to "Ruby Core"
		String path = store.toURI().getPath();
		if (path.contains(".metadata")) //$NON-NLS-1$
		{
			return "Ruby Core"; //$NON-NLS-1$
		}
		return path;
	}

	private boolean isRubyFile(IFileStore file)
	{
		InputStream stream = null;
		IContentTypeManager manager = Platform.getContentTypeManager();
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

		return RUBY_EXTENSION.equalsIgnoreCase(new Path(file.getName()).getFileExtension());
	}
}
