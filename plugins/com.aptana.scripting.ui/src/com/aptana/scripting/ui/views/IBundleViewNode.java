package com.aptana.scripting.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.graphics.Image;

interface IBundleViewNode
{
	/**
	 * getActions
	 * 
	 * @return
	 */
	Action[] getActions();
	
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
