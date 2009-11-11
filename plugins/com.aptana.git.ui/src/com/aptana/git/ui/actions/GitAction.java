package com.aptana.git.ui.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.internal.Launcher;
import com.aptana.git.ui.internal.actions.Messages;

/**
 * Base class for actions that simple call out to actions/commands on the Git executable to be run inside the Eclipse
 * console. Used for global actions like Push, Pull, Status.
 * 
 * @author cwilliams
 */
public abstract class GitAction extends Action implements IObjectActionDelegate
{

	private ISelection selection;
	private Shell shell;
	private IWorkbenchPart targetPart;

	@Override
	public void run()
	{
		File workingDir = getWorkingDir();
		String working = null;
		if (workingDir != null)
			working = workingDir.toString();
		Launcher.launch(GitExecutable.instance().path(), working, getCommand());
	}

	public void run(IAction action)
	{
		run();
	}

	protected abstract String[] getCommand();

	private File getWorkingDir()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length == 0)
			return null;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.getAttached(project);
		if (repo == null)
			return null;
		return new File(repo.workingDirectory());
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		this.selection = selection;
	}

	protected IResource[] getSelectedResources()
	{
		if (this.selection == null)
			return new IResource[0];
		if (!(this.selection instanceof IStructuredSelection))
			return new IResource[0];

		List<IResource> resources = new ArrayList<IResource>();
		IStructuredSelection structured = (IStructuredSelection) this.selection;
		for (Object element : structured.toList())
		{
			if (element == null)
				continue;

			if (element instanceof IResource)
				resources.add((IResource) element);

			if (element instanceof IAdaptable)
			{
				IAdaptable adapt = (IAdaptable) element;
				IResource resource = (IResource) adapt.getAdapter(IResource.class);
				if (resource != null)
					resources.add(resource);
			}
		}
		return resources.toArray(new IResource[resources.size()]);
	}

	@Override
	public boolean isEnabled()
	{
		IResource[] resources = getSelectedResources();
		if (resources == null || resources.length != 1)
			return false;
		IProject project = resources[0].getProject();
		GitRepository repo = GitRepository.getAttached(project);
		if (repo == null)
			return false;
		return true;
	}

	protected void refreshAffectedProjects()
	{
		final Set<IProject> affectedProjects = new HashSet<IProject>();
		for (IResource resource : getSelectedResources())
		{
			if (resource == null)
				continue;
			affectedProjects.add(resource.getProject());
			GitRepository repo = GitRepository.getAttached(resource.getProject());
			if (repo != null)
			{
				affectedProjects.addAll(getAssociatedProjects(repo));
			}
		}

		WorkspaceJob job = new WorkspaceJob(Messages.PullAction_RefreshJob_Title)
		{
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
			{
				int work = 100 * affectedProjects.size();
				SubMonitor sub = SubMonitor.convert(monitor, work);
				for (IProject resource : affectedProjects)
				{
					if (sub.isCanceled())
						return Status.CANCEL_STATUS;
					resource.refreshLocal(IResource.DEPTH_INFINITE, sub.newChild(100));
				}
				sub.done();
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.setUser(true);
		job.schedule();
	}

	private Collection<? extends IProject> getAssociatedProjects(GitRepository repo)
	{
		Set<IProject> projects = new HashSet<IProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			GitRepository other = GitRepository.getAttached(project);
			if (other != null && other.equals(repo))
			{
				projects.add(project);
			}
		}
		return projects;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		if (targetPart != null)
		{
			this.shell = targetPart.getSite().getShell();
			this.targetPart = targetPart;
		}
	}

	protected Shell getShell()
	{
		if (shell != null)
		{
			return shell;
		}
		else if (targetPart != null)
		{
			return targetPart.getSite().getShell();
		}
		else
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench == null)
				return null;
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window == null)
				return null;
			return window.getShell();
		}
	}

	/**
	 * @return IWorkbenchPart
	 */
	protected IWorkbenchPart getTargetPart()
	{
		if (targetPart == null)
		{
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench == null)
				return null;
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			if (window == null)
				return null;
			IWorkbenchPage page = window.getActivePage();
			if (page != null)
			{
				targetPart = page.getActivePart();
			}
		}
		return targetPart;

	}
}
