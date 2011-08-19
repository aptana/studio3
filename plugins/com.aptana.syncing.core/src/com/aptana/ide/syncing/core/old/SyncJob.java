/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.io.efs.EFSUtils;
import com.aptana.ide.core.io.IConnectionPoint;

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

	private Synchronizer fSyncer;
	private List<VirtualFileSyncPair> fPairs;
	private int fDirection;
	private boolean fDeleteRemote;
	private boolean fDeleteLocal;

	private List<VirtualFileSyncPair> fCompletedPairs;
	private int fErrorPairs;

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
			boolean deleteLocal, Client client, String name)
	{
		super(MessageFormat.format("Synchronizing {0}", name)); //$NON-NLS-1$
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
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncContinue(IProgressMonitor)
	 */
	public boolean syncContinue(IProgressMonitor monitor)
	{
		return !monitor.isCanceled();
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncDone(VirtualFileSyncPair, IProgressMonitor)
	 */
	public void syncDone(VirtualFileSyncPair item, IProgressMonitor monitor)
	{
		if (monitor.isCanceled())
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
	}

	/**
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncErrorEvent(VirtualFileSyncPair, Exception,
	 *      IProgressMonitor)
	 */
	public boolean syncErrorEvent(VirtualFileSyncPair item, Exception e, IProgressMonitor monitor)
	{
		if (monitor.isCanceled())
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
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncEvent(VirtualFileSyncPair, int, int,
	 *      IProgressMonitor)
	 */
	public boolean syncEvent(VirtualFileSyncPair item, int index, int totalItems, IProgressMonitor monitor)
	{
		if (monitor.isCanceled())
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
	 * @see com.aptana.ide.syncing.core.events.sync.ISyncEventHandler#syncTransferring(VirtualFileSyncPair, long,
	 *      IProgressMonitor)
	 */
	public void syncTransferring(VirtualFileSyncPair item, long bytes, IProgressMonitor monitor)
	{
		if (monitor.isCanceled())
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
			SubMonitor sub = SubMonitor.convert(monitor, "Syncing files", fPairs.size()); //$NON-NLS-1$
			VirtualFileSyncPair[] pairs = fPairs.toArray(new VirtualFileSyncPair[fPairs.size()]);
			switch (fDirection)
			{
				case BOTH:
					fullSync(pairs, sub.newChild(fPairs.size()));
					break;
				case UPLOAD:
					upload(pairs, sub.newChild(fPairs.size()));
					break;
				case DOWNLOAD:
					download(pairs, sub.newChild(fPairs.size()));
					break;
			}
		}

		return Status.OK_STATUS;
	}

	private boolean fullSync(VirtualFileSyncPair[] pairs, IProgressMonitor sub)
	{
		return fSyncer.fullSyncAndDelete(pairs, fDeleteLocal, fDeleteRemote, sub);
	}

	private boolean download(VirtualFileSyncPair[] pairs, IProgressMonitor sub)
	{
		try
		{
			fSyncer.downloadAndDelete(pairs, fDeleteLocal, sub);
			return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}

	private boolean upload(VirtualFileSyncPair[] pairs, IProgressMonitor sub)
	{
		try
		{
			fSyncer.uploadAndDelete(pairs, fDeleteRemote, sub);
			return true;
		}
		catch (Exception e)
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
