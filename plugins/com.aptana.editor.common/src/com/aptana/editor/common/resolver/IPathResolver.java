/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.resolver;

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * interface allowing clients to access source basing abstract absolute or relative path
 * @author Pavel Petrochenko
 * @author Chris Williams
 */
public interface IPathResolver
{
	/**
	 * @param path
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	String resolveSource(String path, IProgressMonitor monitor) throws Exception;
	
	URI resolveURI(String path);
}