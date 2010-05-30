/*******************************************************************************
 * Copyright (c) 2003, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 * The following Wind River employees contributed to the Terminal component
 * that contains this file: Chris Thew, Fran Litterio, Stephen Lamb,
 * Helmut Haigermoser and Ted Williams.
 *
 * Contributors:
 * Michael Scharf (Wind River) - split into core, view and connector plugins
 * Martin Oberhuber (Wind River) - fixed copyright headers and beautified
 * Anna Dushistova (MontaVista) - [227537] moved actions from terminal.view to terminal plugin
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.control.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.tm.internal.terminal.control.actions.ImageConsts;
import org.eclipse.tm.internal.terminal.provisional.api.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class TerminalPlugin extends AbstractUIPlugin {
	protected static TerminalPlugin fDefault;
	public static final String  PLUGIN_ID  = "com.aptana.org.eclipse.tm.terminal"; //$NON-NLS-1$
	public static final String  HELP_VIEW  = PLUGIN_ID + ".terminal_view"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public TerminalPlugin() {
		fDefault = this;
	}
	/**
	 * Returns the shared instance.
	 */
	public static TerminalPlugin getDefault() {
		return fDefault;
	}

	public static boolean isLogInfoEnabled() {
		return isOptionEnabled(Logger.TRACE_DEBUG_LOG_INFO);
	}
	public static boolean isLogErrorEnabled() {
		return isOptionEnabled(Logger.TRACE_DEBUG_LOG_ERROR);
	}
	public static boolean isLogEnabled() {
		return isOptionEnabled(Logger.TRACE_DEBUG_LOG);
	}

	public static boolean isOptionEnabled(String strOption) {
		String strEnabled = Platform.getDebugOption(strOption);
		if (strEnabled == null)
			return false;

		return new Boolean(strEnabled).booleanValue();
	}

	protected void initializeImageRegistry(ImageRegistry imageRegistry) {
		try {
			// Local toolbars
			putImageInRegistry(imageRegistry, ImageConsts.IMAGE_CLCL_CLEAR_ALL, ImageConsts.IMAGE_DIR_LOCALTOOL + "clear_co.gif"); //$NON-NLS-1$
			// Enabled local toolbars
			putImageInRegistry(imageRegistry, ImageConsts.IMAGE_ELCL_CLEAR_ALL, ImageConsts.IMAGE_DIR_ELCL + "clear_co.gif"); //$NON-NLS-1$
			// Disabled local toolbars
			putImageInRegistry(imageRegistry, ImageConsts.IMAGE_DLCL_CLEAR_ALL, ImageConsts.IMAGE_DIR_DLCL + "clear_co.gif"); //$NON-NLS-1$
		} catch (MalformedURLException malformedURLException) {
			malformedURLException.printStackTrace();
		}
	}

	protected void putImageInRegistry(ImageRegistry imageRegistry, String strKey, String relativePath) throws MalformedURLException {
		URL url = TerminalPlugin.getDefault().getBundle().getEntry(relativePath);
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		imageRegistry.put(strKey, imageDescriptor);
	}
}
