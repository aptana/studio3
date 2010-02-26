package com.aptana.scripting.ui.views;

import org.eclipse.swt.graphics.Image;

interface IBundleViewNode
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
