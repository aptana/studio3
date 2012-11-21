/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.aptana.js.internal.core.index.JSMetadataLoader;

/**
 * @author cwilliams
 */
public class JSCorePlugin extends Plugin
{
	public static final String PLUGIN_ID = "com.aptana.js.core"; //$NON-NLS-1$

	private static JSCorePlugin PLUGIN;

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

		Job job = new JSMetadataLoader();
		job.schedule();
	}

	public void stop(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		PLUGIN = null;
		super.stop(context);
	}
}
