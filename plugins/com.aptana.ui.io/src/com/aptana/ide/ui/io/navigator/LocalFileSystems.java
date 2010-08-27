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

package com.aptana.ide.ui.io.navigator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.aptana.core.util.PlatformUtil;
import com.aptana.ide.core.io.LocalRoot;
import com.aptana.ide.ui.io.CoreIOImages;

/**
 * @author Max Stepanov
 *
 */
public class LocalFileSystems implements IWorkbenchAdapter {

	private static final String MY_COMPUTER_GUID = "::{20D04FE0-3AEA-1069-A2D8-08002B30309D}"; //$NON-NLS-1$
	private static final String MY_NETWORK_PLACES_GUID = "::{208D2C60-3AEA-1069-A2D7-08002B30309D}"; //$NON-NLS-1$
	private static final String DESKTOP = PlatformUtil
			.expandEnvironmentStrings(PlatformUtil.DESKTOP_DIRECTORY);
    
	private static LocalFileSystems instance;
	
	/**
	 * 
	 */
	private LocalFileSystems() {
	}
	
	public static LocalFileSystems getInstance() {
		if (instance == null) {
			instance = new LocalFileSystems();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object o) {
		List<Object> children = new ArrayList<Object>();
		if (Platform.OS_WIN32.equals(Platform.getOS())) {
			File[] files = getWindowsRootFiles();
			for (int i = 0; i < files.length; ++i) {
				children.add(new WindowsFileSystemRoot(files[i]));
			}
		}
		LocalRoot[] otherRoots = LocalRoot.createRoots();
		for (LocalRoot root : otherRoots) {
			children.add(root);
		}
		return children.toArray(new Object[children.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
	    return CoreIOImages.getImageDescriptor(CoreIOImages.IMG_OBJS_DRIVE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
	 */
	public String getLabel(Object o) {
		return Messages.LocalFileSystems_LBL;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
	 */
	public Object getParent(Object o) {
		return null;
	}

	@Override
	public String toString() {
	    return Messages.LocalFileSystems_LBL;
	}

    private static File[] getWindowsRootFiles() {
        File desktop = getWindowsDesktopFile();
        if (desktop == null) {
            return new File[0];
        }
        List<File> roots = new ArrayList<File>();
        File[] files = FileSystemView.getFileSystemView().getFiles(desktop, false);
        String name;
        for (File file : files) {
            name = file.getName();
            if (name.equals(MY_COMPUTER_GUID) || name.equals(MY_NETWORK_PLACES_GUID)
                    || name.startsWith("::")) { //$NON-NLS-1$
                roots.add(file);
            }
        }
        return roots.toArray(new File[roots.size()]);
    }

    private static File getWindowsDesktopFile() {
        IPath desktopPath = new Path(DESKTOP);
        String desktopFilename = desktopPath.lastSegment();
        File[] files = FileSystemView.getFileSystemView().getRoots();
        for (File file : files) {
            if (file.getName().equals(desktopFilename)) {
                return file;
            }
        }
        return null;
    }
}
