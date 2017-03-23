/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.sourcelookup;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.UniformResourceStorage;
import com.aptana.debug.core.DebugCorePlugin;

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
	@Override
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
				IdeLog.logWarning(DebugCorePlugin.getDefault(), e);
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
