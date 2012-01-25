/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

/**
 * IFilter
 */
public interface IFilter<T>
{
	/**
	 * Determine if the specified item should be filtered or not. A true value indicates that the item is not being
	 * filtered.
	 * 
	 * @param item
	 *            The item to possibly filter
	 * @return Returns a boolean
	 */
	boolean include(T item);
}
