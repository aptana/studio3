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

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.util.UIUtils;

public class UserAgentManager
{
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
			return name.compareToIgnoreCase(o.name);
		}
	}

	private static final String[] DEFAULT_USERAGENT_IDS = new String[] { "IE", "Mozilla" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String USERAGENT_ID = "userAgent"; //$NON-NLS-1$
	private static final String ELEMENT_USERAGENT = "user-agent"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_ICON_DISABLED = "icon-disabled"; //$NON-NLS-1$
	private static final Map<String, UserAgent> userAgentsByID = new HashMap<String, UserAgent>();

	private static UserAgentManager INSTANCE;

	private ImageRegistry imageRegistry;

	/**
	 * getInstance
	 * 
	 * @return
	 */
	public synchronized static UserAgentManager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new UserAgentManager();
		}

		return INSTANCE;
	}

	/**
	 * UserAgentManager
	 */
	private UserAgentManager()
	{
		loadExtension();
	}

	/**
	 * getActiveUserAgentIDs
	 * 
	 * @return
	 */
	public String[] getActiveUserAgentIDs()
	{
		IPreferenceStore prefs = UIEplPlugin.getDefault().getPreferenceStore();
		String agentsValue = prefs.getString(IPreferenceConstants.USER_AGENT_PREFERENCE);
		if (StringUtil.isEmpty(agentsValue))
		{
			return new String[0];
		}
		return agentsValue.split(","); //$NON-NLS-1$
	}

	/**
	 * Returns the string array of user agents
	 * 
	 * @return String[]
	 */
	public UserAgent[] getActiveUserAgents()
	{
		return this.getUserAgentsByID(this.getActiveUserAgentIDs());
	}

	/**
	 * getDefaultActiveUserAgents
	 * 
	 * @return
	 */
	public UserAgent[] getDefaultActiveUserAgents()
	{
		return this.getUserAgentsByID(DEFAULT_USERAGENT_IDS);
	}

	/**
	 * getUserAgentsByID
	 * 
	 * @param ids
	 * @return
	 */
	public UserAgent[] getUserAgentsByID(String... ids)
	{
		UserAgent[] result = new UserAgent[ids.length];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = userAgentsByID.get(ids[i]);
		}

		return result;
	}

	/**
	 * getUserAgentImages
	 * 
	 * @param userAgents
	 * @return
	 */
	public Image[] getUserAgentImages(List<String> userAgents)
	{
		Image[] result;

		if (userAgents != null)
		{
			result = this.getUserAgentImages(userAgents.toArray(new String[userAgents.size()]));
		}
		else
		{
			result = new Image[0];
		}

		return result;
	}

	/**
	 * getUserAgentImages
	 */
	public Image[] getUserAgentImages(String... userAgents)
	{
		UserAgent[] activeUserAgents = getActiveUserAgents();
		Set<String> enabledAgents = new HashSet<String>(Arrays.asList(userAgents));
		Image[] result = new Image[activeUserAgents.length];

		Arrays.sort(activeUserAgents);

		for (int i = 0; i < activeUserAgents.length; i++)
		{
			UserAgent userAgent = activeUserAgents[i];

			result[i] = (enabledAgents.contains(userAgent.ID)) ? getImage(userAgent.enabledIconPath)
					: getImage(userAgent.disabledIconPath);
		}

		return result;
	}

	/**
	 * getUserAgents
	 * 
	 * @return
	 */
	public UserAgent[] getAllUserAgents()
	{
		Collection<UserAgent> userAgents = userAgentsByID.values();
		UserAgent[] result = userAgents.toArray(new UserAgent[userAgents.size()]);

		Arrays.sort(result);

		return result;
	}

	public Image getImage(String iconPath)
	{
		if (iconPath == null)
		{
			return null;
		}
		if (imageRegistry == null)
		{
			imageRegistry = new ImageRegistry();
		}

		File file = new File(iconPath);
		if (file.exists())
		{
			String iconFilename = file.getAbsolutePath();
			Image image = imageRegistry.get(iconFilename);
			if (image == null)
			{
				image = new Image(UIUtils.getDisplay(), iconFilename);
				imageRegistry.put(iconFilename, image);
			}
			return image;
		}
		return null;
	}

	/**
	 * loadExtension
	 */
	private void loadExtension()
	{
		// @formatter:off
		EclipseUtil.processConfigurationElements(
			CommonEditorPlugin.PLUGIN_ID,
			USERAGENT_ID,
			new IConfigurationElementProcessor()
			{
				public void processElement(IConfigurationElement element)
				{
					String agentID = element.getAttribute(ATTR_USER_AGENT_ID);

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

						String agentName = element.getAttribute(ATTR_USER_AGENT_NAME);
						userAgentsByID.put(agentID, new UserAgent(agentID, agentName, agentIconPath, agentIconDisabledPath));
					}
				}
			},
			ELEMENT_USERAGENT
		);
		// @formatter:on
	}
}
