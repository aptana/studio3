/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer;

public interface IExplorerUIConstants
{
	/**
	 * ID of the App Explorer view.
	 */
	public static final String VIEW_ID = "com.aptana.explorer.view"; //$NON-NLS-1$

	/**
	 * ID of the drop-down menu for the App Explorer's "gear". Plugins can modify the menu using the
	 * "menu:com.aptana.explorer.gear" URI.
	 */
	public static final String GEAR_MENU_ID = "com.aptana.explorer.gear"; //$NON-NLS-1$

	/**
	 * The path to the icon for commands menu
	 */
	public static final String GEAR_MENU_ICON = "icons/full/elcl16/config.png"; //$NON-NLS-1$

	/**
	 * The path to the icon for commands menu
	 */
	public static final String GEAR_HOT_MENU_ICON = "icons/full/elcl16/config_hot.png"; //$NON-NLS-1$
}
