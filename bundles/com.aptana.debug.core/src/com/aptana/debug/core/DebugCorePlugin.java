/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.debug.core;

import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.debug.core.DebugPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.debug.core.internal.UniformResourceBreakpointChangeNotifier;
import com.aptana.debug.core.sourcelookup.RemoteSourceCacheManager;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Max Stepanov
 */
public class DebugCorePlugin extends Plugin
{
	/**
	 * ID
	 */
	public static final String PLUGIN_ID = "com.aptana.debug.core"; //$NON-NLS-1$

	// The shared instance.
	private static DebugCorePlugin plugin;

	private UniformResourceBreakpointChangeNotifier breakpointHelper;
	private RemoteSourceCacheManager remoteSourceCacheManager;
	private LogLevelFilterManager logLevelFilterManager;

	/**
	 * The constructor.
	 */
	public DebugCorePlugin()
	{
	}

	/**
	 * This method is called upon plug-in activation
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		breakpointHelper = new UniformResourceBreakpointChangeNotifier();
		DebugPlugin.getDefault().addDebugEventListener(remoteSourceCacheManager = new RemoteSourceCacheManager());
	}

	/**
	 * This method is called when the plug-in is stopped
	 * 
	 * @param context
	 * @throws Exception
	 */
	public void stop(BundleContext context) throws Exception
	{
		breakpointHelper.cleanup();
		logLevelFilterManager = null;
		DebugPlugin.getDefault().removeDebugEventListener(remoteSourceCacheManager);
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return DebugCorePlugin
	 */
	public static DebugCorePlugin getDefault()
	{
		return plugin;
	}

	public RemoteSourceCacheManager getRemoteSourceCacheManager()
	{
		return remoteSourceCacheManager;
	}

	/**
	 * Forces to open source element in default editor
	 * 
	 * @param sourceElement
	 */
	public static void openInEditor(Object sourceElement)
	{
		IEditorOpenAdapter adapter = (IEditorOpenAdapter) getDefault().getContributedAdapter(IEditorOpenAdapter.class);
		if (adapter != null)
		{
			adapter.openInEditor(sourceElement);
		}
	}

	/**
	 * Returns a {@link LogLevelFilterManager}.
	 * 
	 * @return A {@link LogLevelFilterManager}
	 */
	public synchronized LogLevelFilterManager getLogLevelFilterManager()
	{
		if (logLevelFilterManager == null)
		{
			logLevelFilterManager = new LogLevelFilterManager();
		}
		return logLevelFilterManager;
	}

	private Object getContributedAdapter(Class<?> clazz)
	{
		Object adapter = null;
		IAdapterManager manager = Platform.getAdapterManager();
		if (manager.hasAdapter(this, clazz.getName()))
		{
			adapter = manager.getAdapter(this, clazz.getName());
			if (adapter == null)
			{
				adapter = manager.loadAdapter(this, clazz.getName());
			}
		}
		return adapter;
	}
}
