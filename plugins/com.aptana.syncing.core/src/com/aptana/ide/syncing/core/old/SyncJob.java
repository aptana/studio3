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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.syncing.core.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.core.io.efs.EFSUtils;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia (mxia@aptana.com)
 */
public class SyncJob extends Job implements ISyncEventHandler
{

	/**
	 * The feedback client.
	 */
	public static interface Client
	{
		/**
		 * Indicates the job started syncing the specified pair.
		 * 
		 * @param item
		 *            the pair being synced
		 */
		public void syncItem(VirtualFileSyncPair item);

		/**
		 * Indicates a certain number of bytes has been synced for a specific pair.
		 * 
		 * @param item
		 *            the pair being synced
		 * @param bytes
		 *            the number of bytes transferred
		 */
		public void syncProgress(VirtualFileSyncPair item, long bytes);

		/**
		 * Indicates the specified pair has completed.
		 * 
		 * @param item
		 *            the completed pair
		 * @param allDone
		 *            true if all pairs have been completed, false otherwise
		 */
		public void syncDone(VirtualFileSyncPair item, boolean allDone);

		/**
		 * Indicates the specified pair has error during transfer.
		 * 
		 * @param item
		 *            the error pair
		 * @param allDone
		 *            true if all pairs have been completed, false otherwise
		 */
		public void syncError(VirtualFileSyncPair item, boolean allDone);
	}

	/**
	 * BOTH
	 */
	public static final int BOTH = 0;

	/**
	 * UPLOAD
	 */
	public static final int UPLOAD = 1;

	/**
	 * DOWNLOAD
	 */
	public static final int DOWNLOAD = 2;

	private static final String NAME = "Syncing"; //$NON-NLS-1$

	private Synchronizer fSyncer;
	private List<VirtualFileSyncPair> fPairs;
	private int fDirection;
	private boolean fDeleteRemote;
	private boolean fDeleteLocal;

	private List<VirtualFileSyncPair> fCompletedPairs;
	private int fErrorPairs;
	private IProgressMonitor progressMonitor;

	private Client fClient;

	/**
	 * Constructor.
	 * 
	 * @param syncer
	 *            the synchronizer
	 * @param pairs
	 *            the list of pairs to sync
	 * @param direction
	 *            the direction which the pairs should be synced (BOTH, UPLOAD, or DOWNLOAD)
	 * @param deleteRemote
	 *            indicates if the remote files should be deleted
	 * @param deleteLocal
	 *            indicates if the local files should be deleted
	 * @param client
	 *            the client to receive feedbacks on the progress of syncing
	 */
	public SyncJob(Synchronizer syncer, List<VirtualFileSyncPair> pairs, int direction, boolean deleteRemote,
			boolean deleteLocal, Client client)
	{
		super(NAME);
		fSyncer = syncer;
		fPairs = pairs;
		fDirection = direction;
		fDeleteRemote = deleteRemote;
		fDeleteLocal = deleteLocal;
		fClient = client;
		fCompletedPairs = new ArrayList<VirtualFileSyncPair>();

		sortPairs();
		fSyncer.setEventHandler(this);
	}

	/**
	 * Returns an array of completed pairs.
	 * 
	 * @return the array of completed pairs
	 */
	public VirtualFileSyncPair[] getCompletedPairs()
	{
		return fCompletedPairs.toArray(new VirtualFileSyncPair[fCompletedPairs.size()]);
	}

	/**
	 * Returns the total number of bytes for the entire transfer.
	 * 
	 * @return the total number of bytes for the entire transfer
	 */
	public long getTotalTransferBytes()
	{
		long bytes = 0;
		int size = fPairs.size();
		for (int i = 0; i < size; ++i)
		{
			bytes += getTransferBytes(fPairs.get(i));
		}
		return bytes;
	}

	public long getTransferBytes(VirtualFileSyncPair pair)
	{
		switch (fDirection)
		{
			case BOTH:
				int state = pair.getSyncState();
				if (state == SyncState.ClientItemIsNewer || state == SyncState.ClientItemOnly
						|| state == SyncState.ClientItemDeleted)
				{
					return pair.getSourceFile().fetchInfo().getLength();
				}
				if (state == SyncState.ServerItemIsNewer || state == SyncState.ServerItemOnly
						|| state == SyncState.ServerItemDeleted)
				{
					return pair.getDestinationFile().fetchInfo().getLength();
				}
				return 0;
			case UPLOAD:
				return pair.getSourceFile().fetchInfo().getLength();
			case DOWNLOAD:
				return pair.getDestinationFile().fetchInfo().getLength();
		}
		return 0;
	}

