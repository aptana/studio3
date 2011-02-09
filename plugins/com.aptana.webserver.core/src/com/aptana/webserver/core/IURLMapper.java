/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.core;

import java.net.URL;

import org.eclipse.core.filesystem.IFileStore;

/**
 * 
 * @author Max Stepanov
 *
 */
public interface IURLMapper {

	/**
	 * Resolve file store to URL
	 * 
	 * @param file
	 * @return
	 */
	public URL resolve(IFileStore file);

	/**
	 * Resolve URL to file store
	 * 
	 * @param url
	 * @return
	 */
	public IFileStore resolve(URL url);

}