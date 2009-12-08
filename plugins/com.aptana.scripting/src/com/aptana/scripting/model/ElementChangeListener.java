package com.aptana.scripting.model;

public interface ElementChangeListener
{
	/**
	 * ElementAdded
	 * 
	 * @param element
	 */
	void elementAdded(AbstractElement element);
	
	/**
	 * ElementDeleted
	 * 
	 * @param element
	 */
	void elementDeleted(AbstractElement element);
	
	/**
	 * ElementModified
	 * 
	 * @param element
	 */
	void elementModified(AbstractElement element);
}