	public int getErrorCount()
	{
		return fPairs.size() - fCompletedPairs.size();
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncContinue()
	 */
	public boolean syncContinue() {
		return !progressMonitor.isCanceled();
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncDone(VirtualFileSyncPair)
	 */
	public void syncDone(VirtualFileSyncPair item)
	{
		if (progressMonitor.isCanceled())
		{
			return;
		}
		fCompletedPairs.add(item);
		int syncState = item.getSyncState();
		if (syncState == SyncState.ClientItemOnly && fDeleteLocal && (fDirection == BOTH || fDirection == DOWNLOAD))
		{
			item.setSyncState(SyncState.ClientItemDeleted);
		}
		else if (syncState == SyncState.ServerItemOnly && fDeleteRemote && (fDirection == BOTH || fDirection == UPLOAD))
		{
			item.setSyncState(SyncState.ServerItemDeleted);
		}
		if (fClient != null)
		{
			fClient.syncDone(item, isDone());
		}
		progressMonitor.worked(1);
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncErrorEvent(VirtualFileSyncPair, Exception)
	 */
	public boolean syncErrorEvent(VirtualFileSyncPair item, Exception e)
	{
		if (progressMonitor.isCanceled())
		{
			return false;
		}

		fErrorPairs++;
		if (fClient != null)
		{
			fClient.syncError(item, isDone());
		}
		return true;
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncEvent(VirtualFileSyncPair, int, int)
	 */
	public boolean syncEvent(VirtualFileSyncPair item, int index, int totalItems)
	{
		if (progressMonitor.isCanceled())
		{
			return false;
		}
		if (fClient != null)
		{
			fClient.syncItem(item);
		}
		return true;
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncTransferring(VirtualFileSyncPair, long)
	 */
	public void syncTransferring(VirtualFileSyncPair item, long bytes)
	{
		if (progressMonitor.isCanceled())
		{
			return;
		}
		if (fClient != null)
		{
			fClient.syncProgress(item, bytes);
		}
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#getFilesEvent(IConnectionPoint, String)
	 */
	public boolean getFilesEvent(IConnectionPoint manager, String path)
	{
		return true;
	}

	/**
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		this.progressMonitor = monitor;

		if (fPairs.size() == 0)
		{
			// nothing to be synced (likely because all files are skipped), so considers it finished
			if (fClient != null)
			{
				fClient.syncDone(null, true);
			}
		}
		else
		{
			monitor.beginTask("Syncing files", fPairs.size()); //$NON-NLS-1$
			VirtualFileSyncPair[] pairs = fPairs.toArray(new VirtualFileSyncPair[fPairs.size()]);
			switch (fDirection)
			{
				case BOTH:
					fullSync(pairs);
					break;
				case UPLOAD:
					upload(pairs);
					break;
				case DOWNLOAD:
					download(pairs);
					break;
			}
		}

		return Status.OK_STATUS;
	}

	private boolean fullSync(VirtualFileSyncPair[] pairs)
	{
		return fSyncer.fullSyncAndDelete(pairs, fDeleteLocal, fDeleteRemote, this.progressMonitor);
	}

	private boolean download(VirtualFileSyncPair[] pairs)
	{
		try
		{
			fSyncer.downloadAndDelete(pairs, fDeleteLocal, this.progressMonitor);
			return true;
		}
		catch (CoreException e)
		{
		}
		return false;
	}

	private boolean upload(VirtualFileSyncPair[] pairs)
	{
		try
		{
			fSyncer.uploadAndDelete(pairs, fDeleteRemote, this.progressMonitor);
			return true;
		}
		catch (CoreException e)
		{
		}
		return false;
	}

	private boolean isDone()
	{
		return fCompletedPairs.size() + fErrorPairs == fPairs.size();
	}

	private void sortPairs()
	{
		Collections.sort(fPairs, new Comparator<VirtualFileSyncPair>()
		{

			public int compare(VirtualFileSyncPair pair1, VirtualFileSyncPair pair2)
			{
				try
				{
					if (pair1 != null && pair2 != null)
					{
						if (fDeleteLocal && pair1.getSyncState() == SyncState.ClientItemOnly
								&& pair2.getSyncState() == SyncState.ClientItemOnly && pair1.getSourceFile() != null
								&& pair2.getSourceFile() != null)
						{
							String s1 = EFSUtils.getAbsolutePath(pair1.getSourceFile());
							String s2 = EFSUtils.getAbsolutePath(pair2.getSourceFile());
							if (s1 != null)
							{
								return -1 * s1.compareToIgnoreCase(s2);
							}
						}
						else if (fDeleteRemote && pair1.getSyncState() == SyncState.ServerItemOnly
								&& pair2.getSyncState() == SyncState.ServerItemOnly
								&& pair1.getDestinationFile() != null && pair2.getDestinationFile() != null)
						{
							String s1 = EFSUtils.getAbsolutePath(pair1.getDestinationFile());
							String s2 = EFSUtils.getAbsolutePath(pair2.getDestinationFile());
							if (s1 != null)
							{
								return -1 * s1.compareToIgnoreCase(s2);
							}
						}
					}
				}
				catch (Exception e)
				{
					// Do nothing and move on
				}
				return 0;
			}

		});
	}

}
