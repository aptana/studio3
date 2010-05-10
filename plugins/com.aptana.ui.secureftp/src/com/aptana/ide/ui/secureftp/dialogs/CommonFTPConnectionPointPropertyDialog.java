/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.ide.ui.secureftp.dialogs;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.filesystem.ftp.IBaseFTPConnectionPoint;
import com.aptana.ide.filesystem.secureftp.IFTPSConnectionPoint;
import com.aptana.ide.filesystem.secureftp.ISFTPConnectionPoint;
import com.aptana.ide.filesystem.secureftp.Policy;
import com.aptana.ide.filesystem.secureftp.SFTPConnectionPoint;
import com.aptana.ide.filesystem.secureftp.SecureUtils;
import com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog;
import com.aptana.ide.ui.ftp.internal.FTPAdvancedOptionsComposite;
import com.aptana.ide.ui.ftp.internal.IOptionsComposite;
import com.aptana.ide.ui.io.dialogs.IDialogConstants;
import com.aptana.ide.ui.secureftp.internal.FTPSAdvancedOptionsComposite;
import com.aptana.ide.ui.secureftp.internal.SFTPAdvancedOptionsComposite;
import com.aptana.ui.UIUtils;

/**
 * @author Max Stepanov
 *
 */
@SuppressWarnings("restriction")
public class CommonFTPConnectionPointPropertyDialog extends FTPConnectionPointPropertyDialog {

	private final ConnectionPointType[] protoTypes;	
	private ConnectionPointType connectionType;
	private Combo protocolButton;
	private Composite keyAuthComposite;
	private Button keyAuthButton;
	private Label keyPathLabel;
	private Composite[] advancedOptionsComposites;
	
