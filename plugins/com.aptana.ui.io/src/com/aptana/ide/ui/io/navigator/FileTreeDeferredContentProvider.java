/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.navigator;

import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.progress.DeferredTreeContentManager;

/**
 * @author Max Stepanov
 *
 */
public class FileTreeDeferredContentProvider extends WorkbenchContentProvider {

	private DeferredTreeContentManager deferredTreeContentManager;

	/**
	 * 
	 */
	public FileTreeDeferredContentProvider(DeferredTreeContentManager deferredTreeContentManager) {
		this.deferredTreeContentManager = deferredTreeContentManager;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
	 */
	@Override
	public Object[] getChildren(Object element) {
		Object[] children = deferredTreeContentManager.getChildren(element);
		if (children != null) {
			return children;
		}
		return super.getChildren(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof FileSystemObject) {
			return ((FileSystemObject) element).isDirectory();
		}
		if (deferredTreeContentManager.isDeferredAdapter(element)) {
			return deferredTreeContentManager.mayHaveChildren(element);
		}
		return super.hasChildren(element);
	}
	
}
