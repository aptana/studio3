package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.mortbay.util.ajax.JSON;
import org.osgi.framework.Version;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationProcessorsRegistry;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.configurations.processor.IConfigurationProcessorDelegate;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.processorDelegates.BaseVersionProcessor;

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
	private static final String CONFIG_ATTR = "configurations"; //$NON-NLS-1$
	private static final String COMPATIBILITY_OK = "ok"; //$NON-NLS-1$
	private static final String COMPATIBILITY_UPDATE = "update"; //$NON-NLS-1$
	private static final String ITEM_EXISTS = "exists"; //$NON-NLS-1$
	private static final String YES = "yes"; //$NON-NLS-1$
	private static final String NO = "no"; //$NON-NLS-1$
	private static final String ITEM_VERSION = "version"; //$NON-NLS-1$
	private static final String ITEM_COMPATIBILITY = "compatibility"; //$NON-NLS-1$
	private static final String ITEM_VERSION_OUTPUT = "rawOutput"; //$NON-NLS-1$

	/**
	 * Compute the versions of the given items in the attributes instance. Items that are not in the supported list of
	 * programs are set to an 'unknown' state, just as they are not installed.
	 */
	@Override
	public ConfigurationStatus computeStatus(IProgressMonitor progressMonitor, Object attributes)
	{
		configurationStatus.removeAttribute(CONFIG_ATTR);
		clearErrorAttributes();
		if (attributes == null || !(attributes instanceof Object[]))
		{
			applyErrorAttributes(Messages.SystemConfigurationProcessor_missingConfigurationItems);
			PortalUIPlugin.logError(new Exception(Messages.SystemConfigurationProcessor_missingConfigurationItems));
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
				applyErrorAttributes(Messages.SystemConfigurationProcessor_wrongConfigurationAttributesStructure);
				PortalUIPlugin.logError(new Exception(
						Messages.SystemConfigurationProcessor_wrongConfigurationAttributesStructure));
				return configurationStatus;
			}
			// We only use the first two arguments. The third is the installation site URL.
			attrItems.put((String) def[0], (String) def[1]);
		}
		// Do the actual processing
		configurationStatus.setStatus(ConfigurationStatus.PROCESSING);

		// For each requested element, check that the item is in the known commands.
		// If it's there, execute the command. If not, set the item's state as unknown.
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
			Object commandResult = delegate.runCommand(IConfigurationProcessorDelegate.VERSION_COMMAND);
			if (commandResult != null)
			{
				Version version = BaseVersionProcessor.parseVersion(commandResult.toString());
				if (version != null)
				{
					Version minVersion = Version.parseVersion(attrItems.get(app));
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
			}
		}
		return delegators;
	}
}
