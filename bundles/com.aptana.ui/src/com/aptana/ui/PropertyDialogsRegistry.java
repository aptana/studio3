/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;

/**
 * @author Max Stepanov
 */
public final class PropertyDialogsRegistry
{

	private static final String TAG_DIALOG = "dialog"; //$NON-NLS-1$
	private static final String ATT_OBJECT_CLASS = "objectClass"; //$NON-NLS-1$
	private static final String ATT_CLASS = "class"; //$NON-NLS-1$

	private static final String EXTENSION_POINT_ID = "propertyDialogs"; //$NON-NLS-1$

	private static PropertyDialogsRegistry instance;

	private Map<String, IConfigurationElement> classNameToElementMap = new HashMap<String, IConfigurationElement>();
	private Map<String, IPropertyDialogProvider> classNameToDialogProviderMap = new HashMap<String, IPropertyDialogProvider>();

	/**
	 * 
	 */
	private PropertyDialogsRegistry()
	{
		readExtensionRegistry();
	}

	public static PropertyDialogsRegistry getInstance()
	{
		if (instance == null)
		{
			instance = new PropertyDialogsRegistry();
		}
		return instance;
	}

	private void readExtensionRegistry()
	{
		EclipseUtil.processConfigurationElements(UIPlugin.PLUGIN_ID, EXTENSION_POINT_ID,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						readElement(element);
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(TAG_DIALOG);
					}
				});
	}

	private void readElement(IConfigurationElement element)
	{
		if (TAG_DIALOG.equals(element.getName()))
		{
			String objectClazz = element.getAttribute(ATT_OBJECT_CLASS);
			if (objectClazz == null || objectClazz.length() == 0)
			{
				return;
			}
			String clazz = element.getAttribute(ATT_CLASS);
			if (clazz == null || clazz.length() == 0)
			{
				return;
			}
			classNameToElementMap.put(objectClazz, element);
		}
	}

	public Dialog createPropertyDialog(Object element, IShellProvider shellProvider) throws CoreException
	{
		if (element != null)
		{
			return createPropertyDialog(element.getClass(), shellProvider);
		}
		return null;
	}

	public Dialog createPropertyDialog(Class<?> elementClass, IShellProvider shellProvider) throws CoreException
	{
		IPropertyDialogProvider provider = getPropertyDialogProvider(elementClass);
		if (provider != null)
		{
			return provider.createPropertyDialog(shellProvider);
		}
		return null;
	}

	private IPropertyDialogProvider getPropertyDialogProvider(Class<?> elementClass) throws CoreException
	{
		Set<String> classes = new HashSet<String>();
		if (classNameToDialogProviderMap.containsKey(elementClass.getCanonicalName()))
		{
			return classNameToDialogProviderMap.get(elementClass.getCanonicalName());
		}
		classes.add(elementClass.getCanonicalName());
		for (Class<?> i : elementClass.getClasses())
		{
			classes.add(i.getCanonicalName());
		}
		for (String className : classNameToElementMap.keySet())
		{
			if (classes.contains(className))
			{
				IPropertyDialogProvider provider = (IPropertyDialogProvider) classNameToElementMap.get(className)
						.createExecutableExtension(ATT_CLASS);
				if (provider != null)
				{
					classNameToDialogProviderMap.put(elementClass.getCanonicalName(), provider);
				}
				return provider;
			}
		}
		return null;
	}
}
