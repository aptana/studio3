/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.commands;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This handler activates the Next editor without showing the Editor list popup.
 * 
 * @author schitale
 */
public class NextEditorHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IWorkbenchWindow activeWorkbenchWindow = HandlerUtil.getActiveWorkbenchWindow(event);
		if (activeWorkbenchWindow == null)
		{
			return null;
		}

		IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage == null)
		{
			return null;
		}

		switchEditor(activePage, next());

		return null;
	}

	protected boolean next()
	{
		return true;
	}

	private static void switchEditor(IWorkbenchPage activePage, boolean next)
	{
		IEditorPart activeEditor = activePage.getActiveEditor();
		if (activeEditor != null)
		{
			IEditorReference[] editorReferences = activePage.getEditorReferences();
			if (editorReferences != null && editorReferences.length >= 2)
			{
				List<IEditorPart> editorsList = new LinkedList<IEditorPart>();
				for (IEditorReference editorReference : editorReferences)
				{
					IWorkbenchPart editorPart = editorReference.getPart(true);
					if (editorPart instanceof IEditorPart)
					{
						editorsList.add((IEditorPart) editorPart);
					}
				}
				int activeEditorIndex = editorsList.indexOf(activeEditor);
				int toEditorIndex = ((activeEditorIndex == -1) ? 0 : (activeEditorIndex + (next ? 1 : -1)));
				if (toEditorIndex < 0)
				{
					toEditorIndex = editorsList.size() - 1;
				}
				else if (toEditorIndex >= editorsList.size())
				{
					toEditorIndex = 0;
				}
				activePage.activate(editorsList.get(toEditorIndex));
			}
		}
	}
}
