/*******************************************************************************
 * Copyright (c) 2011, Alex Blewitt.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alex Blewitt - initial API and implementation
 *******************************************************************************/
package com.bandlem.eclipse.objc;

import com.bandlem.eclipse.objc.SO.Reflect;

/**
 * Wrapper methods to assist with extensions to NSWindow
 * 
 * @author Alex Blewitt <alex.blewitt@gmail.com>
 */
public class BnLWindow
{

	/**
	 * Toggle the window in and out of fullScreen mode.
	 * 
	 * @param window
	 *            the window, which must not be <code>null</code>.
	 */
	public static void toggleFullScreen(Object window)
	{
		long toggleFullScreen = SO.selector("toggleFullScreen:"); //$NON-NLS-1$
		long target = SO.getID(window);
		SO.objc_msgSend(target, toggleFullScreen, 0);
	}

	/**
	 * Returns true if the window is in fullScreen mode already.
	 * 
	 * @param window
	 *            the window, which must not be null.
	 * @return true if the window is in fullScreen mode, false otherwise.
	 */
	public static boolean isFullScreen(Object window)
	{
		long styleMask = Reflect.executeLong(window, "styleMask"); //$NON-NLS-1$
		return (((styleMask >> 14) & 1) == 1);
	}

	/**
	 * Sets the window's fullScreen mode, regardless of current setting
	 * 
	 * @param window
	 *            the window, which must not be null.
	 * @param fullScreen
	 *            true if the window is to go into fullScreen mode, false otherwise.
	 */
	public static void setFullScreen(Object window, boolean fullScreen)
	{
		if (isFullScreen(window) != fullScreen)
		{
			toggleFullScreen(window);
		}
	}
}
