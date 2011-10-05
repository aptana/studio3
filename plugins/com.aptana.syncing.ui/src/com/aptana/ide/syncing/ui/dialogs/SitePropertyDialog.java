/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.syncing.core.ISiteConnection;
import com.aptana.ui.IPropertyDialog;

/**
 * @author Max Stepanov
 */
public class SitePropertyDialog extends SiteConnectionsEditorDialog implements IPropertyDialog
{

	public SitePropertyDialog(Shell parentShell)
	{
		super(parentShell);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#setPropertySource(java.lang.Object)
	 */
	public void setPropertySource(Object element)
	{
		if (element instanceof ISiteConnection)
		{
			setSelection((ISiteConnection) element);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource()
	{
		return null;
	}
}
