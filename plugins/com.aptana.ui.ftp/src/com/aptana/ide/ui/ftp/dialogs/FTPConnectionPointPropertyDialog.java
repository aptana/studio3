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

package com.aptana.ide.ui.ftp.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.aptana.core.StringUtils;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.filesystem.ftp.IBaseFTPConnectionPoint;
import com.aptana.ide.filesystem.ftp.IFTPConstants;
import com.aptana.ide.filesystem.ftp.Policy;
import com.aptana.ide.ui.ftp.FTPUIPlugin;
import com.aptana.ide.ui.ftp.internal.FTPAdvancedOptionsComposite;
import com.aptana.ide.ui.ftp.internal.IConnectionDialog;
import com.aptana.ide.ui.ftp.internal.IConnectionRunnable;
import com.aptana.ide.ui.ftp.internal.IOptionsComposite;
import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.dialogs.FileTreeSelectionDialog;
import com.aptana.ide.ui.io.dialogs.IDialogConstants;
import com.aptana.ui.IPropertyDialog;

/**
 * @author Max Stepanov
 *
 */
public class FTPConnectionPointPropertyDialog extends TitleAreaDialog implements IPropertyDialog, IConnectionDialog {

	private static final String DEFAULT_NAME = Messages.FTPConnectionPointPropertyDialog_Title;
	private static final Pattern HOST_PATTERN = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([-\\w]+(\\.[-\\w]+)*)$"); //$NON-NLS-1$

	protected IBaseRemoteConnectionPoint ftpConnectionPoint;
	protected IBaseRemoteConnectionPoint originalFtpConnectionPoint;
	private boolean isNew = false;
	
	private Text nameText;
	private Text hostText;
	private Text remotePathText;
	private Combo loginCombo;
	protected Label passwordLabel;
	protected Text passwordText;
	protected Button savePasswordButton;
	private Button testButton;
	private Button browseButton;
	protected ExpandableComposite optionsExpandable;
	protected IOptionsComposite advancedOptions;
	private ProgressMonitorPart progressMonitorPart;
	
	private boolean lockedUI;
	private boolean connectionTested;
	private ModifyListener modifyListener;
	private SelectionListener selectionListener;

	private Image titleImage;
	protected Font smallFont;
	
	/**
	 * @param parentShell
	 */
	public FTPConnectionPointPropertyDialog(Shell parentShell) {
		super(parentShell);		
	}

	/* (non-Javadoc)
	 * @see com.aptana.ide.core.io.ui.IPropertyDialog#setPropertyElement(java.lang.Object)
	 */
	public void setPropertySource(Object element) {
		ftpConnectionPoint = null;
		if (element instanceof IBaseRemoteConnectionPoint) {
			ftpConnectionPoint = (IBaseRemoteConnectionPoint) element;
		}
	}
	
	/* (non-Javadoc)
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
	
	protected void createSiteSection(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtils.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_SiteName));
		
		nameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory.fillDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());		
	}

	protected void createPasswordSection(Composite parent) {
		passwordLabel = new Label(parent, SWT.NONE);
		passwordLabel.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(passwordLabel).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		passwordLabel.setText(StringUtils.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_Password));

		passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(GridDataFactory.fillDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());
		
		savePasswordButton = new Button(parent, SWT.CHECK);
		savePasswordButton.setLayoutData(GridDataFactory.fillDefaults().create());
		savePasswordButton.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Save);		
	}

	protected void createAdvancedOptions(Composite parent) {
		advancedOptions = new FTPAdvancedOptionsComposite(parent, SWT.NONE, this);
		((Composite) advancedOptions).setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);

		titleImage = FTPUIPlugin.getImageDescriptor("/icons/full/wizban/ftp.png").createImage(); //$NON-NLS-1$
		smallFont = JFaceResources.getTextFontDescriptor().increaseHeight(-2).createFont(dialogArea.getDisplay());
		dialogArea.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
		
		setTitleImage(titleImage);
		if (ftpConnectionPoint != null) {
			setTitle(MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_MessageTitle_Edit, getConnectionPointType().getName()));
			getShell().setText(Messages.FTPConnectionPointPropertyDialog_Title_Edit);
		} else {
			setTitle(MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_MessageTitle_New, getConnectionPointType().getName()));
			getShell().setText(Messages.FTPConnectionPointPropertyDialog_Title_New);
		}
		
		Composite container = new Composite(dialogArea, SWT.NONE);
		container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		container.setLayout(GridLayoutFactory.swtDefaults()
				.margins(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING), convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING))
				.numColumns(2).create());
		
		/* row 1 */
		createSiteSection(container);

