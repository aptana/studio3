/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.IFilter;
import com.aptana.core.IMap;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.js.core.JSCorePlugin;

class RequireResolverFactory
{

	private static List<IRequireResolver> fgProxies;

	/**
	 * Lazily load resolvers
	 * 
	 * @return
	 */
	private static synchronized List<IRequireResolver> getResolvers()
	{
		if (fgProxies == null)
		{
			final ArrayList<ResolverProxy> proxies = new ArrayList<ResolverProxy>();
			EclipseUtil.processConfigurationElements(JSCorePlugin.PLUGIN_ID, "requireResolvers", //$NON-NLS-1$
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							proxies.add(new ResolverProxy(element));
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet("resolver"); //$NON-NLS-1$
						}
					});
			Collections.sort(proxies);
			fgProxies = CollectionsUtil.map(proxies, new IMap<ResolverProxy, IRequireResolver>()
			{

				public IRequireResolver map(ResolverProxy item)
				{
					try
					{
						return item.getResolver();
					}
					catch (CoreException e)
					{
						IdeLog.logError(JSCorePlugin.getDefault(), e);
						return null;
					}
				}
			});
		}
		return fgProxies;
	}

	private static class ResolverProxy implements Comparable<ResolverProxy>
	{
		private IConfigurationElement ice;
		private int priority;

		ResolverProxy(IConfigurationElement ice)
		{
			this.ice = ice;
			priority = -1;
		}

		IRequireResolver getResolver() throws CoreException
		{
			return (IRequireResolver) ice.createExecutableExtension("class"); //$NON-NLS-1$
		}

		synchronized int getPriority()
		{
			if (priority == -1)
			{
				try
				{
					String pri = ice.getAttribute("priority"); //$NON-NLS-1$
					this.priority = Integer.parseInt(pri);
				}
				catch (Exception e)
				{
					this.priority = 50;
				}
			}
			return priority;
		}

		public int compareTo(ResolverProxy o)
		{
			int thisVal = getPriority();
			int anotherVal = o.getPriority();
			return anotherVal - thisVal;
		}
	}

	/**
	 * This hides the various implementation of require resolvers. We consult our extension point and find all resolvers
	 * that may apply to the current project and location. We then loop through them from highest priority to lowest,
	 * and the first implementation to return a non-null value wins (and that value is returned).
	 * 
	 * @param moduleId
	 *            The id of the module we're looking up (resolving to the file that defines it).
	 * @param project
	 *            The current project.
	 * @param currentDirectory
	 *            The current directory we're starting in (the parent of the file we're working on).
	 * @param indexRoot
	 *            The root location of the index we're currently operating on.
	 * @return The {@link IPath} pointing at the module's main file.
	 */
	public static IPath resolve(final String moduleId, final IProject project, final IPath currentDirectory,
			final IPath indexRoot)
	{
		List<IRequireResolver> resolvers = CollectionsUtil.filter(getResolvers(), new IFilter<IRequireResolver>()
		{
			public boolean include(IRequireResolver item)
			{
				// TODO Use expressions rather than calling applies?
				// We can generate an evaluation context that holds a set of values for current location, project,
				// current index, index root, module id, etc.
				return item != null && item.applies(project, currentDirectory, indexRoot);
			}
		});

		// Go through all resolvers that apply, from highest to lowest priority.
		// First one to give us a non-null result wins!
		for (IRequireResolver resolver : resolvers)
		{
			IPath path = resolver.resolve(moduleId, project, currentDirectory, indexRoot);
			if (path != null)
			{
				return path;
			}
		}
		return null;
	}
}
