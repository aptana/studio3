/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model.filters;

import java.util.ArrayList;
import java.util.List;

public abstract class FilterCollection implements IModelFilter
{
	private static final IModelFilter[] NO_FILTERS = new IModelFilter[0];
	
	private List<IModelFilter> _filters;

	/**
	 * FilterCollection
	 * 
	 * @param filters
	 */
	public FilterCollection(IModelFilter ... filters)
	{
		if (filters != null)
		{
			for (IModelFilter filter : filters)
			{
				this.addFilter(filter);
			}
		}
	}
	
	/**
	 * addFilter
	 * 
	 * @param filter
	 */
	public void addFilter(IModelFilter filter)
	{
		if (filter != null)
		{
			if (this._filters == null)
			{
				this._filters = new ArrayList<IModelFilter>();
			}
			
			this._filters.add(filter);
		}
	}

	/**
	 * getFilters
	 * 
	 * @return
	 */
	public IModelFilter[] getFilters()
	{
		IModelFilter[] result = NO_FILTERS;
		
		if (this._filters != null)
		{
			result = this._filters.toArray(new IModelFilter[this._filters.size()]);
		}
		
		return result;
	}
	
	/**
	 * removeFilter
	 * 
	 * @param filter
	 */
	public void removeFilter(IModelFilter filter)
	{
		if (this._filters != null)
		{
			this._filters.remove(filter);
		}
	}

}
