/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.epl;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.formatter.IDebugScopes;

/**
 * The activator class controls the plug-in life cycle
 */
public class FormatterPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.formatter.epl"; //$NON-NLS-1$

	// The shared instance
	private static FormatterPlugin plugin;
	public static final boolean DEBUG = Boolean.valueOf(Platform.getDebugOption(IDebugScopes.DEBUG)).booleanValue();

	/**
	 * The constructor
	 */
	public FormatterPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#isDebugging()
	 */
	@Override
	public boolean isDebugging()
	{
		if (DEBUG)
			return true;

		return super.isDebugging();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static FormatterPlugin getDefault()
	{
		return plugin;
	}
}
