/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.build.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.aptana.build.ui.internal.preferences.BuildParticipantPreferenceCompositeFactory;
import com.aptana.build.ui.preferences.IBuildParticipantPreferenceCompositeFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class BuildUIPlugin extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.build.ui"; //$NON-NLS-1$

	// The shared instance
	private static BuildUIPlugin plugin;

	/**
	 * The constructor
	 */
	public BuildUIPlugin()
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
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static BuildUIPlugin getDefault()
	{
		return plugin;
	}

	public IBuildParticipantPreferenceCompositeFactory getBuildParticipantPreferenceCompositeFactory()
	{
		return new BuildParticipantPreferenceCompositeFactory();
	}
}
