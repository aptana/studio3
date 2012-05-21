/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.webserver.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.epl.IMemento;
import com.aptana.core.logging.IdeLog;

/**
 * A simple web server configuration. This simply assumes a server mapping to a local document root. Users cannot start,
 * stop, or restart this server.
 * 
 * @author Max Stepanov
 */
public class SimpleWebServer extends AbstractWebServer
{

	private static final String ELEMENT_BASE_URL = "baseURL"; //$NON-NLS-1$
	private static final String ELEMENT_DOCUMENT_ROOT = "documentRoot"; //$NON-NLS-1$

	private URL baseURL;
	private URI documentRoot;

	public SimpleWebServer()
	{
	}

	public URL getBaseURL()
	{
		return baseURL;
	}

	public void saveState(IMemento memento)
	{
		super.saveState(memento);
		if (baseURL != null)
		{
			memento.createChild(ELEMENT_BASE_URL).putTextData(baseURL.toExternalForm());
		}
		if (documentRoot != null)
		{
			memento.createChild(ELEMENT_DOCUMENT_ROOT).putTextData(documentRoot.toASCIIString());
		}
	}

	public void loadState(IMemento memento)
	{
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_BASE_URL);
		if (child != null)
		{
			try
			{
				baseURL = new URL(child.getTextData());
			}
			catch (MalformedURLException e)
			{
				IdeLog.logError(WebServerCorePlugin.getDefault(), e);
			}
		}
		child = memento.getChild(ELEMENT_DOCUMENT_ROOT);
		if (child != null)
		{
			String text = child.getTextData();
			if (text != null)
			{
				try
				{
					documentRoot = URI.create(text);
				}
				catch (IllegalArgumentException e)
				{
					IdeLog.logError(WebServerCorePlugin.getDefault(), e);
				}
			}
		}
	}

	public URI getDocumentRoot()
	{
		return documentRoot;
	}

	/**
	 * @return the documentRoot
	 */
	public IPath getDocumentRootPath()
	{
		URI rootUri = getDocumentRoot();
		if (rootUri == null)
		{
			return null;
		}
		File docRoot = new File(rootUri);
		return Path.fromOSString(docRoot.getAbsolutePath());
	}

	/**
	 * @param documentRoot
	 *            the documentRoot to set
	 */
	public void setDocumentRootPath(IPath documentRoot)
	{
		setDocumentRoot(EFS.getLocalFileSystem().getStore(documentRoot).toURI());
	}

	public String getHostname()
	{
		return getBaseURL().getHost();
	}

	public int getPort()
	{
		return getBaseURL().getPort();
	}

	public void setBaseURL(URL url)
	{
		this.baseURL = url;
	}

	public IStatus stop(boolean force, IProgressMonitor monitor)
	{
		return new Status(IStatus.ERROR, WebServerCorePlugin.PLUGIN_ID, Messages.SimpleWebServer_ERR_StopNotSupported);
	}

	public IStatus start(String mode, IProgressMonitor monitor)
	{
		return new Status(IStatus.ERROR, WebServerCorePlugin.PLUGIN_ID, Messages.SimpleWebServer_ERR_StartNotSupported);
	}

	public String getMode()
	{
		return null;
	}

	public State getState()
	{
		return State.NOT_APPLICABLE;
	}

	public ILaunch getLaunch()
	{
		return null;
	}

	public IProcess[] getProcesses()
	{
		return new IProcess[0];
	}

	public URI resolve(IFileStore file)
	{
		return new URLtoURIMapper(getBaseURL(), getDocumentRoot()).resolve(file);
	}

	public IFileStore resolve(URI uri)
	{
		return new URLtoURIMapper(getBaseURL(), getDocumentRoot()).resolve(uri);
	}

	public void setDocumentRoot(URI uri)
	{
		this.documentRoot = uri;
	}

	public Set<String> getAvailableModes()
	{
		return Collections.emptySet();
	}
}
