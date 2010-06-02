package com.aptana.explorer.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.deploy.wizard.DeployWizard;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.actions.SynchronizeFilesAction;
import com.aptana.terminal.views.TerminalView;

public class DeployHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		ISelection selections = HandlerUtil.getCurrentSelection(event);
		if (selections.isEmpty() || !(selections instanceof IStructuredSelection))
		{
			return null;
		}
		Object selection = ((IStructuredSelection) selections).getFirstElement();
		if (!(selection instanceof IResource))
		{
			return null;
		}
		IProject selectedProject = ((IResource) selection).getProject();

		if (isCapistranoProject(selectedProject))
		{
			TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
					selectedProject.getLocation());
			terminal.sendInput("cap deploy\n"); //$NON-NLS-1$
		}
		else if (isFTPProject(selectedProject))
		{
			SynchronizeFilesAction action = new SynchronizeFilesAction();
			action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActivePart());
			action.setSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
					.getSelection());
			action.run(null);
		}
		else if (isHerokuProject(selectedProject))
		{
			TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
					selectedProject.getLocation());
			terminal.sendInput("git push heroku master\n"); //$NON-NLS-1$
		}
		else
		{
			IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();

			// Instantiates and initializes the wizard
			DeployWizard wizard = new DeployWizard();
			wizard.init(part.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) part.getSite().getSelectionProvider()
					.getSelection());

			// Instantiates the wizard container with the wizard and opens it
			Shell shell = part.getSite().getShell();
			if (shell == null)
			{
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setPageSize(350, 500);
			dialog.create();
			dialog.open();
		}

		return null;
	}

	private boolean isCapistranoProject(IProject selectedProject)
	{
		return selectedProject.getFile("Capfile").exists(); //$NON-NLS-1$
	}

	private boolean isFTPProject(IProject selectedProject)
	{
		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(selectedProject, true);
		return siteConnections.length > 0;
	}

	private boolean isHerokuProject(IProject selectedProject)
	{
		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(selectedProject);
		if (repo != null)
		{
			for (String remote : repo.remotes())
			{
				if (remote.indexOf("heroku") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
			for (String remoteURL : repo.remoteURLs())
			{
				if (remoteURL.indexOf("heroku.com") != -1) //$NON-NLS-1$
				{
					return true;
				}
			}
		}
		return false;
	}

}
