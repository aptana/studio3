/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.s3.internal;

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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.ide.core.io.ConnectionContext;
import com.aptana.ide.core.io.ConnectionPointType;
import com.aptana.ide.core.io.ConnectionPointUtils;
import com.aptana.ide.core.io.CoreIOPlugin;
import com.aptana.ide.core.io.IBaseRemoteConnectionPoint;
import com.aptana.ide.core.io.IConnectionPoint;
import com.aptana.ide.filesystem.s3.Policy;
import com.aptana.ide.filesystem.s3.S3ConnectionPoint;
import com.aptana.ide.ui.io.FileSystemUtils;
import com.aptana.ide.ui.io.dialogs.FileTreeSelectionDialog;
import com.aptana.ide.ui.io.dialogs.IDialogConstants;
import com.aptana.ui.s3.S3UIPlugin;
import com.aptana.ui.s3.dialogs.Messages;
import com.aptana.ui.util.UIUtils;

/**
 * @author Max Stepanov
 */
public class S3ConnectionPropertyComposite extends Composite implements IOptionsComposite.Listener
{

	public static interface Listener
	{
		public void setValid(boolean valid);

		public void error(String message);

		public void lockUI(boolean lock);

		public void layoutShell();

		public boolean close();
	}

	private static final String DEFAULT_NAME = Messages.S3ConnectionPointPropertyDialog_Title;
	private static final Pattern HOST_PATTERN = Pattern
			.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([-\\w]+(\\.[-\\w]+)*)$"); //$NON-NLS-1$
	/**
	 * Since label for secret access key is too long, adjust this dialog's label width
	 */
	private static final int LABEL_WIDTH = IDialogConstants.LABEL_WIDTH + 25;

	protected IBaseRemoteConnectionPoint s3ConnectionPoint;
	protected IBaseRemoteConnectionPoint originalS3ConnectionPoint;

	private Text nameText;
	private Text hostText;
	private Text accessKeyText;
	private Button testButton;
	protected Label passwordLabel;
	protected Text passwordText;
	protected Button savePasswordButton;
	private Text remotePathText;
	private Button browseButton;
	protected ExpandableComposite optionsExpandable;
	private ProgressMonitorPart progressMonitorPart;

	protected Font smallFont;

	private boolean isNew;
	private boolean connectionTested;
	private ModifyListener modifyListener;

	private Listener listener;

	public S3ConnectionPropertyComposite(Composite parent, int style, IBaseRemoteConnectionPoint connectionPoint,
			Listener listener)
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
		group.setText(Messages.S3ConnectionPointPropertyDialog_LBL_GroupInfo);

