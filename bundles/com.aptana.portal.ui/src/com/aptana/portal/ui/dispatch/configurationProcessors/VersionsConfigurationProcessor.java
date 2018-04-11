/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import com.aptana.jetty.util.epl.ajax.JSON;
import org.osgi.framework.Version;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationProcessorsRegistry;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessorDelegate;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.VersionUtil;
import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IPreferenceConstants;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.processorDelegates.CachedVersionProcessorDelegate;

/**
 * A configuration processor that can identify the versions of some specific applications. The supported applications
 * are:
 * <ul>
 * <li>ruby</li>
 * <li>rails</li>
 * <li>git</li>
 * <li>sqlite3</li>
 * </ul>
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class VersionsConfigurationProcessor extends AbstractConfigurationProcessor
{
	/**
	 * Compute the versions of the given items in the attributes instance. Items that are not in the supported list of
	 * programs are set to an 'unknown' state, just as they are not installed.<br>
	 * The computation expects an attributes array with three items - <appName><appVersion><appInstallerURL> (this
	 * specific call don't use the installer-URL, but it needs to be there for the Portal's functionalities)
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		configurationStatus.removeAttribute(CONFIG_ATTR);
		clearErrorAttributes();
		if (attributes == null || !(attributes instanceof Object[]))
		{
			String message = Messages.SystemConfigurationProcessor_missingConfigurationItems;
			applyErrorAttributes(message);
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			return configurationStatus;
		}
		// Place the array values into a hash.
		Object[] attrArray = (Object[]) attributes;
		Map<String, String> attrItems = new HashMap<String, String>();
		for (Object itemDef : attrArray)
		{
			Object[] def = null;
			if (!(itemDef instanceof Object[]) || (def = (Object[]) itemDef).length != 3)
			{
				String message = Messages.SystemConfigurationProcessor_wrongConfigurationAttributesStructure;
				applyErrorAttributes(message);
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
				return configurationStatus;
			}
			// We only use the first two arguments. The third is the installation site URL.
			attrItems.put((String) def[0], (String) def[1]);
		}
		// Do the actual processing
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);

		// For each requested element, check that the item has a processor delegate.
		// If it's there, execute the delegate. If not, set the item's state as unknown.
		Map<String, Map<String, String>> itemsData = new HashMap<String, Map<String, String>>();
		String[] apps = attrItems.keySet().toArray(new String[attrItems.size()]);
		// This processor should have delegators that do the actual processing of the versions.
		// Load the delegators into a new set that we can manipulate.
		Map<String, IConfigurationProcessorDelegate> processorDelegators = getVersionDelegators(apps);
		for (String app : apps)
		{
			if (!processorDelegators.containsKey(app))
			{
				// We'll deal with these later
				continue;
			}
			IConfigurationProcessorDelegate delegate = processorDelegators.get(app);
			Object commandResult = delegate.runCommand(IConfigurationProcessorDelegate.VERSION_COMMAND,
					getActiveWorkingDir());
			if (commandResult != null)
			{
				Version version = VersionUtil.parseVersion(commandResult.toString());
				if (!VersionUtil.isEmpty(version))
				{
					Version minVersion = VersionUtil.parseVersion(attrItems.get(app));
					String compatibility = (version.compareTo(minVersion) >= 0) ? COMPATIBILITY_OK
							: COMPATIBILITY_UPDATE;
					Map<String, String> versionInfo = new HashMap<String, String>(4);
					versionInfo.put(ITEM_EXISTS, YES);
					versionInfo.put(ITEM_VERSION, version.toString());
					versionInfo.put(ITEM_COMPATIBILITY, compatibility);
					versionInfo.put(ITEM_VERSION_OUTPUT, commandResult.toString());
					itemsData.put(app, versionInfo);
					// Remove the name from the original map. Eventually, we will be left with the items we could not
					// locate in the system
					attrItems.remove(app);
				}
			}
		}
		// Traverse what we have left in the original map that was created from the attributes and mark all plug-ins as
		// 'missing'
		Set<String> missingItems = attrItems.keySet();
		for (String item : missingItems)
		{
			Map<String, String> versionInfo = new HashMap<String, String>(4);
			versionInfo.put(ITEM_EXISTS, NO);
			itemsData.put(item, versionInfo);
		}

		// Finally, set the bundle data status into the configuration attribute
		configurationStatus.setAttribute(CONFIG_ATTR, JSON.toString(itemsData));

		configurationStatus.setStatus(ConfigurationStatus.OK);
		return configurationStatus;
	}

	@Override
	public ConfigurationStatus configure(IProgressMonitor progressMonitor, Object attributes)
	{
		// TODO: Shalom - Right now we do not install directly, but pointing to installation instructions.
		return configurationStatus;
	}

	/*
	 * Return only the delegators that supports an application from the apps list and supports the VERSION_COMMAND. This
	 * method returns a map of delegator supported application name to delegator instance.
	 */
	private Map<String, IConfigurationProcessorDelegate> getVersionDelegators(String[] apps)
	{
		Set<String> appsSet = new HashSet<String>();
		for (String app : apps)
		{
			appsSet.add(app);
		}
		Set<IConfigurationProcessorDelegate> allDelegators = ConfigurationProcessorsRegistry.getInstance()
				.getProcessorDelegators(getID());
		Map<String, IConfigurationProcessorDelegate> delegators = new HashMap<String, IConfigurationProcessorDelegate>();
		for (IConfigurationProcessorDelegate delegate : allDelegators)
		{
			if (appsSet.contains(delegate.getSupportedApplication())
					&& delegate.getSupportedCommands().contains(IConfigurationProcessorDelegate.VERSION_COMMAND))
			{
				delegators.put(delegate.getSupportedApplication(), delegate);
				// Remove the item from the list, so that at the end of this loop we will end up with all the apps that
				// did not match to any version delegate.
				appsSet.remove(delegate.getSupportedApplication());
			}
		}

		// For every app that does not have a delegate, add a special delegate that will use the preferences to try to
		// find out the version.
		for (String app : appsSet)
		{
			delegators.put(app, new CachedVersionProcessorDelegate(app));
		}

		return delegators;
	}

	/**
	 * Returns the active working directory according to the <b>last</b> active project in the Project Explorer.<br>
	 * The value is taken from the preferences, therefore, this method can also return null if it fails.
	 * 
	 * @return The active project's working directory (can be null)
	 */
	protected IPath getActiveWorkingDir()
	{
		// FIXME - Shalom: Test is. This might not work after the latest changes to the way we save the active project
		IPreferencesService preferencesService = Platform.getPreferencesService();
		String activeProjectName = preferencesService.getString(ExplorerPlugin.PLUGIN_ID,
				IPreferenceConstants.ACTIVE_PROJECT, null, null);
		IProject result = null;

		if (activeProjectName != null)
		{
			result = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
		}
		if (result == null)
		{
			return null;
		}
		return result.getLocation();
	}
}
