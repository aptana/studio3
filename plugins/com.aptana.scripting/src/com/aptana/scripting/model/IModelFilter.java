package com.aptana.scripting.model;

public interface IModelFilter
{
	/**
	 * exclude
	 * 
	 * @param element
	 * @return
	 */
	boolean exclude(AbstractElement element);
	
	/**
	 * include
	 * 
	 * @param element
	 * @return
	 */
	boolean include(AbstractElement element);
}
