package com.aptana.core;

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
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.aptana.core.resources.FileDeltaRefreshAdapter;
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
		Job job = new Job("Hooking filewatchers to projects") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				addProjectResourceListener();
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		try
		{
			removeProjectResourceListener();
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
		log(new Status(IStatus.INFO, PLUGIN_ID, IStatus.OK, msg, null));
	}

	public static void log(String msg, Throwable e)
	{
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, e));
	}

	public static void log(IStatus status)
	{
		getDefault().getLog().log(status);
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
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, string));
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

	private void removeProjectResourceListener()
	{
		if (fProjectsListener != null)
		{
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fProjectsListener);
			fProjectsListener.dispose();
			fProjectsListener = null;
		}
	}

	private void addProjectResourceListener()
	{
		fProjectsListener = new ResourceListener();
		// TODO Maybe hook to pre-close/pre-delete for unhooking listeners to projects?
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fProjectsListener, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Listens for Project addition/removal/open/close to hook and unhook filewatchers which listen for external changes
	 * to the projects which we then refresh in the workspace to keep in synch.
	 * 
	 * @author cwilliams
	 */
	private class ResourceListener implements IResourceChangeListener
	{

		private Map<IProject, Integer> fWatchers;

		ResourceListener()
		{
			// We also want to iterate over all the existing open projects and hook file watchers on them!
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (IProject project : projects)
			{
				if (project.isOpen())
				{
					hookFilewatcher(project);
				}
			}
		}

		public synchronized void dispose()
		{
			for (IProject project : new HashSet<IProject>(fWatchers.keySet()))
			{
				unhookFilewatcher(project);
			}
			fWatchers = null;
		}

		protected synchronized void hookFilewatcher(IProject newProject)
		{
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

					@Override
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
	}

}
