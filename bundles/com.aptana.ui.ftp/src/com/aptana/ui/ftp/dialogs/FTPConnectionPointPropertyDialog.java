/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.ftp.dialogs;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.filesystem.ftp.IBaseFTPConnectionPoint;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.ftp.FTPUIPlugin;
import com.aptana.ui.ftp.internal.FTPConnectionPropertyComposite;

/**
 * @author Max Stepanov
 */
public class FTPConnectionPointPropertyDialog extends TitleAreaDialog implements IPropertyDialog,
		FTPConnectionPropertyComposite.IListener {

	private Image titleImage;
	private FTPConnectionPropertyComposite ftpComposite;

	private IBaseRemoteConnectionPoint ftpConnectionPoint;
	private boolean lockedUI;

	/**
	 * @param parentShell
	 */
	public FTPConnectionPointPropertyDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.core.io.ui.IPropertyDialog#setPropertyElement(java.lang.Object)
	 */
	public void setPropertySource(Object element) {
		ftpConnectionPoint = null;
		if (element instanceof IBaseRemoteConnectionPoint) {
			ftpConnectionPoint = (IBaseRemoteConnectionPoint) element;
		}
		if (ftpComposite != null) {
			ftpComposite.setConnectionPoint(ftpConnectionPoint);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource() {
		return ftpConnectionPoint;
	}

	protected ConnectionPointType getConnectionPointType() {
		if (ftpConnectionPoint != null) {
			return CoreIOPlugin.getConnectionPointManager().getType(ftpConnectionPoint);
		}
		return CoreIOPlugin.getConnectionPointManager().getType(IBaseFTPConnectionPoint.TYPE_FTP);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		titleImage = FTPUIPlugin.getImageDescriptor("/icons/full/wizban/ftp.png").createImage(); //$NON-NLS-1$
		dialogArea.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});

		setTitleImage(titleImage);
		if (ftpConnectionPoint != null) {
			setTitle(MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_MessageTitle_Edit,
					getConnectionPointType().getName()));
			getShell().setText(Messages.FTPConnectionPointPropertyDialog_Title_Edit);
		}
		else {
			setTitle(MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_MessageTitle_New,
					getConnectionPointType().getName()));
			getShell().setText(Messages.FTPConnectionPointPropertyDialog_Title_New);
		}

		ftpComposite = createConnectionComposite(dialogArea, ftpConnectionPoint);
		ftpComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		return dialogArea;
	}

	protected FTPConnectionPropertyComposite createConnectionComposite(Composite parent,
			IBaseRemoteConnectionPoint connectionPoint) {
		return new FTPConnectionPropertyComposite(parent, SWT.NONE, connectionPoint, this);
	}

	protected FTPConnectionPropertyComposite getConnectionComposite() {
		return ftpComposite;
	}

	protected void dispose() {
		if (titleImage != null) {
			setTitleImage(null);
			titleImage.dispose();
			titleImage = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#canHandleShellCloseEvent()
	 */
	@Override
	protected boolean canHandleShellCloseEvent() {
		return !lockedUI && super.canHandleShellCloseEvent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (!ftpComposite.isValid()) {
			return;
		}
		if (ftpComposite.completeConnection()) {
			ftpConnectionPoint = ftpComposite.getConnectionPoint();
			super.okPressed();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		ftpComposite.setCanceled(true);
		if (!lockedUI) {
			super.cancelPressed();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		try {
			return super.createContents(parent);
		}
		finally {
			if (ftpComposite != null) {
				ftpComposite.validate();
			}
		}
	}

	public void setValid(boolean valid) {
		Button button = getButton(OK);
		if (button != null) {
			button.setEnabled(valid);
		}
	}

	public void error(String message) {
		if (message == null) {
			setErrorMessage(null);
			setMessage(null);
		}
		else {
			setErrorMessage(message);
		}
	}

	public void lockUI(boolean lock) {
		lockedUI = lock;
		getButton(OK).setEnabled(!lock);
	}

	public void layoutShell() {
		Point size = getInitialSize();
		Rectangle bounds = getConstrainedShellBounds(new Rectangle(0, 0, size.x, size.y));
		getShell().setSize(bounds.width, bounds.height);
	}
}
