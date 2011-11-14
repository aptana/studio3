/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.aptana.core.util.ConfigurationElementDispatcher;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.util.UIUtils;

public class UserAgentManager
{
	/**
	 * A class used to process default-user-agents elements in our user agent extension point
	 */
	private class DefaultUserAgentsProcessor implements IConfigurationElementProcessor
	{
		public void processElement(IConfigurationElement element)
		{
			String natureID = element.getAttribute(ATTR_NATURE_ID);

			for (IConfigurationElement ref : element.getChildren(ELEMENT_USER_AGENT_REF))
			{
				String userAgentID = ref.getAttribute(ATTR_USER_AGENT_ID);

				addDefaultUserAgentID(natureID, userAgentID);
			}
		}
	}

	/**
	 * A user agent container
	 */
	public static class UserAgent implements Comparable<UserAgent>
	{
		public final String ID;
		public final String name;
		public final String enabledIconPath;
		public final String disabledIconPath;

		public UserAgent(String ID, String name, String enabledIconPath, String disabledIconPath)
		{
			this.ID = ID;
			this.name = name;
			this.enabledIconPath = enabledIconPath;
			this.disabledIconPath = disabledIconPath;
		}

		public int compareTo(UserAgent o)
		{
			String name = (o != null) ? o.name : StringUtil.EMPTY;

			return this.name.compareToIgnoreCase(name);
		}
	}

	/**
	 * A class used to process user-agent elements in our user agent extension point
	 */
	private class UserAgentProcessor implements IConfigurationElementProcessor
	{
		public void processElement(IConfigurationElement element)
		{
			String agentID = element.getAttribute(ATTR_ID);

			if (agentID != null)
			{
				Bundle bundle = Platform.getBundle(element.getNamespaceIdentifier());

				String agentIconPath = element.getAttribute(ATTR_ICON);
				if (agentIconPath != null)
				{
					URL url = bundle.getEntry(agentIconPath);
					agentIconPath = ResourceUtil.resourcePathToString(url);
				}

				String agentIconDisabledPath = element.getAttribute(ATTR_ICON_DISABLED);
				if (agentIconDisabledPath != null)
				{
					URL url = bundle.getEntry(agentIconDisabledPath);
					agentIconDisabledPath = ResourceUtil.resourcePathToString(url);
				}

				String agentName = element.getAttribute(ATTR_NAME);
				USER_AGENTS_BY_ID.put(agentID, new UserAgent(agentID, agentName, agentIconPath, agentIconDisabledPath));
			}
		}
	}

	// empty array constants
	private static final String[] NO_STRINGS = new String[0];
	public static final UserAgent[] NO_USER_AGENTS = new UserAgent[0];

	/**
	 * A reference to the singleton UserAgentManager object
	 */
	private static UserAgentManager INSTANCE;

	/**
	 * A purposely malformed nature id used by the user agent extension point to define a list of default user agents
	 * for unrecognized nature ids
	 */
	private static final String OTHER_NATURE_ID = "<other>"; //$NON-NLS-1$

