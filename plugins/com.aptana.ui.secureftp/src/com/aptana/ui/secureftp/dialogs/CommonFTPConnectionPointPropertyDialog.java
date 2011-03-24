/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.secureftp.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ui.ftp.dialogs.FTPConnectionPointPropertyDialog;
import com.aptana.ui.ftp.internal.FTPConnectionPropertyComposite;
import com.aptana.ui.secureftp.internal.CommonFTPConnectionPropertyComposite;

/**
 * @author Max Stepanov
 *
 */
public class CommonFTPConnectionPointPropertyDialog extends FTPConnectionPointPropertyDialog {

	private ConnectionPointType connectionType;

	/**
	 * @param parentShell
	 */
	public CommonFTPConnectionPointPropertyDialog(Shell parentShell) {
		super(parentShell);	
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#getConnectionPointType()
	 */
	@Override
	protected ConnectionPointType getConnectionPointType() {
		CommonFTPConnectionPropertyComposite connectionComposite = (CommonFTPConnectionPropertyComposite) getConnectionComposite();
		if (connectionComposite != null) {
			return connectionComposite.getConnectionPointType();
		}
		if (connectionType != null) {
			return connectionType;
		}
		return super.getConnectionPointType();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#setPropertySource(java.lang.Object)
	 */
	@Override
	public void setPropertySource(Object element) {
		super.setPropertySource(element);
		if (element instanceof ConnectionPointType) {
			connectionType = (ConnectionPointType) element;
			CommonFTPConnectionPropertyComposite connectionComposite = (CommonFTPConnectionPropertyComposite) getConnectionComposite();
			if (connectionComposite != null) {
				connectionComposite.setConnectionPointType((ConnectionPointType) element);
			}
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);
		((CommonFTPConnectionPropertyComposite) getConnectionComposite()).setConnectionPointType(connectionType);

		return control;
	}

	@Override
	protected FTPConnectionPropertyComposite createConnectionComposite(Composite parent, IBaseRemoteConnectionPoint connectionPoint) {
		return new CommonFTPConnectionPropertyComposite(parent, SWT.NONE, connectionPoint, this);
	}
}
