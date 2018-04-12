/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import com.aptana.core.IFilter;

/**
 * Combines multiple filters using "or". If any filter returns true for an item, we return true. If all filters return
 * false for an item, we return false.
 * 
 * @author Chris Williams <cwilliams@appcelerator.com>
 * @param <T>
 */
public class OrFilter<T> implements IFilter<T>
{

	private IFilter<T>[] filters;
	private int filterLength;

	public OrFilter(IFilter<T>... filters)
	{
		if (filters == null)
		{
			throw new IllegalArgumentException("Array of IFilter must not be null"); //$NON-NLS-1$
		}
		this.filters = filters;
		this.filterLength = filters.length;
	}

	public boolean include(T item)
	{
		for (int i = 0; i < filterLength; i++)
		{
			if (filters[i].include(item))
			{
				return true;
			}
		}
		return false;
	}

}
