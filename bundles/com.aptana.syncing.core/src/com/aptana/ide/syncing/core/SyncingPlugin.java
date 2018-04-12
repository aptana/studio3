/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core;

import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class SyncingPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.syncing.core"; //$NON-NLS-1$

	// The shared instance
	private static SyncingPlugin plugin;

	/**
	 * The constructor
	 */
	public SyncingPlugin()
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

		Job job = new Job("Initializing Syncing plugin") //$NON-NLS-1$
		{
			protected IStatus run(IProgressMonitor monitor)
			{

				try
				{
					ISavedState lastState = ResourcesPlugin.getWorkspace().addSaveParticipant(PLUGIN_ID,
							new WorkspaceSaveParticipant());
					if (lastState != null)
					{
						IPath location = lastState.lookup(new Path(SiteConnectionManager.STATE_FILENAME));
						if (location != null)
						{
							SiteConnectionManager.getInstance().loadState(getStateLocation().append(location));
						}
						location = lastState.lookup(new Path(DefaultSiteConnection.STATE_FILENAME));
						if (location != null)
						{
							DefaultSiteConnection.getInstance().loadState(getStateLocation().append(location));
						}
					}
				}
				catch (IllegalStateException e)
				{
					IdeLog.logError(getDefault(), e);
				}
				catch (CoreException e)
				{
					IdeLog.logError(getDefault(), e);
				}

				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule(3000);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		ResourcesPlugin.getWorkspace().removeSaveParticipant(PLUGIN_ID);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static SyncingPlugin getDefault()
	{
		return plugin;
	}

	/**
	 * Returns the site Connection Manager instance
	 * 
	 * @return
	 */
	public static ISiteConnectionManager getSiteConnectionManager()
	{
		return SiteConnectionManager.getInstance();
	}

	private class WorkspaceSaveParticipant implements ISaveParticipant
	{

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#prepareToSave(org.eclipse.core.resources.ISaveContext)
		 */
		public void prepareToSave(ISaveContext context) throws CoreException
		{
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#saving(org.eclipse.core.resources.ISaveContext)
		 */
		public void saving(ISaveContext context) throws CoreException
		{
			int saveNum = context.getSaveNumber();
			IPath savePath = new Path(SiteConnectionManager.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			SiteConnectionManager.getInstance().saveState(getStateLocation().append(savePath));
			context.map(new Path(SiteConnectionManager.STATE_FILENAME), savePath);

			savePath = new Path(DefaultSiteConnection.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			DefaultSiteConnection.getInstance().saveState(getStateLocation().append(savePath));
			context.map(new Path(DefaultSiteConnection.STATE_FILENAME), savePath);

			context.needSaveNumber();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#doneSaving(org.eclipse.core.resources.ISaveContext)
		 */
		public void doneSaving(ISaveContext context)
		{
			int prevNum = context.getPreviousSaveNumber();
			IPath prevSavePath = new Path(SiteConnectionManager.STATE_FILENAME).addFileExtension(Integer
					.toString(prevNum));
			getStateLocation().append(prevSavePath).toFile().delete();

			prevSavePath = new Path(DefaultSiteConnection.STATE_FILENAME).addFileExtension(Integer.toString(prevNum));
			getStateLocation().append(prevSavePath).toFile().delete();
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.core.resources.ISaveParticipant#rollback(org.eclipse.core.resources.ISaveContext)
		 */
		public void rollback(ISaveContext context)
		{
			int saveNum = context.getSaveNumber();
			IPath savePath = new Path(SiteConnectionManager.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			getStateLocation().append(savePath).toFile().delete();

			savePath = new Path(DefaultSiteConnection.STATE_FILENAME).addFileExtension(Integer.toString(saveNum));
			getStateLocation().append(savePath).toFile().delete();
		}
	}
}
