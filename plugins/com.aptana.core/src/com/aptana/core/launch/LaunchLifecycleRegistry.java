/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * Class used to properly call extension points related to com.aptana.core.launchLifecycleListener.
 */
public class LaunchLifecycleRegistry
{
	private static final String LAUNCH_LIFECYCLE_CONFIGURATION_TYPE_ID_ATTRIBUTE = "launchConfigurationTypeId"; //$NON-NLS-1$

	private static final String LAUNCH_LIFECYCLE_PRIORITY_ATTRIBUTE = "priority"; //$NON-NLS-1$

	private static final String LAUNCH_LIFECYCLE_LISTENER_EXTENSION_POINT_ID = "com.aptana.core.launchLifecycleListener"; //$NON-NLS-1$

	private LaunchesListener launchesListener;

	private LaunchLifecycleRegistry()
	{

	}

	private static LaunchLifecycleRegistry instance;

	/**
	 * Get our singleton.
	 */
	public static synchronized LaunchLifecycleRegistry getInstance()
	{
		if (instance == null)
		{
			instance = new LaunchLifecycleRegistry();
		}
		return instance;
	}

	private synchronized LaunchesListener getLaunchesListener()
	{
		if (launchesListener == null)
		{
			launchesListener = new LaunchesListener();
		}
		return launchesListener;
	}

	/**
	 * Adds the launch listener to the debug plugin launch manager.
	 */
	public void installLaunchListener()
	{
		try
		{
			DebugPlugin.getDefault().getLaunchManager().addLaunchListener(getLaunchesListener());
		}
		catch (Exception e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
	}

	/**
	 * Removes the launch listener from the debug plugin launch manager.
	 */
	public void uninstallLaunchListener()
	{
		try
		{
			DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(getLaunchesListener());
		}
		catch (Exception e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
	}

	/**
	 * Launch listener class: when a launch is added or terminated, let the extension points that implement
	 * com.aptana.core.launchLifecycleListener know about it (if applicable).
	 */
	private static final class LaunchesListener implements ILaunchesListener2
	{
		public void launchesRemoved(ILaunch[] launches)
		{
		}

		public void launchesChanged(ILaunch[] launches)
		{
		}

		public void launchesAdded(ILaunch[] launches)
		{
			List<IConfigurationElement> lifecycleConfigurationElements = getLifecycleConfigurationElements();
			for (ILaunch launch : launches)
			{
				notify(launch, lifecycleConfigurationElements, true);
			}
		}

		public void launchesTerminated(ILaunch[] launches)
		{
			List<IConfigurationElement> lifecycleConfigurationElements = getLifecycleConfigurationElements();
			for (ILaunch launch : launches)
			{
				notify(launch, lifecycleConfigurationElements, false);
			}
		}

		private List<IConfigurationElement> getLifecycleConfigurationElements()
		{
			List<IConfigurationElement> list = new ArrayList<IConfigurationElement>(5);
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			if (registry != null)
			{
				try
				{
					IExtensionPoint extensionPoint = registry
							.getExtensionPoint(LAUNCH_LIFECYCLE_LISTENER_EXTENSION_POINT_ID);
					IExtension[] extensions = extensionPoint.getExtensions();
					for (IExtension extension : extensions)
					{
						IConfigurationElement[] elements = extension.getConfigurationElements();
						for (IConfigurationElement element : elements)
						{
							list.add(element);
						}
					}

				}
				catch (Exception e)
				{
					IdeLog.logError(CorePlugin.getDefault(), e);
					throw new RuntimeException(e);
				}
			}
			return list;
		}

		/**
		 * Helper class to sort based on priority.
		 */
		class PriorityAndListener implements Comparable<PriorityAndListener>
		{

			private final ILaunchLifecycleListener listener;
			private final int priority;

			public PriorityAndListener(ILaunchLifecycleListener listener, int priority)
			{
				this.listener = listener;
				this.priority = priority;
			}

			public int compareTo(PriorityAndListener o)
			{
				int thisPriority = this.priority;
				int otherPriority = o.priority;
				return (thisPriority < otherPriority ? -1 : (thisPriority == otherPriority ? 0 : 1));
			}

		}

		/**
		 * Receiving the configuration elements, filter the ones applicable for the passed launch (based on the
		 * configuration type id), instance the related class and sort by the priority.
		 * 
		 * @param before
		 *            determines if we should call the method "beforeLaunch" or "afterLaunch".
		 */
		private void notify(ILaunch launch, List<IConfigurationElement> lifecycleConfigurationElements, boolean before)
		{
			List<PriorityAndListener> lst = new ArrayList<PriorityAndListener>(lifecycleConfigurationElements.size());
			for (IConfigurationElement iConfigurationElement : lifecycleConfigurationElements)
			{
				try
				{
					String applyToLaunchConfigurationType = iConfigurationElement
							.getAttribute(LAUNCH_LIFECYCLE_CONFIGURATION_TYPE_ID_ATTRIBUTE);
					// Note: null means that it's applicable to any launch.
					if (applyToLaunchConfigurationType == null
							|| applyToLaunchConfigurationType.equals(launch.getLaunchConfiguration().getType()
									.getIdentifier()))
					{
						ILaunchLifecycleListener listener = (ILaunchLifecycleListener) iConfigurationElement
								.createExecutableExtension("class"); //$NON-NLS-1$
						String priorityStr = iConfigurationElement.getAttribute(LAUNCH_LIFECYCLE_PRIORITY_ATTRIBUTE);
						int priority;
						try
						{
							priority = Integer.parseInt(priorityStr);
						}
						catch (NumberFormatException e)
						{
							priority = 5;
							IdeLog.logError(CorePlugin.getDefault(), e);
						}
						lst.add(new PriorityAndListener(listener, priority));
					}
				}
				catch (Exception e)
				{
					IdeLog.logError(CorePlugin.getDefault(), e);
				}
			}

			// Must sort based on priority here.
			Collections.sort(lst);

			for (PriorityAndListener l : lst)
			{
				if (before)
				{
					l.listener.beforeLaunch(launch);
				}
				else
				{
					l.listener.launchTerminated(launch);
				}
			}
		}
	}

}
