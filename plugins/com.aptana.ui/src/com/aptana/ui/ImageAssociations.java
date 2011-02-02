/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.aptana.core.Identifiable;
import com.aptana.core.util.ClassUtil;

/**
 * @author Max Stepanov
 *
 */
public final class ImageAssociations {

	protected static final String TAG_OBJECT_IMAGE = "objectImage"; //$NON-NLS-1$
	protected static final String TAG_IMAGE = "image"; //$NON-NLS-1$
	protected static final String ATT_ID = "id"; //$NON-NLS-1$
	protected static final String ATT_OBJECT_CLASS = "objectClass"; //$NON-NLS-1$
	protected static final String ATT_ICON = "icon"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = UIPlugin.PLUGIN_ID + ".imageAssociations"; //$NON-NLS-1$

	private static ImageAssociations instance;
	
	private Map<String, ImageDescriptor> idToImageMap = new HashMap<String, ImageDescriptor>();
	private Map<String, ImageDescriptor> classNameToImageMap = new HashMap<String, ImageDescriptor>();
	private Map<Class<?>, ImageDescriptor> classToImageMap = new HashMap<Class<?>, ImageDescriptor>();
	
	/**
	 * 
	 */
	private ImageAssociations() {
		readExtensionRegistry();
	}

	public static ImageAssociations getInstance() {
		if (instance == null) {
			instance = new ImageAssociations();
		}
		return instance;
	}
	
	private void readExtensionRegistry() {
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
							.getConfigurationElementsFor(EXTENSION_POINT_ID);
		for (int i = 0; i < elements.length; ++i) {
			readElement(elements[i]);
		}
	}
	
	private void readElement(IConfigurationElement element) {
		if (TAG_IMAGE.equals(element.getName())) {
			String id = element.getAttribute(ATT_ID);
			if (id == null || id.length() == 0) {
				return;
			}
			String icon = element.getAttribute(ATT_ICON);
			if (icon == null || icon.length() == 0) {
				return;
			}
			ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(), icon);
			if (imageDescriptor == null) {
				return;
			}
			idToImageMap.put(id, imageDescriptor);
		} else if (TAG_OBJECT_IMAGE.equals(element.getName())) {
			String clazz = element.getAttribute(ATT_OBJECT_CLASS);
			if (clazz == null || clazz.length() == 0) {
				return;
			}
			String icon = element.getAttribute(ATT_ICON);
			if (icon == null || icon.length() == 0) {
				return;
			}
			ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(), icon);
			if (imageDescriptor == null) {
				return;
			}
			classNameToImageMap.put(clazz, imageDescriptor);
		}
	}

	/**
	 * getImageDescriptor
	 * @param id
	 * @return
	 */
	public ImageDescriptor getImageDescriptor(String id) {
		return idToImageMap.get(id);
	}

	/**
	 * getImageDescriptor
	 * @param clazz
	 * @return
	 */
	public ImageDescriptor getImageDescriptor(Class<?> clazz) {
		ImageDescriptor imageDescriptor = classToImageMap.get(clazz);
		if (imageDescriptor == null && !classToImageMap.containsKey(clazz)) {
			for (Class<?> i : ClassUtil.getClassesTree(clazz)) {
				imageDescriptor = classToImageMap.get(i);
				if (imageDescriptor == null) {
					imageDescriptor = classNameToImageMap.get(i.getName());
				}
				if (imageDescriptor != null) {
					break;
				}
			}
			classToImageMap.put(clazz, imageDescriptor);
		}
		return imageDescriptor;
	}
	
	public ImageDescriptor getImageDescriptor(Object element) {
		if (element == null) {
			return null;
		}
		if (element instanceof Identifiable) {
			ImageDescriptor imageDescriptor = getImageDescriptor(((Identifiable) element).getId());
			if (imageDescriptor != null) {
				return imageDescriptor;
			}
		}
		return getImageDescriptor(element.getClass());
	}
}
