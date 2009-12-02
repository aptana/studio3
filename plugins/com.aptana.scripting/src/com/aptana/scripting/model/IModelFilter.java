package com.aptana.scripting.model;

public interface IModelFilter
{
	/**
	 * exclude
	 * 
	 * @param element
	 * @return
	 */
	boolean exclude(AbstractNode element);
	
	/**
	 * include
	 * 
	 * @param element
	 * @return
	 */
	boolean include(AbstractNode element);
}
