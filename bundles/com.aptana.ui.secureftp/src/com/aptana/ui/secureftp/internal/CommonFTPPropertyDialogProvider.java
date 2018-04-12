/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.secureftp.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;

import com.aptana.ui.IPropertyDialogProvider;
import com.aptana.ui.secureftp.dialogs.CommonFTPConnectionPointPropertyDialog;

/**
 * @author Max Stepanov
 *
 */
public class CommonFTPPropertyDialogProvider implements IPropertyDialogProvider {

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertyDialogProvider#createPropertyDialog(org.eclipse.jface.window.IShellProvider)
	 */
	public Dialog createPropertyDialog(IShellProvider shellProvider) {
		return new CommonFTPConnectionPointPropertyDialog(shellProvider.getShell());
	}

	@SuppressWarnings("rawtypes")
	public static class Factory implements IAdapterFactory {
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
		 */
		public Object getAdapter(Object adaptableObject, Class adapterType) {
			if (IPropertyDialogProvider.class.equals(adapterType)) {
				return new CommonFTPPropertyDialogProvider();
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
		 */
		public Class[] getAdapterList() {
			return new Class[] { IPropertyDialogProvider.class };
		}
	}
}
