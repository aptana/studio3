/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;

import com.aptana.core.IURIMapper;
import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;

/**
 * A URIMapper for mapping an external base server URL to a document root (typically local).
 * 
 * @author cwilliams
 */
public class URLtoURIMapper implements IURIMapper
{

	private URL baseURL;
	private URI documentRoot;

	public URLtoURIMapper(URL baseURL, URI documentRoot)
	{
		this.baseURL = baseURL;
		this.documentRoot = documentRoot;
	}

	public URI resolve(IFileStore file)
	{
		if (file == null || !isValid())
		{
			return null;
		}
		try
		{
			IPath relativePath = EFSUtils.getRelativePath(EFS.getStore(documentRoot), file);
			if (relativePath != null)
			{
				try
				{
					return URIUtil.append(baseURL.toURI(), relativePath.toPortableString());
				}
				catch (URISyntaxException e)
				{
					IdeLog.logError(WebServerCorePlugin.getDefault(), e);
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(WebServerCorePlugin.getDefault(), e);
		}
		return null;
	}

	public IFileStore resolve(URI uri)
	{
		if (uri == null || !isValid())
		{
			return null;
		}
		try
		{
			return resolve(Path.fromPortableString(baseURL.toURI().relativize(uri).getPath()));
		}
		catch (URISyntaxException e)
		{
			IdeLog.logError(WebServerCorePlugin.getDefault(), e);
			return null;
		}
	}

	/**
	 * Resolves URI relative to server base URL
	 * 
	 * @param uri
	 * @return
	 */
	private IFileStore resolve(IPath path)
	{
		if (!isValid())
		{
			return null;
		}
		try
		{
			return EFS.getStore(documentRoot).getFileStore(path);
		}
		catch (CoreException e)
		{
			IdeLog.logError(WebServerCorePlugin.getDefault(), e);
			return null;
		}
	}

	private boolean isValid()
	{
		return baseURL != null && documentRoot != null;
	}
}
