/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.scripting.internal.model.BundleMonitor;
import com.aptana.scripting.listeners.FileWatcherRegistrant;
import com.aptana.scripting.model.BundleManager;
import com.aptana.scripting.model.RunType;

import net.contentobjects.jnotify.JNotifyException;

/**
 * The activator class controls the plug-in life cycle
 */
public class ScriptingActivator extends Plugin
{
	public static final String PLUGIN_ID = "com.aptana.scripting"; //$NON-NLS-1$
	private static ScriptingActivator plugin;

	/**
	 * Context id set by workbench part to indicate it's an Aptana Editor and make it aware to any generic command.
	 */
	public static final String EDITOR_CONTEXT_ID = "com.aptana.editor.context"; //$NON-NLS-1$

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ScriptingActivator getDefault()
	{
		return plugin;
	}

	/**
	 * This returns the default run type to be used by ScriptingEngine and CommandElement.
	 * 
	 * @return
	 */
	public static RunType getDefaultRunType()
	{
		return RunType.CURRENT_THREAD;
	}

	private BundleManager bundleManager;
	private BundleMonitor bundleMonitor;

	/**
	 * The constructor
	 */
	public ScriptingActivator()
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

		Job startupJob = new Job("Start Ruble bundle manager") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				BundleManager manager = getBundleManager();

				// TODO: Make this an extension point so plugins can contribute these
				// grabbing instances register listeners
				FileWatcherRegistrant.getInstance();

				// load all existing bundles automatically, if we're not running
				// unit tests
				if (EclipseUtil.isTesting())
				{
					System.out.println("Not auto-loading bundles since we are running unit tests"); //$NON-NLS-1$
				}
				else
				{
					manager.loadBundles();
				}

				// turn on project and file monitoring
				try
				{
					getBundleMonitor().beginMonitoring();
				}
				catch (JNotifyException e)
				{
					IdeLog.logError(ScriptingActivator.getDefault(),
							Messages.EarlyStartup_Error_Initializing_File_Monitoring, e);
				}

				return Status.OK_STATUS;
			}
		};

		startupJob.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			bundleManager = null;
			if (bundleMonitor != null)
			{
				bundleMonitor.endMonitoring();
				bundleMonitor = null;
			}

			FileWatcherRegistrant.shutdown();
		}
		catch (Exception e)
		{
			// ignore
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}

	private synchronized BundleMonitor getBundleMonitor()
	{
		if (bundleMonitor == null)
		{
			bundleMonitor = new BundleMonitor(getBundleManager());
		}
		return bundleMonitor;
	}

	public synchronized BundleManager getBundleManager()
	{
		if (bundleManager == null)
		{
			bundleManager = BundleManager.getInstance();
		}
		return bundleManager;
	}
}
