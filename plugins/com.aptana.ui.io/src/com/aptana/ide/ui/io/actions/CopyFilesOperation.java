/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable questionableAssignment

package com.aptana.ide.ui.io.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.io.efs.SyncUtils;
import com.aptana.core.io.vfs.IExtendedFileStore;
import com.aptana.core.logging.IdeLog;
import com.aptana.ide.core.io.preferences.CloakingUtils;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ui.util.UIUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class CopyFilesOperation
{

	/**
	 * The parent shell used to show any dialogs
	 */
	private Shell fShell;

	/**
	 * Flag to indicate if the operation has been canceled by the user
	 */
	private boolean fCancelled;

	private static enum OverwriteStatus
	{
		YES, YES_TO_ALL, NO, CANCEL
	}

	private OverwriteStatus overwriteStatus;

	/**
	 * Constructor.
	 * 
	 * @param shell
	 *            the active shell
	 */
	public CopyFilesOperation(Shell shell)
	{
		if (shell == null)
		{
			fShell = UIUtils.getActiveShell();
		}
		else
		{
			fShell = shell;
		}
	}

	/**
	 * Copies an array of sources to the destination location.
	 * 
	 * @param sources
	 *            the array of IAdaptable objects
	 * @param destination
	 *            the destination file store
	 * @param listener
	 *            an optional job listener
	 */
	public void copyFiles(IAdaptable[] sources, IFileStore destination, IJobChangeListener listener)
	{
		IFileStore[] fileStores = new IFileStore[sources.length];
		for (int i = 0; i < fileStores.length; ++i)
		{
			fileStores[i] = Utils.getFileStore(sources[i]);
		}
		copyFiles(fileStores, destination, listener);
	}

	/**
	 * Copies an array of sources to the destination location.
	 * 
	 * @param sources
	 *            the array of filenames
	 * @param destination
	 *            the destination file store
	 * @param listener
	 *            an optional job listener
	 */
	public void copyFiles(String[] filenames, IFileStore destination, IJobChangeListener listener)
	{
		copyFiles(getFileStores(filenames), destination, listener);
	}

	/**
	 * Copies an array of sources to the destination location.
	 * 
	 * @param sources
	 *            the array of source file stores
	 * @param destination
	 *            the file store representing the destination folder
	 * @param monitor
	 *            an optional progress monitor
	 */
	public IStatus copyFiles(IFileStore[] sources, IFileStore destination, IProgressMonitor monitor)
	{
		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}

		int successCount = 0;
		for (IFileStore source : sources)
		{
			if (copyFile(source, destination.getChild(source.getName()), monitor))
			{
				successCount++;
			}
			if (fCancelled || monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
		}
		return new Status(IStatus.OK, IOUIPlugin.PLUGIN_ID, successCount, Messages.CopyFilesOperation_Status_OK, null);
	}

	/**
	 * Copies an array of files from the source to the destination.
	 * 
	 * @param sources
	 *            the array of IAdaptable objects
	 * @param sourceRoot
	 *            the file store representing the root of source connection
	 * @param destinationRoot
	 *            the file store representing the root of target connection
	 * @param listener
	 *            an optional job listener
	 */
	public void copyFiles(IAdaptable[] sources, IFileStore sourceRoot, IFileStore destinationRoot,
			IJobChangeListener listener)
	{
		IFileStore[] fileStores = new IFileStore[sources.length];
		for (int i = 0; i < fileStores.length; ++i)
		{
			fileStores[i] = Utils.getFileStore(sources[i]);
		}
		copyFiles(fileStores, sourceRoot, destinationRoot, listener);
	}

	/**
	 * Copies an array of files from the source to the destination.
	 * 
	 * @param sources
	 *            the array of filenames
	 * @param sourceRoot
	 *            the file store representing the root of source connection
	 * @param destinationRoot
	 *            the file store representing the root of target connection
	 * @param listener
	 *            an optional job listener
	 */
	public void copyFiles(String[] filenames, IFileStore sourceRoot, IFileStore destinationRoot,
			IJobChangeListener listener)
	{
		copyFiles(getFileStores(filenames), sourceRoot, destinationRoot, listener);
	}

	/**
	 * Copies an array of files from the source to the destination.
	 * 
	 * @param sources
	 *            the array of source file stores
	 * @param sourceRoot
	 *            the file store representing the root of source connection
	 * @param destinationRoot
	 *            the file store representing the root of target connection
	 * @param monitor
	 *            an optional progress monitor
	 */
	public IStatus copyFiles(IFileStore[] sources, IFileStore sourceRoot, IFileStore destinationRoot,
			IProgressMonitor monitor)
	{
		if (monitor == null)
		{
			monitor = new NullProgressMonitor();
		}

		int successCount = 0;
		for (IFileStore source : sources)
		{
			if (copyFile(source, sourceRoot, destinationRoot, monitor))
			{
				successCount++;
			}
			if (fCancelled || monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
		}
		return new Status(IStatus.OK, IOUIPlugin.PLUGIN_ID, successCount, Messages.CopyFilesOperation_Status_OK, null);
	}

	/**
	 * Checks if there is structural conflict for transferring the sources to the destination.
	 * 
	 * @param destination
	 *            the destination adaptable
	 * @param sources
	 *            the array of source adaptables
	 * @return a descriptive error message if the validation fails, and null otherwise
	 */
	public static String validateDestination(IAdaptable destination, IAdaptable[] sources)
	{
		IFileStore[] sourceStores = new IFileStore[sources.length];
		for (int i = 0; i < sourceStores.length; ++i)
		{
			sourceStores[i] = Utils.getFileStore(sources[i]);
		}
		return validateDestination(destination, sourceStores);
	}

	/**
	 * Checks if there is structural conflict for transferring the sources to the destination.
	 * 
	 * @param destination
	 *            the destination adaptable
	 * @param sourceNames
	 *            the array of source filenames
	 * @return a descriptive error message if the validation fails, and null otherwise
	 */
	public static String validateDestination(IAdaptable destination, String[] sourceNames)
	{
		return validateDestination(destination, getFileStores(sourceNames));
	}

	/**
	 * @param sourceStore
	 *            the file to be copied
	 * @param destinationStore
	 *            the destination location
	 * @param monitor
	 *            the progress monitor
	 * @return true if the file is successfully copied, false if the operation did not go through for any reason
	 */
	protected boolean copyFile(IFileStore sourceStore, IFileStore destinationStore, IProgressMonitor monitor)
	{
		if (sourceStore == null || CloakingUtils.isFileCloaked(sourceStore))
		{
			return false;
		}

		boolean success = true;
		monitor.subTask(MessageFormat.format(Messages.CopyFilesOperation_Copy_Subtask, sourceStore.getName(),
				destinationStore.getName()));

		if (destinationStore.equals(sourceStore))
		{
			destinationStore = getNewNameFor(destinationStore);
			if (destinationStore == null)
			{
				return false;
			}
		}
		try
		{
			IFileStore[] childStores = Utils.isDirectory(sourceStore) ? sourceStore.childStores(EFS.NONE, monitor)
					: new IFileStore[0];
			if (Utils.exists(destinationStore) && sourceStore.getName().equals(destinationStore.getName()))
			{
				// a name conflict; ask to overwrite
				if (overwriteStatus != OverwriteStatus.YES_TO_ALL)
				{
					final IFileStore dStore = destinationStore;
					final IFileStore sStore = sourceStore;
					fShell.getDisplay().syncExec(new Runnable()
					{

						public void run()
						{
							MessageDialog dialog = new MessageDialog(fShell,
									Messages.CopyFilesOperation_OverwriteTitle, null, MessageFormat.format(
											Messages.CopyFilesOperation_OverwriteWarning, dStore.toString(),
											sStore.toString()), MessageDialog.CONFIRM, new String[] {
											IDialogConstants.YES_LABEL, IDialogConstants.YES_TO_ALL_LABEL,
											IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
							int retCode = dialog.open();
							switch (retCode)
							{
								case 0: // Yes
									overwriteStatus = OverwriteStatus.YES;
									break;
								case 1: // Yes to All
									overwriteStatus = OverwriteStatus.YES_TO_ALL;
									break;
								case 2: // No
									overwriteStatus = OverwriteStatus.NO;
									break;
								default:
									overwriteStatus = OverwriteStatus.CANCEL;
							}
						}
					});
					switch (overwriteStatus)
					{
						case CANCEL:
							monitor.setCanceled(true);
							// let it fall through since it would return false as well
						case NO:
							return false;
					}
				}
			}
			SyncUtils.copy(sourceStore, null, destinationStore, EFS.NONE, monitor);

			// copy the children recursively
			IFileStore destChildStore;
			for (IFileStore childStore : childStores)
			{
				destChildStore = destinationStore.getChild(childStore.getName());
				copyFile(childStore, destChildStore, monitor);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(IOUIPlugin.getDefault(),
					MessageFormat.format(Messages.CopyFilesOperation_ERR_FailedToCopy, sourceStore, destinationStore),
					e);
			success = false;
		}
		return success;
	}

	/**
	 * @param sourceStore
	 *            the file to be copied
	 * @param sourceRoot
	 *            the source root
	 * @param destinationRoot
	 *            the destination root
	 * @param monitor
	 *            the progress monitor
	 * @return true if the file is successfully copied, false if the operation did not go through for any reason
	 */
	protected boolean copyFile(IFileStore sourceStore, IFileStore sourceRoot, IFileStore destinationRoot,
			IProgressMonitor monitor)
	{
		if (sourceStore == null || CloakingUtils.isFileCloaked(sourceStore))
		{
			return false;
		}

		boolean success = true;
		IFileStore[] sourceStores = null, targetStores = null;
		try
		{
			if (sourceStore.equals(sourceRoot))
			{
				// copying the whole source
				sourceStores = sourceRoot.childStores(EFS.NONE, monitor);
				targetStores = new IFileStore[sourceStores.length];
				for (int i = 0; i < targetStores.length; ++i)
				{
					targetStores[i] = destinationRoot.getChild(sourceStores[i].getName());
				}
			}
			else if (sourceRoot.isParentOf(sourceStore))
			{
				// finds the relative path of the file to be copied and maps to
				// the destination target
				sourceStores = new IFileStore[1];
				sourceStores[0] = sourceStore;

				targetStores = new IFileStore[1];
				String sourceRootPath = sourceRoot.toString();
				String sourcePath = sourceStore.toString();
				int index = sourcePath.indexOf(sourceRootPath);
				if (index > -1)
				{
					String relativePath = sourcePath.substring(index + sourceRootPath.length());
					targetStores[0] = destinationRoot.getFileStore(new Path(relativePath));
					// makes sure the parent folder is created on the
					// destination side
					IFileStore parent = getFolderStore(targetStores[0]);
					if (parent != targetStores[0])
					{
						parent.mkdir(EFS.NONE, monitor);
					}
				}
			}
			if (sourceStores == null)
			{
				// the file to be copied is not a child of the source root;
				// cannot copy
				success = false;
				sourceStores = new IFileStore[0];
				targetStores = new IFileStore[0];
			}

			for (int i = 0; i < sourceStores.length; ++i)
			{
				success = copyFile(sourceStores[i], targetStores[i], monitor) && success;
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(IOUIPlugin.getDefault(), MessageFormat.format(
					Messages.CopyFilesOperation_ERR_FailedToCopyToDest, sourceStore, destinationRoot), e);
			success = false;
		}
		return success;
	}

	private void copyFiles(final IFileStore[] sources, final IFileStore destination, IJobChangeListener listener)
	{
		Job job = new Job(Messages.CopyFilesOperation_CopyJob_Title)
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				return copyFiles(sources, destination, monitor);
			}

			public boolean belongsTo(Object family)
			{
				if (Messages.CopyFilesOperation_CopyJob_Title.equals(family))
				{
					return true;
				}
				return super.belongsTo(family);
			}
		};
		if (listener != null)
		{
			job.addJobChangeListener(listener);
		}
		job.setUser(true);
		job.schedule();
	}

	private void copyFiles(final IFileStore[] sources, final IFileStore sourceRoot, final IFileStore destinationRoot,
			IJobChangeListener listener)
	{
		Job job = new Job(Messages.CopyFilesOperation_CopyJob_Title)
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				return copyFiles(sources, sourceRoot, destinationRoot, monitor);
			}

			public boolean belongsTo(Object family)
			{
				if (Messages.CopyFilesOperation_CopyJob_Title.equals(family))
				{
					return true;
				}
				return super.belongsTo(family);
			}
		};
		if (listener != null)
		{
			job.addJobChangeListener(listener);
		}
		job.setUser(true);
		job.schedule();
	}

	/**
	 * Returns a new name for a copy of the file.
	 * 
	 * @param originalFile
	 *            the file store
	 * @return the new file store for the copy, or <code>null</code> if the file should not be copied
	 */
	private IFileStore getNewNameFor(final IFileStore originalFile)
	{
		final IFileStore parent = originalFile.getParent();
		final String[] returnValue = { "" }; //$NON-NLS-1$
		final String filename = originalFile.getName();
		final boolean isRemote = (originalFile instanceof IExtendedFileStore);

		fShell.getDisplay().syncExec(new Runnable()
		{

			public void run()
			{
				IInputValidator validator = new IInputValidator()
				{
					public String isValid(String string)
					{
						if (filename.equals(string))
						{
							return Messages.CopyFilesOperation_ERR_NameConflict;
						}
						if (!isRemote)
						{
							int type = Utils.isDirectory(originalFile) ? IResource.FOLDER : IResource.FILE;
							IStatus status = ResourcesPlugin.getWorkspace().validateName(string, type);
							if (!status.isOK())
							{
								return status.getMessage();
							}
							if (Utils.exists(parent.getChild(string)))
							{
								return Messages.CopyFilesOperation_ERR_NameExists;
							}
						}
						return null;
					}
				};

				InputDialog dialog = new InputDialog(fShell, Messages.CopyFilesOperation_NameConflictDialog_Title,
						MessageFormat.format(Messages.CopyFilesOperation_NameConflictDialog_Message, filename),
						getAutoNewNameFor(originalFile), validator);
				dialog.setBlockOnOpen(true);
				dialog.open();
				if (dialog.getReturnCode() == Window.CANCEL)
				{
					returnValue[0] = null;
				}
				else
				{
					returnValue[0] = dialog.getValue();
				}
			}
		});
		if (returnValue[0] == null)
		{
			return null;
		}
		return parent.getChild(returnValue[0]);
	}

	private static String getAutoNewNameFor(IFileStore originalFile)
	{
		String name = originalFile.getName();
		IFileStore parent = originalFile.getParent();
		boolean isRemote = (originalFile instanceof IExtendedFileStore);

		String newName;
		int counter = 1;
		while (true)
		{
			if (counter > 1)
			{
				newName = MessageFormat.format(Messages.CopyFilesOperation_DefaultNewName_WithCount, counter, name);
			}
			else
			{
				newName = MessageFormat.format(Messages.CopyFilesOperation_DefaultNewName, name);
			}
			if (isRemote || !Utils.exists(parent.getChild(newName)))
			{
				return newName;
			}
			counter++;
		}
	}

	/**
	 * Checks if there is structural conflict for transferring the sources to the destination.
	 * 
	 * @param destination
	 *            the destination adaptable
	 * @param sourceStores
	 *            the array of source stores
	 * @return a descriptive error message if the validation fails, and null otherwise
	 */
	private static String validateDestination(IAdaptable destination, IFileStore[] sourceStores)
	{
		IResource resource = (IResource) destination.getAdapter(IResource.class);
		if (resource != null && !resource.isAccessible())
		{
			return Messages.CopyFilesOperation_DestinationNotAccessible;
		}
		IFileStore destinationStore = getFolderStore(destination);
		IFileStore sourceParentStore;
		for (IFileStore sourceStore : sourceStores)
		{
			sourceParentStore = sourceStore.getParent();
			if (destinationStore.equals(sourceStore)
					|| (sourceParentStore != null && destinationStore.equals(sourceParentStore)))
			{
				return Messages.CopyFilesOperation_ERR_SourceInDestination;
			}

			if (sourceStore.isParentOf(destinationStore))
			{
				return Messages.CopyFilesOperation_ERR_DestinationInSource;
			}
		}
		return null;
	}

	/**
	 * @param filename
	 *            the filename
	 * @return the corresponding file store, or null if it could not be found
	 */
	private static IFileStore getFileStore(String filename)
	{
		try
		{
			return EFS.getStore((new Path(filename).toFile().toURI()));
		}
		catch (CoreException e)
		{
		}
		return null;
	}

	/**
	 * @param filenames
	 *            an array of filenames
	 * @return the array of corresponding file stores
	 */
	private static IFileStore[] getFileStores(String[] filenames)
	{
		IFileStore[] fileStores = new IFileStore[filenames.length];
		for (int i = 0; i < fileStores.length; ++i)
		{
			fileStores[i] = getFileStore(filenames[i]);
		}
		return fileStores;
	}

	/**
	 * Gets the folder the file belongs in. If the file is a directory, returns itself.
	 * 
	 * @param adaptable
	 *            an IAdaptable that could adapt to an IFileStore
	 * @return the folder file store
	 */
	private static IFileStore getFolderStore(IAdaptable adaptable)
	{
		IFileStore store = Utils.getFileStore(adaptable);
		IFileInfo info = Utils.getFileInfo(adaptable, IExtendedFileStore.EXISTENCE);
		if (store != null && info != null && !info.isDirectory())
		{
			store = store.getParent();
		}
		return store;
	}
}
