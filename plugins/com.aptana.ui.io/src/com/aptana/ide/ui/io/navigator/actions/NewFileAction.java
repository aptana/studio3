/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.navigator.actions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import com.aptana.core.io.vfs.IExtendedFileInfo;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.ide.core.io.preferences.PreferenceUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class NewFileAction extends BaseSelectionListenerAction
{

	private IAdaptable fSelectedElement;
	private IWorkbenchWindow fWindow;
	private String fInitialFilename;

	public NewFileAction(IWorkbenchWindow window)
	{
		this(window, ""); //$NON-NLS-1$
	}

	public NewFileAction(IWorkbenchWindow window, String initialName)
	{
		super(Messages.NewFileAction_Text);
		fWindow = window;
		fInitialFilename = initialName;
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		setToolTipText(Messages.NewFileAction_ToolTip);
	}

	public void run()
	{
		if (fSelectedElement == null)
		{
			return;
		}

		InputDialog input = new InputDialog(fWindow.getShell(), Messages.NewFileAction_InputTitle,
				Messages.NewFileAction_InputMessage, fInitialFilename, null);
		if (input.open() == Window.OK)
		{
			createFile(input.getValue());
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

	protected InputStream getInitialContents(IPath filePath)
	{
		return null;
	}

	protected IFileStore getSelectedDirectory()
	{
		final IFileStore fileStore = Utils.getFileStore(fSelectedElement);
		boolean selectionIsDirectory = Utils.isDirectory(fSelectedElement);
		if (!selectionIsDirectory && fileStore.getParent() != null)
		{
			return fileStore.getParent();
		}
		return fileStore;
	}

	private void createFile(final String filename)
	{
		final IFileStore parentStore = getSelectedDirectory();
		final IFileStore newFile = parentStore.getChild(filename);
		if (Utils.exists(newFile))
		{
			if (!MessageDialog.openConfirm(fWindow.getShell(), Messages.NewFileAction_Confirm_Title,
					Messages.NewFileAction_Confirm_Message))
			{
				return;
			}
		}

		// run the file creation in a job
		Job job = new Job(Messages.NewFileAction_JobTitle)
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try
				{
					OutputStream out = newFile.openOutputStream(EFS.NONE, monitor);
					InputStream in = getInitialContents(Path.fromOSString(newFile.toString()));
					if (in != null)
					{
						// creates the initial contents
						try
						{
							IOUtil.pipe(in, out);
						}
						catch (IOException e)
						{
							IdeLog.logError(IOUIPlugin.getDefault(), e);
						}
						finally
						{
							try
							{
								in.close();
							}
							catch (IOException e)
							{
							}
						}
					}
					try
					{
						out.close();
					}
					catch (IOException e)
					{
					}

					// sets the permissions
					IFileInfo newInfo = newFile.fetchInfo(EFS.NONE, monitor);
					if (newInfo instanceof IExtendedFileInfo)
					{
						IExtendedFileInfo extendedInfo = (IExtendedFileInfo) newInfo;
						extendedInfo.setPermissions(PreferenceUtils.getFilePermissions());
						newFile.putInfo(extendedInfo, IExtendedFileInfo.SET_PERMISSIONS, monitor);
					}

					// opens it in the editor
					EditorUtils.openFileInEditor(newFile, null);

					// refreshes the parent folder
					final IFileStore fileStore = Utils.getFileStore(fSelectedElement);
					boolean selectionIsDirectory = Utils.isDirectory(fSelectedElement);
					if (selectionIsDirectory)
					{
						IOUIPlugin.refreshNavigatorView(fSelectedElement);
					}
					else
					{
						IOUIPlugin.refreshNavigatorView(fileStore.getParent());
					}
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

	private void showError(Exception exception)
	{
		UIUtils.showErrorMessage(exception.getLocalizedMessage(), exception);
	}
}
