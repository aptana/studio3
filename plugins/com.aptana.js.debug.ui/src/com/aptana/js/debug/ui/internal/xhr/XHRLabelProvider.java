/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.xhr;

import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.core.model.xhr.IXHRTransfer;
import com.aptana.js.debug.core.model.xhr.IXHRTransfer.IHeader;

/**
 * @author Max Stepanov
 */
class XHRLabelProvider extends LabelProvider implements ITableLabelProvider {
	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof IXHRTransfer) {
			IXHRTransfer xhr = (IXHRTransfer) element;
			switch (columnIndex) {
				case 1:
					return xhr.getURL();
				case 2:
					return xhr.getMethod();
				case 3: {
					Date date = xhr.getRequestDate();
					if (date != null) {
						return date.toString();
					}
					return StringUtil.EMPTY;
				}
				case 4: {
					Date date = xhr.getResponseDate();
					if (date != null) {
						return date.toString();
					}
					return StringUtil.EMPTY;
				}
				default:
			}
		} else if (element instanceof IHeader) {
			IHeader header = (IHeader) element;
			switch (columnIndex) {
				case 0:
					return header.getName();
				case 1:
					return header.getValue();
				default:
			}
		}
		return null;
	}
}
