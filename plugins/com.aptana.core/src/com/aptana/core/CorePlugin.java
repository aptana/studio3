/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

import com.aptana.core.resources.FileDeltaRefreshAdapter;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.filewatcher.FileWatcher;

/**
 * The activator class controls the plug-in life cycle
 */
public class CorePlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.core"; //$NON-NLS-1$

	// The shared instance
	private static CorePlugin plugin;

	private ResourceListener fProjectsListener;
	private IResourceChangeListener fProjectCreationListener;

	private Job addBuilderJob;
	private Job addFilewatcherJob;

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
		super.start(context);
		plugin = this;
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

		if (inMigrationMode())
		{
			addBuilderJob = new Job(Messages.CorePlugin_Adding_Unified_Builders)
			{
				protected IStatus run(IProgressMonitor monitor)
				{
					return updateProjectNatures(ResourcesPlugin.getWorkspace().getRoot().getProjects(), monitor);
				}
			};
			addBuilderJob.setSystem(!EclipseUtil.showSystemJobs());
			addBuilderJob.setPriority(Job.LONG);
			addBuilderJob.schedule(250);
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
			if (addFilewatcherJob != null)
			{
				addFilewatcherJob.cancel();
				addFilewatcherJob = null;
			}
			if (addBuilderJob != null)
			{
				addBuilderJob.cancel();
				addBuilderJob = null;
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

	public static void log(Throwable e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getLocalizedMessage(), e));
	}

	public static void log(String msg)
	{
		// log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(String msg, Throwable e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void log(IStatus status)
	{
		if (status.getSeverity() > IStatus.INFO)
		{
			getDefault().getLog().log(status);
		}
	}

	/**
	 * logError
	 * 
	 * @param msg
	 * @param e
	 */
	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	/**
	 * logInfo
	 * 
	 * @param string
	 */
	public static void logInfo(String string)
	{
		// getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, string));
	}

	/**
	 * logWarning
	 * 
	 * @param msg
	 */
	public static void logWarning(String msg)
	{
		getDefault().getLog().log(new Status(IStatus.WARNING, PLUGIN_ID, msg));
	}

	/**
	 * trace
	 * 
	 * @param string
	 */
	public static void trace(String string)
	{
		getDefault().getLog().log(new Status(IStatus.OK, PLUGIN_ID, string));
	}

	private IStatus updateProjectNatures(IProject[] projects, IProgressMonitor monitor)
	{
		MultiStatus status = new MultiStatus(PLUGIN_ID, Status.OK, Status.OK_STATUS.getMessage(), null);
		Map<String, String> oldToNewNatures = new HashMap<String, String>();
		oldToNewNatures.put("com.aptana.ide.project.nature.web", "com.aptana.projects.webnature"); //$NON-NLS-1$ //$NON-NLS-2$
		// oldToNewNatures.put("com.aptana.ide.project.remote.nature",
		// "com.aptana.ruby.core.rubynature"); // There is no remote nature now
		oldToNewNatures.put("com.aptana.ide.editor.php.phpnature", "com.aptana.editor.php.phpNature"); //$NON-NLS-1$ //$NON-NLS-2$
		// oldToNewNatures.put("org.radrails.rails.core.railsnature",
		// "org.radrails.rails.core.railsnature"); // Same id
		oldToNewNatures.put("org.rubypeople.rdt.core.rubynature", "com.aptana.ruby.core.rubynature"); //$NON-NLS-1$ //$NON-NLS-2$
		SubMonitor sub = SubMonitor.convert(monitor, 10 * projects.length);
		for (IProject p : projects)
		{
			if (sub.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}

			try
			{
				if (!p.isAccessible())
				{
					continue;
				}
				sub.subTask(p.getName());

				// Look for Studio 1.x and 2.x project natures, attach our new natures where needed
				IProjectDescription desc = p.getDescription();
				List<String> newNatures = new ArrayList<String>();
				for (String nature : desc.getNatureIds())
				{
					String newNature = oldToNewNatures.get(nature);
					if (newNature != null)
					{
						newNatures.add(newNature);
					}
					newNatures.add(nature);
				}
				desc.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
				p.setDescription(desc, sub.newChild(5));

				// Attach builders in case nature was already on project, but before we created the builder
				String[] natureIds = desc.getNatureIds();
				for (int i = 0; i < natureIds.length; i++)
				{
					String natureId = natureIds[i];
					if (ResourceUtil.isAptanaNature(natureId))
					{
						IProjectNature nature = p.getNature(natureId);
						nature.configure();
					}
				}
				status.add(Status.OK_STATUS);
				sub.worked(5);
			}
			catch (CoreException e)
			{
				status.add(e.getStatus());
			}
		}
		sub.done();
		return status;
	}

	private void removeProjectListeners()
	{
		if (fProjectsListener != null)
		{
			fProjectsListener.dispose();
			fProjectsListener = null;
		}

		if (fProjectCreationListener != null)
		{
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fProjectCreationListener);
		}
	}

	/**
	 * Are we migrating projects from Studio 2.x to 3?
	 * 
	 * @return
	 */
	private boolean inMigrationMode()
	{
		return Platform.getPreferencesService().getBoolean(CorePlugin.PLUGIN_ID,
				ICorePreferenceConstants.PREF_AUTO_MIGRATE_OLD_PROJECTS, false, null);
	}

	private void addProjectListeners()
	{
		fProjectsListener = new ResourceListener();
		fProjectsListener.start();

		if (inMigrationMode())
		{
			fProjectCreationListener = new IResourceChangeListener()
			{
				public void resourceChanged(IResourceChangeEvent event)
				{
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
								if (resource.getType() == IResource.ROOT)
								{
									return true;
								}
								if (resource.getType() == IResource.PROJECT)
								{
									// a project was added or opened
									if (delta.getKind() == IResourceDelta.ADDED
											|| (delta.getKind() == IResourceDelta.CHANGED
													&& (delta.getFlags() & IResourceDelta.OPEN) != 0 && resource
														.isAccessible()))
									{
										addBuilderJob = new Job(Messages.CorePlugin_Adding_Unified_Builders)
										{
											protected IStatus run(IProgressMonitor monitor)
											{
												return updateProjectNatures(new IProject[] { resource.getProject() },
														monitor);
											}
										};
										addBuilderJob.setSystem(!EclipseUtil.showSystemJobs());
										addBuilderJob.setPriority(Job.LONG);
										addBuilderJob.schedule();
									}

								}
								return false;
							}
						});
					}
					catch (CoreException e)
					{
						log(e.getStatus());
					}
				}
			};

			ResourcesPlugin.getWorkspace().addResourceChangeListener(fProjectCreationListener,
					IResourceChangeEvent.POST_CHANGE);
		}
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
		private boolean hooked;

		ResourceListener()
		{
			new InstanceScope().getNode(CorePlugin.PLUGIN_ID).addPreferenceChangeListener(this);
		}

		public void start()
		{
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
			// TODO Maybe hook to pre-close/pre-delete for unhooking listeners to projects?
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);

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
			// Don't listen to auto-refresh pref changes anymore
			new InstanceScope().getNode(CorePlugin.PLUGIN_ID).removePreferenceChangeListener(this);
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
				if (newProject != null && newProject.exists() && newProject.getLocation() != null)
				{
					int watcher = FileWatcher.addWatch(newProject.getLocation().toOSString(), IJNotify.FILE_ANY, true,
							new FileDeltaRefreshAdapter());
					if (fWatchers == null)
					{
						fWatchers = new HashMap<IProject, Integer>();
					}
					fWatchers.put(newProject, watcher);
				}
			}
			catch (JNotifyException e)
			{
				logError(e.getMessage(), e);
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
				logError(e.getMessage(), e);
			}
		}

		public void resourceChanged(IResourceChangeEvent event)
		{
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
						IResource resource = delta.getResource();
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
								hookFilewatcher(resource.getProject());
							}
							// a project was removed or closed
							else if (delta.getKind() == IResourceDelta.REMOVED
									|| (delta.getKind() == IResourceDelta.CHANGED
											&& (delta.getFlags() & IResourceDelta.OPEN) != 0 && !resource
												.isAccessible()))
							{
								unhookFilewatcher(resource.getProject());
							}
						}
						return false;
					}
				});
			}
			catch (CoreException e)
			{
				log(e.getStatus());
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

}
