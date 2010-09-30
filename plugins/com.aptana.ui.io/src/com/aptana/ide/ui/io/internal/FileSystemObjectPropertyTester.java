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
package com.aptana.ide.ui.io.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;

import com.aptana.ide.ui.io.FileSystemUtils;

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
            IFileInfo fileInfo = Utils.getFileInfo(adaptable);

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
                } catch (CoreException e) {
                    // ignores the exception
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
