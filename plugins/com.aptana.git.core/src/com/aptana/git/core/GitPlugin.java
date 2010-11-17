/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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

import com.aptana.core.util.IOUtil;
import com.aptana.git.core.model.GitCommit;
import com.aptana.git.core.model.GitRepositoryManager;
import com.aptana.git.core.model.IGitRepositoryManager;
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
	private boolean updateSSHW = true;

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
		job.setSystem(true);
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
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fGitResourceListener);
			getGitRepositoryManager().removeListener(fRepoListener);
			getGitRepositoryManager().removeListenerFromEachRepository(fRepoListener);
			// Remove all the GitRepositories from memory!
			if (fGitRepoManager != null)
				fGitRepoManager.cleanup();
		}
		finally
		{
			fGitRepoManager = null;
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

	public static void logError(String msg, Throwable e)
	{
		getDefault().getLog().log(new Status(IStatus.ERROR, getPluginId(), msg, e));
	}

	protected static void logError(CoreException e)
	{
		getDefault().getLog().log(e.getStatus());
	}

	public static void logWarning(String warning)
	{
		if (getDefault() != null)
			getDefault().getLog().log(new Status(IStatus.WARNING, getPluginId(), warning));
	}

	public static void logError(Exception e)
	{
		if (getDefault() != null)
			getDefault().getLog().log(new Status(IStatus.WARNING, getPluginId(), "", e)); //$NON-NLS-1$
	}

	public static void logInfo(String string)
	{
		if (getDefault() != null && getDefault().isDebugging())
			getDefault().getLog().log(new Status(IStatus.INFO, getPluginId(), string));
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
			IPath path = getStateLocation().append("bin").append("sshw.exe"); //$NON-NLS-1$ //$NON-NLS-2$
			File file = path.toFile();
			if (!file.exists() || updateSSHW)
			{
				try
				{
					file.getParentFile().mkdirs();
					if (file.createNewFile())
					{
						IOUtil.extractFile(PLUGIN_ID, new Path("$os$/sshw.exe"), file); //$NON-NLS-1$
					}
				}
				catch (IOException e)
				{
					logError("Extract file failed.", e); //$NON-NLS-1$
				}
				updateSSHW = false;
			}
			if (file.exists())
			{
				return path;
			}
		}
		return null;
	}

	public IGitRepositoryManager getGitRepositoryManager()
	{
		if (fGitRepoManager == null)
		{
			fGitRepoManager = new GitRepositoryManager();
		}
		return fGitRepoManager;
	}
}
