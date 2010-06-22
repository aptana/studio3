package com.aptana.explorer.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.aptana.deploy.wizard.DeployWizard;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.actions.SynchronizeProjectAction;
import com.aptana.terminal.views.TerminalView;

public class DeployHandler extends AbstractHandler
{

	private IProject selectedProject;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (isCapistranoProject(selectedProject))
		{
			TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
					selectedProject.getLocation());
			terminal.sendInput("cap deploy\n"); //$NON-NLS-1$
		}
		else if (selectedProject != null && isFTPProject(selectedProject))
		{
			SynchronizeProjectAction action = new SynchronizeProjectAction();
			action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActivePart());
			action.setSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
					.getSelection());
			action.run(null);
		}
		else if (selectedProject != null && isHerokuProject(selectedProject))
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
			wizard.setWindowTitle(Messages.DeployHandler_Wizard_Title);

			// Instantiates the wizard container with the wizard and opens it
			Shell shell = part.getSite().getShell();
			if (shell == null)
			{
				shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			}
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.setPageSize(350, 500);
			dialog.setHelpAvailable(false);
			dialog.create();
			dialog.open();
		}

		return null;
	}

	@Override
	public boolean isEnabled()
	{
		return selectedProject != null && selectedProject.isAccessible();
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		selectedProject = null;
		if (evaluationContext instanceof EvaluationContext)
		{
			Object value = ((EvaluationContext) evaluationContext).getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
			if (value instanceof ISelection)
			{
				ISelection selections = (ISelection) value;
				if (!selections.isEmpty() && selections instanceof IStructuredSelection)
				{
					Object selection = ((IStructuredSelection) selections).getFirstElement();
					IResource resource = null;
					if (selection instanceof IResource)
					{
						resource = (IResource) selection;
					}
					else if (selection instanceof IAdaptable)
					{
						resource = (IResource) ((IAdaptable) selection).getAdapter(IResource.class);
					}
					if (resource != null)
					{
						selectedProject = resource.getProject();
					}
				}
			}
		}
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
