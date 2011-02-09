/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
					if (storage.getFileContentRetriever() == fileContentRetriever) {
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
