/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.ide.ui.io;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

/**
 * @author Max Stepanov
 */
public final class CoreIOImages
{
	private static String ICONS_PATH = "$nl$/icons/full/"; //$NON-NLS-1$

	// The plugin registry
	private static ImageRegistry fgImageRegistry = null;

	/*
	 * Available cached Images in the plug-in image registry.
	 */

	/**
	 * IMG_OBJS_PENDING
	 */
	public static final String IMG_OBJS_PENDING = "IMG_OBJS_PENDING"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_CONNECTION
	 */
	public static final String IMG_OBJS_CONNECTION = "IMG_OBJS_CONNECTION"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_SYMLINK
	 */
	public static final String IMG_OBJS_SYMLINK = "IMG_OBJS_SYMLINK"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_DRIVE
	 */
	public static final String IMG_OBJS_DRIVE = "IMG_OBJS_DRIVE"; //$NON-NLS-1$

	/*
	 * Set of predefined Image Descriptors.
	 */
	private static final String T_OBJ = ICONS_PATH + "obj16/"; //$NON-NLS-1$

	private CoreIOImages() {
	}

	/**
	 * Returns the image managed under the given key in this registry.
	 * 
	 * @param key
	 *            the image's key
	 * @return the image managed under the given key
	 */
	public static Image get(String key) {
		return getImageRegistry().get(key);
	}

	/**
	 * Returns the <code>ImageDescriptor</code> identified by the given key, or <code>null</code> if it does not
	 * exist.
	 * 
	 * @param key
	 * @return ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		return getImageRegistry().getDescriptor(key);
	}

	/**
	 * Helper method to access the image registry from the DebugUiPlugin class.
	 * 
	 * @return ImageRegistry
	 */
	public static ImageRegistry getImageRegistry() {
		if (fgImageRegistry == null) {
			initializeImageRegistry();
		}
		return fgImageRegistry;
	}

	/**
	 * initializeImageRegistry
	 */
	private static void initializeImageRegistry() {
		fgImageRegistry = new ImageRegistry(Display.getDefault());
		declareImages();
	}

	/**
	 * declareImages
	 */
	private static void declareImages() {
		declareRegistryImage(IMG_OBJS_PENDING, T_OBJ + "pending.png"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_CONNECTION, T_OBJ + "connection.png"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_SYMLINK, T_OBJ + "symlink.png"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_DRIVE, T_OBJ + "drive.png"); //$NON-NLS-1$
	}

	/**
	 * Declare an Image in the registry table.
	 * 
	 * @param key
	 *            The key to use when registering the image
	 * @param path
	 *            The path where the image can be found. This path is relative to where this plugin class is found (i.e.
	 *            typically the packages directory)
	 */
	private static void declareRegistryImage(String key, String path) {
		ImageDescriptor desc = ImageDescriptor.getMissingImageDescriptor();
		Bundle bundle = Platform.getBundle(IOUIPlugin.PLUGIN_ID);
		URL url = null;
		if (bundle != null)
		{
			url = FileLocator.find(bundle, new Path(path), null);
			desc = ImageDescriptor.createFromURL(url);
		}
		fgImageRegistry.put(key, desc);
	}
}
