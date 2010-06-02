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
package com.aptana.ide.syncing.ui.views;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.ide.syncing.ui.internal.SyncUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionPointLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider {

    private int fSizeIndex = 1;
    private int fModificationIndex = 2;

    public ConnectionPointLabelProvider() {
		super(new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
	}

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
     *      int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        switch (columnIndex) {
        case 0:
            return getImage(element);
        default:
            return null;
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
     *      int)
     */
    public String getColumnText(Object element, int columnIndex) {
        if (columnIndex == 0) {
            return getText(element);
        }
        if (columnIndex == fSizeIndex) {
            return getFilesize(element);
        }
        if (columnIndex == fModificationIndex) {
            return getLastModified(element);
        }
        return ""; //$NON-NLS-1$
    }

    /**
     * @param columnIndex
     *            the index of the "size" column
     */
    public void setSizeIndex(int columnIndex) {
        fSizeIndex = columnIndex;
    }

    /**
     * @param columnIndex
     *            the index of the "last modified" column
     */
    public void setModificationIndex(int columnIndex) {
        fModificationIndex = columnIndex;
    }

    private static String getFilesize(Object element) {
        long rawSize = -1;
        if (element instanceof IResource) {
            rawSize = ((IResource) element).getLocation().toFile().length();
        } else if (element instanceof IAdaptable) {
            IFileInfo fileInfo = SyncUtils.getFileInfo((IAdaptable) element);
            if (fileInfo != null) {
                rawSize = fileInfo.getLength();
            }
        }

        if (rawSize >= 0) {
            long leftover = 0;
            String string = Long.toString(rawSize) + " B"; //$NON-NLS-1$
            if (rawSize > 1024) {
                rawSize = rawSize / 1024;
                leftover = rawSize % 1024;
                long num = rawSize;
                if (leftover >= 512) {
                    num++;
                }
                string = num + " KB"; //$NON-NLS-1$
            }
            if (rawSize > 1024) {
                rawSize = rawSize / 1024;
                leftover = rawSize % 1024;
                long num = rawSize;
                if (leftover >= 512) {
                    num++;
                }
                string = num + " MB"; //$NON-NLS-1$
            }
            if (rawSize > 1024) {
                rawSize = rawSize / 1024;
                leftover = rawSize % 1024;
                long num = rawSize;
                if (leftover >= 512) {
                    num++;
                }
                string = num + " GB"; //$NON-NLS-1$
            }
            if (rawSize > 1024) {
                rawSize = rawSize / 1024;
                leftover = rawSize % 1024;
                long num = rawSize;
                if (leftover >= 512) {
                    num++;
                }
                string = num + " TB"; //$NON-NLS-1$
            }
            return string;
        }
        return ""; //$NON-NLS-1$
    }

    private static String getLastModified(Object element) {
        long timestamp = -1;
        if (element instanceof IResource) {
            timestamp = ((IResource) element).getLocalTimeStamp();
        } else if (element instanceof IAdaptable) {
            IFileInfo fileInfo = SyncUtils.getFileInfo((IAdaptable) element);
            if (fileInfo != null) {
                timestamp = fileInfo.getLastModified();
            }
        }
        if (timestamp >= 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm a"); //$NON-NLS-1$
            return formatter.format(new Date(timestamp));
        }
        return ""; //$NON-NLS-1$
    }

}
