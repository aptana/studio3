package com.aptana.git.ui.internal.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;

import com.aptana.git.core.model.GitExecutable;
import com.aptana.git.core.model.GitRepository;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.git.ui.internal.Launcher;
import com.aptana.git.ui.internal.dialogs.BranchDialog;

public class MergeBranchHandler extends AbstractGitHandler
{

	@Override
	protected Object doExecute(ExecutionEvent event) throws ExecutionException
	{
		final GitRepository repo = getSelectedRepository();
		if (repo == null)
		{
			return null;
		}
		BranchDialog dialog = new BranchDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), repo, true,
				true);
		if (dialog.open() == Window.OK)
		{
			mergeBranch(repo, dialog.getBranch());
		}
		return null;
	}

	public static void mergeBranch(final GitRepository repo, final String branchName)
	{
		Job job = new Job(NLS.bind("git merge {0}", branchName)) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
					ILaunch launch = Launcher.launch(GitExecutable.instance().path().toOSString(),
							repo.workingDirectory(), subMonitor.newChild(75), "merge", //$NON-NLS-1$
							branchName);
					while (!launch.isTerminated())
					{
						Thread.sleep(50);
					}
					repo.index().refresh(subMonitor.newChild(25));
				}
				catch (CoreException e)
				{
					GitUIPlugin.logError(e);
					return e.getStatus();
				}
				catch (Throwable e)
				{
					GitUIPlugin.logError(e.getMessage(), e);
					return new Status(IStatus.ERROR, GitUIPlugin.getPluginId(), e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.setPriority(Job.LONG);
		job.schedule();
	}

}
