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

package com.aptana.browser;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

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
			BrowserPlugin.log("Error registering image " + key + " from " + partialURL, e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	protected static void initializeImageRegistry() {
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
