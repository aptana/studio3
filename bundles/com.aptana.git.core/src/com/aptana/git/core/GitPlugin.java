/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.io.File;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.team.core.history.IFileRevision;
import org.osgi.framework.BundleContext;

import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepositoryManager;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.internal.core.github.GithubManager;
import com.aptana.git.internal.core.storage.CommitFileRevision;

/**
 * The activator class controls the plug-in life cycle
 */
public class GitPlugin extends Plugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.git.core"; //$NON-NLS-1$

	// The shared instance
	private static GitPlugin plugin;

	private GitProjectRefresher fRepoListener;
	private IResourceChangeListener fGitResourceListener;

	private GitRepositoryManager fGitRepoManager;

	private GithubManager fGithubManager;

	/**
	 * The constructor
	 */
	public GitPlugin() // $codepro.audit.disable
						// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.enforceTheSingletonPropertyWithAPrivateConstructor
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		super.start(context);
		plugin = this;
		// Add a resource listener that triggers git repo index refreshes!
		Job job = new Job("Add Git Index Resource listener") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				fGitResourceListener = new GitResourceListener();
				ResourcesPlugin.getWorkspace().addResourceChangeListener(fGitResourceListener,
						IResourceChangeEvent.POST_CHANGE);
				fRepoListener = new GitProjectRefresher();
				getGitRepositoryManager().addListener(fRepoListener);
				getGitRepositoryManager().addListenerToEachRepository(fRepoListener);
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
		job.schedule();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception // $codepro.audit.disable declaredExceptions
	{
		try
		{
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fGitResourceListener);
			getGitRepositoryManager().removeListener(fRepoListener);
			getGitRepositoryManager().removeListenerFromEachRepository(fRepoListener);
			// Remove all the GitRepositories from memory!
			if (fGitRepoManager != null)
			{
				fGitRepoManager.cleanup();
			}
		}
		finally
		{
			fGitRepoManager = null;
			fGithubManager = null;
			plugin = null;
			super.stop(context);
		}
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

	/**
	 * FIXME This doesn't seem like the best place to stick this.
	 * 
	 * @param commit
	 * @param repoRelativePath
	 * @return
	 */
	public static IFileRevision revisionForCommit(GitCommit commit, IPath repoRelativePath)
	{
		return new CommitFileRevision(commit, repoRelativePath);
	}

	public IPath getGIT_SSH()
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			File sshwFile = ResourceUtil.resourcePathToFile(FileLocator.find(getBundle(),
					Path.fromPortableString("$os$/sshw.exe"), null)); //$NON-NLS-1$
			if (sshwFile.isFile())
			{
				return Path.fromOSString(sshwFile.getAbsolutePath());
			}
		}
		return null;
	}

	public IPath getSSH_ASKPASS()
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			return null;
		}
		else if (Platform.OS_LINUX.equals(Platform.getOS()) || Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			File askpassFile = ResourceUtil.resourcePathToFile(FileLocator.find(getBundle(),
					Path.fromPortableString("$os$/ssh-askpass.tcl"), null)); //$NON-NLS-1$
			if (askpassFile.isFile())
			{
				return Path.fromOSString(askpassFile.getAbsolutePath());
			}
		}
		return null;
	}

	public IPath getGIT_ASKPASS()
	{
		if (Platform.OS_WIN32.equals(Platform.getOS()))
		{
			return getGIT_SSH();
		}
		else if (Platform.OS_LINUX.equals(Platform.getOS()) || Platform.OS_MACOSX.equals(Platform.getOS()))
		{
			File askpassFile = ResourceUtil.resourcePathToFile(FileLocator.find(getBundle(),
					Path.fromPortableString("$os$/askpass.tcl"), null)); //$NON-NLS-1$
			if (askpassFile.isFile())
			{
				return Path.fromOSString(askpassFile.getAbsolutePath());
			}
		}
		return null;
	}

	public synchronized IGitRepositoryManager getGitRepositoryManager()
	{
		if (fGitRepoManager == null)
		{
			fGitRepoManager = new GitRepositoryManager();
		}
		return fGitRepoManager;
	}

	public synchronized IGithubManager getGithubManager()
	{
		if (fGithubManager == null)
		{
			fGithubManager = new GithubManager();
		}
		return fGithubManager;
	}
}
