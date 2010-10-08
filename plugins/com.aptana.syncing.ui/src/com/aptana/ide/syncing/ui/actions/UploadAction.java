/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.actions;

import java.text.MessageFormat;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ide.syncing.core.old.Synchronizer;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;
import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.internal.SyncUtils;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ui.DialogUtils;

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
						fileStores[i] = SyncUtils.getFileStore(files[i]);
					}
					IFileStore[] sourceFiles;
					if (fSelectedFromSource)
					{
						sourceFiles = EFSUtils.getAllFiles(fileStores, true, false, monitor);
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
					return new Status(Status.ERROR, SyncingUIPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
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
