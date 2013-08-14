/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ProcessStatus;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IDebugScopes;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubRepository;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.core.model.IGitRepositoryManager;
import com.aptana.git.ui.CloneJob;
import com.aptana.git.ui.GitUIPlugin;

/**
 * @author cwilliams
 */
public class GithubForkWizard extends Wizard implements IImportWizard
{

	private GithubRepositorySelectionPage cloneSource;

	public GithubForkWizard()
	{
	}

	public void init(IWorkbench workbench, IStructuredSelection selection)
	{
		cloneSource = new GithubRepositorySelectionPage();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages()
	{
		addPage(cloneSource);
	}

	@Override
	public boolean performFinish()
	{
		final IGithubManager ghManager = GitPlugin.getDefault().getGithubManager();
		final String owner = cloneSource.getOwner();
		final String repoName = cloneSource.getRepoName();
		// TODO Allow selecting a destination org to fork to!
		final String organization = null; // cloneSource.getOrganization();
		final String dest = cloneSource.getDestination();
		try
		{
			getContainer().run(true, true, new IRunnableWithProgress()
			{

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					try
					{
						monitor.subTask("Forking repository at Github");
						IGithubRepository repo = ghManager.fork(owner, repoName, organization);

						// Now clone the repo!
						CloneJob job = new CloneJob(repo.getSSHURL(), dest);
						IStatus status = job.run(monitor);
						if (!status.isOK())
						{
							if (status instanceof ProcessStatus)
							{
								ProcessStatus ps = (ProcessStatus) status;
								String stderr = ps.getStdErr();
								throw new InvocationTargetException(new CoreException(new Status(status.getSeverity(),
										status.getPlugin(), stderr)));
							}
							throw new InvocationTargetException(new CoreException(status));
						}
						
						// Add upstream remote pointing at parent!
						Set<IProject> projects = job.getCreatedProjects();
						if (!CollectionsUtil.isEmpty(projects))
						{
							monitor.subTask("Setting upstream remote to point at parent repository");
							IProject project = projects.iterator().next();
							IGitRepositoryManager grManager = GitPlugin.getDefault().getGitRepositoryManager();
							GitRepository clonedRepo = grManager.getAttached(project);
							if (clonedRepo != null)
							{
								IGithubRepository parentRepo = repo.getParent();
								clonedRepo.addRemote("upstream", parentRepo.getSSHURL(), false);
							}
						}						
					}
					catch (CoreException e)
					{
						throw new InvocationTargetException(e);
					}
				}
			});
		}
		catch (InvocationTargetException e)
		{
			if (e.getCause() instanceof CoreException)
			{
				CoreException ce = (CoreException) e.getCause();
				MessageDialog.openError(getShell(), "Failed to fork repository", ce.getMessage());
			}
			else
			{
				IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
		catch (InterruptedException e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
		return true;
	}

}
