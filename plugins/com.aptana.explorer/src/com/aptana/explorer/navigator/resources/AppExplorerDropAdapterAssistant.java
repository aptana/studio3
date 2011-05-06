/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.navigator.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorMessages;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorPlugin;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.resources.ResourceDropAdapterAssistant;

import com.aptana.explorer.ExplorerPlugin;

@SuppressWarnings("restriction")
public class AppExplorerDropAdapterAssistant extends ResourceDropAdapterAssistant
{

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget)
	{
		if (aTarget == null || aDropTargetEvent.data == null)
		{
			return Status.CANCEL_STATUS;
		}

		TransferData currentTransfer = aDropAdapter.getCurrentTransfer();
		if (FileTransfer.getInstance().isSupportedType(currentTransfer)
				&& (aDropAdapter.getCurrentTarget() == null && aTarget instanceof IProject))
		{
			// dropping into the root, so perform our special handling
			return performFileDrop(aDropAdapter, aDropTargetEvent.data, (IProject) aTarget);
		}
		return super.handleDrop(aDropAdapter, aDropTargetEvent, aTarget);
	}

	/**
	 * Performs a drop using the FileTransfer transfer type.
	 */
	private IStatus performFileDrop(CommonDropAdapter anAdapter, Object data, final IProject target)
	{
		MultiStatus problems = new MultiStatus(ExplorerPlugin.PLUGIN_ID, 0,
				WorkbenchNavigatorMessages.DropAdapter_problemImporting, null);
		final int currentOperation = anAdapter.getCurrentOperation();
		mergeStatus(problems, validateTarget(target, anAdapter.getCurrentTransfer(), currentOperation));

		final String[] names = (String[]) data;
		Display.getCurrent().asyncExec(new Runnable()
		{
			public void run()
			{
				getShell().forceActive();
				new CopyFilesAndFoldersOperation(getShell()).copyOrLinkFiles(names, target, currentOperation);
			}
		});
		return problems;
	}

	private IStatus validateTarget(IProject target, TransferData transferType, int dropOperation)
	{
		if (!target.isAccessible())
		{
			return WorkbenchNavigatorPlugin
					.createErrorStatus(WorkbenchNavigatorMessages.DropAdapter_canNotDropIntoClosedProject);
		}
		return Status.OK_STATUS;
	}

	private static void mergeStatus(MultiStatus status, IStatus toMerge)
	{
		if (!toMerge.isOK())
		{
			status.merge(toMerge);
		}
	}
}
