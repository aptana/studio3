/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.aptana.js.core.node.INodeJSService;
import com.aptana.js.core.node.INodePackageManager;
import com.aptana.js.internal.core.index.JSMetadataLoader;
import com.aptana.js.internal.core.node.NodeJSService;
import com.aptana.js.internal.core.node.NodePackageManager;

/**
 * @author cwilliams
 */
public class JSCorePlugin extends Plugin
{
	public static final String PLUGIN_ID = "com.aptana.js.core"; //$NON-NLS-1$

	private static JSCorePlugin PLUGIN;

	private INodeJSService fNodeService;
	private INodePackageManager fNpm;

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

		new JSMetadataLoader().schedule();
	}

	public void stop(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		try
		{
			fNodeService = null;
			fNpm = null;
		}
		finally
		{
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

	public synchronized INodePackageManager getNodePackageManager()
	{
		if (fNpm == null)
		{
			fNpm = new NodePackageManager();
		}
		return fNpm;
	}
}
