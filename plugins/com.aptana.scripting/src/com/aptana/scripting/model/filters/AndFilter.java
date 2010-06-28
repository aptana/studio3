package com.aptana.scripting.model.filters;

import com.aptana.scripting.model.AbstractElement;

public class AndFilter extends FilterCollection
{
	/**
	 * AndFilter
	 * 
	 * @param filter
	 */
	public AndFilter(IModelFilter... filters)
	{
		super(filters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IModelFilter#include(com.aptana.scripting.model.AbstractElement)
	 */
	public boolean include(AbstractElement element)
	{
		boolean result = true;

		if (element != null)
		{
			for (IModelFilter filter : this.getFilters())
			{
				if (filter.include(element) == false)
				{
					result = false;
					break;
				}
			}
		}

		return result;
	}
}