	/**
	 * @param parentShell
	 */
	public CommonFTPConnectionPointPropertyDialog(Shell parentShell) {
		super(parentShell);	
		protoTypes = new ConnectionPointType[] {
			CoreIOPlugin.getConnectionPointManager().getType(IBaseFTPConnectionPoint.TYPE_FTP),	
			CoreIOPlugin.getConnectionPointManager().getType(IFTPSConnectionPoint.TYPE_FTPS),	
			CoreIOPlugin.getConnectionPointManager().getType(ISFTPConnectionPoint.TYPE_SFTP)	
		};
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#getConnectionPointType()
	 */
	@Override
	protected ConnectionPointType getConnectionPointType() {
		if (connectionType != null) {
			return connectionType;
		}
		return super.getConnectionPointType();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#setPropertySource(java.lang.Object)
	 */
	@Override
	public void setPropertySource(Object element) {
		super.setPropertySource(element);
		if (ftpConnectionPoint != null) {
			connectionType = CoreIOPlugin.getConnectionPointManager().getType(ftpConnectionPoint);
		} else if (element instanceof ConnectionPointType) {
			connectionType = (ConnectionPointType) element;
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#createSiteSection(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createSiteSection(Composite parent) {
		super.createSiteSection(parent);

		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.CommonFTPConnectionPointPropertyDialog_Protocol));

		protocolButton = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		String[] items = new String[protoTypes.length];
		for (int i = 0; i < items.length; ++i) {
		    items[i] = protoTypes[i].getName();
		}
		protocolButton.setItems(items);
		updateProtocolButton();
		protocolButton.setLayoutData(GridDataFactory.swtDefaults().hint(
				Math.max(
					new PixelConverter(protocolButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
					protocolButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x
				), SWT.DEFAULT).create());
		
		/* -- */
		protocolButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			    changeProtocolType(protoTypes[protocolButton.getSelectionIndex()]);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#createPasswordSection(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createPasswordSection(Composite parent) {		
		keyAuthComposite = new Composite(parent, SWT.NONE);
		keyAuthComposite.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, false).create());
		makeVisible(keyAuthComposite, false);
		keyAuthComposite.setLayout(GridLayoutFactory.fillDefaults().spacing(LayoutConstants.getSpacing().x, 0).numColumns(2).create());
		
		/* row 1 */
		Label label = new Label(keyAuthComposite, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());

		keyAuthButton = new Button(keyAuthComposite, SWT.CHECK);
		keyAuthButton.setLayoutData(GridDataFactory.fillDefaults().create());
		keyAuthButton.setText(Messages.CommonFTPConnectionPointPropertyDialog_UsePublicKeyAuthentication);		

		/* row 2 */
		label = new Label(keyAuthComposite, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());

		keyPathLabel = new Label(keyAuthComposite, SWT.NONE);
		keyPathLabel.setLayoutData(GridDataFactory.fillDefaults().create());
		keyPathLabel.setFont(smallFont);
		keyPathLabel.setText(Messages.CommonFTPConnectionPointPropertyDialog_NoPrivateKeySelected);	

		super.createPasswordSection(parent);		
		updateKeyAuth();
		
		/* -- */
		keyAuthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeKeyAuthentication();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#createAdvancedOptions(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createAdvancedOptions(Composite parent) {
		advancedOptionsComposites = new Composite[] {
				new FTPAdvancedOptionsComposite(parent, SWT.NONE, this),
				new FTPSAdvancedOptionsComposite(parent, SWT.NONE, this),
				new SFTPAdvancedOptionsComposite(parent, SWT.NONE, this)
		};
		for (Composite composite : advancedOptionsComposites) {
			composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
			makeVisible(composite, false);
		}
		updateAdvancedOptions();
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#getAuthId(com.aptana.ide.filesystem.ftp.IBaseRemoteConnectionPoint)
	 */
	@Override
	protected String getAuthId(IBaseRemoteConnectionPoint connectionPoint) {
		if (ISFTPConnectionPoint.TYPE_SFTP.equals(connectionType.getType())) {
			if (keyAuthButton.getSelection()) {
				return Policy.generateAuthId(getConnectionPointType().getType().toUpperCase()+"/PUBLICKEY", connectionPoint);			} //$NON-NLS-1$
		}
		return super.getAuthId(connectionPoint);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#loadPropertiesFrom(com.aptana.ide.filesystem.ftp.IBaseRemoteConnectionPoint)
	 */
	@Override
	protected void loadPropertiesFrom(IBaseRemoteConnectionPoint connectionPoint) {
		if (connectionPoint instanceof SFTPConnectionPoint) {
			SFTPConnectionPoint sftpConnectionPoint = (SFTPConnectionPoint) connectionPoint;
			IPath keyFilePath = sftpConnectionPoint.getKeyFilePath();
			makeVisible(passwordLabel, true);
			makeVisible(passwordText, true);
			makeVisible(savePasswordButton, true);
			if (keyFilePath != null && !keyFilePath.isEmpty()) {
				keyAuthButton.setSelection(true);
				keyPathLabel.setText(keyFilePath.toOSString());
				passwordLabel.setText(StringUtil.makeFormLabel(Messages.CommonFTPConnectionPointPropertyDialog_Passphrase));
				try {
					boolean passphraseProtected = SecureUtils.isKeyPassphraseProtected(keyFilePath.toFile());
					makeVisible(passwordLabel, passphraseProtected);
					makeVisible(passwordText, passphraseProtected);
					makeVisible(savePasswordButton, passphraseProtected);
				} catch (CoreException e) {
				}
			}
			updateLayout();
		}
		super.loadPropertiesFrom(connectionPoint);
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#savePropertiesTo(com.aptana.ide.filesystem.ftp.IBaseRemoteConnectionPoint)
	 */
	@Override
	protected boolean savePropertiesTo(IBaseRemoteConnectionPoint connectionPoint) {
		boolean updated = false;
		if (connectionPoint instanceof SFTPConnectionPoint) {
			SFTPConnectionPoint sftpConnectionPoint = (SFTPConnectionPoint) connectionPoint;
			if (keyAuthButton.getSelection()) {
				IPath keyFilePath = Path.fromPortableString(keyPathLabel.getText());
				if (!connectionPoint.getPath().equals(keyFilePath)) {
					sftpConnectionPoint.setKeyFilePath(keyFilePath);
					updated = true;
				}
			}
		}
		return super.savePropertiesTo(connectionPoint) || updated;
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.dialogs.FTPConnectionPointPropertyDialog#isValid()
	 */
	@Override
	public boolean isValid() {
		if (!super.isValid()) {
			return false;
		}
		String message = null;
		if (keyAuthButton.getSelection()) {
			IPath keyFilePath = Path.fromPortableString(keyPathLabel.getText());
			try {
				boolean passphraseProtected = SecureUtils.isKeyPassphraseProtected(keyFilePath.toFile());
				char[] password = passwordText.getText().toCharArray();
				if (passphraseProtected && password.length > 0) {
					if (!SecureUtils.isPassphraseValid(keyFilePath.toFile(), password)) {
						message = Messages.CommonFTPConnectionPointPropertyDialog_IncorrectPassphrase;
					}
				}
			} catch (CoreException e) {
				message = e.getLocalizedMessage();
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

	private void updateProtocolButton() {
		int currentIndex = Arrays.asList(protoTypes).indexOf(getConnectionPointType());
		if (currentIndex == -1) {
			currentIndex = 0;
		}
		protocolButton.setText(protoTypes[currentIndex].getName());		
	}
	
	private void updateKeyAuth() {
		boolean keyAuthEnabled = ISFTPConnectionPoint.TYPE_SFTP.equals(connectionType.getType());
		makeVisible(keyAuthComposite, keyAuthEnabled);
		keyAuthButton.setSelection(false);
		changeKeyAuthentication();
	}
	
	private void updateAdvancedOptions() {
		int currentIndex = Arrays.asList(protoTypes).indexOf(getConnectionPointType());
		if (currentIndex == -1) {
			currentIndex = 0;
		}
		for (Composite composite : advancedOptionsComposites) {
			makeVisible(composite, false);
		}
		Composite advancedOptionComposite = advancedOptionsComposites[currentIndex];
		makeVisible(advancedOptionComposite, true);
		advancedOptions = (IOptionsComposite) advancedOptionComposite;
	}
	
	private void changeProtocolType(ConnectionPointType newType) {
		if (newType == null || newType == getConnectionPointType()) {
			return;
		}
		connectionType = newType;		
		updateProtocolButton();
		updateKeyAuth();
		updateAdvancedOptions();
		
		updateLayout();
		
		ftpConnectionPoint = getOrCreateConnectionPoint(connectionType);
		advancedOptions.loadPropertiesFrom(ftpConnectionPoint);
	}
	
	private void changeKeyAuthentication() {
		boolean enabled = keyAuthButton.getSelection();
		
		if (enabled) {
			while (true) {
				FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setText(Messages.CommonFTPConnectionPointPropertyDialog_SpecifyPrivateKey);
				String ssh_home = SecureUtils.getSSH_HOME();
				if (ssh_home != null && ssh_home.length() != 0) {
					File dir = new File(ssh_home);
					if (dir.exists() && dir.isDirectory()) {
						dlg.setFilterPath(ssh_home);
						for (String key : SecureUtils.getPrivateKeys()) {
							if (new File(dir, key).exists()) {
								dlg.setFileName(key);
								break;
							}
						}
					}
				}
				String keyFilePath = dlg.open();
				if (keyFilePath == null) {
					keyAuthButton.setSelection(false);
					return;
				}
				try {
					boolean passphraseProtected = SecureUtils.isKeyPassphraseProtected(new File(keyFilePath));
					makeVisible(passwordLabel, passphraseProtected);
					makeVisible(passwordText, passphraseProtected);
					makeVisible(savePasswordButton, passphraseProtected);
				} catch (CoreException e) {
					UIUtils.showErrorMessage(Messages.CommonFTPConnectionPointPropertyDialog_ERR_PrivateKey, e.getLocalizedMessage());
					continue;
				}
				keyPathLabel.setText(keyFilePath);
				break;
			}
			passwordText.setText(""); //$NON-NLS-1$
		} else {
			keyPathLabel.setText(Messages.CommonFTPConnectionPointPropertyDialog_NoPrivateKeySelected);
			makeVisible(passwordLabel, true);
			makeVisible(passwordText, true);
			makeVisible(savePasswordButton, true);
		}
		updateLayout();
		passwordLabel.setText(StringUtil.makeFormLabel(enabled ? Messages.CommonFTPConnectionPointPropertyDialog_Passphrase : Messages.CommonFTPConnectionPointPropertyDialog_Password));
		savePasswordButton.setSelection(false);
		validate();
	}
	
	private static void makeVisible(Control control, boolean visible) {
		control.setVisible(visible);
		((GridData) control.getLayoutData()).exclude = !visible;
	}
	
	private void updateLayout() {
		if (dialogArea != null) {
			((Composite) dialogArea).layout(true, true);
			layoutShell();
		}
	}
}
