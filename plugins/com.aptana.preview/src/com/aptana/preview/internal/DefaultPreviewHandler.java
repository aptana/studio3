/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.internal;

import java.net.MalformedURLException;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.debug.core.ILaunchManager;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.preview.IPreviewHandler;
import com.aptana.preview.PreviewConfig;
import com.aptana.preview.PreviewPlugin;
import com.aptana.preview.SourceConfig;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.IServer.State;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.core.WorkspaceResolvingURIMapper;

/**
 * @author Max Stepanov
 */
public final class DefaultPreviewHandler implements IPreviewHandler
{

	private static DefaultPreviewHandler instance;
	private IContentType contentTypeHTML;

	/**
	 * 
	 */
	private DefaultPreviewHandler()
	{
		contentTypeHTML = Platform.getContentTypeManager().findContentTypeFor("index.html"); //$NON-NLS-1$
	}

	public synchronized static DefaultPreviewHandler getInstance()
	{
		if (instance == null)
		{
			instance = new DefaultPreviewHandler();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.preview.IPreviewHandler#handle(com.aptana.preview.SourceConfig )
	 */
	public PreviewConfig handle(SourceConfig config) throws CoreException
	{
		if (contentTypeHTML != null && contentTypeHTML.isAssociatedWith(config.getLocation().lastSegment()))
		{
			try
			{
				IPath location = config.getLocation();
				URI uri = null;
				if (config.getProject() != null)
				{
					IFile resource = ResourcesPlugin.getWorkspace().getRoot().getFile(location);

					IServer server = WebServerCorePlugin.getDefault().getBuiltinWebServer();
					if (server.getState() != State.STARTED && server.getState() != State.STARTING)
					{
						IStatus result = server.start(ILaunchManager.RUN_MODE, new NullProgressMonitor());
						if (!result.isOK())
						{
							// TODO Wrap in a better error?
							throw new CoreException(result);
						}
					}

					uri = new WorkspaceResolvingURIMapper(server).resolve(EFSUtils.getFileStore(resource));
					if (uri == null)
					{
						location = resource.getLocation();
					}
				}
				if (config.getContent() != null)
				{
					return null; // we're not handling content preview requests
				}
				if (uri == null)
				{
					uri = location.toFile().toURI();
				}
				return new PreviewConfig(uri.toURL());
			}
			catch (MalformedURLException e)
			{
				throw new CoreException(new Status(IStatus.ERROR, PreviewPlugin.PLUGIN_ID, "", e)); //$NON-NLS-1$
			}
		}
		return null;
	}

}
