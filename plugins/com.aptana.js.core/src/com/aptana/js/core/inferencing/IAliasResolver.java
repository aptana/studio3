/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import org.eclipse.core.runtime.IPath;

public interface IAliasResolver
{
	/**
	 * Resolves a destination type for a source type in the current editor path
	 * 
	 * @param sourceType
	 * @return resolved Type
	 */
	public String resolve(String sourceType, IPath editorPath, IPath projectPath);
}
