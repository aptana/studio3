/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ide.ui.io.Utils;
import com.aptana.ui.DialogUtils;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class UploadAction extends BaseSyncAction
{

	private IJobChangeListener jobListener;

	private static String MESSAGE_TITLE = StringUtil.ellipsify(Messages.UploadAction_MessageTitle);

	protected void performAction(final IAdaptable[] files, final ISiteConnection site) throws CoreException
	{
		final Synchronizer syncer = new Synchronizer();
		Job job = new Job(MESSAGE_TITLE)
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				monitor.subTask(StringUtil.ellipsify(Messages.BaseSyncAction_RetrievingItems));

				try
				{
					IConnectionPoint source = site.getSource();
					IConnectionPoint target = site.getDestination();
					// retrieves the root filestore of each end
					IFileStore sourceRoot = (fSourceRoot == null) ? source.getRoot() : fSourceRoot;
					// makes sure the target end point is connected
					if (!target.isConnected())
					{
						target.connect(monitor);
					}
					final IFileStore targetRoot = (fDestinationRoot == null) ? target.getRoot() : fDestinationRoot;
					syncer.setClientFileManager(source);
					syncer.setServerFileManager(target);
					syncer.setClientFileRoot(sourceRoot);
					syncer.setServerFileRoot(targetRoot);

					// gets the filestores of the files to be copied
					IFileStore[] fileStores = new IFileStore[files.length];
					for (int i = 0; i < fileStores.length; ++i)
					{
						fileStores[i] = Utils.getFileStore(files[i]);
					}
					IFileStore[] sourceFiles;
					if (fSelectedFromSource)
					{
						sourceFiles = EFSUtils.getAllFiles(fileStores, true, false, monitor);
						// adds the parent directories
						List<IFileStore> newFiles = new ArrayList<IFileStore>();
						for (IFileStore fileStore : fileStores)
						{
							if (!fileStore.equals(sourceRoot))
							{
								List<IFileStore> folders = new ArrayList<IFileStore>();
								IFileStore parent = fileStore.getParent();
								while (parent != null && !sourceRoot.equals(parent))
								{
									if (!newFiles.contains(parent))
									{
										folders.add(0, parent);
									}
									parent = parent.getParent();
								}
								newFiles.addAll(folders);
							}
						}
						newFiles.addAll(Arrays.asList(sourceFiles));
						sourceFiles = newFiles.toArray(new IFileStore[newFiles.size()]);
					}
					else
					{
						// the selection is from the destination, so do a reverse download
						sourceFiles = SyncUtils.getDownloadFiles(target, source, fileStores, true, monitor);
					}
					final VirtualFileSyncPair[] items = syncer.createSyncItems(sourceFiles, new IFileStore[0], monitor);

					syncer.setEventHandler(new SyncActionEventHandler(Messages.UploadAction_MessageTitle, items.length,
							monitor, new SyncActionEventHandler.Client()
							{

								public void syncCompleted()
								{
									IOUIPlugin.refreshNavigatorView(targetRoot);
									postAction(syncer);
									syncer.setEventHandler(null);
									syncer.disconnect();
								}
							}));
					syncer.upload(items, monitor);
				}
				catch (OperationCanceledException e)
				{
					return Status.CANCEL_STATUS;
				}
				catch (Exception e)
				{
					IdeLog.logError(SyncingUIPlugin.getDefault(), Messages.UploadAction_ERR_FailToUpload, e);
					return new Status(Status.ERROR, SyncingUIPlugin.PLUGIN_ID, Messages.UploadAction_ERR_FailToUpload,
							e);
				}
				finally
				{
					StudioAnalytics.getInstance().sendEvent(
							new FeatureEvent("remote.upload." + site.getDestination().getType(), null)); //$NON-NLS-1$
				}

				return Status.OK_STATUS;
			}
		};
		if (jobListener != null)
		{
			job.addJobChangeListener(jobListener);
		}
		job.setUser(true);
		job.schedule();
	}

	public void addJobListener(IJobChangeListener listener)
	{
		jobListener = listener;
	}

	@Override
	protected String getMessageTitle()
	{
		return MESSAGE_TITLE;
	}

	private void postAction(final Synchronizer syncer)
	{
		getShell().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				DialogUtils.openIgnoreMessageDialogInformation(getShell(), MESSAGE_TITLE, MessageFormat.format(
						Messages.UploadAction_PostMessage, syncer.getClientFileTransferedCount(),
						syncer.getServerDirectoryCreatedCount()), SyncingUIPlugin.getDefault().getPreferenceStore(),
						IPreferenceConstants.IGNORE_DIALOG_FILE_UPLOAD);
			}
		});
	}
}
