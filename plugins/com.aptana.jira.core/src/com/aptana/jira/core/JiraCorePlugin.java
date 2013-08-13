/**
 * Aptana Studio
 * Copyright (c) 2012-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class JiraCorePlugin extends Plugin
{

	public static final String PLUGIN_ID = "com.aptana.jira.core"; //$NON-NLS-1$

	private static JiraCorePlugin plugin;
	private JiraManager fManager;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static JiraCorePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * The constructor
	 */
	public JiraCorePlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		fManager = null;
		plugin = null;
		super.stop(context);
	}

	public synchronized JiraManager getJiraManager()
	{
		if (fManager == null)
		{
			fManager = new JiraManager();
		}
		return fManager;
	}
}
