/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ide.ui.io.dialogs;

import java.net.URI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.GenericConnectionPoint;
import com.aptana.ide.ui.io.IOUIPlugin;
import com.aptana.ui.IPropertyDialog;

/**
 * @author Max Stepanov
 *
 */
public class GenericConnectionPropertyDialog extends TitleAreaDialog implements IPropertyDialog {

	private static final String DEFAULT_NAME = Messages.GenericConnectionPropertyDialog_NewConnection;
	
	private GenericConnectionPoint genericConnectionPoint;
	private boolean isNew = false;

	private Text nameText;
	private Text uriText;

	private ModifyListener modifyListener;

	/**
	 * @param parentShell
	 */
	public GenericConnectionPropertyDialog(Shell parentShell) {
		super(parentShell);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertyDialog#setPropertySource(java.lang.Object)
	 */
	public void setPropertySource(Object element) {
		genericConnectionPoint = null;
		if (element instanceof GenericConnectionPoint) {
			genericConnectionPoint = (GenericConnectionPoint) element;
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.IPropertyDialog#getPropertySource()
	 */
	public Object getPropertySource() {
		return genericConnectionPoint;
	}

	private String getConnectionPointType() {
		return GenericConnectionPoint.TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		if (genericConnectionPoint != null) {
			setTitle(Messages.GenericConnectionPropertyDialog_EditTitle);
			getShell().setText(Messages.GenericConnectionPropertyDialog_EditText);
		} else {
			setTitle(Messages.GenericConnectionPropertyDialog_CreateTitle);
			getShell().setText(Messages.GenericConnectionPropertyDialog_CreateText);
		}
		
		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory.swtDefaults()
				.margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING))
				.numColumns(2).create());
		
		/* row 1 */
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.GenericConnectionPropertyDialog_Name));
		
		nameText = new Text(container, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.fillDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());
		
		/* row 2 */
		label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.GenericConnectionPropertyDialog_URI));

		uriText = new Text(container, SWT.SINGLE | SWT.BORDER);
		uriText.setLayoutData(GridDataFactory.swtDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());
				
		/* -- */
		addListeners();

		if (genericConnectionPoint == null) {
			try {
				genericConnectionPoint = (GenericConnectionPoint) CoreIOPlugin.getConnectionPointManager().createConnectionPoint(getConnectionPointType());
				genericConnectionPoint.setName(DEFAULT_NAME);
				isNew = true;
			} catch (CoreException e) {
				IdeLog.logError(IOUIPlugin.getDefault(), Messages.GenericConnectionPropertyDialog_FailedToCreate, e);
				close(); // $codepro.audit.disable closeInFinally
			}
		}
		loadPropertiesFrom(genericConnectionPoint);

		return dialogArea;
	}

	protected void addListeners() {
		if (modifyListener == null) {
			modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validate();
				}
			};
		}
		nameText.addModifyListener(modifyListener);
		uriText.addModifyListener(modifyListener);
	}
	
	protected void removeListeners() {
		if (modifyListener != null) {
			nameText.removeModifyListener(modifyListener);
			uriText.removeModifyListener(modifyListener);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		if (!isValid()) {
			return;
		}
		if (savePropertiesTo(genericConnectionPoint)) {
			/* TODO: notify */
			genericConnectionPoint.hashCode();
		}
		if (isNew) {
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(genericConnectionPoint);
		}
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		try {
			return super.createContents(parent);
		} finally {
			validate();
		}
	}

	protected void loadPropertiesFrom(GenericConnectionPoint connectionPoint) {
		removeListeners();
		try {
			nameText.setText(valueOrEmpty(connectionPoint.getName()));
			URI uri = connectionPoint.getURI();
			uriText.setText((uri != null) ? uri.toString() : ""); //$NON-NLS-1$
		} finally {
			addListeners();
		}
	}

	protected boolean savePropertiesTo(GenericConnectionPoint connectionPoint) {
		boolean updated = false;
		String name = nameText.getText();
		if (!name.equals(connectionPoint.getName())) {
			connectionPoint.setName(name);
			updated = true;
		}
		URI uri = URI.create(uriText.getText());
		if (!uri.equals(connectionPoint.getURI())) {
			connectionPoint.setURI(uri);
			updated = true;
		}
		return updated;
	}

	public void validate() {
		boolean valid = isValid();
		getButton(OK).setEnabled(valid);
	}
	
	public boolean isValid() {
		String message = null;
		if (nameText.getText().length() == 0) {
			message = Messages.GenericConnectionPropertyDialog_SpecifyShortcut;
		} else {
			try {
				if (!URI.create(uriText.getText()).isAbsolute()) {
					message = Messages.GenericConnectionPropertyDialog_SpecifyValidAbsoluteURI;
				}
			} catch (Exception e) {
				message = Messages.GenericConnectionPropertyDialog_SpecifyValidURI;
			}
		}
		if (message != null) {
			setErrorMessage(message);
		} else {
			setErrorMessage(null);
			setMessage(null);
			return true;
		}
		return false;
	}

	protected static String valueOrEmpty(String value) {
		if (value != null) {
			return value;
		}
		return ""; //$NON-NLS-1$
	}
}
