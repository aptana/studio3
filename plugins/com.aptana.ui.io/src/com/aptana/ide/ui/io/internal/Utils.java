/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.internal;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.core.io.efs.EFSUtils;
import com.aptana.ide.ui.io.FileSystemUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class Utils {

    public static IFileStore getFileStore(Object adaptable) {
        if (adaptable instanceof IResource) {
        	return EFSUtils.getFileStore((IResource) adaptable);
        }
        return FileSystemUtils.getFileStore(adaptable);
    }

    public static IFileInfo getFileInfo(IAdaptable adaptable) {
        IFileInfo fileInfo = (IFileInfo) adaptable.getAdapter(IFileInfo.class);
        if (fileInfo == null) {
            IFileStore fileStore = getFileStore(adaptable);
            if (fileStore != null) {
                fileInfo = FileSystemUtils.fetchFileInfo(fileStore);
            }
        }
        return fileInfo;
    }
}
