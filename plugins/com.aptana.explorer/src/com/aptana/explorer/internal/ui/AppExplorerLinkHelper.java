package com.aptana.explorer.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.part.FileEditorInput;

public class AppExplorerLinkHelper implements ILinkHelper
{

	@Override
	public IStructuredSelection findSelection(IEditorInput anInput)
	{
		IFile file = ResourceUtil.getFile(anInput);
		if (file == null)
		{
			return StructuredSelection.EMPTY;
		}
		return new StructuredSelection(file);
	}

	@Override
	public void activateEditor(IWorkbenchPage aPage, IStructuredSelection aSelection)
	{
		if (aSelection == null || aSelection.isEmpty())
		{
			return;
		}
		if (aSelection.getFirstElement() instanceof IFile)
		{
			IEditorInput fileInput = new FileEditorInput((IFile) aSelection.getFirstElement());
			IEditorPart editor;
			if ((editor = aPage.findEditor(fileInput)) != null)
			{
				aPage.bringToTop(editor);
			}
		}
	}
}
