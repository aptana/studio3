/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.ui.SWTUtils;
import com.aptana.ui.epl.UIEplPlugin;

public class UserAgentManager
{
	public class UserAgent implements Comparable<UserAgent>
	{
		public final String ID;
		public final String name;
		public final Image enabledIcon;
		public final Image disabledIcon;
		
		public UserAgent(String ID, String name, Image enabledIcon, Image disabledIcon)
		{
			this.ID = ID;
			this.name = name;
			this.enabledIcon = enabledIcon;
			this.disabledIcon = disabledIcon;
		}

		public int compareTo(UserAgent o)
		{
			return this.name.compareToIgnoreCase(o.name);
		}
	}
	
	private static final String[] DEFAULT_USERAGENT_IDS = new String[] { "IE", "Mozilla" }; //$NON-NLS-1$ //$NON-NLS-2$
	
	private static final String USERAGENT_ID = "userAgent"; //$NON-NLS-1$
	private static final String ELEMENT_USERAGENT = "user-agent"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_ICON_DISABLED = "icon-disabled"; //$NON-NLS-1$
	private static final Map<String,UserAgent> userAgentsByID = new HashMap<String,UserAgent>();

	private static UserAgentManager INSTANCE;
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static UserAgentManager getInstance()
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
		this.loadExtension();
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
	public UserAgent[] getUserAgentsByID(String ... ids)
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
	public Image[] getUserAgentImages(String ... userAgents)
	{
		UserAgent[] activeUserAgents = this.getActiveUserAgents();
		Set<String> enabledAgents = new HashSet<String>(Arrays.asList(userAgents));
		Image[] result = new Image[activeUserAgents.length];
		
		Arrays.sort(activeUserAgents);
		
		for (int i = 0; i < activeUserAgents.length; i++)
		{
			UserAgent userAgent = activeUserAgents[i];
			
			result[i] = (enabledAgents.contains(userAgent.ID)) ? userAgent.enabledIcon : userAgent.disabledIcon;
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

	/**
	 * loadExtension
	 */
	private void loadExtension()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		
		if (registry != null)
		{
			IExtensionPoint extensionPoint = registry.getExtensionPoint(CommonEditorPlugin.PLUGIN_ID, USERAGENT_ID);

			if (extensionPoint != null)
			{
				IExtension[] extensions = extensionPoint.getExtensions();

				for (int i = 0; i < extensions.length; i++)
				{
					IExtension extension = extensions[i];
					IConfigurationElement[] elements = extension.getConfigurationElements();

					for (int j = 0; j < elements.length; j++)
					{
						IConfigurationElement element = elements[j];
						
						if (element.getName().equals(ELEMENT_USERAGENT))
						{
							String agentID = element.getAttribute(ATTR_USER_AGENT_ID);
							String agentName = element.getAttribute(ATTR_USER_AGENT_NAME);
							String agentIconPath = element.getAttribute(ATTR_ICON);
							String agentIconDisabledPath = element.getAttribute(ATTR_ICON_DISABLED);
							
							if (agentID != null)
							{
								IExtension ext = element.getDeclaringExtension();
								String pluginId = ext.getNamespaceIdentifier();
								Bundle bundle = Platform.getBundle(pluginId);
								
								if (agentIconPath != null && agentIconDisabledPath != null)
								{
									
									Image agentIcon = SWTUtils.getImage(bundle, agentIconPath);
									Image agentIconDisabled = SWTUtils.getImage(bundle, agentIconDisabledPath);
									
									if (agentIcon != null && agentIconDisabled != null)
									{
										userAgentsByID.put(agentID, new UserAgent(agentID, agentName, agentIcon, agentIconDisabled));
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
