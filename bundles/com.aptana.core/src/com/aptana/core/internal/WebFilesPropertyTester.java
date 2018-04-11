/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.aptana.core.CorePlugin;
import com.aptana.core.ICorePreferenceConstants;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;

/**
 * @author Max Stepanov
 */
public class WebFilesPropertyTester extends PropertyTester
{

	private Set<String> extensions;

	/**
	 * 
	 */
	public WebFilesPropertyTester()
	{
		super();
		loadExtensions();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[],
	 * java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		if (receiver instanceof IFile)
		{
			if ("isWebRunnable".equals(property)) { //$NON-NLS-1$
				boolean value = true;
				if (expectedValue != null && expectedValue instanceof Boolean)
				{
					value = ((Boolean) expectedValue).booleanValue();
				}
				String ext = ((IFile) receiver).getFileExtension();
				if (ext != null && ext.length() > 0)
				{
					return extensions.contains(ext) == value;
				}
			}
		}
		return false;
	}

	private void loadExtensions()
	{
		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(CorePlugin.PLUGIN_ID);
		String[] files = preferences.get(ICorePreferenceConstants.PREF_WEB_FILES, StringUtil.EMPTY).split(";"); //$NON-NLS-1$
		extensions = new HashSet<String>(ArrayUtil.length(files));
		for (String ext : files)
		{
			int index = ext.lastIndexOf('.');
			if (index >= 0)
			{
				ext = ext.substring(index + 1);
			}
			extensions.add(ext);
		}
	}

}
