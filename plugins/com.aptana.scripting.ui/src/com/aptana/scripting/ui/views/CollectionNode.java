package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;

interface CollectionNode
{
	/**
	 * getImage
	 * 
	 * @return
	 */
	Image getImage();
	
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
