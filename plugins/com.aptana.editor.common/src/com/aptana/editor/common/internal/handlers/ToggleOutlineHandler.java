/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.CommonEditorPlugin;

public class ToggleOutlineHandler extends AbstractHandler
{

	private static final String OUTLINE_VIEW_ID = "org.eclipse.ui.views.ContentOutline"; //$NON-NLS-1$

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
					IdeLog.logError(CommonEditorPlugin.getDefault(), Messages.ToggleOutlineHandler_ERR_OpeningOutline,
							e);
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
