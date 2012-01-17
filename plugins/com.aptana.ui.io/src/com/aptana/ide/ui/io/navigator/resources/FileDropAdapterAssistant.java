/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.resources.ResourceDropAdapterAssistant;

import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.util.ArrayUtil;
import com.aptana.ide.core.io.LocalRoot;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ide.ui.io.actions.CopyFilesOperation;
import com.aptana.ide.ui.io.actions.MoveFilesOperation;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileDropAdapterAssistant extends ResourceDropAdapterAssistant
{

	private static final IAdaptable[] EMPTY_ADAPTABLE = new IAdaptable[0];

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget)
	{
		IStatus status = null;
		try
		{
			status = super.handleDrop(aDropAdapter, aDropTargetEvent, aTarget);
		}
		catch (Exception e)
		{
			// ignores the exception to allow our customized handler to take over
		}
		if (Status.OK_STATUS.equals(status) || (status instanceof MultiStatus && ((MultiStatus) status).isOK()))
		{
			return status;
		}

		if (aDropAdapter.getCurrentTarget() == null || aDropTargetEvent.data == null)
		{
			return Status.CANCEL_STATUS;
		}

		IAdaptable[] sources = null;
		TransferData currentTransfer = aDropAdapter.getCurrentTransfer();
		if (LocalSelectionTransfer.getTransfer().isSupportedType(currentTransfer))
		{
			sources = getSelectedSourceFiles();
			aDropTargetEvent.detail = DND.DROP_NONE;
		}

		if (FileTransfer.getInstance().isSupportedType(currentTransfer))
		{
			status = performDrop(aDropAdapter, (String[]) aDropTargetEvent.data);
		}
		else if (sources != null && sources.length > 0)
		{
			if (aDropAdapter.getCurrentOperation() == DND.DROP_COPY
					|| !isFromSameFilesystem(aDropAdapter.getCurrentTarget(), sources))
			{
				status = performCopy(aDropAdapter, sources);
			}
			else
			{
				status = performMove(aDropAdapter, sources);
			}
		}
		openError(status);

		return status;
	}

	@Override
	public IStatus validateDrop(Object target, int operation, TransferData transferType)
	{
		IStatus status = super.validateDrop(target, operation, transferType);
		if (Status.OK_STATUS.equals(status))
		{
			return status;
		}

		if (!(target instanceof IAdaptable))
		{
			return createStatus(Messages.FileDropAdapterAssistant_ERR_NotAdaptable);
		}

		IAdaptable destination = (IAdaptable) target;
		IFileStore fileStore = Utils.getFileStore(destination);
		if (fileStore == null)
		{
			return createStatus(Messages.FileDropAdapterAssistant_ERR_NotIFileStore);
		}

		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType))
		{
			IAdaptable[] files = getSelectedSourceFiles();
			if (files.length == 0)
			{
				return createStatus(Messages.FileDropAdapterAssistant_ERR_InvalidDropSelection);
			}
			for (Object file : files)
			{
				if (file instanceof LocalRoot)
				{
					return createStatus(Messages.FileDropAdapterAssistant_ERR_DropLocalRoot);
				}
			}

			String message = CopyFilesOperation.validateDestination(destination, files);
			if (message != null)
			{
				return createStatus(message);
			}
		}
		else if (FileTransfer.getInstance().isSupportedType(transferType))
		{
			String[] sourceNames = (String[]) FileTransfer.getInstance().nativeToJava(transferType);
			if (sourceNames == null)
			{
				sourceNames = ArrayUtil.NO_STRINGS;
			}

			String message = CopyFilesOperation.validateDestination(destination, sourceNames);
			if (message != null)
			{
				return createStatus(message);
			}
		}

		return Status.OK_STATUS;
	}

	private IStatus performCopy(final CommonDropAdapter dropAdapter, IAdaptable[] sources)
	{
		MultiStatus problems = new MultiStatus(IOUIPlugin.PLUGIN_ID, 1, Messages.FileDropAdapterAssistant_ERR_Copying,
				null);
		IStatus validate = validateDrop(dropAdapter.getCurrentTarget(), dropAdapter.getCurrentOperation(),
				dropAdapter.getCurrentTransfer());
		if (!validate.isOK())
		{
			problems.merge(validate);
		}

		final IFileStore destination = getFolderStore((IAdaptable) dropAdapter.getCurrentTarget());
		CopyFilesOperation operation = new CopyFilesOperation(getShell());
		operation.copyFiles(sources, destination, new JobChangeAdapter()
		{

			public void done(IJobChangeEvent event)
			{
				refresh(dropAdapter.getCurrentTarget());
			}
		});

		return problems;
	}

	private IStatus performDrop(final CommonDropAdapter dropAdapter, String[] data)
	{
		MultiStatus problems = new MultiStatus(IOUIPlugin.PLUGIN_ID, 0,
				Messages.FileDropAdapterAssistant_ERR_Importing, null);
		IStatus validate = validateDrop(dropAdapter.getCurrentTarget(), dropAdapter.getCurrentOperation(),
				dropAdapter.getCurrentTransfer());
		if (!validate.isOK())
		{
			problems.merge(validate);
		}

		final IFileStore destination = getFolderStore((IAdaptable) dropAdapter.getCurrentTarget());
		CopyFilesOperation operation = new CopyFilesOperation(getShell());
		operation.copyFiles(data, destination, new JobChangeAdapter()
		{

			public void done(IJobChangeEvent event)
			{
				refresh(dropAdapter.getCurrentTarget());
			}
		});

		return problems;
	}

	private IStatus performMove(final CommonDropAdapter dropAdapter, final IAdaptable[] sources)
	{
		MultiStatus problems = new MultiStatus(IOUIPlugin.PLUGIN_ID, 1, Messages.FileDropAdapterAssistant_ERR_Moving,
				null);
		IStatus validate = validateDrop(dropAdapter.getCurrentTarget(), dropAdapter.getCurrentOperation(),
				dropAdapter.getCurrentTransfer());
		if (!validate.isOK())
		{
			problems.merge(validate);
		}

		final IFileStore destination = getFolderStore((IAdaptable) dropAdapter.getCurrentTarget());
		MoveFilesOperation operation = new MoveFilesOperation(getShell());
		operation.copyFiles(sources, destination, new JobChangeAdapter()
		{

			public void done(IJobChangeEvent event)
			{
				// refreshes the target
				refresh(dropAdapter.getCurrentTarget());
				// refreshes the source's parent folder
				IFileStore fileStore;
				for (IAdaptable source : sources)
				{
					fileStore = (IFileStore) source.getAdapter(IFileStore.class);
					if (fileStore != null)
					{
						refresh(fileStore.getParent());
					}
				}
			}
		});

		return problems;
	}

	private void openError(IStatus status) // $codepro.audit.disable overridingPrivateMethod
	{
		if (status == null)
		{
			return;
		}

		String title = Messages.FileDropAdapterAssistant_ERR_DragAndDrop_Title;
		int codes = IStatus.ERROR | IStatus.WARNING;

		if (status.isMultiStatus())
		{
			IStatus[] children = status.getChildren();
			if (children.length == 1)
			{
				ErrorDialog.openError(getShell(), status.getMessage(), null, children[0], codes);
			}
			else
			{
				ErrorDialog.openError(getShell(), title, null, status, codes);
			}
		}
		else
		{
			ErrorDialog.openError(getShell(), title, null, status, codes);
		}
	}

	private void refresh(Object element)
	{
		IResource resource = null;
		if (element instanceof IAdaptable)
		{
			resource = (IResource) ((IAdaptable) element).getAdapter(IResource.class);
		}
		if (resource != null)
		{
			try
			{
				resource.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			catch (CoreException e)
			{
			}
		}
		else
		{
			IOUIPlugin.refreshNavigatorView(element);
		}
	}

	private static IAdaptable[] getSelectedSourceFiles()
	{
		ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
		if (selection instanceof IStructuredSelection)
		{
			return getSelectedSourceFiles((IStructuredSelection) selection);
		}
		return EMPTY_ADAPTABLE;
	}

	@SuppressWarnings("rawtypes")
	private static IAdaptable[] getSelectedSourceFiles(IStructuredSelection selection)
	{
		List<IAdaptable> selectedFiles = new ArrayList<IAdaptable>();

		Iterator iter = selection.iterator();
		Object object;
		IFileStore fileStore;
		IAdaptable adaptable;
		while (iter.hasNext())
		{
			object = iter.next();
			if (object instanceof IAdaptable)
			{
				adaptable = (IAdaptable) object;
				fileStore = Utils.getFileStore(adaptable);
				if (fileStore != null)
				{
					// valid selection
					selectedFiles.add(adaptable);
				}
			}
		}

		return selectedFiles.toArray(new IAdaptable[selectedFiles.size()]);
	}

	private static IFileStore getFolderStore(IAdaptable destination)
	{
		IFileStore store = Utils.getFileStore(destination);
		IFileInfo info = Utils.getFileInfo(destination, IExtendedFileStore.EXISTENCE);
		if (store != null && info != null && !info.isDirectory())
		{
			store = store.getParent();
		}
		return store;
	}

	/**
	 * @param destination
	 *            the destination target
	 * @param sources
	 *            the array of selected source files
	 * @return true if the sources are from the same file system as the destination, false otherwise
	 */
	private static boolean isFromSameFilesystem(Object destination, Object[] sources)
	{
		IFileStore fileStore = Utils.getFileStore(destination);
		if (fileStore == null)
		{
			return false;
		}
		IFileSystem filesystem = fileStore.getFileSystem();
		IFileStore sourceFileStore;
		for (Object source : sources)
		{
			sourceFileStore = Utils.getFileStore(source);
			if (sourceFileStore == null)
			{
				return false;
			}
			if (!sourceFileStore.getFileSystem().equals(filesystem))
			{
				return false;
			}
		}
		return true;
	}

	private static Status createStatus(String message)
	{
		return new Status(IStatus.INFO, IOUIPlugin.PLUGIN_ID, 0, message, null);
	}
}
