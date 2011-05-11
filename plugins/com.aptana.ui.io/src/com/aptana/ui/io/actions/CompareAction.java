/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.io.actions;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.internal.CompareEditor;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.ide.ui.io.Utils;
import com.aptana.ui.io.compare.FileStoreCompareEditorInput;

@SuppressWarnings("restriction")
public class CompareAction implements IObjectActionDelegate
{

	private IWorkbenchPage fWorkbenchPage;
	private IStructuredSelection fSelection;
	private FileStoreCompareEditorInput fEditorInput;

	public CompareAction()
	{
	}

	public void run(IAction action)
	{
		if (fSelection == null)
		{
			return;
		}
		Object[] elements = fSelection.toArray();
		if (elements.length != 2)
		{
			return;
		}
		IFileStore left = Utils.getFileStore(elements[0]);
		IFileStore right = Utils.getFileStore(elements[1]);

		CompareConfiguration cc = new CompareConfiguration();
		// buffered merge mode: don't ask for confirmation when switching between modified resources
		cc.setProperty(CompareEditor.CONFIRM_SAVE_PROPERTY, new Boolean(false));

		fEditorInput = new FileStoreCompareEditorInput(cc);
		fEditorInput.setLeftFileStore(left);
		fEditorInput.setRightFileStore(right);
		fEditorInput.initializeCompareConfiguration();
		CompareUI.openCompareEditorOnPage(fEditorInput, fWorkbenchPage);
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		fSelection = null;
		if (selection instanceof IStructuredSelection)
		{
			fSelection = (IStructuredSelection) selection;
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		fWorkbenchPage = targetPart.getSite().getPage();
	}
}
