/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.logging.IdeLog;
import com.aptana.deploy.DeployPlugin;
import com.aptana.deploy.IDeployProvider;
import com.aptana.deploy.preferences.DeployPreferenceUtil;

public class DeployProviderRegistry
{
	/**
	 * unique id of the provider.
	 */
	private static final String PROVIDER_ID_ATTRIBUTE = "id"; //$NON-NLS-1$

	/**
	 * Element name to register a deploy provider.
	 */
	private static final String PROVIDER_ELEMENT_NAME = "provider"; //$NON-NLS-1$

	/**
	 * Extension point name/id.
	 */
	private static final String DEPLOY_PROVIDERS_EXP_PT = "deployProviders"; //$NON-NLS-1$

	/**
	 * Cache
	 */
	private Map<String, IConfigurationElement> providersById;

	private static DeployProviderRegistry fgInstance;

	private DeployProviderRegistry()
	{

	}

	public synchronized static DeployProviderRegistry getInstance()
	{
		if (fgInstance == null)
		{
			fgInstance = new DeployProviderRegistry();
		}
		return fgInstance;
	}

	public IDeployProvider getProvider(IContainer container)
	{
		IDeployProvider provider = getConfiguredProvider(container);
		if (provider == null)
		{
			// Grab providers from ext pt!
			Collection<IDeployProvider> providers = getAllProviders();
			// Now go through the providers and find one that handles this project
			for (IDeployProvider aProvider : providers)
			{
				if (aProvider.handles(container))
				{
					provider = aProvider;
					break;
				}
			}
		}
		return provider;
	}

	private IDeployProvider getConfiguredProvider(IContainer container)
	{
		// check what deploy provider id is stored for project, then get provider from ext pt matching that id.
		String id = DeployPreferenceUtil.getDeployProviderId(container);
		if (id == null)
		{
			return null;
		}
		return getProviderById(id);
	}

	private IDeployProvider createProvider(IConfigurationElement element) throws CoreException
	{
		return (IDeployProvider) element.createExecutableExtension("class"); //$NON-NLS-1$
	}

	private Collection<IDeployProvider> getAllProviders()
	{
		Collection<IDeployProvider> providers = new ArrayList<IDeployProvider>();
		for (IConfigurationElement element : providersById().values())
		{
			try
			{
				providers.add(createProvider(element));
			}
			catch (CoreException e)
			{
				IdeLog.logError(DeployPlugin.getDefault(), e);
			}
		}
		return providers;
	}

	private synchronized Map<String, IConfigurationElement> providersById()
	{
		if (providersById == null)
		{
			providersById = new HashMap<String, IConfigurationElement>();
			try
			{
				IExtensionRegistry registry = Platform.getExtensionRegistry();
				IConfigurationElement[] elements = registry.getConfigurationElementsFor(
						DeployPlugin.getPluginIdentifier(), DEPLOY_PROVIDERS_EXP_PT);
				for (IConfigurationElement element : elements)
				{
					if (PROVIDER_ELEMENT_NAME.equals(element.getName()))
					{
						String providerId = element.getAttribute(PROVIDER_ID_ATTRIBUTE);
						providersById.put(providerId, element);
					}
				}
			}
			catch (InvalidRegistryObjectException e)
			{
				IdeLog.logError(DeployPlugin.getDefault(), e);
			}
		}
		return providersById;
	}

	public IDeployProvider getProviderById(String id)
	{
		if (id == null)
		{
			return null;
		}
		IConfigurationElement element = providersById().get(id);
		if (element != null)
		{
			try
			{
				return createProvider(element);
			}
			catch (CoreException e)
			{
				IdeLog.logError(DeployPlugin.getDefault(), e);
			}
		}
		return null;
	}

	public String getIdForProvider(IDeployProvider provider)
	{
		if (provider == null)
		{
			return null;
		}
		String className = provider.getClass().getName();
		for (Map.Entry<String, IConfigurationElement> entry : providersById().entrySet())
		{
			if (className.equals(entry.getValue().getAttribute("class"))) //$NON-NLS-1$
			{
				return entry.getKey();
			}
		}
		return null;
	}
}
