package com.aptana.explorer.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.deploy.actions.DeployAction;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.model.GitRepository;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.SiteConnectionUtils;
import com.aptana.ide.syncing.ui.actions.SynchronizeAction;
import com.aptana.terminal.views.TerminalView;

public class DeployHandler extends AbstractHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{

		IStructuredSelection selections = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		IProject selectedProject = (IProject) selections.getFirstElement();
				
		if(isCapistranoProject(selectedProject))
		{
			TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
					selectedProject.getLocation());
			terminal.sendInput("cap deploy" + '\n');
			
		} else if (isFTPProject(selectedProject)) {
			
			SynchronizeAction action = new SynchronizeAction();
			action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActivePart());
			action.setSelection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
					.getSelection());
			action.run(null);
			
		} else if (isHerokuProject(selectedProject)) {
			
			TerminalView terminal = TerminalView.openView(selectedProject.getName(), selectedProject.getName(),
					selectedProject.getLocation());
			terminal.sendInput("git push heroku master" + '\n');
			
		} else {
			
			DeployAction action = new DeployAction();
			action.setActivePart(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getActivePart());
			action.selectionChanged(null, PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getSelectionService().getSelection());
			action.run(null);
			
		}
		
		return null;
	}
	
	
	private boolean isCapistranoProject(IProject selectedProject)
	{
		if (selectedProject.getFile("Capfile").exists()) //$NON-NLS-1$
			return true;

		return false;
	}

	private boolean isFTPProject(IProject selectedProject)
	{
		ISiteConnection[] siteConnections = SiteConnectionUtils.findSitesForSource(selectedProject);

		if (siteConnections.length > 0)
			return true;

		return false;
	}

	private boolean isHerokuProject(IProject selectedProject)
	{

		GitRepository repo = GitPlugin.getDefault().getGitRepositoryManager().getAttached(selectedProject);
		if (repo != null)
		{
			for (String remote : repo.remotes())
			{
				if (remote.indexOf("heroku") != -1)
				{
					return true;
				}
			}
			for (String remoteURL : repo.remoteURLs())
			{
				if (remoteURL.indexOf("heroku.com") != -1)
				{
					return true;
				}
			}
		}
		return false;

	}

}
