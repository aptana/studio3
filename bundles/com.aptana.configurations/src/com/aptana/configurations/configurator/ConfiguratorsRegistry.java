/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.configurations.configurator;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.aptana.configurations.ConfigurationsPlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;

/**
 * A registry for the Studio's configurators.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class ConfiguratorsRegistry
{
	private static final String EXTENSION_POINT_ID = ConfigurationsPlugin.PLUGIN_ID + ".configurators"; //$NON-NLS-1$
	private static final String ATT_PROCESSOR = "processor"; //$NON-NLS-1$
	private static final String ATT_ID = "id"; //$NON-NLS-1$
	private static final String ATT_NAME = "name"; //$NON-NLS-1$

	private static ConfiguratorsRegistry instance = null;

	private Set<IConfigurator> configurators;

	// private constructor
	private ConfiguratorsRegistry()
	{
		loadExtensions();
	}

	/**
	 * Returns an instance of this registry.
	 * 
	 * @return a ConfiguratorsRegistry instance
	 */
	public static ConfiguratorsRegistry getInstance()
	{
		if (instance == null)
		{
			instance = new ConfiguratorsRegistry();
		}
		return instance;
	}

	/**
	 * Returns the registered configurators. In case the <code>enabledOnly</code> is <code>true</code>, the returned set
	 * will hold only {@link IConfigurator}s that return <code>true</code> for the {@link IConfigurator#isEnabled()}.
	 * 
	 * @param enabledOnly
	 * @return An unmodified set of registered {@link IConfigurator}s.
	 */
	public Set<IConfigurator> getConfigurators(boolean enabledOnly)
	{
		if (enabledOnly)
		{
			Set<IConfigurator> enabledConfigurators = new HashSet<IConfigurator>(configurators.size());
			for (IConfigurator configurator : configurators)
			{
				if (configurator.isEnabled())
				{
					enabledConfigurators.add(configurator);
				}
			}
			return Collections.unmodifiableSet(enabledConfigurators);
		}
		return Collections.unmodifiableSet(configurators);
	}

	/**
	 * Loads the 'configurators' extensions.
	 */
	private void loadExtensions()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
				EXTENSION_POINT_ID);
		configurators = new HashSet<IConfigurator>(ArrayUtil.length(elements));
		for (IConfigurationElement element : elements)
		{
			String id = element.getAttribute(ATT_ID);
			String name = element.getAttribute(ATT_NAME);
			String processorId = element.getAttribute(ATT_PROCESSOR);
			if (id != null && name != null && processorId != null)
			{
				configurators.add(new Configurator(name, id, processorId));
			}
			else
			{
				IdeLog.logError(ConfigurationsPlugin.getDefault(),
						"Error creating a configurator. One of the required attributes was one", (String) null); //$NON-NLS-1$
			}
		}
	}

}
