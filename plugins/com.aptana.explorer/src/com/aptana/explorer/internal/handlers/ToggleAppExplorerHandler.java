package com.aptana.explorer.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.explorer.ExplorerPlugin;
import com.aptana.explorer.IExplorerUIConstants;

public class ToggleAppExplorerHandler extends AbstractHandler
{

	public ToggleAppExplorerHandler()
	{
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		boolean toShow = !HandlerUtil.toggleCommandState(event.getCommand());
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null)
		{
			if (toShow)
			{
				try
				{
					page.showView(IExplorerUIConstants.VIEW_ID);
				}
				catch (PartInitException e)
				{
					ExplorerPlugin.logError(Messages.ToggleAppExplorerHandler_ERR_OpeningAppExplorer, e);
				}
			}
			else
			{
				IViewPart viewPart = page.findView(IExplorerUIConstants.VIEW_ID);
				if (viewPart != null)
				{
					page.hideView(viewPart);
				}
			}
		}
		return null;
	}
}
