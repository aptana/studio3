/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.osgi.framework.BundleContext;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.FileDeltaRefreshAdapter;
import com.aptana.core.resources.RefreshThread;
import com.aptana.core.util.EclipseUtil;
import com.aptana.filewatcher.FileWatcher;
import com.eaio.uuid.MACAddress;

/**
 * The activator class controls the plug-in life cycle
 */
public class CorePlugin extends Plugin implements IPreferenceChangeListener
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.core"; //$NON-NLS-1$

	// The machine id
	private static final String MID_SEPARATOR = "-"; //$NON-NLS-1$
	private static String mid;

	// The shared instance
	private static CorePlugin plugin;

	private ResourceListener fProjectsListener;

	private Job addFilewatcherJob;

	private BundleContext context;

	/**
	 * The constructor
	 */
	public CorePlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		this.context = context;
		super.start(context);

		plugin = this;
		initializeMID();

		// Perhaps don't enable this if platform is already in -debug mode?
		//
		// Place after context & plugin assignments, as this relies on both existing already
		enableDebugging();

		addFilewatcherJob = new Job(Messages.CorePlugin_Hooking_Filewatchers)
		{
			protected IStatus run(IProgressMonitor monitor)
			{
				addProjectListeners();
				return Status.OK_STATUS;
			}
		};
		addFilewatcherJob.setSystem(!EclipseUtil.showSystemJobs());
		addFilewatcherJob.setPriority(Job.LONG);
		addFilewatcherJob.schedule(250);

		IdeLog.flushCache();
	}

	/**
	 * Enable the debugging options
	 */
	private void enableDebugging()
	{
		EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID).addPreferenceChangeListener(this);

		/**
		 * Returns the current severity preference
		 * 
		 * @return
		 */
		IdeLog.StatusLevel currentSeverity = IdeLog.getSeverityPreference();
		IdeLog.setCurrentSeverity(currentSeverity);

		// If we are currently in debug mode, don't change the default settings
		if (!Platform.inDebugMode())
		{
			Boolean checked = Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
					ICorePreferenceConstants.PREF_ENABLE_COMPONENT_DEBUGGING, false, null);
			EclipseUtil.setPlatformDebugging(checked);
			if (checked)
			{
				String[] components = EclipseUtil.getCurrentDebuggableComponents();
				EclipseUtil.setBundleDebugOptions(components, true);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			// Don't listen to auto-refresh pref changes anymore
			EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID).removePreferenceChangeListener(this);

			if (addFilewatcherJob != null)
			{
				addFilewatcherJob.cancel();
				addFilewatcherJob = null;
			}
			removeProjectListeners();
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
	public static CorePlugin getDefault()
	{
		return plugin;
	}

	private void removeProjectListeners()
	{
		if (fProjectsListener != null)
		{
			fProjectsListener.dispose();
			fProjectsListener = null;
		}
	}

	private void addProjectListeners()
	{
		fProjectsListener = new ResourceListener();
		fProjectsListener.start();
	}

	public static String getAptanaStudioVersion()
	{
		String version = EclipseUtil.getPluginVersion(EclipseUtil.STANDALONE_PLUGIN_ID);
		if (version == null)
		{
			version = EclipseUtil.getPluginVersion(PLUGIN_ID);
		}
		if (version == null)
		{
			version = EclipseUtil.getProductVersion();
		}
		return version;
	}

	/**
	 * Listens for Project addition/removal/open/close to hook and unhook filewatchers which listen for external changes
	 * to the projects which we then refresh in the workspace to keep in synch.
	 * 
	 * @author cwilliams
	 */
	private static class ResourceListener implements IResourceChangeListener, IPreferenceChangeListener
	{

		private Map<IProject, Integer> fWatchers;
		private RefreshThread fRefreshThread;
		private FileDeltaRefreshAdapter fAdapter;
		private boolean hooked;

		ResourceListener()
		{
			fRefreshThread = new RefreshThread();
			fAdapter = new FileDeltaRefreshAdapter(fRefreshThread);
			EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID).addPreferenceChangeListener(this);
		}

		public void start()
		{
			fRefreshThread.start();
			if (autoHookFileWatcher())
			{
				hookAll();
			}
		}

		/**
		 * Hook a filewatcher to every open project, and add a resource listener to handle projects getting
		 * added/opened/closed.
		 */
		private void hookAll()
		{
			ResourcesPlugin.getWorkspace()
					.addResourceChangeListener(
							this,
							IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_DELETE
									| IResourceChangeEvent.PRE_CLOSE);

			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects)
			{
				if (project.isAccessible())
				{
					hookFilewatcher(project);
				}
			}
			hooked = true;
		}

		private boolean autoHookFileWatcher()
		{
			return Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
					ICorePreferenceConstants.PREF_AUTO_REFRESH_PROJECTS, true, null);
		}

		public synchronized void dispose()
		{
			fRefreshThread.terminate();
			// Don't listen to auto-refresh pref changes anymore
			EclipseUtil.instanceScope().getNode(CorePlugin.PLUGIN_ID).removePreferenceChangeListener(this);
			// Now remove all the existing file watchers
			unhookAll();
		}

		private void unhookAll()
		{
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
			if (fWatchers != null)
			{
				for (IProject project : new HashSet<IProject>(fWatchers.keySet()))
				{
					unhookFilewatcher(project);
				}
				fWatchers = null;
			}
			hooked = false;
		}

		protected synchronized void hookFilewatcher(IProject newProject)
		{
			if (!autoHookFileWatcher())
			{
				return;
			}
			try
			{
				if (newProject != null && newProject.exists() && newProject.getLocation() != null
						&& (fWatchers == null || !fWatchers.containsKey(newProject)))
				{
					int watcher = FileWatcher.addWatch(newProject.getLocation().toOSString(), IJNotify.FILE_ANY, true,
							fAdapter);
					if (fWatchers == null)
					{
						fWatchers = new HashMap<IProject, Integer>();
					}
					fWatchers.put(newProject, watcher);
				}
			}
			catch (JNotifyException e)
			{
				IdeLog.logError(getDefault(), e);
			}
		}

		protected synchronized void unhookFilewatcher(IProject project)
		{
			if (project == null || fWatchers == null)
			{
				return;
			}
			try
			{
				Integer watcher = fWatchers.remove(project);
				if (watcher != null)
				{
					FileWatcher.removeWatch(watcher);
				}
			}
			catch (JNotifyException e)
			{
				IdeLog.logError(CorePlugin.getDefault(), e);
			}
		}

		public void resourceChanged(IResourceChangeEvent event)
		{
			if (IResourceChangeEvent.PRE_DELETE == event.getType() || IResourceChangeEvent.PRE_CLOSE == event.getType())
			{
				IResource project = event.getResource();
				unhookFilewatcher(project.getProject());
				return;
			}
			IResourceDelta delta = event.getDelta();
			if (delta == null)
			{
				return;
			}
			try
			{
				delta.accept(new IResourceDeltaVisitor()
				{

					public boolean visit(IResourceDelta delta) throws CoreException
					{
						final IResource resource = delta.getResource();
						if (resource.getType() == IResource.FILE || resource.getType() == IResource.FOLDER)
						{
							return false;
						}
						if (resource.getType() == IResource.ROOT)
						{
							return true;
						}
						if (resource.getType() == IResource.PROJECT)
						{
							// a project was added or opened
							if (delta.getKind() == IResourceDelta.ADDED
									|| (delta.getKind() == IResourceDelta.CHANGED
											&& (delta.getFlags() & IResourceDelta.OPEN) != 0 && resource.isAccessible()))
							{
								Job job = new Job("Hooking file watcher to new project...") //$NON-NLS-1$
								{
									protected IStatus run(IProgressMonitor monitor)
									{
										hookFilewatcher(resource.getProject());
										fRefreshThread.refresh(resource.getProject(), IResource.DEPTH_INFINITE);
										return Status.OK_STATUS;
									};
								};
								job.setSystem(EclipseUtil.showSystemJobs());
								job.schedule(1000);
							}
						}
						return false;
					}
				});
			}
			catch (CoreException e)
			{
				IStatus status = new Status(e.getStatus().getSeverity(), CorePlugin.PLUGIN_ID, e.getLocalizedMessage());
				IdeLog.log(getDefault(), status);
			}
		}

		public void preferenceChange(PreferenceChangeEvent event)
		{
			// This might be instance or default that changed. So what do we do?
			if (ICorePreferenceConstants.PREF_AUTO_REFRESH_PROJECTS.equals(event.getKey()))
			{
				// we we're already hooked and now we're not supposed to, unhook
				if (hooked && !autoHookFileWatcher())
				{
					unhookAll();
				}
				// if we're not already hooked and now we're supposed to, hook
				else if (!hooked && autoHookFileWatcher())
				{
					hookAll();
				}
			}
		}
	}

	/**
	 * Returns the current bundle context
	 * 
	 * @return
	 */
	public BundleContext getContext()
	{
		return context;
	}

	/**
	 * Respond to a preference change event
	 */
	public void preferenceChange(PreferenceChangeEvent event)
	{
		if (ICorePreferenceConstants.PREF_DEBUG_LEVEL.equals(event.getKey()))
		{
			IdeLog.setCurrentSeverity(IdeLog.getSeverityPreference());
		}
	}

	public static String getMID()
	{
		return mid;
	}

	private void initializeMID()
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
			byte[] result = md.digest(MACAddress.getMACAddress().getBytes("UTF-8")); //$NON-NLS-1$
			Formatter formatter = new Formatter();
			for (byte b : result)
			{
				formatter.format("%02x", b); //$NON-NLS-1$
			}
			mid = formatter.toString();
			// puts mid in 8-4-4-4-12 format
			mid = mid.substring(0, 8) + MID_SEPARATOR + mid.substring(8, 12) + MID_SEPARATOR + mid.substring(12, 16)
					+ MID_SEPARATOR + mid.substring(16, 20) + MID_SEPARATOR + mid.substring(20, 32);
		}
		catch (NoSuchAlgorithmException e)
		{
			IdeLog.logError(getDefault(), Messages.CorePlugin_MD5_generation_error, e);
		}
		catch (UnsupportedEncodingException e)
		{
			IdeLog.logError(getDefault(), e);
		}
	}
}
