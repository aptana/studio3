/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.buildpath.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.aptana.core.build.IBuildParticipantManager;
import com.aptana.core.internal.build.BuildParticipantManager;

public class BuildPathCorePlugin extends Plugin
{
	private static BuildPathCorePlugin plugin;

	public static final String PLUGIN_ID = "com.aptana.buildpath.core"; //$NON-NLS-1$

	private IBuildParticipantManager fBuildParticipantManager;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static BuildPathCorePlugin getDefault()
	{
		return plugin;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		super.start(bundleContext);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception
	{
		fBuildParticipantManager = null;
		plugin = null;
		super.stop(bundleContext);
	}

	public synchronized IBuildParticipantManager getBuildParticipantManager()
	{
		if (fBuildParticipantManager == null)
		{
			fBuildParticipantManager = new BuildParticipantManager();
		}
		return fBuildParticipantManager;
	}
}
