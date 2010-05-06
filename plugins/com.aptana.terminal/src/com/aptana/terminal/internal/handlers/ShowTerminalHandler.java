package com.aptana.terminal.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.aptana.terminal.Activator;
import com.aptana.terminal.views.TerminalView;

public class ShowTerminalHandler extends AbstractHandler
{

	private static final String EXPLORER_PLUGIN_ID = "com.aptana.explorer"; //$NON-NLS-1$
	private static final String EXPLORER_ACTIVE_PROJECT = "activeProject"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IProject project = null;
		String activeProjectName = Platform.getPreferencesService().getString(EXPLORER_PLUGIN_ID,
				EXPLORER_ACTIVE_PROJECT, null, null);
		if (activeProjectName != null)
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(activeProjectName);
		}

		if (project == null)
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if (page != null)
			{
				try
				{
					page.showView(TerminalView.ID);
				}
				catch (PartInitException e)
				{
					Activator.logError(Messages.ShowTerminalHandler_ERR_OpeningTerminal, e);
				}
			}
		}
		else
		{
			TerminalView.openView(project.getName(), project.getName(), project.getLocation());
		}
		return null;
	}
}
