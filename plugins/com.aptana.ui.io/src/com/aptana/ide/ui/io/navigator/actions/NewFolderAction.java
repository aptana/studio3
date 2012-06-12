/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.ide.core.io.preferences.PermissionDirection;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewFolderAction extends BaseSelectionListenerAction
{

	private IAdaptable fSelectedElement;
	private IWorkbenchWindow fWindow;

	public NewFolderAction(IWorkbenchWindow window)
	{
		super(Messages.NewFolderAction_Text);
		fWindow = window;
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
		setToolTipText(Messages.NewFolderAction_ToolTip);
	}

	public void run()
	{
		if (fSelectedElement == null)
		{
			return;
		}
		final IFileStore fileStore = Utils.getFileStore(fSelectedElement);
		final boolean selectionIsDirectory = Utils.isDirectory(fSelectedElement);

		InputDialog input = new InputDialog(fWindow.getShell(), Messages.NewFolderAction_InputTitle,
				Messages.NewFolderAction_InputMessage, "", null); //$NON-NLS-1$
		if (input.open() == Window.OK)
		{
			final String name = input.getValue();
			// run the folder creation in a job
			Job job = new Job(Messages.NewFolderAction_JobTitle)
			{

				@Override
				protected IStatus run(IProgressMonitor monitor)
				{
					try
					{
						IFileStore parentStore = fileStore;
						Object element = fSelectedElement;
						if (!selectionIsDirectory && fileStore.getParent() != null)
						{
							parentStore = fileStore.getParent();
							// TODO: needs to find the element corresponding to
							// the parent folder
							element = null;
						}
						IFileStore newFolder = parentStore.getChild(name);
						newFolder.mkdir(EFS.NONE, monitor);

						if (PreferenceUtils.getUpdatePermissions(PermissionDirection.UPLOAD)
								&& PreferenceUtils.getSpecificPermissions(PermissionDirection.UPLOAD))
						{
							// sets the permissions
							IFileInfo newInfo = newFolder.fetchInfo(EFS.NONE, monitor);
							if (newInfo instanceof IExtendedFileInfo)
							{
								IExtendedFileInfo extendedInfo = (IExtendedFileInfo) newInfo;
								extendedInfo.setPermissions(PreferenceUtils
										.getFolderPermissions(PermissionDirection.UPLOAD));
								newFolder.putInfo(extendedInfo, IExtendedFileInfo.SET_PERMISSIONS, monitor);
							}
						}

						IOUIPlugin.refreshNavigatorView(element);
					}
					catch (CoreException e)
					{
						showError(e);
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	protected boolean updateSelection(IStructuredSelection selection)
	{
		fSelectedElement = null;

		if (selection != null && !selection.isEmpty())
		{
			Object element = selection.getFirstElement();
			if (element instanceof IAdaptable)
			{
				fSelectedElement = (IAdaptable) element;
			}
		}

		return super.updateSelection(selection) && fSelectedElement != null;
	}

	private void showError(Exception exception)
	{
		UIUtils.showErrorMessage(exception.getLocalizedMessage(), exception);
	}
}
