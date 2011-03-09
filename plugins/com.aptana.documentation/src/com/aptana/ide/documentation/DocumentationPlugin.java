/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.ide.documentation;

import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class DocumentationPlugin extends AbstractUIPlugin
{

	private static Hashtable<String, Image> images = new Hashtable<String, Image>();

	/**
	 * PLUGIN_ID
	 */
	public static final String PLUGIN_ID = "com.aptana.ide.documentation"; //$NON-NLS-1$

	public static final String GETTING_STARTED_CONTENT_URL = PLUGIN_ID + ".getting_started_url"; //$NON-NLS-1$
    
	/**
	 * @since 2.0
	 */
    public static final String RELEASE_NOTES_URL_SYSTEM_PROPERTY = PLUGIN_ID + ".release_notes_url"; //$NON-NLS-1$

	// The shared instance.
	private static DocumentationPlugin plugin;

	/**
	 * The constructor.
	 */
	public DocumentationPlugin()
	{
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return The instance of the Documentation plugin
	 */
	public static DocumentationPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * getImage
	 * 
	 * @param path
	 * @return Image
	 */
	public static Image getImage(String path)
	{
		if (images.get(path) == null)
		{
			ImageDescriptor id = getImageDescriptor(path);

			if (id == null)
			{
				return null;
			}

			Image i = id.createImage();

			images.put(path, i);

			return i;
		}
		else
		{
			return (Image) images.get(path);
		}
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
