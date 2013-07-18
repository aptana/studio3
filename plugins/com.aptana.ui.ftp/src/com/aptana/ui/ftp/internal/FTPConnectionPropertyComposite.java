/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable unnecessaryExceptions
// $codepro.audit.disable exceptionUsage.exceptionCreation

package com.aptana.ui.ftp.internal;

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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.filesystem.ftp.IBaseFTPConnectionPoint;
import com.aptana.filesystem.ftp.IFTPConstants;
import com.aptana.filesystem.ftp.Policy;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.dialogs.FileTreeSelectionDialog;
import com.aptana.ide.ui.io.dialogs.IDialogConstants;
import com.aptana.ui.ftp.FTPUIPlugin;
import com.aptana.ui.ftp.dialogs.Messages;
import com.aptana.ui.util.UIUtils;
import com.aptana.usage.FeatureEvent;
import com.aptana.usage.StudioAnalytics;

/**
 * @author Max Stepanov
 */
public class FTPConnectionPropertyComposite extends Composite implements IOptionsComposite.IListener
{

	public static interface IListener
	{
		public void setValid(boolean valid);

		public void error(String message);

		public void lockUI(boolean lock);

		public void layoutShell();

		public boolean close();
	}

	private static final String DEFAULT_NAME = Messages.FTPConnectionPointPropertyDialog_Title;
	private static final Pattern HOST_PATTERN = Pattern
			.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([-\\w]+(\\.[-\\w]+)*)$"); //$NON-NLS-1$

	protected IBaseRemoteConnectionPoint ftpConnectionPoint;
	protected IBaseRemoteConnectionPoint originalFtpConnectionPoint;

	private Text nameText;
	private Text hostText;
	private Combo loginCombo;
	private Button testButton;
	protected Label passwordLabel;
	protected Text passwordText;
	protected Button savePasswordButton;
	private Text remotePathText;
	private Button browseButton;
	protected ExpandableComposite optionsExpandable;
	protected IOptionsComposite advancedOptions;
	private ProgressMonitorPart progressMonitorPart;

	protected Font smallFont;

	private boolean isNew;
	private boolean connectionTested;
	private ModifyListener modifyListener;
	private SelectionListener selectionListener;

	private IListener listener;

