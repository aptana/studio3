/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
