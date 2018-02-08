/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core.old.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.aptana.core.util.FileUtil;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.syncing.core.old.VirtualFileSyncPair;

/**
 * @author Max Stepanov (mstepanov@aptana.com)
 */
public class SyncEventHandlerAdapterWithProgressMonitor extends SyncEventHandlerAdapter
{

	private static final int TRANSFER_SCALE = 1024;
	private static final int PATH_DISPLAY_CHARACTERS = 40;

	private IProgressMonitor monitor;
	private Map<VirtualFileSyncPair, SubProgressMonitor> itemsProgress = new HashMap<VirtualFileSyncPair, SubProgressMonitor>();
	private Map<VirtualFileSyncPair, Long> itemsTransfer = new HashMap<VirtualFileSyncPair, Long>();

	/**
	 * 
	 */
	public SyncEventHandlerAdapterWithProgressMonitor(IProgressMonitor monitor)
	{
		this.monitor = (monitor != null) ? monitor : new NullProgressMonitor();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.core.io.sync.SyncEventHandlerAdapter#getFilesEvent(com.aptana.ide.core.io.IVirtualFileManager,
	 * java.lang.String)
	 */
	@Override
	public boolean getFilesEvent(IConnectionPoint manager, String path)
	{
		monitor.subTask(FileUtil.compressPath(path, PATH_DISPLAY_CHARACTERS));
		return !monitor.isCanceled() && super.getFilesEvent(manager, path);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.sync.SyncEventHandlerAdapter#syncContinue()
	 */
	@Override
	public boolean syncContinue(IProgressMonitor monitor)
	{
		return !monitor.isCanceled() && super.syncContinue(monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.core.io.sync.SyncEventHandlerAdapter#syncDone(com.aptana.ide.core.io.sync.VirtualFileSyncPair)
	 */
	@Override
	public void syncDone(VirtualFileSyncPair item, IProgressMonitor monitor)
	{
		SubProgressMonitor itemProgressMonitor = itemsProgress.get(item);
		if (itemProgressMonitor != null)
		{
			itemProgressMonitor.done();
			itemsProgress.remove(item);
			itemsTransfer.remove(item);
		}
		super.syncDone(item, monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.core.io.sync.SyncEventHandlerAdapter#syncErrorEvent(com.aptana.ide.core.io.sync.VirtualFileSyncPair
	 * , java.lang.Exception)
	 */
	@Override
	public boolean syncErrorEvent(VirtualFileSyncPair item, Exception e, IProgressMonitor monitor)
	{
		SubProgressMonitor itemProgressMonitor = itemsProgress.get(item);
		if (itemProgressMonitor != null)
		{
			itemProgressMonitor.done();
			itemsProgress.remove(item);
			itemsTransfer.remove(item);
		}
		return !monitor.isCanceled() && super.syncErrorEvent(item, e, monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.core.io.sync.SyncEventHandlerAdapter#syncEvent(com.aptana.ide.core.io.sync.VirtualFileSyncPair,
	 * int, int)
	 */
	@Override
	public boolean syncEvent(VirtualFileSyncPair item, int index, int totalItems, IProgressMonitor monitor)
	{
		SubProgressMonitor itemProgressMonitor = itemsProgress.get(item);
		if (itemProgressMonitor == null && item != null)
		{
			itemProgressMonitor = new SubProgressMonitor(monitor, 1);
			itemsProgress.put(item, itemProgressMonitor);
			itemsTransfer.put(item, Long.valueOf(0));
			monitor.subTask(FileUtil.compressPath(item.getRelativePath(), PATH_DISPLAY_CHARACTERS));
			itemProgressMonitor.beginTask(item.getRelativePath(), getItemProgressAmount(item));
		}
		return !monitor.isCanceled() && super.syncEvent(item, index, totalItems, monitor);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ide.core.io.sync.SyncEventHandlerAdapter#syncTransferring(com.aptana.ide.core.io.sync.VirtualFileSyncPair
	 * , long)
	 */
	@Override
	public void syncTransferring(VirtualFileSyncPair item, long bytes, IProgressMonitor monitor)
	{
		SubProgressMonitor itemProgressMonitor = itemsProgress.get(item);
		if (itemProgressMonitor != null)
		{
			long delta = bytes - ((Long) itemsTransfer.get(item));
			itemsTransfer.put(item, Long.valueOf(bytes));
			itemProgressMonitor.worked((int) (delta / TRANSFER_SCALE));
		}
		super.syncTransferring(item, bytes, monitor);
	}

	private static int getItemProgressAmount(VirtualFileSyncPair item)
	{
		switch (item.getSyncDirection())
		{
			case VirtualFileSyncPair.Direction_ClientToServer:
				return (int) (item.getSourceFile().fetchInfo().getLength() / TRANSFER_SCALE);
			case VirtualFileSyncPair.Direction_ServerToClient:
				return (int) (item.getDestinationFile().fetchInfo().getLength() / TRANSFER_SCALE);
		}
		return 1;
	}

}
