/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.server;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.URIUtil;

import com.aptana.core.epl.IMemento;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.preview.Activator;

/**
 * @author Max Stepanov
 * 
 */
public class SimpleWebServerConfiguration extends AbstractWebServerConfiguration {

	private static final String ELEMENT_BASE_URL = "baseURL"; //$NON-NLS-1$
	private static final String ELEMENT_DOCUMENT_ROOT = "documentRoot"; //$NON-NLS-1$

	private URL baseURL;
	private IPath documentRoot;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.preview.server.AbstractWebServerConfiguration#resolve(org.
	 * eclipse.core.filesystem.IFileStore)
	 */
	@Override
	public URL resolve(IFileStore file) {
		if (!isValid()) {
			return null;
		}
		IPath relativePath = EFSUtils.getRelativePath(EFS.getLocalFileSystem().getStore(documentRoot), file);
		if (relativePath != null) {
			try {
				URI uri = URIUtil.append(baseURL.toURI(), relativePath.toPortableString());
				return uri.toURL();
			} catch (URISyntaxException e) {
				Activator.log(e);
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.preview.server.AbstractWebServerConfiguration#resolve(java
	 * .net.URL)
	 */
	@Override
	public IFileStore resolve(URL url) {
		if (!isValid()) {
			return null;
		}
		// TODO
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.preview.server.AbstractWebServerConfiguration#loadState(com
	 * .aptana.core.epl.IMemento)
	 */
	@Override
	protected void loadState(IMemento memento) {
		super.loadState(memento);
		IMemento child = memento.getChild(ELEMENT_BASE_URL);
		if (child != null) {
			try {
				baseURL = new URL(child.getTextData());
			} catch (MalformedURLException e) {
				Activator.log(e);
			}
		}
		child = memento.getChild(ELEMENT_DOCUMENT_ROOT);
		if (child != null) {
			String text = child.getTextData();
			if (text != null) {
				documentRoot = Path.fromPortableString(text);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.aptana.preview.server.AbstractWebServerConfiguration#saveState(com
	 * .aptana.core.epl.IMemento)
	 */
	@Override
	protected void saveState(IMemento memento) {
		super.saveState(memento);
		if (baseURL != null) {
			memento.createChild(ELEMENT_BASE_URL).putTextData(baseURL.toExternalForm());
		}
		if (documentRoot != null) {
			memento.createChild(ELEMENT_DOCUMENT_ROOT).putTextData(documentRoot.toPortableString());
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
	 * @param baseURL
	 *            the baseURL to set
	 */
	public void setBaseURL(URL baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * @return the documentRoot
	 */
	public IPath getDocumentRoot() {
		return documentRoot;
	}

	/**
	 * @param documentRoot
	 *            the documentRoot to set
	 */
	public void setDocumentRoot(IPath documentRoot) {
		this.documentRoot = documentRoot;
	}

}
