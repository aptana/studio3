/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.jira.ui.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.jira.core.JiraCorePlugin;
import com.aptana.jira.core.JiraManager;
import com.aptana.jira.core.JiraUser;
import com.aptana.jira.ui.JiraUIPlugin;
import com.aptana.ui.preferences.AbstractAccountPageProvider;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class JiraPreferencePageProvider extends AbstractAccountPageProvider
{

	private static final String SIGNUP_URL = "https://jira.appcelerator.org/secure/Signup!default.jspa"; //$NON-NLS-1$

	private Group main;
	private Composite loginComposite;
	private Composite logoutComposite;
	private Text usernameText;
	private Text passwordText;
	private Button testButton;
	private Button createAccountButton;
	private Label userLabel;
	private Button logoutButton;

	public JiraPreferencePageProvider()
	{
	}

	public JiraPreferencePageProvider(IProgressMonitor progressMonitor)
	{
		super(progressMonitor);
	}

	public Control createContents(Composite parent)
	{
		main = new Group(parent, SWT.NONE);
		main.setText(Messages.JiraPreferencePageProvider_LBL_Jira);
		main.setLayout(GridLayoutFactory.fillDefaults().create());

		loginComposite = createLoginComponents(main);
		boolean isLoggedOut = isLoggedOut();
		loginComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).exclude(!isLoggedOut).create());
		logoutComposite = createLogoutComponents(main);
		logoutComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).exclude(isLoggedOut).create());

		return main;
	}

	private Composite createLoginComponents(Composite parent)
	{
		Composite loginComp = new Composite(parent, SWT.NONE);
		loginComp.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

		Label label = new Label(loginComp, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.JiraPreferencePageProvider_LBL_Username));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		ModifyListener modifyListener = new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				updateButtonStates();
			}
		};
		usernameText = new Text(loginComp, SWT.BORDER);
		usernameText
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		usernameText.addModifyListener(modifyListener);

		testButton = new Button(loginComp, SWT.NONE);
		testButton.setText(Messages.JiraPreferencePageProvider_LBL_Validate);
		testButton.setLayoutData(GridDataFactory.swtDefaults().hint(getButtonWidthHint(testButton), SWT.DEFAULT)
				.create());
		testButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (login(true))
				{
					// shows a success message
					MessageDialog.openInformation(UIUtils.getActiveShell(),
							Messages.JiraPreferencePageProvider_Success_Title,
							Messages.JiraPreferencePageProvider_Success_Message);
				}
			}
		});

		label = new Label(loginComp, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.JiraPreferencePageProvider_LBL_Password));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		passwordText = new Text(loginComp, SWT.BORDER | SWT.PASSWORD);
		passwordText
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		passwordText.addModifyListener(modifyListener);

		createAccountButton = new Button(loginComp, SWT.NONE);
		createAccountButton.setText(StringUtil.ellipsify(Messages.JiraPreferencePageProvider_LBL_Signup));
		createAccountButton.setLayoutData(GridDataFactory.swtDefaults()
				.hint(getButtonWidthHint(createAccountButton), SWT.DEFAULT).create());
		createAccountButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				WorkbenchBrowserUtil.launchExternalBrowser(SIGNUP_URL);
			}
		});

		KeyListener keyListener = new KeyListener()
		{

			public void keyPressed(KeyEvent e)
			{
				if (e.character == SWT.CR || e.character == SWT.KEYPAD_CR)
				{
					if (testButton.isEnabled())
					{
						login(true);
					}
				}
			}

			public void keyReleased(KeyEvent e)
			{
			}
		};
		usernameText.addKeyListener(keyListener);
		passwordText.addKeyListener(keyListener);

		updateButtonStates();
		adjustWidth();

		return loginComp;
	}

	private Composite createLogoutComponents(Composite parent)
	{
		Composite logoutComp = new Composite(parent, SWT.NONE);
		logoutComp.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

		Label label = new Label(logoutComp, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.JiraPreferencePageProvider_LBL_User));
		userLabel = new Label(logoutComp, SWT.NONE);
		logoutButton = new Button(logoutComp, SWT.PUSH);
		logoutButton.setText(Messages.JiraPreferencePageProvider_LBL_Logout);
		logoutButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				logout();
			}
		});
		updateUserText();

		return logoutComp;
	}

	public boolean performOk()
	{
		if (isLoggedOut())
		{
			return login(false);
		}
		return true;
	}

	protected JiraManager getJiraManager()
	{
		return JiraCorePlugin.getDefault().getJiraManager();
	}

	private boolean isLoggedOut()
	{
		return getJiraManager().getUser() == null;
	}

	private void updateUserText()
	{
		JiraUser user = getJiraManager().getUser();
		if (user != null)
		{
			userLabel.setText(user.getUsername());
		}
	}

	private void updateButtonStates()
	{
		testButton.setEnabled(!StringUtil.isEmpty(usernameText.getText())
				&& !StringUtil.isEmpty(passwordText.getText()));
	}

	private void adjustWidth()
	{
		List<Control> actionControls = new ArrayList<Control>();
		actionControls.add(testButton);
		actionControls.add(createAccountButton);

		SWTUtils.resizeControlWidthInGrid(actionControls);
	}

	private boolean login(boolean test)
	{
		final String username = usernameText.getText();
		final String password = passwordText.getText();
		if (StringUtil.isEmpty(username))
		{
			if (test)
			{
				MessageDialog.openError(main.getShell(), Messages.JiraPreferencePageProvider_ERR_InvalidInput_Title,
						Messages.JiraPreferencePageProvider_ERR_EmptyUsername);
				return false;
			}
			return true;
		}
		if (StringUtil.isEmpty(password))
		{
			if (test)
			{
				MessageDialog.openError(main.getShell(), Messages.JiraPreferencePageProvider_ERR_InvalidInput_Title,
						Messages.JiraPreferencePageProvider_ERR_EmptyPassword);
				return false;
			}
			return true;
		}

		setUILocked(true);
		firePreValidationStartEvent();
		try
		{
			ModalContext.run(new IRunnableWithProgress()
			{

				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
				{
					monitor.beginTask(Messages.JiraPreferencePageProvider_ValidateCredentials, IProgressMonitor.UNKNOWN);
					try
					{
						IStatus status = getJiraManager().login(username, password);
						if (status.isOK())
						{
							// successful; now re-layout to show the logout components
							UIUtils.getDisplay().asyncExec(new Runnable()
							{

								public void run()
								{
									updateUserText();
									layout();
								}
							});
						}
						else
						{
							throw new InvocationTargetException(new CoreException(status));
						}
					}
					finally
					{
						if (!monitor.isCanceled())
						{
							monitor.done();
						}
					}
				}
			}, true, getProgressMonitor(), UIUtils.getDisplay());
		}
		catch (InvocationTargetException e)
		{
			if (main != null && !main.isDisposed())
			{
				MessageDialog.openError(main.getShell(), Messages.JiraPreferencePageProvider_ERR_LoginFailed_Title, e
						.getTargetException().getMessage());
			}
			return false;
		}
		catch (Exception e)
		{
			IdeLog.logError(JiraUIPlugin.getDefault(), e);
		}
		finally
		{
			setUILocked(false);
			firePostValidationEndEvent();
		}

		return true;
	}

	private void logout()
	{
		getJiraManager().logout();
		usernameText.setText(StringUtil.EMPTY);
		passwordText.setText(StringUtil.EMPTY);
		usernameText.setFocus();
		layout();
	}

	private void layout()
	{
		boolean isLoggedOut = isLoggedOut();
		loginComposite.setVisible(isLoggedOut);
		((GridData) loginComposite.getLayoutData()).exclude = !isLoggedOut;
		logoutComposite.setVisible(!isLoggedOut);
		((GridData) logoutComposite.getLayoutData()).exclude = isLoggedOut;
		main.getParent().layout(true, true);
	}

	private void setUILocked(boolean locked)
	{
		if (main == null || main.isDisposed())
		{
			return;
		}
		usernameText.setEnabled(!locked);
		passwordText.setEnabled(!locked);
		testButton.setEnabled(!locked);
		createAccountButton.setEnabled(!locked);
	}

	private static int getButtonWidthHint(Button button)
	{
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}
}
