/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.browser;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.logging.IdeLog;

/**
 * @author Max Stepanov
 *
 */
public final class ImageResource {

	private static ImageRegistry imageRegistry;

	private static final String URL_PREFIX = "$nl$/icons/"; //$NON-NLS-1$
	private static final String URL_OBJ = URL_PREFIX + "obj16/"; //$NON-NLS-1$
	private static final String URL_ELCL = URL_PREFIX + "elcl16/"; //$NON-NLS-1$
	private static final String URL_DLCL = URL_PREFIX + "dlcl16/"; //$NON-NLS-1$

	public static final String IMG_OBJ_BROWSER = "IMG_OBJ_BROWSER"; //$NON-NLS-1$

	// toolbar images
	public static final String IMG_ELCL_NAV_BACKWARD = "IMG_ELCL_NAV_BACKWARD"; //$NON-NLS-1$
	public static final String IMG_ELCL_NAV_FORWARD = "IMG_ELCL_NAV_FORWARD"; //$NON-NLS-1$
	public static final String IMG_ELCL_NAV_STOP = "IMG_ELCL_NAV_STOP"; //$NON-NLS-1$
	public static final String IMG_ELCL_NAV_REFRESH = "IMG_ELCL_NAV_REFRESH"; //$NON-NLS-1$
	public static final String IMG_ELCL_NAV_GO = "IMG_ELCL_NAV_GO"; //$NON-NLS-1$
	public static final String IMG_ELCL_NAV_HOME = "IMG_ELCL_NAV_HOME"; //$NON-NLS-1$
	public static final String IMG_ELCL_COMMAND = "IMG_ELCL_COMMAND"; //$NON-NLS-1$

	public static final String IMG_DLCL_NAV_BACKWARD = "IMG_DLCL_NAV_BACKWARD"; //$NON-NLS-1$
	public static final String IMG_DLCL_NAV_FORWARD = "IMG_DLCL_NAV_FORWARD"; //$NON-NLS-1$
	public static final String IMG_DLCL_NAV_STOP = "IMG_DLCL_NAV_STOP"; //$NON-NLS-1$
	public static final String IMG_DLCL_NAV_REFRESH = "IMG_DLCL_NAV_REFRESH"; //$NON-NLS-1$
	public static final String IMG_DLCL_NAV_GO = "IMG_DLCL_NAV_GO"; //$NON-NLS-1$
	public static final String IMG_DLCL_NAV_HOME = "IMG_DLCL_NAV_HOME"; //$NON-NLS-1$

	private ImageResource() {
	}
	
	public static Image getImage(String key) {
		if (imageRegistry == null) {
			initializeImageRegistry();
		}
		return imageRegistry.get(key);
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		if (imageRegistry == null) {
			initializeImageRegistry();
		}
		return imageRegistry.getDescriptor(key);
	}

	private static void registerImage(String key, String partialURL) {
		try {
			URL url = FileLocator.find(BrowserPlugin.getDefault().getBundle(), new Path(partialURL), null);
			ImageDescriptor id = ImageDescriptor.createFromURL(url);
			imageRegistry.put(key, id);
		} catch (Exception e) {
			IdeLog.logError(BrowserPlugin.getDefault(),
					MessageFormat.format("Error registering image {0} from {1}", key, partialURL), e); //$NON-NLS-1$
		}
	}

	private static void initializeImageRegistry() {
		imageRegistry = new ImageRegistry();
	
		// load Web browser images
		registerImage(IMG_OBJ_BROWSER, URL_OBJ + "browser.png"); //$NON-NLS-1$

		registerImage(IMG_ELCL_NAV_BACKWARD, URL_ELCL + "nav_backward.gif"); //$NON-NLS-1$
		registerImage(IMG_ELCL_NAV_FORWARD, URL_ELCL + "nav_forward.gif"); //$NON-NLS-1$
		registerImage(IMG_ELCL_NAV_STOP, URL_ELCL + "nav_stop.gif"); //$NON-NLS-1$
		registerImage(IMG_ELCL_NAV_REFRESH, URL_ELCL + "nav_refresh.gif"); //$NON-NLS-1$
		registerImage(IMG_ELCL_NAV_GO, URL_ELCL + "nav_go.gif"); //$NON-NLS-1$
		registerImage(IMG_ELCL_NAV_HOME, URL_ELCL + "nav_home.gif"); //$NON-NLS-1$
		registerImage(IMG_ELCL_COMMAND, URL_ELCL + "command.png"); //$NON-NLS-1$
		
		registerImage(IMG_DLCL_NAV_BACKWARD, URL_DLCL + "nav_backward.gif"); //$NON-NLS-1$
		registerImage(IMG_DLCL_NAV_FORWARD, URL_DLCL + "nav_forward.gif"); //$NON-NLS-1$
		registerImage(IMG_DLCL_NAV_STOP, URL_DLCL + "nav_stop.gif"); //$NON-NLS-1$
		registerImage(IMG_DLCL_NAV_REFRESH, URL_DLCL + "nav_refresh.gif"); //$NON-NLS-1$
		registerImage(IMG_DLCL_NAV_GO, URL_DLCL + "nav_go.gif"); //$NON-NLS-1$
		registerImage(IMG_DLCL_NAV_HOME, URL_DLCL + "nav_home.gif"); //$NON-NLS-1$	
	}
}
