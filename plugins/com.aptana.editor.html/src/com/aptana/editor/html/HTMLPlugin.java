/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;

/**
 * The activator class controls the plug-in life cycle
 */
public class HTMLPlugin extends AbstractUIPlugin {
	
	public static final String PLUGIN_ID = "com.aptana.editor.html"; //$NON-NLS-1$
	
	private static HTMLPlugin plugin;

	private IDocumentProvider htmlDocumentProvider;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static HTMLPlugin getDefault() {
		return plugin;
	}

	/**
	 * getImage
	 * 
	 * @param path
	 * @return
	 */
	public static Image getImage(String path) {
		ImageRegistry registry = plugin.getImageRegistry();
		Image image = registry.get(path);

		if (image == null) {
			ImageDescriptor id = getImageDescriptor(path);

			if (id != null) {
				registry.put(path, id);
				image = registry.get(path);
			}
		}

		return image;
	}

	/**
	 * getImageDescriptor
	 * 
	 * @param path
	 * @return
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * The constructor
	 */
	public HTMLPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		Job job = new HTMLMetadataLoader();
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;

		super.stop(context);
	}

	/**
	 * Returns HTML document provider
	 * 
	 * @return
	 */
	public synchronized IDocumentProvider getHTMLDocumentProvider() {
		if (htmlDocumentProvider == null) {
			htmlDocumentProvider = new HTMLDocumentProvider();
		}
		return htmlDocumentProvider;
	}

	/**
	 * Log a particular status
	 * 
	 * @deprecated Use IdeLog instead
	 */
	public static void log(IStatus status)
	{
		IdeLog.log(getDefault(), status);
	}

	/**
	 * logError
	 * 
	 * @param e
	 * @deprecated Use IdeLog instead
	 */
	public static void log(Throwable e)
	{
		IdeLog.logError(getDefault(), e.getLocalizedMessage(), e);
	}

	/**
	 * logError
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logError(Throwable e)
	{
		IdeLog.logError(getDefault(), e.getLocalizedMessage(), e);
	}

	/**
	 * logError
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logError(String message, Throwable e)
	{
		IdeLog.logError(getDefault(), message, e);
	}

	/**
	 * logWarning
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 * @param e
	 */
	public static void logWarning(String message, Throwable e)
	{
		IdeLog.logWarning(getDefault(), message, e, null);
	}

	/**
	 * logInfo
	 * 
	 * @deprecated Use IdeLog instead
	 * @param message
	 */
	public static void logInfo(String message)
	{
		IdeLog.logInfo(getDefault(), message, null);
	}
}
