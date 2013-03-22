/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.sourcemap;

import org.eclipse.core.runtime.IPath;

/**
 * Represents a source map result.
 * 
 * @author sgibly@appcelerator.com
 */
public interface ISourceMapResult
{
	/**
	 * Returns the mapped file path.
	 * 
	 * @return A file path
	 */
	IPath getFile();

	/**
	 * Returns the mapped line number.
	 * 
	 * @return line number (1 based)
	 */
	int getLineNumber();

	/**
	 * Returns the mapped column position (offset).
	 * 
	 * @return The column position (1 based)
	 */
	int getColumnPosition();

}
