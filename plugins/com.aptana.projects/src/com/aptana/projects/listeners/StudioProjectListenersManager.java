/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;

/**
 * Manages the IStudioProjectListeners
 * 
 * @author Nam Le <nle@appcelerator.com>
 */
public class StudioProjectListenersManager
{

	private final String EXTENSION_POINT = "projectListeners";//$NON-NLS-1$
	private final String ELEMENT_PROJECT_LISTENER = "projectListener";//$NON-NLS-1$
	private final String ELEMENT_CLASS = "class";//$NON-NLS-1$
	private final String ELEMENT_PRIORITY = "priority";//$NON-NLS-1$
	private final String ELEMENT_PROJECT_NATURE = "natureId";//$NON-NLS-1$

	private class ProjectListenerElement
	{
		int priority;
		IStudioProjectListener projectListener;

		ProjectListenerElement(IStudioProjectListener listener, int priority)
		{
			this.projectListener = listener;
			this.priority = priority;
		}
	}

	private final Map<String, List<ProjectListenerElement>> listeners = new HashMap<String, List<ProjectListenerElement>>();

	private static StudioProjectListenersManager INSTANCE;

	public synchronized static StudioProjectListenersManager getManager()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new StudioProjectListenersManager();
		}

		return INSTANCE;
	}

	private StudioProjectListenersManager()
	{
		readExtensionRegistry();
	}

	private void readExtensionRegistry()
	{
		EclipseUtil.processConfigurationElements(ProjectsPlugin.PLUGIN_ID, EXTENSION_POINT,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						readElement(element);
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_PROJECT_LISTENER);
					}
				});
	}

	private void readElement(IConfigurationElement element)
	{
		if (ELEMENT_PROJECT_LISTENER.equals(element.getName()))
		{
			String listenerClass = element.getAttribute(ELEMENT_CLASS);
			if (!StringUtil.isEmpty(listenerClass))
			{
				try
				{
					IStudioProjectListener listener = (IStudioProjectListener) element
							.createExecutableExtension(ELEMENT_CLASS);

					String priority = element.getAttribute(ELEMENT_PRIORITY);
					int priorityValue = 100;
					try
					{
						priorityValue = Integer.valueOf(priority);
					}
					catch (Exception e)
					{
						// Invalid priority integer, ignore and use the default value
					}

					String projectNature = element.getAttribute(ELEMENT_PROJECT_NATURE);

					List<ProjectListenerElement> values = listeners.get(projectNature);
					if (values == null)
					{
						values = new ArrayList<StudioProjectListenersManager.ProjectListenerElement>();
						listeners.put(projectNature, values);
					}

					values.add(new ProjectListenerElement(listener, priorityValue));
				}
				catch (CoreException e)
				{
					// ignores the exception since it's optional
				}
			}
		}
	}

	/**
	 * Returns an array of project listeners ordered based on priority
	 * 
	 * @param projectNature
	 * @return
	 */
	public IStudioProjectListener[] getProjectListeners(String... projectNatures)
	{
		List<ProjectListenerElement> listenerElements = new ArrayList<ProjectListenerElement>();

		// Add any global listeners
		List<ProjectListenerElement> globalListeners = listeners.get(null);
		if (globalListeners != null)
		{
			listenerElements.addAll(globalListeners);
		}

		// Add listeners that match the project natures
		if (projectNatures != null)
		{
			for (String nature : projectNatures)
			{
				List<ProjectListenerElement> values = this.listeners.get(nature);
				if (values != null)
				{
					listenerElements.addAll(values);
				}
			}
		}

		// Sort the listeners based on priority
		Collections.sort(listenerElements, new Comparator<ProjectListenerElement>()
		{

			public int compare(ProjectListenerElement o1, ProjectListenerElement o2)
			{
				if (o1 == null)
				{
					return -1;
				}
				else if (o2 == null)
				{
					return 1;
				}

				return o1.priority - o2.priority;
			}
		});

		List<IStudioProjectListener> projectListeners = new ArrayList<IStudioProjectListener>();
		for (ProjectListenerElement element : listenerElements)
		{
			projectListeners.add(element.projectListener);
		}
		return projectListeners.toArray(new IStudioProjectListener[projectListeners.size()]);
	}
}
