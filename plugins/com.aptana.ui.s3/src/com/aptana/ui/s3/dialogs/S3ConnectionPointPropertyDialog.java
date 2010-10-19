/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.s3.dialogs;

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
import com.aptana.ide.filesystem.s3.S3ConnectionPoint;
import com.aptana.ui.IPropertyDialog;
import com.aptana.ui.s3.S3UIPlugin;
import com.aptana.ui.s3.internal.S3ConnectionPropertyComposite;

/**
 * @author Max Stepanov
 *
 */
public class S3ConnectionPointPropertyDialog extends TitleAreaDialog implements IPropertyDialog, S3ConnectionPropertyComposite.Listener {

	private Image titleImage;
	private S3ConnectionPropertyComposite s3Composite;

	private IBaseRemoteConnectionPoint s3ConnectionPoint;
	private boolean lockedUI;

	/**
	 * @param parentShell
	 */
	public S3ConnectionPointPropertyDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ui.IPropertyDialog#setPropertyElement(java.lang.Object)
	 */
	public void setPropertySource(Object element) {
		s3ConnectionPoint = null;
		if (element instanceof IBaseRemoteConnectionPoint) {
			s3ConnectionPoint = (IBaseRemoteConnectionPoint) element;
		}
		if (s3Composite != null) {
			s3Composite.setConnectionPoint(s3ConnectionPoint);
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource() {
		return s3ConnectionPoint;
	}

	protected ConnectionPointType getConnectionPointType() {
		if (s3ConnectionPoint != null) {
			return CoreIOPlugin.getConnectionPointManager().getType(s3ConnectionPoint);
		}
		return CoreIOPlugin.getConnectionPointManager().getType(S3ConnectionPoint.TYPE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		titleImage = S3UIPlugin.getImageDescriptor("/icons/full/wizban/s3.png").createImage(); //$NON-NLS-1$
		dialogArea.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
		
		setTitleImage(titleImage);
		if (s3ConnectionPoint != null) {
			setTitle(MessageFormat.format(Messages.S3ConnectionPointPropertyDialog_MessageTitle_Edit, getConnectionPointType().getName()));
			getShell().setText(Messages.S3ConnectionPointPropertyDialog_Title_Edit);
		} else {
			setTitle(MessageFormat.format(Messages.S3ConnectionPointPropertyDialog_MessageTitle_New, getConnectionPointType().getName()));
			getShell().setText(Messages.S3ConnectionPointPropertyDialog_Title_New);
		}
		
		s3Composite = createConnectionComposite(dialogArea, s3ConnectionPoint);
		s3Composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		
		return dialogArea;
	}
	
	protected S3ConnectionPropertyComposite createConnectionComposite(Composite parent, IBaseRemoteConnectionPoint connectionPoint) {
		return new S3ConnectionPropertyComposite(parent, SWT.NONE, connectionPoint, this);
	}

	protected S3ConnectionPropertyComposite getConnectionComposite() {
		return s3Composite;
	}

	protected void dispose() {
		if (titleImage != null) {
			setTitleImage(null);
			titleImage.dispose();
			titleImage = null;
		}		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#canHandleShellCloseEvent()
	 */
	@Override
	protected boolean canHandleShellCloseEvent() {
		return !lockedUI && super.canHandleShellCloseEvent();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (!s3Composite.isValid()) {
			return;
		}
		if (s3Composite.completeConnection()) {
			s3ConnectionPoint = s3Composite.getConnectionPoint();
			super.okPressed();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		s3Composite.setCanceled(true);
		if (!lockedUI) {
			super.cancelPressed();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		try {
			return super.createContents(parent);
		} finally {
			s3Composite.validate();
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
		} else {
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
