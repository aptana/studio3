/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.aptana.js.core.inferencing.IAliasResolver;
import com.aptana.js.core.node.INodeJS;
import com.aptana.js.core.node.INodeJSService;
import com.aptana.js.core.node.INodePackageManager;
import com.aptana.js.internal.core.index.JSMetadataLoader;
import com.aptana.js.internal.core.inferencing.AliasResolverFactory;
import com.aptana.js.internal.core.node.NodeJSService;

/**
 * @author cwilliams
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JSCorePlugin extends Plugin
{
	public static final String PLUGIN_ID = "com.aptana.js.core"; //$NON-NLS-1$

	private static JSCorePlugin PLUGIN;

	private INodeJSService fNodeService;

	private ServiceTracker proxyTracker;

	private AliasResolverFactory fAliasResolverFactory;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JSCorePlugin getDefault()
	{
		return PLUGIN;
	}

	public void start(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		super.start(context);
		PLUGIN = this;

		// Load JS Metadata in background
		new JSMetadataLoader().schedule();

		// Hook up tracker to proxy service
		proxyTracker = new ServiceTracker(getBundle().getBundleContext(), IProxyService.class.getName(), null);
		proxyTracker.open();
	}

	public void stop(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		try
		{
			if (proxyTracker != null)
			{
				proxyTracker.close();
			}
		}
		finally
		{
			proxyTracker = null;
			fNodeService = null;
			fAliasResolverFactory = null;
			PLUGIN = null;
			super.stop(context);
		}
	}

	public synchronized INodeJSService getNodeJSService()
	{
		if (fNodeService == null)
		{
			fNodeService = new NodeJSService();
		}
		return fNodeService;
	}

	public synchronized IAliasResolver getAliasResolver()
	{
		if (fAliasResolverFactory == null)
		{
			fAliasResolverFactory = new AliasResolverFactory();
		}
		return fAliasResolverFactory;
	}

	/**
	 * Returns the NPM instance tied to the Node instance user has set up (or we detected if they didn't).
	 * 
	 * @return
	 */
	public INodePackageManager getNodePackageManager()
	{
		INodeJS nodeJS = getNodeJSService().getValidExecutable();
		if (nodeJS == null)
		{
			return null;
		}
		return nodeJS.getNPM();
	}

	public IProxyService getProxyService()
	{
		return (IProxyService) proxyTracker.getService();
	}
}
