/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.navigator;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import com.aptana.ide.core.io.IConnectionPointCategory;
import com.aptana.ide.core.io.LocalRoot;
import com.aptana.ide.ui.io.FileSystemUtils;

/**
 * @author Max Stepanov
 *
 */
public class FileTreeNameSorter extends ViewerSorter {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
	 */
	@Override
	public int category(Object element) {
	    if (element instanceof WorkspaceProjects) {
	        return 0;
	    } else if (element instanceof LocalFileSystems) {
	        return 1;
	    } else if (element instanceof IConnectionPointCategory) {
			return 2;
	    } else if (element instanceof IProject) {
	    	return 3;
		} else if (element instanceof IResource) {
		    return ((IResource) element).getLocation().toFile().isDirectory() ? 3 : 4;
		} else if (element instanceof IAdaptable) {
		    IFileInfo fileInfo = FileSystemUtils.getFileInfo(element);
            if (fileInfo != null) {
                return fileInfo.isDirectory() ? 3 : 4;
            }
		}
		return super.category(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof LocalRoot && e2 instanceof LocalRoot) {
			return 0;
		}
		if (e1 instanceof IConnectionPointCategory && e2 instanceof IConnectionPointCategory) {
			return ((IConnectionPointCategory) e1).compareTo(e2);
		}
		return super.compare(viewer, e1, e2);
	}

}
