/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.ILinkHelper;

import com.aptana.core.logging.IdeLog;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.internal.UniformFileStoreEditorInputFactory;

public class FilesystemLinkHelper implements ILinkHelper
{

	public IStructuredSelection findSelection(IEditorInput anInput)
	{
		IFileStore fileStore = (IFileStore) anInput.getAdapter(IFileStore.class);
		IFileInfo fileInfo = (IFileInfo) anInput.getAdapter(IFileInfo.class);
		if (fileStore == null || fileInfo == null)
		{
			return StructuredSelection.EMPTY;
		}
		return new StructuredSelection(new FileSystemObject(fileStore, fileInfo));
	}

	public void activateEditor(IWorkbenchPage aPage, IStructuredSelection aSelection)
	{
		if (aSelection == null || aSelection.isEmpty())
		{
			return;
		}
		Object element = aSelection.getFirstElement();
		if (element instanceof FileSystemObject)
		{
			FileSystemObject file = (FileSystemObject) element;
			IFileStore fileStore = file.getFileStore();
			try
			{
				IEditorPart editorPart = aPage.findEditor(UniformFileStoreEditorInputFactory.getUniformEditorInput(
						fileStore, new NullProgressMonitor()));
				aPage.bringToTop(editorPart);
			}
			catch (CoreException e)
			{
				IdeLog.logError(IOUIPlugin.getDefault(), e);
			}
		}
	}
}
