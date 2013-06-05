/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable staticFieldNamingConvention
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor

package com.aptana.browser;

import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.browser.internal.BrowserConfigurationManager;
import com.aptana.browser.support.WorkbenchBrowserSupport;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Max Stepanov
 */
public class BrowserPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.browser"; //$NON-NLS-1$

	// The shared instance
	private static BrowserPlugin plugin;

	private BrowserConfigurationManager browserConfigManager;
	private WorkbenchBrowserSupport defaultWorkbenchBrowser;

	/**
	 * The constructor
	 */
	public BrowserPlugin()
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
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		if (browserConfigManager != null)
		{
			browserConfigManager.clear();
			browserConfigManager = null;
		}
		defaultWorkbenchBrowser = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static BrowserPlugin getDefault()
	{
		return plugin;
	}

	public synchronized IWorkbenchBrowserSupport getBrowserSupport()
	{
		if (defaultWorkbenchBrowser == null)
		{
			defaultWorkbenchBrowser = new WorkbenchBrowserSupport();
		}
		return defaultWorkbenchBrowser;
	}

	public synchronized BrowserConfigurationManager getBrowserConfigurationManager()
	{
		if (browserConfigManager == null)
		{
			browserConfigManager = new BrowserConfigurationManager();
		}
		return browserConfigManager;
	}
}
