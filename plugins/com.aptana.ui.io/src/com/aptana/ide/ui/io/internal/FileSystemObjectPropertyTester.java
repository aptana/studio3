/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.Utils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class FileSystemObjectPropertyTester extends PropertyTester {

    private static final String PROPERTY_IS_DIRECTORY = "isDirectory"; //$NON-NLS-1$
    private static final String PROPERTY_IS_LOCAL = "isLocal"; //$NON-NLS-1$
    private static final String PROPERTY_IS_SYMLINK = "isSymlink"; //$NON-NLS-1$
    private static final String PROPERTY_IS_PRIVATE = "isPrivate"; //$NON-NLS-1$

    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (receiver instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) receiver;
            IFileStore fileStore = Utils.getFileStore(adaptable);
            IFileInfo fileInfo = Utils.getFileInfo(adaptable, EFS.NONE);

            boolean value = toBoolean(expectedValue);
            if (PROPERTY_IS_DIRECTORY.equals(property)) {
                return fileInfo.isDirectory() == value;
            } else if (PROPERTY_IS_SYMLINK.equals(property)) {
            	return fileInfo.getAttribute(EFS.ATTRIBUTE_SYMLINK) == value;
            } else if (PROPERTY_IS_PRIVATE.equals(property)) {
            	return FileSystemUtils.isPrivate(fileInfo) == value;
            } else if (PROPERTY_IS_LOCAL.equals(property)) {
                try {
                    return (fileStore.toLocalFile(EFS.NONE, null) != null) == value;
                } catch (CoreException ignore) {
                    // ignores the exception
                	ignore.getCause();
                }
                return false;
            }
        }
        return false;
    }

    private static boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return false;
    }

}
