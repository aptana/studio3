package com.aptana.scripting.model.filters;

import com.aptana.scripting.model.AbstractElement;

public class OrFilter extends FilterCollection
{
	/**
	 * OrFilter
	 * 
	 * @param filter
	 */
	public OrFilter(IModelFilter... filters)
	{
		super(filters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#include(com.aptana.scripting.model.AbstractElement)
	 */
	public boolean include(AbstractElement element)
	{
		boolean result = false;

		if (element != null)
		{
			for (IModelFilter filter : this.getFilters())
			{
				if (filter.include(element))
				{
					result = true;
					break;
				}
			}
		}

		return result;
	}
}
