/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.secureftp.internal;

import java.nio.charset.Charset;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.filesystem.secureftp.ISFTPConnectionPoint;
import com.aptana.filesystem.secureftp.ISFTPConstants;
import com.aptana.ide.ui.io.dialogs.IDialogConstants;
import com.aptana.ui.ftp.internal.IOptionsComposite;
import com.aptana.ui.ftp.internal.NumberVerifyListener;

/**
 * @author Max Stepanov
 *
 */
public class SFTPAdvancedOptionsComposite extends Composite implements IOptionsComposite {
	
	private IListener listener;
	private Combo compressionCombo;
	private Text portText;
	private Combo encodingCombo;
	
	private ModifyListener modifyListener;
	
	/**
	 * @param parent
	 * @param style
	 */
	public SFTPAdvancedOptionsComposite(Composite parent, int style, IListener listener) {
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
		label.setText(StringUtil.makeFormLabel(Messages.SFTPAdvancedOptionsComposite_Compression));

		compressionCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		compressionCombo.add(ISFTPConstants.COMPRESSION_AUTO);
		compressionCombo.add(ISFTPConstants.COMPRESSION_NONE);
		compressionCombo.add(ISFTPConstants.COMPRESSION_ZLIB);
		compressionCombo.setLayoutData(GridDataFactory.swtDefaults().hint(
				compressionCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x, SWT.DEFAULT).create());

		label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).hint(
				new PixelConverter(this).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());

		label = new Label(this, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().create());
		label.setText(StringUtil.makeFormLabel(Messages.SFTPAdvancedOptionsComposite_Port));
		
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
		label.setText(StringUtil.makeFormLabel(Messages.SFTPAdvancedOptionsComposite_Encoding));

		encodingCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		encodingCombo.setItems(Charset.availableCharsets().keySet().toArray(ArrayUtil.NO_STRINGS));
		encodingCombo.setLayoutData(GridDataFactory.swtDefaults().hint(
				encodingCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x, SWT.DEFAULT)
				.span(4, 1).create());

		/* -- */
		addListeners();
		portText.addVerifyListener(new NumberVerifyListener());		
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertiesEditor#loadPropertiesFrom(java.lang.Object)
	 */
	public void loadPropertiesFrom(Object element) {
		Assert.isLegal(element instanceof ISFTPConnectionPoint);
		ISFTPConnectionPoint sftpConnectionPoint = (ISFTPConnectionPoint) element;

		removeListeners();
		try {
			int index = compressionCombo.indexOf(String.valueOf(sftpConnectionPoint.getCompression()));
			if (index >= 0) {
				compressionCombo.select(index);
			}
			portText.setText(Integer.toString(sftpConnectionPoint.getPort()));
			index = encodingCombo.indexOf(String.valueOf(sftpConnectionPoint.getEncoding()));
			if (index >= 0) {
				encodingCombo.select(index);
			}
		} finally {
			addListeners();
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.io.IPropertiesEditor#savePropertiesTo(java.lang.Object)
	 */
	public boolean savePropertiesTo(Object element) {
		Assert.isLegal(element instanceof ISFTPConnectionPoint);
		boolean updated = false;
		ISFTPConnectionPoint sftpConnectionPoint = (ISFTPConnectionPoint) element;
		
		String compression = compressionCombo.getItem(compressionCombo.getSelectionIndex());
		if (!sftpConnectionPoint.getCompression().equals(compression)) {
			sftpConnectionPoint.setCompression(compression);
			updated = true;
		}
		int port = Integer.parseInt(portText.getText());
		if (sftpConnectionPoint.getPort() != port) {
			sftpConnectionPoint.setPort(port);
			updated = true;
		}
		String encoding = encodingCombo.getItem(encodingCombo.getSelectionIndex());
		if (!sftpConnectionPoint.getEncoding().equals(encoding)) {
			sftpConnectionPoint.setEncoding(encoding);
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
			return Messages.SFTPAdvancedOptionsComposite_InvalidPort;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.ftp.internal.IOptionsComposite#setValid(boolean)
	 */
	public void setValid(boolean valid) {
	}

	/* (non-Javadoc)
	 * @see com.aptana.ui.ftp.internal.IOptionsComposite#lockUI(boolean)
	 */
	public void lockUI(boolean lock) {
		compressionCombo.setEnabled(!lock);
		portText.setEnabled(!lock);
		encodingCombo.setEnabled(!lock);
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
