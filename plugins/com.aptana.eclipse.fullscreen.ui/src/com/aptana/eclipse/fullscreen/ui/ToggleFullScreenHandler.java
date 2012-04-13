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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bandlem.eclipse.objc.BnLWindow;

/**
 * Provides a handler which can be used for mapping to keystrokes as well as a menu item to go into and out of
 * fullscreen mode.
 * 
 * @author Alex Blewitt <alex.blewitt@gmail.com>
 */
@SuppressWarnings("restriction")
public class ToggleFullScreenHandler extends AbstractHandler
{

	/**
	 * Toggles the window into fullScreen mode, via {@link BnLWindow#toggleFullScreen(NSWindow)}.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		NSWindow[] windows = getWindows();
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
	protected NSWindow[] getWindows()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		NSWindow[] nsWindows = new NSWindow[windows.length];
		for (int i = 0; i < windows.length; i++)
		{
			nsWindows[i] = windows[i].getShell().view.window();
		}
		return nsWindows;
	}

	/**
	 * This is enabled if we are on OSX 10.7 and above, and there are windows present.
	 */
	@Override
	public boolean isEnabled()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		return windows.length >= 1 && OS.VERSION >= 0x1070;
	}
}
