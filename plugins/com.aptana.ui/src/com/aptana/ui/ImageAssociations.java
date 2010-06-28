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
