/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common;

import org.eclipse.core.runtime.IAdapterFactory;

import com.aptana.scripting.ui.ICommandElementsProvider;

@SuppressWarnings("rawtypes")
public class CommandElementsProviderAdapterFactory implements IAdapterFactory
{
	private static final Class[] ADAPTERS = new Class[] { ICommandElementsProvider.class };

	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType == ICommandElementsProvider.class)
		{
			if (adaptableObject instanceof AbstractThemeableEditor)
			{
				return ((AbstractThemeableEditor) adaptableObject).getCommandElementsProvider();
			}
		}
		return null;
	}

	public Class[] getAdapterList()
	{
		return ADAPTERS;
	}

}
