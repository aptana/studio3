/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import com.aptana.core.IFilter;

/**
 * Combines multiple filters using "and". If any filter returns false for an item, we return false. If all filters
 * return true for an item, we return true.
 * 
 * @author Chris Williams <cwilliams@appcelerator.com>
 * @param <T>
 */
public class AndFilter<T> implements IFilter<T>
{

	private IFilter<T>[] filters;
	private int filterLength;

	public AndFilter(IFilter<T>... filters)
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
			if (!filters[i].include(item))
			{
				return false;
			}
		}
		return true;
	}

}
