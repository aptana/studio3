/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.js.debug.ui.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.internal.ui.ImageDescriptorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public final class DebugUIImages {
	private static String ICONS_PATH = "$nl$/icons/full/"; //$NON-NLS-1$

	// The plugin registry
	private static ImageRegistry fgImageRegistry = null;
	private static ImageDescriptorRegistry fgImageDescriptorRegistry = null;

	/*
	 * Available cached Images in the Java debug plug-in image registry.
	 */

	/**
	 * IMG_OBJS_VARIABLE
	 */
	public static final String IMG_OBJS_VARIABLE = "IMG_OBJS_VARIABLE"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_LOCAL_VARIABLE
	 */
	public static final String IMG_OBJS_LOCAL_VARIABLE = "IMG_OBJS_LOCAL_VARIABLE"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_FIELD
	 */
	public static final String IMG_OBJS_FIELD = "IMG_OBJS_FIELD"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_CONSTANT_FIELD
	 */
	public static final String IMG_OBJS_CONSTANT_FIELD = "IMG_OBJS_CONSTANT_FIELD"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_EXCEPTION_VARIABLE
	 */
	public static final String IMG_OBJS_EXCEPTION_VARIABLE = "IMG_OBJS_EXCEPTION_VARIABLE"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_JSEXCEPTION
	 */
	public static final String IMG_OBJS_JSEXCEPTION = "IMG_OBJS_JSEXCEPTION"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_JSWATCHPOINT
	 */
	public static final String IMG_OBJS_JSWATCHPOINT = "IMG_OBJS_JSWATCHPOINT"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_TOP_SCRIPT_ELEMENT
	 */
	public static final String IMG_OBJS_TOP_SCRIPT_ELEMENT = "IMG_OBJS_TOP_SCRIPT_ELEMENT"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_SCRIPT_ELEMENT
	 */
	public static final String IMG_OBJS_SCRIPT_ELEMENT = "IMG_OBJS_SCRIPT_ELEMENT"; //$NON-NLS-1$

	/**
	 * IMG_OBJS_INSPECT
	 */
	public static final String IMG_OBJS_INSPECT = "IMG_OBJS_INSPECT"; //$NON-NLS-1$

	/**
	 * IMG_OVR_CONDITIONAL_BREAKPOINT
	 */
	public static final String IMG_OVR_CONDITIONAL_BREAKPOINT = "IMG_OVR_CONDITIONAL_BREAKPOINT"; //$NON-NLS-1$

	/**
	 * IMG_OVR_CONDITIONAL_BREAKPOINT_DISABLED
	 */
	public static final String IMG_OVR_CONDITIONAL_BREAKPOINT_DISABLED = "IMG_OVR_CONDITIONAL_BREAKPOINT_DISABLED"; //$NON-NLS-1$

	/**
	 * IMG_OVR_METHOD_BREAKPOINT_ENTRY
	 */
	public static final String IMG_OVR_METHOD_BREAKPOINT_ENTRY = "IMG_OVR_METHOD_BREAKPOINT_ENTRY"; //$NON-NLS-1$

	/**
	 * IMG_OVR_METHOD_BREAKPOINT_ENTRY_DISABLED
	 */
	public static final String IMG_OVR_METHOD_BREAKPOINT_ENTRY_DISABLED = "IMG_OVR_METHOD_BREAKPOINT_ENTRY_DISABLED"; //$NON-NLS-1$

	/**
	 * IMG_OVR_METHOD_BREAKPOINT_EXIT
	 */
	public static final String IMG_OVR_METHOD_BREAKPOINT_EXIT = "IMG_OVR_METHOD_BREAKPOINT_EXIT"; //$NON-NLS-1$

	/**
	 * IMG_OVR_METHOD_BREAKPOINT_EXIT_DISABLED
	 */
	public static final String IMG_OVR_METHOD_BREAKPOINT_EXIT_DISABLED = "IMG_OVR_METHOD_BREAKPOINT_EXIT_DISABLED"; //$NON-NLS-1$

	/*
	 * Set of predefined Image Descriptors.
	 */
	private static final String T_OBJ = ICONS_PATH + "obj16/"; //$NON-NLS-1$
	private static final String T_OVR = ICONS_PATH + "ovr16/"; //$NON-NLS-1$

	private DebugUIImages() {
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
	 * Returns the <code>ImageDescriptor</code> identified by the given key, or <code>null</code> if it does not exist.
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
	 * getImageDescriptorRegistry
	 * 
	 * @return ImageDescriptorRegistry
	 */
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		if (fgImageDescriptorRegistry == null) {
			fgImageDescriptorRegistry = new ImageDescriptorRegistry();
		}
		return fgImageDescriptorRegistry;
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
		declareRegistryImage(IMG_OBJS_VARIABLE, T_OBJ + "variable_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_LOCAL_VARIABLE, T_OBJ + "localvariable_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_FIELD, T_OBJ + "field_public_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_CONSTANT_FIELD, T_OBJ + "field_const_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_EXCEPTION_VARIABLE, T_OBJ + "excvariable_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_JSEXCEPTION, T_OBJ + "jsexception_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_JSWATCHPOINT, T_OBJ + "jswatch_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_TOP_SCRIPT_ELEMENT, T_OBJ + "topScriptElement_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_SCRIPT_ELEMENT, T_OBJ + "scriptElement_obj.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OBJS_INSPECT, T_OBJ + "inspect_obj.gif"); //$NON-NLS-1$

		declareRegistryImage(IMG_OVR_CONDITIONAL_BREAKPOINT, T_OVR + "conditional_ovr.gif"); //$NON-NLS-1$
		declareRegistryImage(IMG_OVR_CONDITIONAL_BREAKPOINT_DISABLED, T_OVR + "conditional_ovr_disabled.gif"); //$NON-NLS-1$

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
		Bundle bundle = Platform.getBundle(JSDebugUIPlugin.PLUGIN_ID);
		URL url = null;
		if (bundle != null) {
			url = FileLocator.find(bundle, new Path(path), null);
			desc = ImageDescriptor.createFromURL(url);
		}
		fgImageRegistry.put(key, desc);
	}
}
