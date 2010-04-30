package com.aptana.portal.ui.dispatch.configurationProcessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.mortbay.util.ajax.JSON;
import org.osgi.framework.Version;

import com.aptana.configurations.processor.AbstractConfigurationProcessor;
import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.util.ProcessUtil;

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
	// Match x.y and x.y.z
	private static final String VERSION_PATTERN = "(\\d+)\\.(\\d+)(\\.(\\d+))?"; //$NON-NLS-1$
	private static final String CONFIG_ATTR = "configurations"; //$NON-NLS-1$
	private static final String COMPATIBILITY_OK = "ok"; //$NON-NLS-1$
	private static final String COMPATIBILITY_UPDATE = "update"; //$NON-NLS-1$
	private static final String ITEM_EXISTS = "exists"; //$NON-NLS-1$
	private static final String YES = "yes"; //$NON-NLS-1$
	private static final String NO = "no"; //$NON-NLS-1$
	private static final String ITEM_VERSION = "version"; //$NON-NLS-1$
	private static final String ITEM_COMPATIBILITY = "compatibility"; //$NON-NLS-1$
	private static final String ITEM_VERSION_OUTPUT = "rawOutput"; //$NON-NLS-1$
	private static Map<String, String> knownCommands = new HashMap<String, String>(5);
	static
	{
		knownCommands.put("ruby", "ruby --version"); //$NON-NLS-1$//$NON-NLS-2$
		knownCommands.put("rails", "rails --version"); //$NON-NLS-1$//$NON-NLS-2$
		knownCommands.put("git", "git --version"); //$NON-NLS-1$//$NON-NLS-2$
		knownCommands.put("sqlite3", "sqlite3 --version"); //$NON-NLS-1$//$NON-NLS-2$
	}

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
		// Get the shell path
		String shellCommandPath = getShellPath();
		if (shellCommandPath == null)
		{
			if (Platform.OS_WIN32.equals(Platform.getOS()))
			{
				// In case we are on Windows, try to get the result by executing 'cmd'
				shellCommandPath = "cmd"; //$NON-NLS-1$
			}
			else
			{
				applyErrorAttributes(Messages.SystemConfigurationProcessor_noShellCommandPath);
				PortalUIPlugin.logError(new Exception(Messages.SystemConfigurationProcessor_noShellCommandPath));
				return configurationStatus;
			}
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
		String[] commands = attrItems.keySet().toArray(new String[attrItems.size()]);
		for (String command : commands)
		{
			if (!knownCommands.containsKey(command))
			{
				// We'll deal with these later
				continue;
			}
			String versionOutput = ProcessUtil.outputForCommand(shellCommandPath, null, new String[] {
					"-c", knownCommands.get(command) }); //$NON-NLS-1$
			Pattern pattern = Pattern.compile(VERSION_PATTERN);
			Matcher matcher = pattern.matcher(versionOutput);
			if (matcher.find())
			{
				String version = matcher.group();
				Version readVersion = Version.parseVersion(version);
				Version minVersion = Version.parseVersion(attrItems.get(command));
				String compatibility = (readVersion.compareTo(minVersion) >= 0) ? COMPATIBILITY_OK
						: COMPATIBILITY_UPDATE;
				Map<String, String> versionInfo = new HashMap<String, String>(4);
				versionInfo.put(ITEM_EXISTS, YES);
				versionInfo.put(ITEM_VERSION, version);
				versionInfo.put(ITEM_COMPATIBILITY, compatibility);
				versionInfo.put(ITEM_VERSION_OUTPUT, versionOutput);
				itemsData.put(command, versionInfo);
				// Remove the name from the original map. Eventually, we will be left with the items we could not
				// locate in the system
				attrItems.remove(command);
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

}
