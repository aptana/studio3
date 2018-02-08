/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable declaredExceptions
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable staticFieldNamingConvention

package com.aptana.webserver.core;

import org.eclipse.core.internal.resources.DelayedSnapshotJob;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.util.EclipseUtil;
import com.aptana.webserver.internal.core.ServerManager;
import com.aptana.webserver.internal.core.builtin.LocalWebServer;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("restriction")
public class WebServerCorePlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.webserver.core"; //$NON-NLS-1$

	// The shared instance
	private static WebServerCorePlugin plugin;

	private ServerManager serverManager;
	private LocalWebServer defaultWebServer;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;

		Job job = new Job("Restoring saved state of servers") //$NON-NLS-1$
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(PLUGIN_ID,
							new WorkspaceSaveParticipant());
					if (lastState != null)
					{
						IPath location = lastState.lookup(new Path(ServerManager.STATE_FILENAME));
						if (location != null)
						{
							((ServerManager) getServerManager()).loadState(getStateLocation().append(location));
						}
					}
					return Status.OK_STATUS;
				}
				catch (IllegalStateException e)
				{
					return new Status(IStatus.ERROR, WebServerCorePlugin.PLUGIN_ID, e.getMessage(), e);
				}
				catch (CoreException e)
				{
					return e.getStatus();
				}
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			ResourcesPlugin.getWorkspace().removeSaveParticipant(PLUGIN_ID);

			serverManager = null;
			if (defaultWebServer != null)
			{
				defaultWebServer.stop(true, new NullProgressMonitor());
				defaultWebServer = null;
			}
		}
		finally
		{
			plugin = null;
			super.stop(context);
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static WebServerCorePlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Get instance of server configuration manager
	 * 
	 * @return
	 */
	public synchronized IServerManager getServerManager()
	{
		if (serverManager == null)
		{
			serverManager = new ServerManager();
		}
		return serverManager;
	}

	/**
	 * Save state of server configurations
	 */
	public void saveServerConfigurations()
	{
		new DelayedSnapshotJob(((Workspace) ResourcesPlugin.getWorkspace()).getSaveManager()).schedule();
	}

	public IServer getBuiltinWebServer()
	{
		ensureDefaultWebServer();
		return defaultWebServer;
	}

	private synchronized void ensureDefaultWebServer()
	{
		if (defaultWebServer == null)
		{
			defaultWebServer = new LocalWebServer(EFSUtils.getFileStore(ResourcesPlugin.getWorkspace().getRoot())
					.toURI());
		}
	}

	private class WorkspaceSaveParticipant implements ISaveParticipant
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse .core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context)
		{
			IPath prevSavePath = new Path(ServerManager.STATE_FILENAME).addFileExtension(Integer.toString(context
					.getPreviousSaveNumber()));
			getStateLocation().append(prevSavePath).toFile().delete();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse .core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException
		{
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse. core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context)
		{
			IPath savePath = new Path(ServerManager.STATE_FILENAME).addFileExtension(Integer.toString(context
					.getSaveNumber()));
			getStateLocation().append(savePath).toFile().delete();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core .resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException
		{
			IPath savePath = new Path(ServerManager.STATE_FILENAME).addFileExtension(Integer.toString(context
					.getSaveNumber()));
			((ServerManager) getServerManager()).saveState(getStateLocation().append(savePath));
			context.map(new Path(ServerManager.STATE_FILENAME), savePath);
			context.needSaveNumber();
		}
	}
}
