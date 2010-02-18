package com.aptana.scripting.ui.views;

interface CollectionNode
{
	/**
	 * getLabel
	 * 
	 * @return
	 */
	String getLabel();
	
	/**
	 * getChildren
	 * 
	 * @return
	 */
	Object[] getChildren();
	
	/**
	 * hasChildren
	 * 
	 * @return
	 */
	boolean hasChildren();
}
