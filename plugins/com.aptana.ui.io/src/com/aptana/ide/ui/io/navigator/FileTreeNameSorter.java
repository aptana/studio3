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