	public FTPConnectionPropertyComposite(Composite parent, int style, IBaseRemoteConnectionPoint connectionPoint,
			IListener listener)
	{
		super(parent, style);
		setConnectionPoint(connectionPoint);
		this.listener = listener;

		PixelConverter converter = new PixelConverter(this);
		setLayout(GridLayoutFactory
				.swtDefaults()
				.margins(converter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN),
						converter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN))
				.spacing(converter.convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING),
						converter.convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING)).numColumns(2)
				.create());

		/* row 1 */
		createSiteSection(this);

		Group group = new Group(this, SWT.NONE);
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
		group.setText(Messages.FTPConnectionPointPropertyDialog_LBL_GroupInfo);

		/* row 2 */
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
						SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_Server));

		hostText = new Text(group, SWT.SINGLE | SWT.BORDER);
		hostText.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(hostText).convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH),
						SWT.DEFAULT).span(2, 1).create());

		/* row 3 */
		new Label(group, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());

		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
		smallFont = JFaceResources.getTextFontDescriptor().increaseHeight(-2).createFont(getDisplay());
		label.setFont(smallFont);
		label.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Example);

		/* row 4 */
		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
						SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_Username));

		loginCombo = new Combo(group, SWT.DROP_DOWN | SWT.BORDER);
		loginCombo.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(loginCombo).convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH),
						SWT.DEFAULT).grab(true, false).create());
		loginCombo.add(IFTPConstants.LOGIN_ANONYMOUS);

		testButton = new Button(group, SWT.PUSH);
		testButton.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Test);
		testButton.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(Math.max(
						new PixelConverter(testButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
						testButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x), SWT.DEFAULT).create());

		/* row 5 */
		createPasswordSection(group);

		/* row 6 */
		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
						SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_RemotePath));

		remotePathText = new Text(group, SWT.SINGLE | SWT.BORDER);
		remotePathText.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(remotePathText)
						.convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());

		browseButton = new Button(group, SWT.PUSH);
		browseButton.setText('&' + StringUtil.ellipsify(CoreStrings.BROWSE));
		browseButton.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(Math.max(
						new PixelConverter(browseButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
						browseButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x), SWT.DEFAULT).create());

		/* row 7 */
		optionsExpandable = new ExpandableComposite(this, SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.FOCUS_TITLE);
		optionsExpandable.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Options);
		optionsExpandable.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true)
				.minSize(SWT.DEFAULT, SWT.DEFAULT).create());
		Group optionsGroup = new Group(optionsExpandable, SWT.NONE);
		optionsGroup.setLayout(GridLayoutFactory.fillDefaults().create());

		createAdvancedOptions(optionsGroup);

		optionsExpandable.setClient(optionsGroup);
		optionsExpandable.addExpansionListener(new ExpansionAdapter()
		{

			public void expansionStateChanged(ExpansionEvent e)
			{
				FTPConnectionPropertyComposite.this.listener.layoutShell();
			}
		});

		/* row 8 */
		progressMonitorPart = new ProgressMonitorPart(this, GridLayoutFactory.fillDefaults().create());
		progressMonitorPart.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).exclude(true).span(2, 1)
				.create());

		/* -- */
		addListeners();

		passwordText.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				e.doit = false;
				testConnection();
			}
		});

		testButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (testConnection())
				{
					MessageDialog.openInformation(
							getShell(),
							Messages.FTPConnectionPointPropertyDialog_Succeed_Title,
							MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_Succeed_Message,
									hostText.getText()));
				}
			}
		});

		browseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				browseConnection();
			}
		});

		if (ftpConnectionPoint == null)
		{
			isNew = true;
			ftpConnectionPoint = getOrCreateConnectionPoint(getConnectionPointType());
			ftpConnectionPoint.setName(DEFAULT_NAME);
		}
		else
		{
			originalFtpConnectionPoint = ftpConnectionPoint;
		}
		loadPropertiesFrom(ftpConnectionPoint);
		connectionTested = !isNew;
	}

	public boolean completeConnection()
	{
		if (DEFAULT_NAME.equals(nameText.getText()))
		{
			nameText.setText(hostText.getText());
		}
		if (!connectionTested)
		{
			if (!testConnection())
			{
				MessageDialog dlg = new MessageDialog(getShell(),
						Messages.FTPConnectionPointPropertyDialog_ConfirmTitle, null,
						Messages.FTPConnectionPointPropertyDialog_ConfirmMessage, MessageDialog.QUESTION, new String[] {
								IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
								Messages.FTPConnectionPointPropertyDialog_LBL_Edit }, 2);
				int code = dlg.open();
				switch (code)
				{
					case 1:
						return true;
					case 2:
						return false;
					default:
				}
			}
		}
		CoreIOPlugin.getAuthenticationManager().setPassword(getAuthId(ftpConnectionPoint),
				passwordText.getText().toCharArray(), savePasswordButton.getSelection());

		boolean changed = savePropertiesTo(ftpConnectionPoint);
		if (isNew)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(ftpConnectionPoint);
			StudioAnalytics.getInstance().sendEvent(
					new FeatureEvent("remote.new." + getConnectionPoint().getId(), null)); //$NON-NLS-1$
		}
		else if (ftpConnectionPoint != originalFtpConnectionPoint) // $codepro.audit.disable useEquals
		{
			ftpConnectionPoint.setId(originalFtpConnectionPoint.getId());
			CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(originalFtpConnectionPoint);
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(ftpConnectionPoint);
		}
		else if (changed)
		{
			CoreIOPlugin.getConnectionPointManager().connectionPointChanged(ftpConnectionPoint);
		}
		return true;
	}

	public IBaseRemoteConnectionPoint getConnectionPoint()
	{
		return ftpConnectionPoint;
	}

	public void setConnectionPoint(IBaseRemoteConnectionPoint connectionPoint)
	{
		ftpConnectionPoint = connectionPoint;
	}

	public void setCanceled(boolean canceled)
	{
		progressMonitorPart.setCanceled(canceled);
	}

	@Override
	public void dispose()
	{
		if (smallFont != null)
		{
			smallFont.dispose();
			smallFont = null;
		}
		super.dispose();
	}

	public boolean isValid()
	{
		String message = null;
		String name = nameText.getText().trim();
		if (name.length() == 0)
		{
			message = Messages.FTPConnectionPointPropertyDialog_ERR_NameEmpty;
		}
		else if ((originalFtpConnectionPoint == null || !name.equalsIgnoreCase(originalFtpConnectionPoint.getName()))
				&& !ConnectionPointUtils.isConnectionPointNameUnique(nameText.getText()))
		{
			message = MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_ERR_NameExists, name);
		}
		else if (!HOST_PATTERN.matcher(hostText.getText()).matches())
		{
			message = Messages.FTPConnectionPointPropertyDialog_ERR_InvalidHost;
		}
		else if (loginCombo.getText().length() == 0)
		{
			message = Messages.FTPConnectionPointPropertyDialog_ERR_NoUsername;
		}
		else
		{
			message = advancedOptions.isValid();
		}
		listener.error(message);
		return message == null;
	}

	public boolean testConnection(ConnectionContext context, final IConnectionRunnable connectRunnable)
	{
		// WORKAROUND: getting contents after the control is disabled will return empty string if not called here
		hostText.getText();
		loginCombo.getText();
		passwordText.getText();
		remotePathText.getText();
		lockUI(true);
		((GridData) progressMonitorPart.getLayoutData()).exclude = false;
		listener.layoutShell();
		try
		{
			final IBaseRemoteConnectionPoint connectionPoint = isNew ? ftpConnectionPoint
					: (IBaseRemoteConnectionPoint) CoreIOPlugin.getConnectionPointManager().cloneConnectionPoint(
							ftpConnectionPoint);
			savePropertiesTo(connectionPoint);
			if (context == null)
			{
				context = new ConnectionContext(); // $codepro.audit.disable questionableAssignment
				context.setBoolean(ConnectionContext.QUICK_CONNECT, true);
			}
			context.setBoolean(ConnectionContext.NO_PASSWORD_PROMPT, true);
			CoreIOPlugin.setConnectionContext(connectionPoint, context);

			ModalContext.run(new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					try
					{
						try
						{
							if (connectRunnable != null)
							{
								connectRunnable.beforeConnect(connectionPoint);
							}
							connectionPoint.connect(monitor);
							if (connectRunnable != null)
							{
								connectRunnable.afterConnect(connectionPoint, monitor);
							}
						}
						finally
						{
							try
							{
								connectionPoint.disconnect(monitor);
							}
							catch (CoreException e)
							{
								IdeLog.logWarning(FTPUIPlugin.getDefault(), e);
							}
						}
					}
					catch (CoreException e)
					{
						throw new InvocationTargetException(e);
					}
					finally
					{
						CoreIOPlugin.clearConnectionContext(connectionPoint);
						monitor.done();
					}
				}
			}, true, progressMonitorPart, getShell().getDisplay());

			return connectionTested = true;
		}
		catch (InterruptedException e)
		{
			e.getCause();
		}
		catch (InvocationTargetException e)
		{
			showErrorDialog(e.getTargetException());
		}
		catch (CoreException e)
		{
			showErrorDialog(e);
		}
		finally
		{
			if (!progressMonitorPart.isDisposed())
			{
				((GridData) progressMonitorPart.getLayoutData()).exclude = true;
				listener.layoutShell();
				lockUI(false);
			}
		}
		return false;
	}

	public void validate()
	{
		if (isDisposed())
		{
			return;
		}
		boolean valid = isValid();
		testButton.setEnabled(valid);
		browseButton.setEnabled(valid);
		advancedOptions.setValid(valid);
		listener.setValid(valid);
	}

	protected void createSiteSection(Composite parent)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
						SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_SiteName));

		nameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(new PixelConverter(nameText).convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH),
						SWT.DEFAULT).grab(true, false).create());
	}

	protected void createPasswordSection(Composite parent)
	{
		passwordLabel = new Label(parent, SWT.NONE);
		passwordLabel.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(passwordLabel).convertHorizontalDLUsToPixels(IDialogConstants.LABEL_WIDTH),
						SWT.DEFAULT).create());
		passwordLabel.setText(StringUtil.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_LBL_Password));

		passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(new PixelConverter(passwordText)
						.convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());

		savePasswordButton = new Button(parent, SWT.CHECK);
		savePasswordButton.setLayoutData(GridDataFactory.fillDefaults().create());
		savePasswordButton.setText(Messages.FTPConnectionPointPropertyDialog_LBL_Save);
	}

	protected void createAdvancedOptions(Composite parent)
	{
		advancedOptions = new FTPAdvancedOptionsComposite(parent, SWT.NONE, this);
		((Composite) advancedOptions).setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
	}

	protected ConnectionPointType getConnectionPointType()
	{
		if (ftpConnectionPoint != null)
		{
			return CoreIOPlugin.getConnectionPointManager().getType(ftpConnectionPoint);
		}
		return CoreIOPlugin.getConnectionPointManager().getType(IBaseFTPConnectionPoint.TYPE_FTP);
	}

	protected IBaseRemoteConnectionPoint getOrCreateConnectionPoint(ConnectionPointType connectionPointType)
	{
		if (!isNew)
		{
			if (CoreIOPlugin.getConnectionPointManager().getType(originalFtpConnectionPoint)
					.equals(connectionPointType))
			{
				return originalFtpConnectionPoint;
			}
		}
		try
		{
			return (IBaseRemoteConnectionPoint) CoreIOPlugin.getConnectionPointManager().createConnectionPoint(
					connectionPointType);
		}
		catch (CoreException e)
		{
			IdeLog.logError(FTPUIPlugin.getDefault(), Messages.FTPConnectionPointPropertyDialog_ERR_FailedCreate, e);
			listener.close(); // $codepro.audit.disable closeInFinally
			throw new SWTException(e.getLocalizedMessage());
		}
	}

	protected String getAuthId(IBaseRemoteConnectionPoint connectionPoint)
	{
		return Policy.generateAuthId(getConnectionPointType().getType().toUpperCase(), connectionPoint);
	}

	protected void lockUI(boolean lock)
	{
		listener.lockUI(lock);
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

	protected void loadPropertiesFrom(IBaseRemoteConnectionPoint connectionPoint)
	{
		removeListeners();
		try
		{
			nameText.setText(valueOrEmpty(connectionPoint.getName()));
			hostText.setText(valueOrEmpty(connectionPoint.getHost()));
			remotePathText.setText(connectionPoint.getPath().toPortableString());
			String login = connectionPoint.getLogin();
			int index = loginCombo.indexOf(login);
			if (index >= 0)
			{
				loginCombo.select(index);
			}
			else
			{
				loginCombo.setText(login);
			}
			String authId = getAuthId(connectionPoint);
			boolean persistent = CoreIOPlugin.getAuthenticationManager().hasPersistent(authId);
			savePasswordButton.setSelection(persistent);
			char[] password = connectionPoint.getPassword();
			if (persistent && password == null)
			{
				password = CoreIOPlugin.getAuthenticationManager().getPassword(authId);
			}
			if (password != null)
			{
				passwordText.setText(String.copyValueOf(password));
			}
			advancedOptions.loadPropertiesFrom(connectionPoint);
		}
		finally
		{
			addListeners();
		}
	}

	protected boolean savePropertiesTo(IBaseRemoteConnectionPoint connectionPoint)
	{
		boolean updated = false;
		String name = nameText.getText().trim();
		if (!name.equals(connectionPoint.getName()))
		{
			connectionPoint.setName(name);
			updated = true;
		}
		String host = hostText.getText();
		if (!host.equals(connectionPoint.getHost()))
		{
			connectionPoint.setHost(host);
			updated = true;
		}
		IPath path = Path.fromPortableString(remotePathText.getText());
		if (!connectionPoint.getPath().equals(path))
		{
			connectionPoint.setPath(path);
			updated = true;
		}
		String login = loginCombo.getText();
		if (!login.equals(connectionPoint.getLogin()))
		{
			connectionPoint.setLogin(login);
			updated = true;
		}
		char[] password = passwordText.getText().toCharArray();
		if (!Arrays.equals(password, connectionPoint.getPassword()))
		{
			connectionPoint.setPassword(password);
			updated = true;
		}

		if (advancedOptions.savePropertiesTo(connectionPoint))
		{
			updated = true;
		}
		return updated;
	}

	protected void addListeners()
	{
		if (modifyListener == null)
		{
			modifyListener = new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					validate();
					if (e.widget != nameText) // $codepro.audit.disable useEquals
					{
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
		if (selectionListener == null)
		{
			selectionListener = new SelectionAdapter()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					validate();
				}
			};
		}
		loginCombo.addSelectionListener(selectionListener);
	}

	protected void removeListeners()
	{
		if (modifyListener != null)
		{
			nameText.removeModifyListener(modifyListener);
			hostText.removeModifyListener(modifyListener);
			loginCombo.removeModifyListener(modifyListener);
			passwordText.removeModifyListener(modifyListener);
			remotePathText.removeModifyListener(modifyListener);
		}
		if (selectionListener != null)
		{
			loginCombo.removeSelectionListener(selectionListener);
		}
	}

	private boolean testConnection()
	{
		return testConnection(null, null);
	}

	private void browseConnection()
	{
		testConnection(null, new IConnectionRunnable()
		{

			public void afterConnect(final IConnectionPoint connectionPoint, IProgressMonitor monitor)
					throws CoreException, InterruptedException
			{
				monitor.beginTask(Messages.FTPConnectionPointPropertyDialog_Task_Browse, IProgressMonitor.UNKNOWN);
				monitor.subTask(""); //$NON-NLS-1$
				UIUtils.getDisplay().syncExec(new Runnable()
				{
					public void run()
					{
						showBrowseDialog(connectionPoint);
					}
				});
				monitor.done();
			}

			public void beforeConnect(IConnectionPoint connectionPoint) throws CoreException, InterruptedException
			{
				((IBaseRemoteConnectionPoint) connectionPoint).setPath(Path.ROOT);
			}
		});
	}

	private void showBrowseDialog(IConnectionPoint connectionPoint)
	{
		FileTreeSelectionDialog dlg = new FileTreeSelectionDialog(getShell(), false);
		dlg.setTitle(MessageFormat.format(Messages.FTPConnectionPointPropertyDialog_Title_Browse,
				((IBaseRemoteConnectionPoint) connectionPoint).getHost()));
		dlg.setMessage(StringUtil.makeFormLabel(Messages.FTPConnectionPointPropertyDialog_Message_Browse));
		dlg.setInput(connectionPoint);
		String pathString = remotePathText.getText();
		try
		{
			IFileStore selection = connectionPoint.getRoot();
			if (pathString.length() > 0)
			{
				selection = selection.getFileStore(Path.fromPortableString(pathString));
			}
			dlg.setInitialSelection(selection);
		}
		catch (CoreException e)
		{
			IdeLog.logWarning(FTPUIPlugin.getDefault(), e);
		}
		if (dlg.open() == Window.OK)
		{
			URI uri = FileSystemUtils.getURI(dlg.getFirstResult());
			if (uri != null)
			{
				String path = Path.fromPortableString(connectionPoint.getRootURI().relativize(uri).toString())
						.makeAbsolute().toPortableString();
				remotePathText.setText(path);
			}
		}
	}

	private void showErrorDialog(Throwable e)
	{
		String message = Messages.FTPConnectionPointPropertyDialog_DefaultErrorMsg;
		if (e instanceof CoreException)
		{
			message = ((CoreException) e).getStatus().getMessage();
		}
		MessageDialog.openError(getShell(), Messages.FTPConnectionPointPropertyDialog_ErrorTitle, message);
	}

	private static String valueOrEmpty(String value)
	{
		if (value != null)
		{
			return value;
		}
		return ""; //$NON-NLS-1$
	}
}
