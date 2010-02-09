package com.aptana.scripting.model;

public interface ElementChangeListener
{
	/**
	 * elementAdded
	 * 
	 * @param element
	 */
	void elementAdded(AbstractElement element);

	/**
	 * elementDeleted
	 * 
	 * @param element
	 */
	void elementDeleted(AbstractElement element);
}
