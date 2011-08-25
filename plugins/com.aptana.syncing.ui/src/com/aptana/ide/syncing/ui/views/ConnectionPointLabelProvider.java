/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.views;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.aptana.ide.syncing.ui.internal.SyncPresentationUtils;

/**
 * @author Michael Xia (mxia@aptana.com)
 */
public class ConnectionPointLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider
{

	private int fSizeIndex = 1;
	private int fModificationIndex = 2;

	public ConnectionPointLabelProvider()
	{
		super(new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		switch (columnIndex)
		{
			case 0:
				return getImage(element);
			default:
				return null;
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		if (columnIndex == 0)
		{
			return getText(element);
		}
		if (columnIndex == fSizeIndex)
		{
			return SyncPresentationUtils.getFileSize(element);
		}
		if (columnIndex == fModificationIndex)
		{
			return SyncPresentationUtils.getLastModified(element);
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @param columnIndex
	 *            the index of the "size" column
	 */
	public void setSizeIndex(int columnIndex)
	{
		fSizeIndex = columnIndex;
	}

	/**
	 * @param columnIndex
	 *            the index of the "last modified" column
	 */
	public void setModificationIndex(int columnIndex)
	{
		fModificationIndex = columnIndex;
	}

}
