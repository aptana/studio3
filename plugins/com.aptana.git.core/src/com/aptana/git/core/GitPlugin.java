package com.aptana.git.core;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.history.IFileRevision;
import org.osgi.framework.BundleContext;

import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.internal.core.storage.CommitFileRevision;

/**
 * The activator class controls the plug-in life cycle
 */
public class GitPlugin extends Plugin
{

	// The plug-in ID
	private static final String PLUGIN_ID = "com.aptana.git";

	// The shared instance
	private static GitPlugin plugin;

	private com.aptana.git.core.GitProjectRefresher fRepoListener;

	/**
	 * The constructor
	 */
	public GitPlugin()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
		// Add a resource listener that triggers git repo index refreshes!
		Job job = new Job("Add Git Index Resource listener")
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				ResourcesPlugin.getWorkspace().addResourceChangeListener(new GitResourceListener(),
						IResourceChangeEvent.POST_CHANGE);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
		fRepoListener = new GitProjectRefresher();
		GitRepository.addListener(fRepoListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		GitRepository.removeListener(fRepoListener);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static GitPlugin getDefault()
	{
		return plugin;
	}

	public static String getPluginId()
	{
		return PLUGIN_ID;
	}

	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginId(), msg, e));
	}

	public static void logInfo(String string)
	{
		getDefault().getLog().log(new Status(IStatus.INFO, getPluginId(), string));
	}

	public static void trace(String string)
	{
		getDefault().getLog().log(new Status(IStatus.OK, getPluginId(), string));
	}

	/**
	 * FIXME This doesn't seem like the best place to stick this.
	 * 
	 * @param commit
	 * @param fileName
	 * @return
	 */
	public static IFileRevision revisionForCommit(GitCommit commit, String fileName)
	{
		return new CommitFileRevision(commit, fileName);
	}
}
