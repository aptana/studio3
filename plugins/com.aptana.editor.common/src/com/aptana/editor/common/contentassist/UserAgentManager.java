package com.aptana.editor.common.contentassist;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.util.SWTUtils;

public class UserAgentManager
{
	class UserAgent
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
	}
	
	private static final String USERAGENT_ID = "userAgent"; //$NON-NLS-1$
	private static final String ELEMENT_USERAGENT = "user-agent"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_ICON = "icon"; //$NON-NLS-1$
	private static final String ATTR_ICON_DISABLED = "icon-disabled"; //$NON-NLS-1$
	private static final Map<String,UserAgent> userAgentsByName = new HashMap<String,UserAgent>();

	private static UserAgentManager INSTANCE;
	
	/**
	 * UserAgentManager
	 */
	private UserAgentManager()
	{
		this.loadExtension();
	}
	
	/**
	 * getUserAgentImages
	 */
	public Image[] getUserAgentImages(String[] userAgents)
	{
		Set<String> nameSet = userAgentsByName.keySet();
		String[] allNames = nameSet.toArray(new String[nameSet.size()]);
		Set<String> enabledAgents = new HashSet<String>(Arrays.asList(userAgents));
		Image[] result = new Image[allNames.length];
		
		Arrays.sort(allNames);
		
		for (int i = 0; i < allNames.length; i++)
		{
			String name = allNames[i];
			UserAgent userAgent = userAgentsByName.get(name);
			
			result[i] = (enabledAgents.contains(name)) ? userAgent.enabledIcon : userAgent.disabledIcon;
		}
		
		return result;
	}
	
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
										userAgentsByName.put(agentID, new UserAgent(agentID, agentName, agentIcon, agentIconDisabled));
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
