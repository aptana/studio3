/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;

import com.aptana.core.epl.IMemento;
import com.aptana.core.io.efs.EFSUtils;

/**
 * @author Max Stepanov
 *
 */
public class EFSWebServerConfiguration extends AbstractWebServerConfiguration {

	private static final String ELEMENT_BASE_URL = "baseURL"; //$NON-NLS-1$
	private static final String ELEMENT_DOCUMENT_ROOT = "documentRoot"; //$NON-NLS-1$

	private URL baseURL;
	protected URI documentRoot;

	/* (non-Javadoc)
	 * @see com.aptana.webserver.core.AbstractWebServerConfiguration#resolve(org.eclipse.core.filesystem.IFileStore)
	 */
	@Override
	public URI resolve(IFileStore file) {
		if (!isValid()) {
			return null;
		}
		try {
			IPath relativePath = EFSUtils.getRelativePath(EFS.getStore(documentRoot), file);
			if (relativePath != null) {
				try {
					return URIUtil.append(baseURL.toURI(), relativePath.toPortableString());
				} catch (URISyntaxException e) {
					WebServerCorePlugin.log(e);
				}
			}
		} catch (CoreException e) {
			WebServerCorePlugin.log(e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.webserver.core.AbstractWebServerConfiguration#resolve(java.net.URL)
	 */
	@Override
	public IFileStore resolve(URI uri) {
		if (!isValid()) {
			return null;
		}
		try {
			return resolve(Path.fromPortableString(baseURL.toURI().relativize(uri).getPath()));
		} catch (URISyntaxException e) {
			WebServerCorePlugin.log(e);
			return null;
		}
	}
	
	/**
	 * Resolves URI relative to server base URL
	 * @param uri
	 * @return
	 */
	public IFileStore resolve(IPath path) {
		if (!isValid()) {
			return null;
		}
		try {
			return EFS.getStore(documentRoot).getFileStore(path);
		} catch (CoreException e) {
			WebServerCorePlugin.log(e);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.webserver.core.AbstractWebServerConfiguration#loadState(com.aptana.core.epl.IMemento)
	 */
	@Override
	protected void loadState(IMemento memento) {
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_BASE_URL);
		if (child != null) {
			try {
				baseURL = new URL(child.getTextData());
			} catch (MalformedURLException e) {
				WebServerCorePlugin.log(e);
			}
		}
		child = memento.getChild(ELEMENT_DOCUMENT_ROOT);
		if (child != null) {
			String text = child.getTextData();
			if (text != null) {
				try {
					documentRoot = URI.create(text);
				} catch (IllegalArgumentException e) {
					WebServerCorePlugin.log(e);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.webserver.core.AbstractWebServerConfiguration#saveState(com.aptana.core.epl.IMemento)
	 */
	@Override
	protected void saveState(IMemento memento) {
		super.saveState(memento);
		if (baseURL != null) {
			memento.createChild(ELEMENT_BASE_URL).putTextData(baseURL.toExternalForm());
		}
		if (documentRoot != null) {
			memento.createChild(ELEMENT_DOCUMENT_ROOT).putTextData(documentRoot.toASCIIString());
		}
	}

	private boolean isValid() {
		return baseURL != null && documentRoot != null;
	}

	/**
	 * @return the baseURL
	 */
	public URL getBaseURL() {
		return baseURL;
	}

	/**
	 * @param baseURL the baseURL to set
	 */
	public void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * @return the documentRoot
	 */
	public URI getDocumentRoot() {
		return documentRoot;
	}

	/**
	 * @param documentRoot the documentRoot to set
	 */
	public void setDocumentRoot(URI documentRoot) {
		this.documentRoot = documentRoot;
	}
	
	public static EFSWebServerConfiguration create(URL baseURL, URI documentRoot) {
		EFSWebServerConfiguration configuration = new EFSWebServerConfiguration();
		configuration.setBaseURL(baseURL);
		configuration.setDocumentRoot(documentRoot);
		return configuration;
	}

}
