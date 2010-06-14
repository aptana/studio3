package com.aptana.editor.ruby.index;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
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
	public void index(Set<IFile> files, final Index index, IProgressMonitor monitor)
	{
		for (final IFile file : files)
		{
			if (!isRubyFile(file))
				continue;
			try
			{
				// grab the source of the file we're going to parse
				String source = IOUtil.read(file.getContents());

				// minor optimization when creating a new empty file
				if (source != null && source.length() > 0)
				{

					RubySourceParser sourceParser = new RubySourceParser();
					ParserResult result = sourceParser.parse(file.getName(), source);
					Node root = result.getAST();
					ISourceElementRequestor builder = new RubySourceIndexer(index, file);
					SourceElementVisitor visitor = new SourceElementVisitor(builder);
					visitor.acceptNode(root);
				}
			}
			catch (Exception e)
			{
				Activator.log(e);
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
}
