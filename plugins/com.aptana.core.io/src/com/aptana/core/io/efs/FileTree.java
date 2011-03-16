/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.io.efs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileInfo;

/**
 * @author Max Stepanov
 *
 */
/* package */ class FileTree extends org.eclipse.core.filesystem.provider.FileTree {

	private static final IFileStore[] EMPTY_FILE_STORE_ARRAY = new IFileStore[0];
	private static final IFileInfo[] EMPTY_FILE_INFO_ARRAY = new IFileInfo[0];
	
	private Map<IFileStore, IFileStore[]> treeMap = new HashMap<IFileStore, IFileStore[]>();
	private Map<IFileStore, IFileInfo> infoMap = new HashMap<IFileStore, IFileInfo>();

	/**
	 * @param treeRoot
	 */
	FileTree(IFileStore treeRoot) {
		super(treeRoot);
	}
	
	/* package */ void addChildren(IFileStore parent, IFileStore[] stores, IFileInfo[] infos) {
		for (int i = 0; i < stores.length; ++i) {
			infoMap.put(stores[i], infos[i]);
		}
		treeMap.put(parent, stores);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileTree#getChildInfos(org.eclipse.core.filesystem.IFileStore)
	 */
	@Override
	public IFileInfo[] getChildInfos(IFileStore store) {
		IFileStore[] result = treeMap.get(store);
		if (result != null) {
			List<IFileInfo> list = new ArrayList<IFileInfo>();
			for (IFileStore file : result) {
				list.add(infoMap.get(file));
			}
			return list.toArray(new IFileInfo[list.size()]);
		}
		return EMPTY_FILE_INFO_ARRAY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileTree#getChildStores(org.eclipse.core.filesystem.IFileStore)
	 */
	@Override
	public IFileStore[] getChildStores(IFileStore store) {
		IFileStore[] result = treeMap.get(store);
		if (result != null) {
			return result;
		}
		return EMPTY_FILE_STORE_ARRAY;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.filesystem.provider.FileTree#getFileInfo(org.eclipse.core.filesystem.IFileStore)
	 */
	@Override
	public IFileInfo getFileInfo(IFileStore store) {
		IFileInfo result = infoMap.get(store);
		if (result != null) {
			return result;
		}
		return new FileInfo(store.getName());
	}

}
