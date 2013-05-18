/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.ExpiringMap;
import com.aptana.core.util.StringUtil;
import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;
import com.aptana.deploy.util.DeployProviderUtil;

class DeployValues
{
	private Boolean deployableValue;
	private Map<String, Boolean> deployTypeValues;

	public DeployValues()
	{
		deployTypeValues = new HashMap<String, Boolean>(5);
	}

	public Boolean getDeployTypeValue(String arg)
	{
		if (arg == null)
		{
			return false;
		}
		return deployTypeValues.get(arg);
	}

	public void setDeployTypeValue(String arg, Boolean value)
	{
		deployTypeValues.put(arg, value);
	}

	public Boolean getDeployableValue()
	{
		return deployableValue;
	}

	public void setDeployableValue(Boolean deployableValue)
	{
		this.deployableValue = deployableValue;
	}
}

public class ProjectPropertyTester extends PropertyTester
{

	private static Map<String, DeployValues> containerCache = new ExpiringMap<String, DeployValues>(300000);

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue)
	{
		IResource resource = getResource(receiver);
		if (resource != null)
		{
			IContainer container;
			if (receiver instanceof IContainer)
			{
				container = (IContainer) resource;
			}
			else
			{
				container = resource.getParent();
			}
			if (!container.isAccessible())
			{
				return false;
			}
			IPath location = container.getLocation();
			if (location == null)
			{
				return false;
			}
			if ("isDeployable".equals(property)) //$NON-NLS-1$
			{
				DeployValues deployValues = containerCache.get(location.toOSString());
				if (deployValues != null)
				{
					Boolean deployableValue = deployValues.getDeployableValue();
					if (deployableValue != null)
					{
						return deployableValue;
					}
				}
				else
				{
					deployValues = resetCache(container);
					return deployValues.getDeployableValue();
				}
			}
			else if ("isDeployType".equals(property)) //$NON-NLS-1$
			{
				String arg = (String) expectedValue;
				DeployValues deployValues = containerCache.get(location.toOSString());
				if (deployValues != null)
				{
					Boolean deployTypeValue = deployValues.getDeployTypeValue(arg);
					if (deployTypeValue != null)
					{
						return deployTypeValue;
					}
				}
				else
				{
					deployValues = resetCache(container);
				}
				boolean isDeployType = isDeployType(container, arg);
				deployValues.setDeployTypeValue(arg, isDeployType);
				return isDeployType;
			}
		}
		return false;
	}

	private boolean isDeployType(IContainer container, String arg)
	{
		String id = DeployPreferenceUtil.getDeployProviderId(container);
		if (id != null)
		{
			return arg.equals(id);
		}
		// Instantiate provider with id, then call handles and check that!
		IDeployProvider provider = DeployProviderRegistry.getInstance().getProviderById(arg);
		return provider != null && provider.handles(container);
	}

	public static DeployValues resetCache(IContainer container)
	{
		containerCache.clear();
		DeployValues deployValues = new DeployValues();
		containerCache.put(container.getLocation().toOSString(), deployValues);

		// Check if we have an explicitly set deployment provider
		boolean deployableValue = isDeployable(container);
		deployValues.setDeployableValue(deployableValue);
		return deployValues;
	}

	private static boolean isDeployable(IContainer container)
	{
		String id = DeployPreferenceUtil.getDeployProviderId(container);
		if (id != null)
		{
			if (StringUtil.EMPTY.equals(id))
			{
				return false;
			}
			return true;
		}
		IDeployProvider provider = DeployProviderRegistry.getInstance().getProvider(container);
		if (provider != null)
		{
			DeployPreferenceUtil.setDeployType(container, DeployProviderUtil.getIdForProvider(provider));
			return true;
		}
		DeployPreferenceUtil.setDeployType(container, StringUtil.EMPTY);
		return false;
	}

	private IResource getResource(Object receiver)
	{
		if (receiver instanceof IResource)
		{
			return (IResource) receiver;
		}
		if (receiver instanceof IAdaptable)
		{
			return (IResource) ((IAdaptable) receiver).getAdapter(IResource.class);
		}
		return null;
	}
}
