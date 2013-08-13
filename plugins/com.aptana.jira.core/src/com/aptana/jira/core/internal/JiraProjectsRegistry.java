/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.jira.core.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.jira.core.JiraCorePlugin;

/**
 * @author pinnamuri
 */
public class JiraProjectsRegistry
{
	private Map<String, String> projectProviders;

	private static final String ELEMENT_TYPE = "provider"; //$NON-NLS-1$
	private static final String PROJECT_PROVIDER_EXTENSION_POINT_ID = "jiraProjectProviders"; //$NON-NLS-1$
	private static final String PROJECT_CODE = "projectCode"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "projectName"; //$NON-NLS-1$

	private synchronized void loadProviders()
	{
		if (projectProviders == null)
		{
			projectProviders = new HashMap<String, String>();

			EclipseUtil.processConfigurationElements(JiraCorePlugin.PLUGIN_ID, PROJECT_PROVIDER_EXTENSION_POINT_ID,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							String elementName = element.getName();
							if (ELEMENT_TYPE.equals(elementName))
							{
								try
								{
									String projectName = element.getAttribute(PROJECT_NAME);
									String projectCode = element.getAttribute(PROJECT_CODE);
									projectProviders.put(projectName, projectCode);
								}
								catch (Exception e)
								{
									IdeLog.logError(
											JiraCorePlugin.getDefault(),
											"Failed to load Jira project provider information from plugin: " + element.getContributor().getName(), e); //$NON-NLS-1$
								}
							}
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_TYPE);
						}
					});
		}
	}

	public Map<String, String> getProjectProviders()
	{
		loadProviders();
		return projectProviders;
	}

}
