/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.navigator;

import org.eclipse.jface.viewers.IElementComparer;

/**
 * @author Max Stepanov
 *
 */
public class FileSystemElementComparer implements IElementComparer {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IElementComparer#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object a, Object b) {
		if (a instanceof FileSystemObject && !(b instanceof FileSystemObject)) {
			return ((FileSystemObject) a).getFileStore().equals(b);
		} else if (b instanceof FileSystemObject && !(a instanceof FileSystemObject)) {
			return ((FileSystemObject) b).getFileStore().equals(a);			
		}
		return a.equals(b);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IElementComparer#hashCode(java.lang.Object)
	 */
	public int hashCode(Object element) {
		return element.hashCode();
	}

}
