/**
 * Appcelerator Titanium Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Proprietary and Confidential - This source code is not for redistribution
 */

package com.aptana.jira.core.internal;

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
	private JiraProjectInfo projectProviderInfo;
	private int highestPriority;

	private static final String ELEMENT_TYPE = "provider"; //$NON-NLS-1$
	private static final String PROJECT_PROVIDER_EXTENSION_POINT_ID = "jiraProjectProviders"; //$NON-NLS-1$
	private static final String PROJECT_CODE = "projectCode"; //$NON-NLS-1$
	private static final String PROJECT_NAME = "projectName"; //$NON-NLS-1$
	private static final String PRIORITY = "priority"; //$NON-NLS-1$
	private final int DEFAULT_PRIORITY = 60;

	private synchronized void loadProviders()
	{
		if (projectProviderInfo == null)
		{
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
									String priorityStr = element.getAttribute(PRIORITY);
									int priority = DEFAULT_PRIORITY;
									try
									{
										priority = Integer.parseInt(priorityStr);
									}
									catch (NumberFormatException ignore)
									{
									}

									if (highestPriority < priority)
									{
										highestPriority = priority;
										projectProviderInfo = new JiraProjectInfo(projectName, projectCode);
									}
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

	public JiraProjectInfo getProjectInfo()
	{
		loadProviders();
		return projectProviderInfo;
	}

	public class JiraProjectInfo
	{
		private String projectName;
		private String projectCode;

		private JiraProjectInfo(String projectName, String projectCode)
		{
			this.projectName = projectName;
			this.projectCode = projectCode;
		}

		public String getProjectName()
		{
			return projectName;
		}

		public String getProjectCode()
		{
			return projectCode;
		}
	}

}
