/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;

import com.aptana.core.IFilter;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.inferencing.IAliasResolver;

/**
 * Delegates to registered extension {@link IAliasResolver}s. First one to return a non-null value wins. Ordered by
 * priority.
 */
public class AliasResolverFactory implements IAliasResolver
{
	private final String EXTENSION_POINT = "aliasResolver";//$NON-NLS-1$
	private final String ELEMENT_CONTRIBUTOR = "resolver";//$NON-NLS-1$
	private final String ELEMENT_PRIORITY = "priority";//$NON-NLS-1$
	private final String ELEMENT_CLASS = "class";//$NON-NLS-1$

	private List<ResolverProxy> fProxies = null;

	public AliasResolverFactory()
	{
	}

	/**
	 * Lazily load resolvers
	 * 
	 * @return
	 */
	private synchronized List<ResolverProxy> getResolverProxies()
	{
		if (fProxies == null)
		{
			fProxies = new ArrayList<ResolverProxy>();
			EclipseUtil.processConfigurationElements(JSCorePlugin.PLUGIN_ID, EXTENSION_POINT,
					new IConfigurationElementProcessor()
					{

						public void processElement(IConfigurationElement element)
						{
							fProxies.add(new ResolverProxy(element));
						}

						public Set<String> getSupportElementNames()
						{
							return CollectionsUtil.newSet(ELEMENT_CONTRIBUTOR);
						}
					});

			Collections.sort(fProxies);
		}
		return fProxies;
	}

	/**
	 * This hides the various implementation of alias resolvers. We consult our extension point and find all resolvers
	 * that may apply to this type. We then loop through them in order of priority and the first implementation to
	 * return a non-null value wins (and that value is returned).
	 * 
	 * @param sourceType
	 * @return
	 */
	public String resolve(final String sourceType, final IPath editorPath, final IPath projectPath)
	{
		List<ResolverProxy> proxies = CollectionsUtil.filter(getResolverProxies(), new IFilter<ResolverProxy>()
		{
			public boolean include(ResolverProxy item)
			{
				return item != null;
			}
		});

		// Go through all resolvers that apply
		// First one to give us a non-null result wins!
		for (ResolverProxy resolverProxy : proxies)
		{
			// Get the resolver from the proxy
			try
			{
				IAliasResolver resolver = resolverProxy.getResolver();
				String destinationType = resolver.resolve(sourceType, editorPath, projectPath);
				if (destinationType != null)
				{
					return destinationType;
				}
			}
			catch (CoreException e)
			{
				IdeLog.logWarning(JSCorePlugin.getDefault(), e);
				continue;
			}
			catch (Exception e)
			{
				IdeLog.logWarning(JSCorePlugin.getDefault(), e);
			}

		}
		return sourceType;
	}

	private class ResolverProxy implements Comparable<ResolverProxy>
	{
		private IConfigurationElement ice;
		private int priority;
		private IAliasResolver resolver;

		ResolverProxy(IConfigurationElement ice)
		{
			this.ice = ice;
			priority = -1;

		}

		IAliasResolver getResolver() throws CoreException
		{
			if (resolver == null)
			{
				resolver = (IAliasResolver) ice.createExecutableExtension(ELEMENT_CLASS);
			}
			return resolver;
		}

		synchronized int getPriority()
		{
			if (priority == -1)
			{
				try
				{
					String pri = ice.getAttribute(ELEMENT_PRIORITY);
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
}
