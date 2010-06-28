/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
