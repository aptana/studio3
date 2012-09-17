/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.aptana.buildpath.core.BuildPathCorePlugin;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.build.BuildContext;

/**
 * Special subclass of {@link BuildContext} that is used for reconciling. The contents/inputStream are attached to the
 * editor's contents.
 * 
 * @author cwilliams
 */
public class ReconcileContext extends BuildContext
{

	private String contents;
	private String contentType;
	private URI uri;

	/**
	 * When reconciling a file that is in the workspace
	 * 
	 * @param contentType
	 * @param file
	 * @param contents
	 */
	public ReconcileContext(String contentType, IFile file, String contents)
	{
		super(file);
		this.contents = (contents == null) ? StringUtil.EMPTY : contents;
		this.contentType = contentType;
	}

	/**
	 * When reconciling a file external to the workspace.
	 * 
	 * @param contentType
	 * @param uri
	 * @param contents
	 */
	public ReconcileContext(String contentType, URI uri, String contents)
	{
		this(contentType, (IFile) null, contents);
		this.uri = uri;
	}

	@Override
	public URI getURI()
	{
		if (uri != null)
		{
			return uri;
		}
		return super.getURI();
	}

	@Override
	public String getName()
	{
		String name = super.getName();
		if (name != null)
		{
			return name;
		}
		return Path.fromPortableString(getURI().getPath()).lastSegment();
	}

	@SuppressWarnings("sync-override")
	@Override
	public String getContents()
	{
		return contents;
	}

	@Override
	public InputStream openInputStream(IProgressMonitor monitor) throws CoreException
	{
		try
		{
			String charset = getCharset();
			if (charset == null)
			{
				charset = IOUtil.UTF_8;
			}
			return new ByteArrayInputStream(getContents().getBytes(charset));
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

	@Override
	public boolean isReconcile()
	{
		return true;
	}
}
