/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.secureftp.internal;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.TimeZone;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.TimeZoneUtil;
import com.aptana.filesystem.secureftp.IFTPSConnectionPoint;
import com.aptana.filesystem.secureftp.IFTPSConstants;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.ui.io.dialogs.IDialogConstants;
import com.aptana.ui.ftp.internal.IOptionsComposite;
import com.aptana.ui.ftp.internal.NumberVerifyListener;

/**
 * @author Max Stepanov
 *
 */
public class FTPSAdvancedOptionsComposite extends Composite implements IOptionsComposite {
	
	private static final String EMPTY = ""; //$NON-NLS-1$
	
	private IListener listener;
	private Combo securityMethodCombo;
	private Button validateCertificateCheckbox;
	private Button noSSLSessionResumptionCheckbox;
	private Combo modeCombo;
	private Text portText;
	private Combo encodingCombo;
	private Combo timezoneCombo;
	private Button detectButton;
	
	private ModifyListener modifyListener;
	
	/**
	 * @param parent
	 * @param style
	 */
	public FTPSAdvancedOptionsComposite(Composite parent, int style, IListener listener) {
		super(parent, style);
		this.listener = listener;
		
		setLayout(GridLayoutFactory.swtDefaults().numColumns(5)
				.spacing(new PixelConverter(this).convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING),
						new PixelConverter(this).convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING))
				.create());

		/* row 1 */
		Label label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(this).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPSAdvancedOptionsComposite_SSLMethod));

		securityMethodCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		securityMethodCombo.add(Messages.FTPSAdvancedOptionsComposite_MethodExplicit);
		securityMethodCombo.add(Messages.FTPSAdvancedOptionsComposite_MethodImplicit);
		securityMethodCombo.setLayoutData(GridDataFactory.swtDefaults().hint(
				securityMethodCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x, SWT.DEFAULT)
				.span(3, 1).create());
		
		validateCertificateCheckbox = new Button(this, SWT.CHECK);
		validateCertificateCheckbox.setText(Messages.FTPSAdvancedOptionsComposite_ValidateCertificate);
		validateCertificateCheckbox.setLayoutData(GridDataFactory.swtDefaults().create());

		label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().span(4, 1).create());

		noSSLSessionResumptionCheckbox = new Button(this, SWT.CHECK);
		noSSLSessionResumptionCheckbox.setText(Messages.FTPSAdvancedOptionsComposite_NoSSLSessionResumption);
		noSSLSessionResumptionCheckbox.setLayoutData(GridDataFactory.swtDefaults().create());

		label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(this).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPSAdvancedOptionsComposite_ConnectMode));

		modeCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		modeCombo.add(Messages.FTPSAdvancedOptionsComposite_ModeActive);
		modeCombo.add(Messages.FTPSAdvancedOptionsComposite_ModePassive);
		modeCombo.setLayoutData(GridDataFactory.swtDefaults().hint(
				modeCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x, SWT.DEFAULT).create());

		label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).hint(
				new PixelConverter(this).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());

		label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPSAdvancedOptionsComposite_Port));
		
		portText = new Text(this, SWT.SINGLE | SWT.RIGHT | SWT.BORDER);
		portText.setLayoutData(GridDataFactory.swtDefaults().hint(
				Math.max(
						new PixelConverter(portText).convertWidthInCharsToPixels(5),
						portText.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x
					), SWT.DEFAULT).create());

		/* row 2 */
		label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(this).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPSAdvancedOptionsComposite_Encoding));

		encodingCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		encodingCombo.setItems(Charset.availableCharsets().keySet().toArray(ArrayUtil.NO_STRINGS));
		encodingCombo.setLayoutData(GridDataFactory.swtDefaults().hint(
				encodingCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x, SWT.DEFAULT)
				.span(4, 1).create());

		/* row 2 */
		Composite container = new Composite(this, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(5, 1).create());
		container.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
		
		label = new Label(container, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(this).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPSAdvancedOptionsComposite_Timezone));

		timezoneCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		String[] timezones = TimeZone.getAvailableIDs();
		Arrays.sort(timezones);
		timezoneCombo.setItems(timezones);
		timezoneCombo.add(EMPTY, 0);
		timezoneCombo.setLayoutData(GridDataFactory.swtDefaults().hint(
				timezoneCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x, SWT.DEFAULT)
				.create());
		
		detectButton = new Button(container, SWT.PUSH);
		detectButton.setText(Messages.FTPSAdvancedOptionsComposite_Detect);
		detectButton.setLayoutData(GridDataFactory.fillDefaults().hint(
				Math.max(
					new PixelConverter(detectButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
					detectButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x
				), SWT.DEFAULT).create());

		/* -- */
		addListeners();
		
		securityMethodCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (securityMethodCombo.getSelectionIndex() == 0) {
					portText.setText(Integer.toString(IFTPSConstants.FTP_PORT_DEFAULT));
				} else {
					portText.setText(Integer.toString(IFTPSConstants.FTPS_IMPLICIT_PORT));
				}
			}
		});
		
		portText.addVerifyListener(new NumberVerifyListener());
		
		detectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				detectTimezone();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertiesEditor#loadPropertiesFrom(java.lang.Object)
	 */
	public void loadPropertiesFrom(Object element) {
		Assert.isLegal(element instanceof IFTPSConnectionPoint);
		IFTPSConnectionPoint ftpsConnectionPoint = (IFTPSConnectionPoint) element;

		removeListeners();
		try {
			securityMethodCombo.select(ftpsConnectionPoint.isExplicit() ? 0 : 1);
			validateCertificateCheckbox.setSelection(ftpsConnectionPoint.isValidateCertificate());
			noSSLSessionResumptionCheckbox.setSelection(ftpsConnectionPoint.isNoSSLSessionResumption());
			modeCombo.select(ftpsConnectionPoint.isPassiveMode() ? 1 : 0);
			portText.setText(Integer.toString(ftpsConnectionPoint.getPort()));
			int index = encodingCombo.indexOf(String.valueOf(ftpsConnectionPoint.getEncoding()));
			if (index >= 0) {
				encodingCombo.select(index);
			}
			index = timezoneCombo.indexOf(String.valueOf(ftpsConnectionPoint.getTimezone()));
			if (index >= 0) {
				timezoneCombo.select(index);
			} else {
				timezoneCombo.select(timezoneCombo.indexOf(EMPTY));
			}
		} finally {
			addListeners();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertiesEditor#savePropertiesTo(java.lang.Object)
	 */
	public boolean savePropertiesTo(Object element) {
		Assert.isLegal(element instanceof IFTPSConnectionPoint);
		boolean updated = false;
		IFTPSConnectionPoint ftpsConnectionPoint = (IFTPSConnectionPoint) element;
		
		boolean explicit = securityMethodCombo.getSelectionIndex() == 0;
		if (ftpsConnectionPoint.isExplicit() != explicit) {
			ftpsConnectionPoint.setExplicit(explicit);
			updated = true;
		}
		boolean validate = validateCertificateCheckbox.getSelection();
		if (ftpsConnectionPoint.isValidateCertificate() != validate) {
			ftpsConnectionPoint.setValidateCertificate(validate);
			updated = true;
		}
		boolean noResumption = noSSLSessionResumptionCheckbox.getSelection();
		if (ftpsConnectionPoint.isNoSSLSessionResumption() != noResumption) {
			ftpsConnectionPoint.setNoSSLSessionResumption(noResumption);
			updated = true;
		}
		boolean passiveMode = modeCombo.getSelectionIndex() == 1;
		if (ftpsConnectionPoint.isPassiveMode() != passiveMode) {
			ftpsConnectionPoint.setPassiveMode(passiveMode);
			updated = true;
		}
		int port = Integer.parseInt(portText.getText());
		if (ftpsConnectionPoint.getPort() != port) {
			ftpsConnectionPoint.setPort(port);
			updated = true;
		}
		String encoding = encodingCombo.getItem(encodingCombo.getSelectionIndex());
		if (!ftpsConnectionPoint.getEncoding().equals(encoding)) {
			ftpsConnectionPoint.setEncoding(encoding);
			updated = true;
		}
		String timezone = timezoneCombo.getItem(timezoneCombo.getSelectionIndex());
		if (EMPTY.equals(timezone)) {
			timezone = null;
		}
		// compare both not null
		if (ftpsConnectionPoint.getTimezone() != timezone && (timezone == null || !timezone.equals(ftpsConnectionPoint.getTimezone()))) { // $codepro.audit.disable useEquals, stringComparison
			ftpsConnectionPoint.setTimezone(timezone);
			updated = true;
		}
		return updated;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.ftp.internal.IOptionsComposite#isValid()
	 */
	public String isValid() {
		int port = 0;
		try {
			port = Integer.parseInt(portText.getText());
		} catch (NumberFormatException e) {
			e.getCause();
		}
		if (port <= 0) {
			return Messages.FTPSAdvancedOptionsComposite_InvalidPort;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.ftp.internal.IOptionsComposite#setValid(boolean)
	 */
	public void setValid(boolean valid) {
		detectButton.setEnabled(valid);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.ftp.internal.IOptionsComposite#lockUI(boolean)
	 */
	public void lockUI(boolean lock) {
		securityMethodCombo.setEnabled(!lock);
		validateCertificateCheckbox.setEnabled(!lock);
		noSSLSessionResumptionCheckbox.setEnabled(!lock);
		modeCombo.setEnabled(!lock);
		portText.setEnabled(!lock);
		encodingCombo.setEnabled(!lock);
		timezoneCombo.setEnabled(!lock);
		detectButton.setEnabled(!lock);
	}
	
	private void detectTimezone() {
		if (!listener.isValid()) {
			return;
		}
		ConnectionContext context = new ConnectionContext();
		context.setBoolean(ConnectionContext.DETECT_TIMEZONE, true);
		if (listener.testConnection(context, null)) {
			String[] tzones = (String[]) context.get(ConnectionContext.SERVER_TIMEZONE);
			if (tzones != null && tzones.length > 0) {
				String tz = timezoneCombo.getItem(timezoneCombo.getSelectionIndex());
				if (!Arrays.asList(tzones).contains(tz)) {
					tz = TimeZoneUtil.getCommonTimeZone(tzones);
					int index = timezoneCombo.indexOf(tz);
					if (index >= 0) {
						timezoneCombo.select(index);
					}
				}
			}
		}
	}

	protected void addListeners() {
		if (modifyListener == null) {
			modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					listener.validate();
				}
			};
		}
		portText.addModifyListener(modifyListener);
	}
	
	protected void removeListeners() {
		if (modifyListener != null) {
			portText.removeModifyListener(modifyListener);
		}
	}
}