	/**
	 * A list of user agents to use as a default in the cases when we don't have a default set for a given nature id and
	 * we don't have a special "other" default list. Most likely this will never be used
	 */
	private static final String[] LAST_RESORT_DEFAULT_USER_AGENT = new String[] { "IE", "Mozilla", "Chrome" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	/**
	 * The delimiter used between a list of user agent ids in the preference value
	 */
	private static final String USER_AGENT_DELIMITER = ","; //$NON-NLS-1$

	/**
	 * The delimiter used between each entries where an entry defines a nature and its active user agents
	 */
	private static final String ENTRY_DELIMITER = ";"; //$NON-NLS-1$

	/**
	 * The delimiter used between the nature and the user agent list in a entry
	 */
	private static final String NAME_VALUE_SEPARATOR = ":"; //$NON-NLS-1$

	/**
	 * A mapping of user agent ID to its corresponding UserAgent instance. This is a global map and contains all user
	 * agents known at runtime
	 */
	private static final Map<String, UserAgent> USER_AGENTS_BY_ID = new HashMap<String, UserAgent>();

	/**
	 * A mapping of nature ID to a list of user agent ids. The user agent id list contains the ids which are considered
	 * to be the default user agents for the given nature. The user agent list is defined via the default-user-agents
	 * element in the userAgent extension point
	 */
	private static Map<String, Set<String>> DEFAULT_USER_AGENT_IDS = new HashMap<String, Set<String>>();

	/**
	 * A mapping of nature ID to an array of user agent ids. The user agent id array contains the ids which are
	 * currently active for the given nature. This is mainly a in-memory cache of the string representation in the
	 * preference key. This structure is generated by {@link #loadPreference()} and is converted to a preference key
	 * value via {@link #savePreference()}
	 */
	private static Map<String, String[]> ACTIVE_USER_AGENTS_BY_NATURE_ID;

	/**
	 * The extension point id for the user agent extension
	 */
	private static final String USERAGENT_ID = "userAgent"; //$NON-NLS-1$

	// user-agent element and its attributes
	private static final String ELEMENT_USER_AGENT = "user-agent"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_ICON_DISABLED = "icon-disabled"; //$NON-NLS-1$

	// default-user-agents element and its attributes
	private static final String ELEMENT_DEFAULT_USER_AGENTS = "default-user-agents"; //$NON-NLS-1$
	private static final String ELEMENT_USER_AGENT_REF = "user-agent-ref"; //$NON-NLS-1$
	private static final String ATTR_NATURE_ID = "nature-id"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_ID = "user-agent-id"; //$NON-NLS-1$

	/**
	 * Add a user agent ID to the list of IDs for a given nature. These IDs are considered to be default user agents for
	 * that nature.
	 * 
	 * @param natureID
	 *            The nature ID to which to associate the user agent ID
	 * @param userAgentID
	 *            The user agent ID to associate with the given natureID. Adding duplicated user agent ids is OK
	 */
	private static void addDefaultUserAgentID(String natureID, String userAgentID)
	{
		if (!DEFAULT_USER_AGENT_IDS.containsKey(natureID))
		{
			DEFAULT_USER_AGENT_IDS.put(natureID, new HashSet<String>());
		}

		DEFAULT_USER_AGENT_IDS.get(natureID).add(userAgentID);
	}

	/**
	 * Grab the singleton instance of the UserAgentManager. This method is responsible for creating the instance if it
	 * does not exist. The instance is initialized (processes extension point and preference key) when it is created
	 * 
	 * @return Returns the singleton instance of this class
	 */
	public synchronized static UserAgentManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new UserAgentManager();
			INSTANCE.loadExtension();
			INSTANCE.loadPreference();
		}

