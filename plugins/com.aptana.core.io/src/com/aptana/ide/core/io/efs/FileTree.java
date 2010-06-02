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

package com.aptana.ide.core.io.efs;

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
