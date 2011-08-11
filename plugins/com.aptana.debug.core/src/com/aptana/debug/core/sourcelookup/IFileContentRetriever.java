/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.sourcelookup;

import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Max Stepanov
 */
public interface IFileContentRetriever {

	/**
	 * Returns source code input stream for the provided URI
	 * 
	 * @param uri
	 * @return InputStream
	 * @throws CoreException
	 */
	InputStream getContents(URI uri) throws CoreException;
}
