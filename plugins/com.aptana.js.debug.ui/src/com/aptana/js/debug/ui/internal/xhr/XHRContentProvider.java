/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.xhr;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.aptana.js.debug.core.model.xhr.IXHRService;
import com.aptana.js.debug.core.model.xhr.IXHRTransfer.IHeader;

/**
 * @author Max Stepanov
 */
class XHRContentProvider implements IStructuredContentProvider {
	private static final Object[] EMPTY = new Object[0];

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IXHRService) {
			return ((IXHRService) inputElement).getTransfers();
		} else if (inputElement instanceof IHeader[]) {
			return (IHeader[]) inputElement;
		}
		return EMPTY;
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
	 *      java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
