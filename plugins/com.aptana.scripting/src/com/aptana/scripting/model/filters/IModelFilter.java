package com.aptana.scripting.model.filters;

import com.aptana.scripting.model.AbstractElement;

public interface IModelFilter
{
	/**
	 * include
	 * 
	 * @param element
	 * @return
	 */
	boolean include(AbstractElement element);
}
