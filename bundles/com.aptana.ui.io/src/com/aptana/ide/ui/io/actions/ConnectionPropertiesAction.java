/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;

import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.PropertyDialogsRegistry;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 *
 */
public class ConnectionPropertiesAction extends ConnectionActionDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (connectionPoint == null) {
			return;
		}
		try {
			Dialog dlg = PropertyDialogsRegistry.getInstance().createPropertyDialog(connectionPoint, targetPart.getSite());
			if (dlg != null) {
				if (dlg instanceof IPropertyDialog) {
					((IPropertyDialog) dlg).setPropertySource(connectionPoint);
				}
				dlg.open();
			}
		} catch (CoreException e) {
			UIUtils.showErrorMessage(Messages.ConnectionPropertiesAction_FailedToCreate, e);
		}
	}
}
