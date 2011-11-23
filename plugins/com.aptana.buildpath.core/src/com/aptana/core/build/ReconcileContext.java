package com.aptana.core.build;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.index.core.build.BuildContext;

public class ReconcileContext extends BuildContext
{

	private String contents;
	private String contentType;

	public ReconcileContext(String contentType, IFile file, String contents)
	{
		super(file);
		this.contents = contents;
		this.contentType = contentType;
	}

	@Override
	public String getContents() throws CoreException
	{
		return contents;
	}

	@Override
	public InputStream openInputStream(IProgressMonitor monitor) throws CoreException
	{
		try
		{
			return new ByteArrayInputStream(getContents().getBytes(getCharset()));
		}
		catch (UnsupportedEncodingException e)
		{
			throw new CoreException(new Status(IStatus.ERROR, BuildPathCorePlugin.PLUGIN_ID,
					"Failed to open input stream on editor contents due to unsupported encoding exception", e)); //$NON-NLS-1$
		}
	}

	@Override
	public String getContentType() throws CoreException
	{
		return contentType;
	}

}
