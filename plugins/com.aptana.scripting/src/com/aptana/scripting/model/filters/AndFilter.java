/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
