/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.preview.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.aptana.preview.PreviewManager;

/**
 * @author Max Stepanov
 */
public class ShowPreviewHandler extends AbstractHandler
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands. ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editorPart = null;
		if (workbenchPage != null)
		{
			editorPart = workbenchPage.getActiveEditor();
		}
		if (editorPart != null)
		{
			PreviewManager.getInstance().openPreviewForEditor(editorPart);
		}
		return null;
	}
}
