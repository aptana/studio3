/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.IOUIPlugin;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemCopyAction extends BaseSelectionListenerAction
{

	/**
	 * The id of this action
	 */
	public static final String ID = IOUIPlugin.PLUGIN_ID + ".CopyAction"; //$NON-NLS-1$

	/**
	 * The shell in which to show any dialogs
	 */
	private Shell fShell;

	/**
	 * System clipboard
	 */
	private Clipboard fClipboard;

	private List<IFileStore> fFileStores;

	/**
	 * Associated paste action. May be <code>null</code>
	 */
	private FileSystemPasteAction fPasteAction;

	public FileSystemCopyAction(Shell shell, Clipboard clipboard)
	{
		super(Messages.FileSystemCopyAction_TXT);
		fShell = shell;
		fClipboard = clipboard;
		fFileStores = new ArrayList<IFileStore>();

		setToolTipText(Messages.FileSystemCopyAction_TTP);
		setId(ID);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, "CopyHelpId"); //$NON-NLS-1$
	}

	public FileSystemCopyAction(Shell shell, Clipboard clipboard, FileSystemPasteAction pasteAction)
	{
		this(shell, clipboard);
		fPasteAction = pasteAction;
	}

	@Override
	public void run()
	{
		// Get the file names and a string representation
		IFileStore[] fileStores = fFileStores.toArray(new IFileStore[fFileStores.size()]);
		String[] fileNames = new String[fileStores.length];
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < fileStores.length; ++i)
		{
			fileNames[i] = fileStores[i].toString();
			if (i > 0)
			{
				buf.append('\n'); // $codepro.audit.disable platformSpecificLineSeparator
			}
			buf.append(fileStores[i].getName());
		}
		setClipboard(fileStores, fileNames, buf.toString());

		// update the enablement of the paste action
		// workaround since the clipboard does not support callbacks
		if (fPasteAction != null && fPasteAction.getStructuredSelection() != null)
		{
			fPasteAction.selectionChanged(fPasteAction.getStructuredSelection());
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection)
	{
		fFileStores.clear();
		if (!super.updateSelection(selection))
		{
			return false;
		}

		if (selection == null || selection.isEmpty())
		{
			return false;
		}
		Object[] elements = selection.toArray();

		IFileStore fileStore;
		for (Object element : elements)
		{
			fileStore = getFileStore(element);
			if (fileStore != null)
			{
				fFileStores.add(fileStore);
			}
		}
		if (fFileStores.size() == 0)
		{
			return false;
		}

		// must have a common parent
		IFileStore firstParent = fFileStores.get(0).getParent();
		if (firstParent == null)
		{
			return false;
		}

		for (IFileStore store : fFileStores)
		{
			if (!store.getParent().equals(firstParent))
			{
				return false;
			}
		}

		return true;
	}

	private void setClipboard(IFileStore[] fileStores, String[] fileNames, String names)
	{
		try
		{
			// set the clipboard contents
			LocalSelectionTransfer selectionTransfer = LocalSelectionTransfer.getTransfer();
			selectionTransfer.setSelection(new StructuredSelection(fileStores));
			if (fileNames.length > 0)
			{
				fClipboard.setContents(new Object[] { fileStores, fileNames, names }, new Transfer[] {
						LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance(), TextTransfer.getInstance() });
			}
			else
			{
				fClipboard.setContents(new Object[] { fileStores, names },
						new Transfer[] { LocalSelectionTransfer.getTransfer(), TextTransfer.getInstance() });
			}
		}
		catch (SWTError e)
		{
			if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD)
			{
				throw e; // $codepro.audit.disable thrownExceptions
			}
			if (MessageDialog.openQuestion(fShell, "Problem with copy title", //$NON-NLS-1$
					"Problem with copy.")) { //$NON-NLS-1$
				setClipboard(fileStores, fileNames, names);
			}
		}
	}

	private static IFileStore getFileStore(Object adaptable)
	{
		if (adaptable instanceof IAdaptable)
		{
			IResource resource = (IResource) ((IAdaptable) adaptable).getAdapter(IResource.class);
			if (resource != null)
			{
				try
				{
					return EFS.getStore(resource.getLocationURI());
				}
				catch (CoreException e)
				{
					return EFSUtils.getFileStore(resource);
				}
			}
		}
		return FileSystemUtils.getFileStore(adaptable);
	}
}
