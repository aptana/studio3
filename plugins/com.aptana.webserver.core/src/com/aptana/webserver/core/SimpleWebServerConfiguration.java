/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.core;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Max Stepanov
 *
 */
public class SimpleWebServerConfiguration extends EFSWebServerConfiguration {

	/**
	 * @return the documentRoot
	 */
	public IPath getDocumentRootPath() {
		if (documentRoot == null) {
			return null;
		}
		return Path.fromPortableString(documentRoot.getSchemeSpecificPart());
	}

	/**
	 * @param documentRoot the documentRoot to set
	 */
	public void setDocumentRootPath(IPath documentRoot) {
		this.documentRoot = EFS.getLocalFileSystem().getStore(documentRoot).toURI();
	}
}
