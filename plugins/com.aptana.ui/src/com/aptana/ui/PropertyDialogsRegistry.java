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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;

/**
 * @author Max Stepanov
 *
 */
public final class PropertyDialogsRegistry {

	protected static final String TAG_DIALOG = "dialog"; //$NON-NLS-1$
	protected static final String ATT_OBJECT_CLASS = "objectClass"; //$NON-NLS-1$
	protected static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = UIPlugin.PLUGIN_ID + ".propertyDialogs"; //$NON-NLS-1$

	private static PropertyDialogsRegistry instance;
	
	private Map<String, IConfigurationElement> classNameToElementMap = new HashMap<String, IConfigurationElement>();
	private Map<String, IPropertyDialogProvider> classNameToDialogProviderMap = new HashMap<String, IPropertyDialogProvider>();
	
	/**
	 * 
	 */
	private PropertyDialogsRegistry() {
		readExtensionRegistry();
	}
	
	public static PropertyDialogsRegistry getInstance() {
		if (instance == null) {
			instance = new PropertyDialogsRegistry();
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
		if (TAG_DIALOG.equals(element.getName())) {
			String objectClazz = element.getAttribute(ATT_OBJECT_CLASS);
			if (objectClazz == null || objectClazz.length() == 0) {
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0) {
				return;
			}
			classNameToElementMap.put(objectClazz, element);
		}
	}

	public Dialog createPropertyDialog(Object element, IShellProvider shellProvider) throws CoreException {
		if (element != null) {
			return createPropertyDialog(element.getClass(), shellProvider);
		}
		return null;
	}

	public Dialog createPropertyDialog(Class<?> elementClass, IShellProvider shellProvider) throws CoreException {
		IPropertyDialogProvider provider = getPropertyDialogProvider(elementClass);
		if (provider != null) {
			return provider.createPropertyDialog(shellProvider);
		}
		return null;
	}

	private IPropertyDialogProvider getPropertyDialogProvider(Class<?> elementClass) throws CoreException {
		Set<String> classes = new HashSet<String>();
		if (classNameToDialogProviderMap.containsKey(elementClass.getCanonicalName())) {
			return classNameToDialogProviderMap.get(elementClass.getCanonicalName());
		}
		classes.add(elementClass.getCanonicalName());
		for (Class<?> i : elementClass.getClasses()) {
			classes.add(i.getCanonicalName());
		}
		for (String className : classNameToElementMap.keySet()) {
			if (classes.contains(className)) {
				IPropertyDialogProvider provider = (IPropertyDialogProvider) classNameToElementMap.get(className).createExecutableExtension(ATT_CLASS);
				if (provider != null) {
					classNameToDialogProviderMap.put(elementClass.getCanonicalName(), provider);
				}
				return provider;
			}
		}
		return null;
	}
}
