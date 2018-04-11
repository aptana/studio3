/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.projects.internal.listeners;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.IFilter;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;
import com.aptana.projects.ProjectsPlugin;
import com.aptana.projects.listeners.IProjectListenersManager;
import com.aptana.projects.listeners.IStudioProjectListener;

/**
 * Manages the IStudioProjectListeners
 * 
 * @author Nam Le <nle@appcelerator.com>
 * @author Chris Williams <cwilliams@appcelerator.com>
 */
public class StudioProjectListenersManager implements IProjectListenersManager
{

	private final String EXTENSION_POINT = "projectListeners";//$NON-NLS-1$
	private final String ELEMENT_PROJECT_LISTENER = "projectListener";//$NON-NLS-1$
	private final String ELEMENT_CLASS = "class";//$NON-NLS-1$
	private final String ELEMENT_PRIORITY = "priority";//$NON-NLS-1$
	private final String ELEMENT_PROJECT_NATURE = "natureId";//$NON-NLS-1$

	private class ProjectListenerElement
	{
		private Integer priority;
		private IStudioProjectListener projectListener;
		private final IConfigurationElement ice;

		ProjectListenerElement(IStudioProjectListener listener, int priority)
		{
			this(null);
			this.projectListener = listener;
			this.priority = priority;
		}

		ProjectListenerElement(IConfigurationElement element)
		{
			this.ice = element;
		}

		public synchronized IStudioProjectListener getListener()
		{
			if (projectListener == null)
			{
				try
				{
					projectListener = (IStudioProjectListener) ice.createExecutableExtension(ELEMENT_CLASS);
				}
				catch (CoreException e)
				{
					IdeLog.logError(ProjectsPlugin.getDefault(), MessageFormat.format(
							"Failed to create project listener from contributor: {0}, class: {1}", ice.getContributor() //$NON-NLS-1$
									.getName(), ice.getAttribute(ELEMENT_CLASS)), e);
				}
			}
			return projectListener;
		}

		public synchronized int getPriority()
		{
			if (priority == null)
			{
				String rawPriority = ice.getAttribute(ELEMENT_PRIORITY);
				try
				{
					priority = Integer.valueOf(rawPriority);
				}
				catch (Exception e)
				{
					// Invalid priority integer, ignore and use the default value
					priority = 100;
				}
			}
			return priority;
		}
	}

	private final Map<String, List<ProjectListenerElement>> listeners;

	public StudioProjectListenersManager()
	{
		listeners = new HashMap<String, List<ProjectListenerElement>>();
		// FIXME Load the registry lazily
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
				String projectNature = element.getAttribute(ELEMENT_PROJECT_NATURE);

				List<ProjectListenerElement> values = listeners.get(projectNature);
				if (values == null)
				{
					values = new ArrayList<StudioProjectListenersManager.ProjectListenerElement>();
					listeners.put(projectNature, values);
				}

				values.add(new ProjectListenerElement(element));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.internal.listeners.IProjectListenersManager#addListener(com.aptana.projects.listeners.
	 * IStudioProjectListener, int, java.lang.String)
	 */
	public synchronized boolean addListener(IStudioProjectListener listener, int priority, String projectNature)
	{
		List<ProjectListenerElement> values = listeners.get(projectNature);
		if (values == null)
		{
			values = new ArrayList<StudioProjectListenersManager.ProjectListenerElement>(2);
		}

		values.add(new ProjectListenerElement(listener, priority));
		listeners.put(projectNature, values);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.projects.internal.listeners.IProjectListenersManager#getProjectListeners(java.lang.String)
	 */
	public synchronized IStudioProjectListener[] getProjectListeners(String... projectNatures)
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

				return o1.getPriority() - o2.getPriority();
			}
		});

		List<IStudioProjectListener> projectListeners = new ArrayList<IStudioProjectListener>();
		for (ProjectListenerElement element : listenerElements)
		{
			projectListeners.add(element.getListener());
		}
		return projectListeners.toArray(new IStudioProjectListener[projectListeners.size()]);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.projects.internal.listeners.IProjectListenersManager#removeListener(com.aptana.projects.listeners.
	 * IStudioProjectListener, java.lang.String)
	 */
	public synchronized boolean removeListener(final IStudioProjectListener listener, String projectNature)
	{
		if (listener == null)
		{
			return false;
		}
		List<ProjectListenerElement> values = listeners.get(projectNature);
		if (values == null)
		{
			// Nothing to remove!
			return false;
		}

		ProjectListenerElement element = CollectionsUtil.find(values, new IFilter<ProjectListenerElement>()
		{
			public boolean include(ProjectListenerElement item)
			{
				return listener.equals(item.getListener());
			}
		});
		if (element == null)
		{
			return false;
		}
		values.remove(element);
		listeners.put(projectNature, values);
		return true;
	}
}
