/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core.sourcelookup;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IDebugTarget;

/**
 * @author Max Stepanov
 */
public final class RemoteSourceCacheManager implements IDebugEventSetListener {

	private Map<URI, RemoteFileStorage> cache = new HashMap<URI, RemoteFileStorage>();
	private IFileContentRetriever fileContentRetriever;

	/**
	 * getStorage
	 * 
	 * @param uri
	 * @return RemoteFileStorage
	 */
	public synchronized RemoteFileStorage getStorage(URI uri) {
		return cache.get(uri);
	}

	/**
	 * add
	 * 
	 * @param uri
	 * @param storage
	 */
	public synchronized void add(URI uri, RemoteFileStorage storage) {
		cache.put(uri, storage);
		if (storage.getFileContentRetriever() == null) {
			storage.setFileContentRetriever(fileContentRetriever);
		}

	}

	/**
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent event : events) {
			if (event.getSource() instanceof IDebugTarget) {
				switch (event.getKind()) {
					case DebugEvent.CREATE:
						updateStorageContent((IDebugTarget) event.getSource(), false);
						break;
					case DebugEvent.TERMINATE:
						updateStorageContent((IDebugTarget) event.getSource(), true);
						break;
					default:
						break;
				}
			}
		}
	}

	/**
	 * updateStorageContent
	 * 
	 * @param target
	 * @param clear
	 */
	private synchronized void updateStorageContent(IDebugTarget target, boolean clear) {
		fileContentRetriever = (IFileContentRetriever) target.getAdapter(IFileContentRetriever.class);
		if (fileContentRetriever != null) {
			for (RemoteFileStorage storage : cache.values()) {
				if (clear) {
					if (storage.getFileContentRetriever() == fileContentRetriever) { // $codepro.audit.disable useEquals
						storage.setFileContentRetriever(null);

					}
				} else {
					if (storage.getFileContentRetriever() == null) {
						storage.setFileContentRetriever(fileContentRetriever);
					}
				}
			}
		}
		if (clear) {
			fileContentRetriever = null;
		}
	}
}
