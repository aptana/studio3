/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

/**
 * This returns the top level content types that are possible.
 * 
 * @author schitale
 *
 */
public interface ITopContentTypesProvider {
	// TODO Use generic collections instead of arrays
	String[][] getTopContentTypes();
}
