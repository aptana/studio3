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
