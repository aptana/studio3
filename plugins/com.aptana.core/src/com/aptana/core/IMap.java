/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

/**
 * IMap
 */
public interface IMap<T, U>
{
	/**
	 * Transform item of type T to a new item of type U
	 * 
	 * @param item
	 *            The item to transform
	 * @return Returns a transformed item of type U
	 */
	U map(T item);
}
