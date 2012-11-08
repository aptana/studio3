/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class YAMLPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.editor.yaml"; //$NON-NLS-1$

	// The shared instance
	private static YAMLPlugin plugin;

	private IDocumentProvider yamlDocumentProvider;

	/**
	 * The constructor
	 */
	public YAMLPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static YAMLPlugin getDefault()
	{
		return plugin;
	}

	public static Image getImage(String path)
	{
		Image image = getDefault().getImageRegistry().get(path);
		if (image == null)
		{
			ImageDescriptor desc = imageDescriptorFromPlugin(PLUGIN_ID, path);
			getDefault().getImageRegistry().put(path, desc);
		}
		return getDefault().getImageRegistry().get(path);
	}

	/**
	 * Returns YAML document provider
	 * 
	 * @return
	 */
	public synchronized IDocumentProvider getYAMLDocumentProvider()
	{
		if (yamlDocumentProvider == null)
		{
			yamlDocumentProvider = new YAMLDocumentProvider();
		}
		return yamlDocumentProvider;
	}

}