		/* row 2 */
		Label label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.S3ConnectionPointPropertyDialog_LBL_Server));

		hostText = new Text(group, SWT.SINGLE | SWT.BORDER);
		hostText.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(hostText).convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH),
						SWT.DEFAULT).span(2, 1).create());
		hostText.setText(S3ConnectionPoint.DEFAULT_HOST);

		/* row 3 */
		new Label(group, SWT.NONE).setLayoutData(GridDataFactory.swtDefaults().create());

		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).create());
		smallFont = JFaceResources.getTextFontDescriptor().increaseHeight(-2).createFont(getDisplay());
		label.setFont(smallFont);
		label.setText(Messages.S3ConnectionPointPropertyDialog_LBL_Example);

		/* row 4 */
		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.S3ConnectionPointPropertyDialog_LBL_AccessKeyID));

		accessKeyText = new Text(group, SWT.SINGLE | SWT.BORDER);
		accessKeyText.setLayoutData(GridDataFactory
				.swtDefaults()
				.hint(new PixelConverter(accessKeyText)
						.convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());

		testButton = new Button(group, SWT.PUSH);
		testButton.setText(Messages.S3ConnectionPointPropertyDialog_LBL_Test);
		testButton.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(Math.max(
						new PixelConverter(testButton).convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH),
						testButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x), SWT.DEFAULT).create());

		/* row 5 */
		createPasswordSection(group);

		/* row 6 */
		label = new Label(group, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.S3ConnectionPointPropertyDialog_LBL_RemotePath));

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
							Messages.S3ConnectionPointPropertyDialog_Succeed_Title,
							MessageFormat.format(Messages.S3ConnectionPointPropertyDialog_Succeed_Message,
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

		if (s3ConnectionPoint == null)
		{
			isNew = true;
			s3ConnectionPoint = getOrCreateConnectionPoint(getConnectionPointType());
			s3ConnectionPoint.setName(DEFAULT_NAME);
		}
		else
		{
			originalS3ConnectionPoint = s3ConnectionPoint;
		}
		loadPropertiesFrom(s3ConnectionPoint);
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
						Messages.S3ConnectionPointPropertyDialog_ConfirmTitle, null,
						Messages.S3ConnectionPointPropertyDialog_ConfirmMessage, MessageDialog.QUESTION, new String[] {
								IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
								Messages.S3ConnectionPointPropertyDialog_LBL_Edit }, 2);
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
		CoreIOPlugin.getAuthenticationManager().setPassword(getAuthId(s3ConnectionPoint),
				passwordText.getText().toCharArray(), savePasswordButton.getSelection());

		boolean changed = savePropertiesTo(s3ConnectionPoint);
		if (isNew)
		{
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(s3ConnectionPoint);
		}
		else if (s3ConnectionPoint != originalS3ConnectionPoint)
		{
			s3ConnectionPoint.setId(originalS3ConnectionPoint.getId());
			CoreIOPlugin.getConnectionPointManager().removeConnectionPoint(originalS3ConnectionPoint);
			CoreIOPlugin.getConnectionPointManager().addConnectionPoint(s3ConnectionPoint);
		}
		else if (changed)
		{
			CoreIOPlugin.getConnectionPointManager().connectionPointChanged(s3ConnectionPoint);
		}
		return true;
	}

	public IBaseRemoteConnectionPoint getConnectionPoint()
	{
		return s3ConnectionPoint;
	}

	public void setConnectionPoint(IBaseRemoteConnectionPoint connectionPoint)
	{
		s3ConnectionPoint = connectionPoint;
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
			message = Messages.S3ConnectionPointPropertyDialog_ERR_NameEmpty;
		}
		else if ((originalS3ConnectionPoint == null || !name.equalsIgnoreCase(originalS3ConnectionPoint.getName()))
				&& !ConnectionPointUtils.isConnectionPointNameUnique(name))
		{
			message = MessageFormat.format(Messages.S3ConnectionPointPropertyDialog_ERR_NameExists, name);
		}
		else if (!HOST_PATTERN.matcher(hostText.getText()).matches())
		{
			message = Messages.S3ConnectionPointPropertyDialog_ERR_InvalidHost;
		}
		else if (accessKeyText.getText().length() == 0)
		{
			message = Messages.S3ConnectionPointPropertyDialog_ERR_NoAccessKey;
		}
		listener.error(message);
		return (message == null);
	}

	public boolean testConnection(ConnectionContext context, final IConnectionRunnable connectRunnable)
	{
		// WORKAROUND: getting contents after the control is disabled will return empty string if not called here
		hostText.getText();
		accessKeyText.getText();
		passwordText.getText();
		remotePathText.getText();
		lockUI(true);
		((GridData) progressMonitorPart.getLayoutData()).exclude = false;
		listener.layoutShell();
		try
		{
			final IBaseRemoteConnectionPoint connectionPoint = isNew ? s3ConnectionPoint
					: (IBaseRemoteConnectionPoint) CoreIOPlugin.getConnectionPointManager().cloneConnectionPoint(
							s3ConnectionPoint);
			savePropertiesTo(connectionPoint);
			if (context == null)
			{
				context = new ConnectionContext();
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
								IdeLog.logWarning(S3UIPlugin.getDefault(), e);
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
		listener.setValid(valid);
	}

	protected void createSiteSection(Composite parent)
	{
		Label label = new Label(parent, SWT.NONE);
		label.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(label).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT).create());
		label.setText(StringUtil.makeFormLabel(Messages.S3ConnectionPointPropertyDialog_LBL_SiteName));

		nameText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		nameText.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(new PixelConverter(nameText).convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH),
						SWT.DEFAULT).grab(true, false).create());
	}

	protected void createPasswordSection(Composite parent)
	{
		passwordLabel = new Label(parent, SWT.NONE);
		passwordLabel.setLayoutData(GridDataFactory.swtDefaults()
				.hint(new PixelConverter(passwordLabel).convertHorizontalDLUsToPixels(LABEL_WIDTH), SWT.DEFAULT)
				.create());
		passwordLabel.setText(StringUtil.makeFormLabel(Messages.S3ConnectionPointPropertyDialog_LBL_SecretAccessKey));

		passwordText = new Text(parent, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(GridDataFactory
				.fillDefaults()
				.hint(new PixelConverter(passwordText)
						.convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH), SWT.DEFAULT)
				.grab(true, false).create());

		savePasswordButton = new Button(parent, SWT.CHECK);
		savePasswordButton.setLayoutData(GridDataFactory.fillDefaults().create());
		savePasswordButton.setText(Messages.S3ConnectionPointPropertyDialog_LBL_Save);
	}

	protected ConnectionPointType getConnectionPointType()
	{
		if (s3ConnectionPoint != null)
		{
			return CoreIOPlugin.getConnectionPointManager().getType(s3ConnectionPoint);
		}
		return CoreIOPlugin.getConnectionPointManager().getType(S3ConnectionPoint.TYPE);
	}

	protected IBaseRemoteConnectionPoint getOrCreateConnectionPoint(ConnectionPointType connectionPointType)
	{
		if (!isNew)
		{
			if (CoreIOPlugin.getConnectionPointManager().getType(originalS3ConnectionPoint).equals(connectionPointType))
			{
				return originalS3ConnectionPoint;
			}
		}
		try
		{
			return (IBaseRemoteConnectionPoint) CoreIOPlugin.getConnectionPointManager().createConnectionPoint(
					connectionPointType);
		}
		catch (CoreException e)
		{
			IdeLog.logError(S3UIPlugin.getDefault(), Messages.S3ConnectionPointPropertyDialog_ERR_FailedCreate, e);
			listener.close();
			throw new SWTException();
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
		accessKeyText.setEnabled(!lock);
		passwordText.setEnabled(!lock);
		savePasswordButton.setEnabled(!lock);
		remotePathText.setEnabled(!lock);
		remotePathText.setEnabled(!lock);
		testButton.setEnabled(!lock);
		browseButton.setEnabled(!lock);
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
			accessKeyText.setText(login);

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
		}
		finally
		{
			addListeners();
		}
	}

	protected boolean savePropertiesTo(IBaseRemoteConnectionPoint connectionPoint)
	{
		boolean updated = false;
		String name = nameText.getText();
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
		String login = accessKeyText.getText();
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
					if (e.widget != nameText)
					{
						connectionTested = false;
					}
				}
			};
		}
		nameText.addModifyListener(modifyListener);
		hostText.addModifyListener(modifyListener);
		accessKeyText.addModifyListener(modifyListener);
		passwordText.addModifyListener(modifyListener);
		remotePathText.addModifyListener(modifyListener);
	}

	protected void removeListeners()
	{
		if (modifyListener != null)
		{
			nameText.removeModifyListener(modifyListener);
			hostText.removeModifyListener(modifyListener);
			accessKeyText.removeModifyListener(modifyListener);
			passwordText.removeModifyListener(modifyListener);
			remotePathText.removeModifyListener(modifyListener);
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
				monitor.beginTask(Messages.S3ConnectionPointPropertyDialog_Task_Browse, IProgressMonitor.UNKNOWN);
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
		dlg.setTitle(MessageFormat.format(Messages.S3ConnectionPointPropertyDialog_Title_Browse,
				((IBaseRemoteConnectionPoint) connectionPoint).getHost()));
		dlg.setMessage(StringUtil.makeFormLabel(Messages.S3ConnectionPointPropertyDialog_Message_Browse));
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
			IdeLog.logWarning(S3UIPlugin.getDefault(), e);
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
		String message = Messages.S3ConnectionPointPropertyDialog_DefaultErrorMsg;
		if (e instanceof CoreException)
		{
			message = ((CoreException) e).getStatus().getMessage();
		}
		MessageDialog.openError(getShell(), Messages.S3ConnectionPointPropertyDialog_ErrorTitle, message);
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
