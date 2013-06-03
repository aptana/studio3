/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.CorePlugin;
import com.aptana.core.IMap;
import com.aptana.core.IUserAgent;
import com.aptana.core.IUserAgentManager;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;

public class UserAgentManager implements IUserAgentManager
{

	/**
	 * The extension point id for the user agent extension
	 */
	private static final String USERAGENT_ID = "userAgent"; //$NON-NLS-1$

	// user-agent element and its attributes
	private static final String ELEMENT_USER_AGENT = "user-agent"; //$NON-NLS-1$

	// default-user-agents element and its attributes
	private static final String ELEMENT_DEFAULT_USER_AGENTS = "default-user-agents"; //$NON-NLS-1$
	private static final String ELEMENT_USER_AGENT_REF = "user-agent-ref"; //$NON-NLS-1$
	private static final String ATTR_NATURE_ID = "nature-id"; //$NON-NLS-1$
	private static final String ATTR_USER_AGENT_ID = "user-agent-id"; //$NON-NLS-1$

	/**
	 * A purposely malformed nature id used by the user agent extension point to define a list of default user agents
	 * for unrecognized nature ids
	 */
	private static final String OTHER_NATURE_ID = "<other>"; //$NON-NLS-1$

	/**
	 * A list of user agents to use as a default in the cases when we don't have a default set for a given nature id and
	 * we don't have a special "other" default list. Most likely this will never be used.
	 */
	private static final Set<String> LAST_RESORT_DEFAULT_USER_AGENTS = CollectionsUtil
			.newSet("IE", "Mozilla", "Chrome"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private Map<String, IUserAgent> fUserAgents;
	private HashMap<String, Set<String>> fDefaultUserAgents;

	public IUserAgent[] getAllUserAgents()
	{
		lazyLoad();
		return fUserAgents.values().toArray(new IUserAgent[fUserAgents.size()]);
	}

	/**
	 * Process the userAgent extension point. This populates the global list of known UserAgents and populates the
	 * default user agent list per nature. This is expected to be run only once and is called when the singleton
	 * instance is created.
	 */
	private synchronized void lazyLoad()
	{
		if (fUserAgents == null || fDefaultUserAgents == null)
		{
			final Set<IUserAgent> userAgents = new HashSet<IUserAgent>();
			fDefaultUserAgents = new HashMap<String, Set<String>>();

			EclipseUtil.processConfigurationElements(CorePlugin.PLUGIN_ID, USERAGENT_ID,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							String elementName = element.getName();
							if (ELEMENT_USER_AGENT.equals(elementName))
							{
								userAgents.add(new LazyUserAgent(element));
							}
							else if (ELEMENT_DEFAULT_USER_AGENTS.equals(elementName))
							{
								String natureID = element.getAttribute(ATTR_NATURE_ID);
								IConfigurationElement[] children = element.getChildren(ELEMENT_USER_AGENT_REF);
								if (!ArrayUtil.isEmpty(children))
								{
									Set<String> userAgentIds = new HashSet<String>(children.length);
									for (IConfigurationElement ref : children)
									{
										String userAgentID = ref.getAttribute(ATTR_USER_AGENT_ID);
										userAgentIds.add(userAgentID);
									}
									fDefaultUserAgents.put(natureID, userAgentIds);
								}
							}
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_USER_AGENT, ELEMENT_DEFAULT_USER_AGENTS);
						}
					});

			// make a map from user agent id to user agent
			fUserAgents = CollectionsUtil.mapFromValues(userAgents, new IMap<IUserAgent, String>()
			{

				public String map(IUserAgent item)
				{
					return item.getID();
				}
			});
		}
	}

	public IUserAgent[] getDefaultUserAgents(String natureID)
	{
		lazyLoad();

		Set<String> ids;
		if (fDefaultUserAgents.containsKey(natureID))
		{
			// get default list defined specifically for this nature
			ids = fDefaultUserAgents.get(natureID);
		}
		else if (fDefaultUserAgents.containsKey(OTHER_NATURE_ID))
		{
			// use default list for "other" nature if we didn't get a recognizable nature
			ids = fDefaultUserAgents.get(OTHER_NATURE_ID);
		}
		else
		{
			// use our "last resort" default list if we didn't have an "other" list
			ids = LAST_RESORT_DEFAULT_USER_AGENTS;
		}

		if (CollectionsUtil.isEmpty(ids))
		{
			return NO_USER_AGENTS;
		}

		List<IUserAgent> result = CollectionsUtil.map(ids, new IMap<String, IUserAgent>()
		{
			public IUserAgent map(final String id)
			{
				return getUserAgentById(id);
			}
		});
		return result.toArray(new IUserAgent[result.size()]);
	}

	public IUserAgent getUserAgentById(String id)
	{
		lazyLoad();
		return fUserAgents.get(id);
	}

	public boolean addUserAgent(IUserAgent agent)
	{
		lazyLoad();
		fUserAgents.put(agent.getID(), agent);
		return true;
	}
}
