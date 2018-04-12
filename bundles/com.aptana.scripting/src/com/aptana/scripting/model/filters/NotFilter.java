/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model.filters;

import com.aptana.scripting.model.AbstractElement;

public class NotFilter implements IModelFilter
{
	private IModelFilter _filter;
	
	/**
	 * NotFilter
	 */
	public NotFilter()
	{
	}

	/**
	 * NotFilter
	 * 
	 * @param filter
	 */
	public NotFilter(IModelFilter filter)
	{
		this._filter = filter;
	}

	/**
	 * getFilter
	 * 
	 * @return
	 */
	public IModelFilter getFilter()
	{
		return this._filter;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#include(com.aptana.scripting.model.AbstractElement)
	 */
	public boolean include(AbstractElement element)
	{
		boolean result = false;
		
		if (this._filter != null)
		{
			result = (this._filter.include(element) == false);
		}
		
		return result;
	}
	
	/**
	 * setFilter
	 * 
	 * @param filter
	 */
	public void setFilter(IModelFilter filter)
	{
		this._filter = filter;
	}
}
