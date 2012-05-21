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
package com.aptana.eclipse.fullscreen.ui;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bandlem.eclipse.objc.BnLWindow;

/**
 * Provides a handler which can be used for mapping to keystrokes as well as a menu item to go into and out of
 * fullscreen mode.
 * 
 * @author Alex Blewitt <alex.blewitt@gmail.com>
 */
public class ToggleFullScreenHandler extends AbstractHandler
{

	private static final Object[] EMPTY_ARRAY = new Object[0];

	/**
	 * Toggles the window into fullScreen mode.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Object[] windows = getWindows();
		if (windows.length > 0)
		{
			BnLWindow.toggleFullScreen(windows[0]);
		}
		return null;
	}

	/**
	 * Helper to get the active window. Returns the windows in the workbench's array.
	 * 
	 * @return the NSWindow to go into fullScreen mode.
	 */
	protected Object[] getWindows()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		try
		{
			Object nsWindows = Array.newInstance(Class.forName("org.eclipse.swt.internal.cocoa.NSWindow"), //$NON-NLS-1$
					windows.length);
			for (int i = 0; i < windows.length; i++)
			{
				Array.set(nsWindows, i, Activator.getNSWindow(windows[i].getShell()));
			}
			return (Object[]) nsWindows;
		}
		catch (Exception e)
		{
			// ignores since non-OSX platforms will always throw the exception
		}
		return EMPTY_ARRAY;
	}

	/**
	 * This is enabled if we are on OSX 10.7 and above, and there are windows present.
	 */
	@Override
	public boolean isEnabled()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		return windows.length >= 1 && getOSVersion() >= 0x1070;
	}

	public static int getOSVersion()
	{
		try
		{
			Class<?> os = Class.forName("org.eclipse.swt.internal.cocoa.OS"); //$NON-NLS-1$
			Field version = os.getField("VERSION"); //$NON-NLS-1$
			return Integer.parseInt(version.get(os).toString());
		}
		catch (Exception e)
		{
			// ignores since non-OSX platforms will always throw the exception
		}
		return 0;
	}
}
