/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor

package com.aptana.debug.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;
import com.aptana.ui.util.UIUtils;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebugUiPlugin extends AbstractUIPlugin {
	/**
	 * ID
	 */
	public static final String PLUGIN_ID = "com.aptana.debug.ui"; //$NON-NLS-1$

	// The shared instance.
	private static DebugUiPlugin plugin;

	/**
	 * The constructor.
	 */
	public DebugUiPlugin() {
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return DebugUiPlugin
	 */
	public static DebugUiPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns the standard display to be used. The method first checks, if the thread calling this method has an
	 * associated display. If so, this display is returned. Otherwise the method returns the default display.
	 * 
	 * @return Display
	 */
	public static Display getStandardDisplay() {
		Display display;
		display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * Utility method with conventions
	 * 
	 * @param message
	 * @param t
	 */
	public static void errorDialog(String message, Throwable t) {
		IdeLog.logWarning(getDefault(), t);
		Shell shell = UIUtils.getActiveShell();
		if (shell != null) {
			IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR,
					"Error logged from Studio Debug UI: ", t); //$NON-NLS-1$	
			ErrorDialog.openError(shell, "Error", message, status); //$NON-NLS-1$
		}
	}
}
