/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.actions.TextActionHandler;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.LocalRoot;

public class FileSystemEditActionGroup extends ActionGroup
{

	private Clipboard fClipboard;
	private Shell fShell;
	private Tree fTree;

	private FileSystemCopyAction fCopyAction;
	private FileSystemPasteAction fPasteAction;
	private BaseSelectionListenerAction fDeleteAction;

	private TextActionHandler fTextActionHandler;

	public FileSystemEditActionGroup(Shell shell, Tree tree)
	{
		fShell = shell;
		fTree = tree;
		makeActions();
	}

	@Override
	public void dispose()
	{
		if (fClipboard != null)
		{
			fClipboard.dispose();
			fClipboard = null;
		}
		super.dispose();
	}

	@Override
	public void fillContextMenu(IMenuManager menu)
	{
		IStructuredSelection selection = getSelection();
		fCopyAction.selectionChanged(selection);
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, fCopyAction);
		fPasteAction.selectionChanged(selection);
		menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, fPasteAction);

		if (selection != null && !selection.isEmpty())
		{
			Object[] elements = selection.toArray();
			boolean allFileSystemObjects = true;
			for (Object element : elements)
			{
				if (element instanceof LocalRoot || element instanceof IConnectionPoint)
				{
					allFileSystemObjects = false;
					break;
				}
			}
			if (allFileSystemObjects)
			{
				menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, fDeleteAction);
			}
		}
	}

	@Override
	public void fillActionBars(IActionBars actionBars)
	{
		if (fTextActionHandler == null)
		{
			fTextActionHandler = new TextActionHandler(actionBars);
		}

		fTextActionHandler.setCopyAction(fCopyAction);
		fTextActionHandler.setPasteAction(fPasteAction);
		fTextActionHandler.setDeleteAction(fDeleteAction);
		updateActionBars();

		fTextActionHandler.updateActionBars();
	}

	@Override
	public void updateActionBars()
	{
		IStructuredSelection selection = getSelection();

		fCopyAction.selectionChanged(selection);
		fPasteAction.selectionChanged(selection);
		fDeleteAction.selectionChanged(selection);
	}

	/**
	 * Handles a key pressed event by invoking the appropriate action.
	 * 
	 * @param event
	 *            The key event
	 */
	public void handleKeyPressed(KeyEvent event)
	{
		if (event.character == SWT.DEL && event.stateMask == 0)
		{
			if (fDeleteAction.isEnabled())
			{
				fDeleteAction.run();
			}
			event.doit = false;
		}
	}

	protected void makeActions()
	{
		fClipboard = new Clipboard(fShell.getDisplay());

		ISharedImages images = PlatformUI.getWorkbench().getSharedImages();

		fPasteAction = new FileSystemPasteAction(fShell, fClipboard);
		fPasteAction.setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		fPasteAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		fPasteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);

		fCopyAction = new FileSystemCopyAction(fShell, fClipboard, fPasteAction);
		fCopyAction.setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		fCopyAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		fCopyAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);

		fDeleteAction = createDeleteAction(fShell);
		fDeleteAction.setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		fDeleteAction.setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		fDeleteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_DELETE);
	}

	protected BaseSelectionListenerAction createDeleteAction(Shell shell)
	{
		return new FileSystemDeleteAction(shell, fTree);
	}

	private IStructuredSelection getSelection()
	{
		return (IStructuredSelection) getContext().getSelection();
	}
}
