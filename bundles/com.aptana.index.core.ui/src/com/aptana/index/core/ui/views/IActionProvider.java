/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.index.core.ui.views;

import org.eclipse.jface.action.IAction;

/**
 * IActionProvider
 */
public interface IActionProvider
{
	/**
	 * Return a list of IActions associated with the specified object.
	 * 
	 * @param view
	 * @param object
	 * @return Returns an array of IAction instances. This value may be null
	 */
	IAction[] getActions(IndexView view, Object object);
}
