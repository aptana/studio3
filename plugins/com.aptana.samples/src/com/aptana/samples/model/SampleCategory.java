/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.samples.model;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.util.EclipseUtil;
import com.aptana.samples.handlers.ISampleProjectHandler;

public class SampleCategory implements Comparable<SampleCategory>
{
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_PROJECT_HANDLER = "projectHandler"; //$NON-NLS-1$

	private final String id;
	private final String name;
	private final IConfigurationElement configElement;
	private ISampleProjectHandler projectHandler;

	public SampleCategory(String id, String name, IConfigurationElement configElement)
	{
		this.id = id;
		this.name = name;
		this.configElement = configElement;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public URL getIconFile()
	{
		return EclipseUtil.getResourceURL(configElement, ATTR_ICON);
	}

	public ISampleProjectHandler getProjectHandler()
	{
		if (projectHandler == null)
		{
			try
			{
				projectHandler = (ISampleProjectHandler) configElement.createExecutableExtension(ATTR_PROJECT_HANDLER);
			}
			catch (CoreException e)
			{
				// ignores the exception since it's optional
			}
		}
		return projectHandler;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof SampleCategory))
		{
			return false;
		}
		SampleCategory category = (SampleCategory) obj;
		return id.equals(category.id) && name.equals(category.name);
	}

	@Override
	public int hashCode()
	{
		return id.hashCode() * 31 + name.hashCode();
	}

	public int compareTo(SampleCategory o)
	{
		return name.compareTo(o.name);
	}
}