		return INSTANCE;
	}

	/**
	 * A registry of images used to maintain icons for each user agent
	 */
	private ImageRegistry imageRegistry;

	/**
	 * Make sure no one accept the class itself can instantiate this class
	 */
	private UserAgentManager()
	{
	}

	/**
	 * Given a list of nature IDs, return the list of active user agent IDs. The current implementation processes only
	 * the first (primary) nature ID. The signature allows for multiple nature IDs in case secondary natures need to be
	 * taken into consideration at a later point in time
	 * 
	 * @param natureIDs
	 *            An array of nature IDs to process
	 * @return Returns an array of user agent IDs
	 */
	public String[] getActiveUserAgentIDs(String... natureIDs)
	{
		String[] result = NO_STRINGS;

		if (natureIDs != null && natureIDs.length > 0)
		{
			// NOTE: Currently, we only care about the primary nature.
			String natureID = natureIDs[0];

			result = ACTIVE_USER_AGENTS_BY_NATURE_ID.get(natureID);
		}

		return result;
	}

	/**
	 * Returns an array of UserAgent instances for a given list of nature IDs. This method uses
	 * {@link #getActiveUserAgentIDs(String...)} and therefore has the same limitations on natureIDs as described there
	 * 
	 * @return Returns an array array of UserAgent instances
	 */
	public UserAgent[] getActiveUserAgents(String... natureIDs)
	{
		return getUserAgentsByID(getActiveUserAgentIDs(natureIDs));
	}

	/**
	 * Returns an array of UserAgents for all user agents known at runtime
	 * 
	 * @return Returns an array of UserAgent instances
	 */
	public UserAgent[] getAllUserAgents()
	{
		Collection<UserAgent> userAgents = USER_AGENTS_BY_ID.values();

		return userAgents.toArray(new UserAgent[userAgents.size()]);
	}

	/**
	 * Returns a list of user agent IDs which are the default IDs for the specified nature. This method is typically
	 * used in part to reset the list of active user agents for a given nature ID, particularly in the Content Assist
	 * preference page.
	 * 
	 * @param natureID
	 *            The nature ID to use when looking up the default user agent ID list
	 * @return Returns an array of user agent IDs
	 */
	public String[] getDefaultUserAgentIDs(String natureID)
	{
		String[] result;

		if (DEFAULT_USER_AGENT_IDS.containsKey(natureID))
		{
			// get default list defined specifically for this nature
			Set<String> userAgentIDs = DEFAULT_USER_AGENT_IDS.get(natureID);

			result = userAgentIDs.toArray(new String[userAgentIDs.size()]);
		}
		else if (DEFAULT_USER_AGENT_IDS.containsKey(OTHER_NATURE_ID))
		{
			// use default list for "other" nature if we didn't get a recognizable nature
			Set<String> userAgentIDs = DEFAULT_USER_AGENT_IDS.get(OTHER_NATURE_ID);

			result = userAgentIDs.toArray(new String[userAgentIDs.size()]);
		}
		else
		{
			// use our "last resort" default list if we didn't have an "other" list
			result = LAST_RESORT_DEFAULT_USER_AGENT;
		}

		return result;
	}

	/**
	 * Returns a list of UserAgent instances which are the default user agents for the specified nature. Internally,
	 * this method uses {@link #getDefaultUserAgentIDs(String)}
	 * 
	 * @param natureID
	 *            The nature ID to use when looking up the default UserAgent list
	 * @return Returns an array of UserAgent instances
	 */
	public UserAgent[] getDefaultUserAgents(String natureID)
	{
		UserAgent[] result = NO_USER_AGENTS;
		String[] userAgentIDs = getDefaultUserAgentIDs(natureID);

		if (userAgentIDs != null)
		{
			result = getUserAgentsByID(userAgentIDs);
		}

		return result;
	}

	/**
	 * Return an icon for the given path
	 * 
	 * @param iconPath
	 *            The path to the icon image
	 * @return Returns an Image or null
	 */
	public Image getImage(String iconPath)
	{
		Image result = null;

		if (iconPath != null)
		{
			if (imageRegistry == null)
			{
				imageRegistry = new ImageRegistry();
			}

			File file = new File(iconPath);

			if (file.exists())
			{
				String iconFilename = file.getAbsolutePath();

				result = imageRegistry.get(iconFilename);

				if (result == null)
				{
					result = new Image(UIUtils.getDisplay(), iconFilename);
					imageRegistry.put(iconFilename, result);
				}
			}
		}

		return result;
	}

	/**
	 * Return an array of icons, one for each user agent ID in the userAgents array. The specified list of natures is
	 * used to determine the list of active user agents via {@link #getActiveUserAgents(String...)}. These user agents
	 * are compared to the user agents passed into this method. All user agents that are not in the specified array will
	 * return disabled icons. All others return enabled icons.
	 * 
	 * @param userAgents
	 *            An array of user agent IDs
	 * @return Returns an array of Images
	 */
	public Image[] getUserAgentImages(String[] natureIDs, String... userAgents)
	{
		UserAgent[] activeUserAgents = getActiveUserAgents(natureIDs);
		Set<String> enabledAgents = new HashSet<String>(Arrays.asList(userAgents));
		Image[] result = new Image[activeUserAgents.length];

		Arrays.sort(activeUserAgents);

		for (int i = 0; i < activeUserAgents.length; i++)
		{
			UserAgent userAgent = activeUserAgents[i];

			if (userAgent != null)
			{
				// @formatter:off
				result[i] = (enabledAgents.contains(userAgent.ID))
					? getImage(userAgent.enabledIconPath)
					: getImage(userAgent.disabledIconPath);
				// @formatter:on
			}
		}

		return result;
	}

	/**
	 * Return an array of UserAgents, one for each recognized user agent ID
	 * 
	 * @param ids
	 *            An array of user agent ids
	 * @return Returns an array of UserAgent instances
	 */
	public UserAgent[] getUserAgentsByID(String... ids)
	{
		List<UserAgent> result = new ArrayList<UserAgent>();

		if (ids != null && ids.length > 0)
		{
			for (String id : ids)
			{
				UserAgent userAgent = USER_AGENTS_BY_ID.get(id);

				if (userAgent != null)
				{
					result.add(userAgent);
				}
			}
		}

		return result.toArray(new UserAgent[result.size()]);
	}

	/**
	 * Process the userAgent extension point. This populates the global list of known UserAgents and populates the
	 * default user agent list per nature. This is expected to be run only once and is called when the singleton
	 * instance is created.
	 */
	private void loadExtension()
	{
		// configure dispatcher for each element type we process
		ConfigurationElementDispatcher dispatcher = new ConfigurationElementDispatcher();
		dispatcher.addElementProcessor(ELEMENT_USER_AGENT, new UserAgentProcessor());
		dispatcher.addElementProcessor(ELEMENT_DEFAULT_USER_AGENTS, new DefaultUserAgentsProcessor());

		// @formatter:off
		EclipseUtil.processConfigurationElements(
			CommonEditorPlugin.PLUGIN_ID,
			USERAGENT_ID,
			dispatcher,
			ELEMENT_USER_AGENT,
			ELEMENT_DEFAULT_USER_AGENTS
		);
		// @formatter:on
	}

	/**
	 * Process the preference key value associated with UserAgentManager. This method is automatically called when the
	 * singleton instance is created. If the preference key value is somehow manipulated outside of UserAgentManager
	 * (and it shouldn't be), then this method will need to be invoked to update the in-memory cache of
	 * nature/user-agent info.
	 */
	public void loadPreference()
	{
		Map<String, String[]> result = new HashMap<String, String[]>();

		// grab preference value
		IPreferenceStore prefs = UIEplPlugin.getDefault().getPreferenceStore();
		String preferenceValue = prefs.getString(IPreferenceConstants.USER_AGENT_PREFERENCE);

		if (!StringUtil.isEmpty(preferenceValue))
		{
			if (preferenceValue.contains(NAME_VALUE_SEPARATOR) && preferenceValue.contains(ENTRY_DELIMITER))
			{
				// looks like the latest format for this pref key
				String[] entries = preferenceValue.split(ENTRY_DELIMITER);

				for (String entry : entries)
				{
					String[] nameValue = entry.split(NAME_VALUE_SEPARATOR);
					String natureID = nameValue[0];

					if (nameValue.length > 1)
					{
						String userAgentIDsString = nameValue[1];
						String[] userAgentIDs = userAgentIDsString.split(USER_AGENT_DELIMITER);

						result.put(natureID, userAgentIDs);
					}
					else
					{
						result.put(natureID, NO_STRINGS);
					}
				}
			}
			else
			{
				// assume this is an old style preference and update each nature ID with its user agent settings

				// NOTE: We don't manipulate these arrays directly, so it should be fine to reference the same
				// array for all natures
				String[] userAgentIDs = preferenceValue.split(USER_AGENT_DELIMITER);

				for (String natureID : ResourceUtil.getAptanaNaturesMap().values())
				{
					result.put(natureID, userAgentIDs);
				}
			}
		}

		// cache result
		ACTIVE_USER_AGENTS_BY_NATURE_ID = result;
	}

	/**
	 * Save the current in-memory cache of nature/user-agent info to the UserAgentManager preference key. Note that
	 * changes made to the cache via {@link #setActiveUserAgents(String, String[])} will not be automatically reflected
	 * in the preference key value. This method needs to be called to make those changes persist between Studio
	 * sessions.
	 */
	public void savePreference()
	{
		// convert active user agents to a string representation
		List<String> natureEntries = new ArrayList<String>();

		for (Map.Entry<String, String[]> entry : ACTIVE_USER_AGENTS_BY_NATURE_ID.entrySet())
		{
			String natureID = entry.getKey();
			String userAgentIDs = StringUtil.join(USER_AGENT_DELIMITER, entry.getValue());

			natureEntries.add(natureID + NAME_VALUE_SEPARATOR + userAgentIDs);
		}

		String value = StringUtil.join(ENTRY_DELIMITER, natureEntries);

		// grab preference store
		IPreferenceStore prefs = UIEplPlugin.getDefault().getPreferenceStore();

		// save value
		prefs.setValue(IPreferenceConstants.USER_AGENT_PREFERENCE, value);
	}

	/**
	 * Set the currently active list of user agent IDs for the given nature ID. This updates the UserAgentMemory
	 * in-memory cache. To persist these changes, {@link #savePreference()} will need to be called. This allows for
	 * multiple changes to be made with a final single write to the preference key.
	 * 
	 * @param userAgents
	 */
	public void setActiveUserAgents(String natureID, String[] userAgentIDs)
	{
		if (!StringUtil.isEmpty(natureID))
		{
			String[] value = (userAgentIDs != null) ? userAgentIDs : NO_STRINGS;

			ACTIVE_USER_AGENTS_BY_NATURE_ID.put(natureID, value);
		}
	}
}
