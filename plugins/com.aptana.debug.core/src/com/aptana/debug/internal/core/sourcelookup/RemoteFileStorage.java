/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.internal.core.sourcelookup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.debug.core.JSDebugPlugin;
import com.aptana.debug.internal.core.IFileContentRetriever;

/**
 * @author Max Stepanov
 */
public class RemoteFileStorage extends UniformResourceStorage {
	private URI fURI;
	private IFileContentRetriever fRetriever;
	private InputStream fIn;

	/**
	 * RemoteFileStorage
	 * 
	 * @param uri
	 * @param retriever
	 */
	public RemoteFileStorage(URI uri, IFileContentRetriever retriever) {
		super();
		fURI = uri;
		fRetriever = retriever;
	}

	/**
	 * setFileContentRetriever
	 * 
	 * @param retriever
	 */
	protected void setFileContentRetriever(IFileContentRetriever retriever) {
		fRetriever = retriever;
		if (retriever != null) {
			fIn = null;
		}
	}

	/**
	 * getFileContentRetriever
	 * 
	 * @return IFileContentRetriever
	 */
	protected IFileContentRetriever getFileContentRetriever() {
		return fRetriever;
	}

	/**
	 * isValid
	 * 
	 * @return boolean
	 */
	public boolean isValid() {
		return fIn != null || super.isValid();
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		if (fIn == null && fRetriever != null) {
			fIn = fRetriever.getContents(fURI);
		}
		if (fIn != null) {
			try {
				if (fIn.markSupported()) {
					fIn.reset();
				}
			} catch (IOException e) {
				JSDebugPlugin.log(e);
			}
			return fIn;
		}
		return super.getContents();
	}

	/**
	 * @see com.aptana.ide.core.resources.UniformResourceStorage#exists()
	 */
	public boolean exists() {
		if (fRetriever != null) {
			return true;
		}
		return super.exists();
	}

	/**
	 * @see com.aptana.ide.core.resources.UniformResourceStorage#getURI()
	 */
	public URI getURI() {
		return fURI;
	}

	/**
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		String name = super.getName();
		if (name.length() == 0) {
			URI uri = getURI();
			if (uri != null) {
				String scheme = uri.getScheme();
				if (scheme.startsWith("http")) //$NON-NLS-1$
				{
					name = "default.html"; //$NON-NLS-1$
				} else if ("dbgsource".equals(scheme)) //$NON-NLS-1$
				{
					name = uri.getSchemeSpecificPart();
					int index = name.lastIndexOf('/');
					if (index >= 0) {
						name = name.substring(index + 1);
					}
				}
			}
		}
		return name;
	}
}
