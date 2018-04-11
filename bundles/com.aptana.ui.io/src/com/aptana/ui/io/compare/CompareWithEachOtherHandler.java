/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.io.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.internal.CompareEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.ide.ui.io.Utils;

@SuppressWarnings("restriction")
public class CompareWithEachOtherHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
		{
			return null;
		}
		Object[] elements = ((IStructuredSelection) selection).toArray();
		if (elements.length != 2)
		{
			return null;
		}
		IFileStore left = Utils.getFileStore(elements[0]);
		IFileStore right = Utils.getFileStore(elements[1]);

		CompareConfiguration cc = new CompareConfiguration();
		// buffered merge mode: don't ask for confirmation when switching between modified resources
		cc.setProperty(CompareEditor.CONFIRM_SAVE_PROPERTY, Boolean.FALSE);

		FileStoreCompareEditorInput editorInput = new FileStoreCompareEditorInput(cc);
		editorInput.setLeftFileStore(left);
		editorInput.setRightFileStore(right);
		editorInput.initializeCompareConfiguration();
		CompareUI.openCompareEditorOnPage(editorInput, HandlerUtil.getActiveWorkbenchWindow(event).getActivePage());

		return null;
	}
}
