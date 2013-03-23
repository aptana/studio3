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
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ide.ui.io.actions.CopyFilesOperation;
import com.aptana.ide.ui.io.navigator.FileSystemObject;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemPasteAction extends BaseSelectionListenerAction
{

	/**
	 * The id of this action
	 */
	public static final String ID = IOUIPlugin.PLUGIN_ID + ".PasteAction"; //$NON-NLS-1$

	private static final IFileStore[] NO_FILES = new IFileStore[0];
	/**
	 * The shell in which to show any dialogs
	 */
	private Shell fShell;

	private List<Object> selectedFiles;

	/**
	 * System clipboard
	 */
	private Clipboard fClipboard;

	private IFileStore[] fClipboardData;
	private List<IFileStore> fDestFileStores;

	public FileSystemPasteAction(Shell shell, Clipboard clipboard)
	{
		super(Messages.FileSystemPasteAction_TXT);
		fShell = shell;
		fClipboard = clipboard;
		fDestFileStores = new ArrayList<IFileStore>();
		selectedFiles = new ArrayList<Object>();

		setToolTipText(Messages.FileSystemPasteAction_TTP);
		setId(ID);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, "HelpId"); //$NON-NLS-1$
	}

	@Override
	public void run()
	{
		// try file transfer
		JobChangeAdapter jobAdapter = new JobChangeAdapter()
		{

			@Override
			public void done(IJobChangeEvent event)
			{
				for (Object file : selectedFiles)
				{
					if (file instanceof IResource)
					{
						try
						{
							((IResource) file).refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
						}
						catch (CoreException e)
						{
							IdeLog.logError(IOUIPlugin.getDefault(), e);
						}
					}
					else
					{
						IOUIPlugin.refreshNavigatorView(file);
					}
				}
			}
		};
		if (fClipboardData != null && fClipboardData.length > 0 && fDestFileStores.size() > 0)
		{
			CopyFilesOperation operation = new CopyFilesOperation(fShell);
			operation.copyFiles(fClipboardData, fDestFileStores.get(0), jobAdapter);
			return;
		}

		// try other transfer
		FileTransfer fileTransfer = FileTransfer.getInstance();
		String[] fileData = (String[]) fClipboard.getContents(fileTransfer);

		if (fileData != null && fileData.length > 0 && fDestFileStores.size() > 0)
		{
			CopyFilesOperation operation = new CopyFilesOperation(fShell);
			operation.copyFiles(fileData, fDestFileStores.get(0), jobAdapter);
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection)
	{
		fDestFileStores.clear();
		selectedFiles.clear();
		fClipboardData = NO_FILES;
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
				if (!Utils.isDirectory(fileStore))
				{
					fileStore = fileStore.getParent();
				}
				fDestFileStores.add(fileStore);

				if (element instanceof IAdaptable)
				{
					IResource resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
					if (resource != null)
					{
						selectedFiles.add((resource instanceof IContainer) ? resource : resource.getParent());
					}
					else
					{
						selectedFiles.add(new FileSystemObject(fileStore, FileSystemUtils.getFileInfo(fileStore)));
					}
				}
			}
		}
		if (fDestFileStores.size() == 0)
		{
			return false;
		}

		fShell.getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				// clipboard must have files
				LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();
				Object contents = fClipboard.getContents(transfer);
				if (contents instanceof StructuredSelection)
				{
					Object[] elements = ((StructuredSelection) contents).toArray();
					List<IFileStore> fileStores = new ArrayList<IFileStore>();
					for (Object element : elements)
					{
						if (element instanceof IFileStore)
						{
							fileStores.add((IFileStore) element);
						}
					}
					fClipboardData = fileStores.toArray(new IFileStore[fileStores.size()]);
				}
			}
		});
		if (fClipboardData.length > 0)
		{
			return true;
		}

		TransferData[] transfers = fClipboard.getAvailableTypes();
		FileTransfer fileTransfer = FileTransfer.getInstance();
		for (int i = 0; i < transfers.length; ++i)
		{
			if (fileTransfer.isSupportedType(transfers[i]))
			{
				return true;
			}
		}
		return false;
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
