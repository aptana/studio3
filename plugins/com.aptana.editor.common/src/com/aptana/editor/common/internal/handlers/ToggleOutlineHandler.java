package com.aptana.editor.common.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.editor.common.CommonEditorPlugin;

public class ToggleOutlineHandler extends AbstractHandler
{

	private static final String OUTLINE_VIEW_ID = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$

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
					page.showView(OUTLINE_VIEW_ID);
				}
				catch (PartInitException e)
				{
					CommonEditorPlugin.logError(Messages.ToggleOutlineHandler_ERR_OpeningOutline, e);
				}
			}
			else
			{
				IViewPart viewPart = page.findView(OUTLINE_VIEW_ID);
				if (viewPart != null)
				{
					page.hideView(viewPart);
				}
			}
		}
		return null;
	}
}
