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

import com.bandlem.eclipse.objc.BnLWindow;

/**
 * Provides mapping for the Escape key, such that it only works when the window is already in fullscreen mode.
 * 
 * @author Alex Blewitt <alex.blewit@gmail.com>
 */
public class ToggleFullScreenEscapeHandler extends ToggleFullScreenHandler
{

	/**
	 * This is enabled when {@link ToggleFullScreenHandler#isEnabled()}, and we're in fullScreen mode.
	 */
	@Override
	public boolean isEnabled()
	{
		Object[] windows = getWindows();
		return super.isEnabled() && windows != null && windows.length >= 1 && BnLWindow.isFullScreen(windows[0]);
	}
}
