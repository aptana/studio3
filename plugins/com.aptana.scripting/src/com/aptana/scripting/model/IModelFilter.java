package com.aptana.scripting.model;

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
