/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;

/**
 * @author Max Stepanov
 */
public interface IURIMapper
{

	/**
	 * Resolve file store to URI
	 * 
	 * @param file
	 * @return
	 */
	public URI resolve(IFileStore file);

	/**
	 * Resolve URI to file store
	 * 
	 * @param uri
	 * @return
	 */
	public IFileStore resolve(URI uri);

}