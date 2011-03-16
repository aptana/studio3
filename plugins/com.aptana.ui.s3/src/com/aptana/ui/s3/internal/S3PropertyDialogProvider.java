/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.s3.internal;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;

import com.aptana.ui.IPropertyDialogProvider;
import com.aptana.ui.s3.dialogs.S3ConnectionPointPropertyDialog;

/**
 * @author Max Stepanov
 *
 */
public class S3PropertyDialogProvider implements IPropertyDialogProvider {

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertyDialogProvider#createPropertyDialog(org.eclipse.jface.window.IShellProvider)
	 */
	public Dialog createPropertyDialog(IShellProvider shellProvider) {
		IPropertyDialogProvider contributedPropertyDialogProvider = (IPropertyDialogProvider) Platform.getAdapterManager()
								.loadAdapter(this, IPropertyDialogProvider.class.getName());
		if (contributedPropertyDialogProvider != null && contributedPropertyDialogProvider != this) {
			return contributedPropertyDialogProvider.createPropertyDialog(shellProvider);
		}
		return new S3ConnectionPointPropertyDialog(shellProvider.getShell());
	}
}