		Group group = new Group(container, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
		group.setText(Messages.FTPConnectionPointPropertyDialog_LBL_GroupInfo);
		
		/* row 2 */
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtils.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_Server));

		hostText = new Text(group, SWT.SINGLE | SWT.BORDER);
		hostText.setLayoutData(GridDataFactory.swtDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.span(2, 1).create());

		/* row 3 */
		new Label(group, SWT.NONE)
			.setLayoutData(GridDataFactory.swtDefaults().create());
		
		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
		label.setFont(smallFont);
		label.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Example);
		
		/* row 4 */
		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtils.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_Username));

		loginCombo = new Combo(group, SWT.DROP_DOWN | SWT.BORDER);
		loginCombo.setLayoutData(GridDataFactory.swtDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());
		loginCombo.add(IFTPConstants.LOGIN_ANONYMOUS);
		
		testButton = new Button(group, SWT.PUSH);
		testButton.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Test);
		testButton.setLayoutData(GridDataFactory.fillDefaults().hint(
				Math.max(
					new PixelConverter(testButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
					testButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x
				), SWT.DEFAULT).create());

		/* row 5 */
		createPasswordSection(group);

		/* row 6 */
		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().hint(
				new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
				SWT.DEFAULT).create());
		label.setText(StringUtils.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_RemotePath));

		remotePathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		remotePathText.setLayoutData(GridDataFactory.swtDefaults()
				.hint(convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());
		
		browseButton = new Button(group, SWT.PUSH);
		browseButton.setText('&' + StringUtils.ellipsify("Browse"));
		browseButton.setLayoutData(GridDataFactory.fillDefaults().hint(
				Math.max(
					new PixelConverter(browseButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
					browseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x
				), SWT.DEFAULT).create());
		
		/* row 7 */
		optionsExpandable = new ExpandableComposite(container, SWT.NONE, ExpandableComposite.TWISTIE | ExpandableComposite.FOCUS_TITLE);
		optionsExpandable.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Options);
		optionsExpandable.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
		Group optionsGroup = new Group(optionsExpandable, SWT.NONE);
		optionsGroup.setLayout(GridLayoutFactory.fillDefaults().create());
		
		createAdvancedOptions(optionsGroup);
		
		optionsExpandable.setClient(optionsGroup);
		optionsExpandable.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				layoutShell();
			}
		});
		
		/* row 8 */
		progressMonitorPart = new ProgressMonitorPart(container, GridLayoutFactory.fillDefaults().create());
		progressMonitorPart.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).exclude(true).span(2, 1).create());

		/* -- */
		addListeners();

		passwordText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				e.doit = false;
				testConnection();
			}
		});
		
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (testConnection()) {
                    MessageDialog.openInformation(getShell(),
                            Messages.FTPConnectionPointPropertyDialog_Succeed_Title,
                            MessageFormat.format(
                                    Messages.FTPConnectionPointPropertyDialog_Succeed_Message,
                                    hostText.getText()));
				}
			}
		});
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseConnection();
			}
		});
		
		if (ftpConnectionPoint == null) {
			isNew = true;
			ftpConnectionPoint = getOrCreateConnectionPoint(getConnectionPointType());
			ftpConnectionPoint.setName(DEFAULT_NAME);
		} else {
			originalFtpConnectionPoint = ftpConnectionPoint;
		}
		loadPropertiesFrom(ftpConnectionPoint);
		connectionTested = !isNew;

		return dialogArea;
	}
	
	protected IBaseRemoteConnectionPoint getOrCreateConnectionPoint(ConnectionPointType connectionPointType) {
		if (!isNew) {
			if (CoreIOPlugin.getConnectionPointManager().getType(originalFtpConnectionPoint).equals(connectionPointType)) {
				return originalFtpConnectionPoint;
			}
		}
		try {
			return (IBaseRemoteConnectionPoint) CoreIOPlugin.getConnectionPointManager()
							.createConnectionPoint(connectionPointType);
		} catch (CoreException e) {
			FTPUIPlugin.logError(Messages.FTPConnectionPointPropertyDialog_ERR_FailedCreate, e);
			close();
			throw new SWTException();
		}
	}
	
	protected void dispose() {
		if (titleImage != null) {
			setTitleImage(null);
			titleImage.dispose();
			titleImage = null;
		}
		if (smallFont != null) {
			smallFont.dispose();
			smallFont = null;
		}		
	}
	
	protected void addListeners() {
		if (modifyListener == null) {
			modifyListener = new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validate();
					if (e.widget != nameText) {
						connectionTested = false;
					}
				}
			};
		}
		nameText.addModifyListener(modifyListener);
		hostText.addModifyListener(modifyListener);
		loginCombo.addModifyListener(modifyListener);
		passwordText.addModifyListener(modifyListener);
		remotePathText.addModifyListener(modifyListener);
		if (selectionListener == null) {
			selectionListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					validate();
				}
			};
		}
		loginCombo.addSelectionListener(selectionListener);
	}
	
	protected void removeListeners() {
		if (modifyListener != null) {
			nameText.removeModifyListener(modifyListener);
			hostText.removeModifyListener(modifyListener);
			loginCombo.removeModifyListener(modifyListener);
			passwordText.removeModifyListener(modifyListener);
			remotePathText.removeModifyListener(modifyListener);
		}
		if (selectionListener != null) {
			loginCombo.removeSelectionListener(selectionListener);			
		}
	}
	
	protected void layoutShell() {
		Point size = getInitialSize();
		Rectangle bounds = getConstrainedShellBounds(new Rectangle(0, 0, size.x, size.y));
		getShell().setSize(bounds.width, bounds.height);
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
		if (!isValid()) {
			return;
		}
		if (DEFAULT_NAME.equals(nameText.getText())) {
			nameText.setText(hostText.getText());
		}
		if (!connectionTested) {
			if (!testConnection()) {
				MessageDialog dlg = new MessageDialog(getShell(),
						Messages.FTPConnectionPointPropertyDialog_ConfirmTitle,
						null,
						Messages.FTPConnectionPointPropertyDialog_ConfirmMessage,
						MessageDialog.QUESTION,
						new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, Messages.FTPConnectionPointPropertyDialog_LBL_Edit },
						2);
				int code = dlg.open();
				switch (code) {
				case 1:
					super.okPressed();
				case 2:
					return;
				default:
				}
			}
		}
		CoreIOPlugin.getAuthenticationManager().setPassword(
				getAuthId(ftpConnectionPoint),
				passwordText.getText().toCharArray(), savePasswordButton.getSelection());

		boolean changed = savePropertiesTo(ftpConnectionPoint);
		if (isNew) {
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(ftpConnectionPoint);
		} else if (ftpConnectionPoint != originalFtpConnectionPoint) {
			CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(originalFtpConnectionPoint);
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(ftpConnectionPoint);
		} else if (changed) {
            CoreIOPlugin.getConnectionPointManager().connectionPointChanged(ftpConnectionPoint);
		}
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#cancelPressed()
	 */
	@Override
	protected void cancelPressed() {
		progressMonitorPart.setCanceled(true);
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
			validate();
		}
	}
	
	protected String getAuthId(IBaseRemoteConnectionPoint connectionPoint) {
		return Policy.generateAuthId(getConnectionPointType().getType().toUpperCase(), connectionPoint);
	}

	protected void loadPropertiesFrom(IBaseRemoteConnectionPoint connectionPoint) {
		removeListeners();
		try {
			nameText.setText(valueOrEmpty(connectionPoint.getName()));
			hostText.setText(valueOrEmpty(connectionPoint.getHost()));
			remotePathText.setText(connectionPoint.getPath().toPortableString());
			String login = connectionPoint.getLogin();
			int index = loginCombo.indexOf(login);
			if (index >= 0) {
				loginCombo.select(index);
			} else {
				loginCombo.setText(login);
			}
			String authId = getAuthId(connectionPoint);
			boolean persistent = CoreIOPlugin.getAuthenticationManager().hasPersistent(authId);
			savePasswordButton.setSelection(persistent);
			char[] password = connectionPoint.getPassword();
			if (persistent && password == null) {
				password = CoreIOPlugin.getAuthenticationManager().getPassword(authId);				
			}
			if (password != null) {
				passwordText.setText(String.copyValueOf(password));
			}
			advancedOptions.loadPropertiesFrom(connectionPoint);
		} finally {
			addListeners();
		}
	}

	protected boolean savePropertiesTo(IBaseRemoteConnectionPoint connectionPoint) {
		boolean updated = false;
		String name = nameText.getText();
		if (!name.equals(connectionPoint.getName())) {
			connectionPoint.setName(name);
			updated = true;
		}
		String host = hostText.getText();
		if (!host.equals(connectionPoint.getHost())) {
			connectionPoint.setHost(host);
			updated = true;
		}
		IPath path = Path.fromPortableString(remotePathText.getText());
		if (!connectionPoint.getPath().equals(path)) {
			connectionPoint.setPath(path);
			updated = true;
		}
		String login = loginCombo.getText();
		if (!login.equals(connectionPoint.getLogin())) {
			connectionPoint.setLogin(login);
			updated = true;
		}
		char[] password = passwordText.getText().toCharArray();
		if (!Arrays.equals(password, connectionPoint.getPassword())) {
			connectionPoint.setPassword(password);
			updated = true;
		}
		
		if (advancedOptions.savePropertiesTo(connectionPoint)) {
			updated = true;
		}
		return updated;
	}
	
	public void validate() {
		Button button = getButton(OK);
		if (button == null) {
			return;
		}
		boolean valid = isValid();
		button.setEnabled(valid);
		testButton.setEnabled(valid);
		browseButton.setEnabled(valid);
		advancedOptions.setValid(valid);
	}

	public boolean isValid() {
		String message = null;
		if (nameText.getText().length() == 0) {
			message = Messages.FTPConnectionPointPropertyDialog_ERR_NameEmpty;
		} else if (!HOST_PATTERN.matcher(hostText.getText()).matches()) {
			message = Messages.FTPConnectionPointPropertyDialog_ERR_InvalidHost;
		} else if (loginCombo.getText().length() == 0) {
			message = Messages.FTPConnectionPointPropertyDialog_ERR_NoUsername;
		} else {
			message = advancedOptions.isValid();
		}
		setErrorMessage(message);
		return (message == null);
	}

	private boolean testConnection() {
		return testConnection(null, null);
	}
	
	/* (non-Javadoc)
	 * @see com.aptana.ide.ui.ftp.internal.IConnectionDialog#testConnection(com.aptana.ide.core.io.ConnectionContext, com.aptana.ide.ui.ftp.internal.IConnectionRunnable)
	 */
	public boolean testConnection(ConnectionContext context, final IConnectionRunnable connectRunnable) {
		// WORKAROUND: getting contents after the control is disabled will return empty string if not called here
		hostText.getText();
		loginCombo.getText();
		passwordText.getText();
		remotePathText.getText();
		lockUI(true);
		((GridData) progressMonitorPart.getLayoutData()).exclude = false;
		layoutShell();
		try {
			final IBaseRemoteConnectionPoint connectionPoint =  isNew ? ftpConnectionPoint
					: (IBaseRemoteConnectionPoint) CoreIOPlugin.getConnectionPointManager().cloneConnectionPoint(ftpConnectionPoint);
            savePropertiesTo(connectionPoint);
			if (context == null) {
				context = new ConnectionContext();
				context.setBoolean(ConnectionContext.QUICK_CONNECT, true);
			}
			context.setBoolean(ConnectionContext.NO_PASSWORD_PROMPT, true);
			CoreIOPlugin.setConnectionContext(connectionPoint, context);
			
			ModalContext.run(new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						try {
							if (connectRunnable != null) {
								connectRunnable.beforeConnect(connectionPoint);
							}
							connectionPoint.connect(monitor);
							if (connectRunnable != null) {
								connectRunnable.afterConnect(connectionPoint, monitor);
							}
						} finally {
							try {
								connectionPoint.disconnect(monitor);
							} catch (CoreException e) {
								FTPUIPlugin.logImportant("", e); //$NON-NLS-1$
							}
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			}, true, progressMonitorPart, getShell().getDisplay());
			
			return connectionTested = true;
		} catch (InterruptedException e) {
		} catch (InvocationTargetException e) {
			showErrorDialog(e.getTargetException());
		} catch (CoreException e) {
			showErrorDialog(e);
		} finally {
			if (!progressMonitorPart.isDisposed()) {
				((GridData) progressMonitorPart.getLayoutData()).exclude = true;
				layoutShell();
				lockUI(false);
			}
		}
		return false;
	}
	
	private void browseConnection() {
		testConnection(null, new IConnectionRunnable() {
			public void afterConnect(final IConnectionPoint connectionPoint, IProgressMonitor monitor) throws CoreException, InterruptedException {
				monitor.beginTask(Messages.FTPConnectionPointPropertyDialog_Task_Browse, IProgressMonitor.UNKNOWN);
				monitor.subTask(""); //$NON-NLS-1$
				getShell().getDisplay().syncExec(new Runnable() {
					public void run() {
						showBrowseDialog(connectionPoint);
					}
				});
				monitor.done();
			}

			public void beforeConnect(IConnectionPoint connectionPoint) throws CoreException, InterruptedException {
				((IBaseRemoteConnectionPoint) connectionPoint).setPath(Path.ROOT);
			}
		});
	}
	
	private void showBrowseDialog(IConnectionPoint connectionPoint) {
		FileTreeSelectionDialog dlg = new FileTreeSelectionDialog(getShell(), false);
		dlg.setTitle(MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_Title_Browse, ((IBaseRemoteConnectionPoint) connectionPoint).getHost()));
		dlg.setMessage(StringUtils.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_Message_Browse));
		dlg.setInput(connectionPoint);
		String pathString = remotePathText.getText();
		try {
			IFileStore selection = connectionPoint.getRoot();
			if (pathString.length() > 0) {
				selection = selection.getFileStore(Path.fromPortableString(pathString));
			}
			dlg.setInitialSelection(selection);
		} catch (CoreException e) {
			FTPUIPlugin.logImportant("", e); //$NON-NLS-1$
		}
		if (dlg.open() == Window.OK) {
			URI uri = FileSystemUtils.getURI(dlg.getFirstResult());
			if (uri != null) {
				String path = Path.fromPortableString(connectionPoint.getRootURI().relativize(uri).toString()).makeAbsolute().toPortableString();
				remotePathText.setText(path);
			}
		}
	}

	private void showErrorDialog(Throwable e) {
		String message = Messages.FTPConnectionPointPropertyDialog_DefaultErrorMsg;
		if (e instanceof CoreException) {
			message = ((CoreException) e).getStatus().getMessage();
		}
		MessageDialog.openError(getShell(), Messages.FTPConnectionPointPropertyDialog_ErrorTitle, message);
	}

	protected void lockUI(boolean lock) {
		lockedUI = lock;
		getButton(OK).setEnabled(!lock);
		nameText.setEnabled(!lock);
		hostText.setEnabled(!lock);
		loginCombo.setEnabled(!lock);
		passwordText.setEnabled(!lock);
		savePasswordButton.setEnabled(!lock);
		remotePathText.setEnabled(!lock);
		remotePathText.setEnabled(!lock);
		testButton.setEnabled(!lock);
		browseButton.setEnabled(!lock);
		
		advancedOptions.lockUI(lock);
	}

	private static String valueOrEmpty(String value) {
		if (value != null) {
			return value;
		}
		return ""; //$NON-NLS-1$
	}
}
