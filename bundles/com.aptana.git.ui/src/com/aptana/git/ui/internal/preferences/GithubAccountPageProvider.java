/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.preferences;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.github.IGithubManager;
import com.aptana.git.core.github.IGithubUser;
import com.aptana.git.ui.GitUIPlugin;
import com.aptana.ui.preferences.AbstractAccountPageProvider;
import com.aptana.ui.util.SWTUtils;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.util.WorkbenchBrowserUtil;

public class GithubAccountPageProvider extends AbstractAccountPageProvider
{

	public static interface GithubListener
	{
		public void loggedIn();

		public void loggedOut();
	}

	private static final String SIGNUP_URL = "https://github.com/"; //$NON-NLS-1$

	private Group main;
	private Composite loginComposite;
	private Composite logoutComposite;
	private Text usernameText;
	private Text passwordText;
	private Button testButton;
	private Button createAccountButton;
	private Label userLabel;
	private Button logoutButton;

	private List<GithubListener> listeners;

	public GithubAccountPageProvider()
	{
		this(new NullProgressMonitor());
	}

	public GithubAccountPageProvider(IProgressMonitor progressMonitor)
	{
		super(progressMonitor);
		listeners = new ArrayList<GithubListener>();
	}

	public Control createContents(Composite parent)
	{
		main = new Group(parent, SWT.NONE);
		main.setText(Messages.GithubAccountPageProvider_Name);
		main.setLayout(GridLayoutFactory.fillDefaults().create());

		loginComposite = createLoginComponents(main);
		boolean isLoggedOut = isLoggedOut();
		loginComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).exclude(!isLoggedOut).create());
		logoutComposite = createLogoutComponents(main);
		logoutComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).exclude(isLoggedOut).create());

		return main;
	}

	public boolean addListener(GithubListener listener)
	{
		if (listener == null)
		{
			return false;
		}
		if (!listeners.contains(listener))
		{
			return listeners.add(listener);
		}
		return false;
	}

	public boolean removeListener(GithubListener listener)
	{
		if (listener == null)
		{
			return false;
		}
		return listeners.remove(listener);
	}

	private Composite createLoginComponents(Composite parent)
	{
		Composite loginComp = new Composite(parent, SWT.NONE);
		loginComp.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

		Label label = new Label(loginComp, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.GithubAccountPageProvider_Username_LBL));
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
		testButton.setText(Messages.GithubAccountPageProvider_Validate_LBL);
		testButton.setLayoutData(GridDataFactory.swtDefaults().hint(getButtonWidthHint(testButton), SWT.DEFAULT)
				.create());
		testButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				login(true);
			}
		});

		label = new Label(loginComp, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.GithubAccountPageProvider_Password_LBL));
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		passwordText = new Text(loginComp, SWT.BORDER | SWT.PASSWORD);
		passwordText
				.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		passwordText.addModifyListener(modifyListener);

		createAccountButton = new Button(loginComp, SWT.NONE);
		createAccountButton.setText(StringUtil.ellipsify(Messages.GithubAccountPageProvider_Signup_LBL));
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
		
		Composite noteComp = new Composite(loginComp, SWT.NONE);
		noteComp.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
		noteComp.setLayoutData(GridDataFactory.swtDefaults().span(3, 1).create());

		Link linkLabel = new Link(noteComp, SWT.NONE);
		linkLabel.setText(Messages.GithubAccountPageProvider_NoteMessageToUsePersonalToken);
		linkLabel.setLayoutData(GridDataFactory.swtDefaults().align(SWT.LEFT, SWT.TOP).grab(false, false).create());
		linkLabel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					IWebBrowser browser = GitUIPlugin
							.getDefault()
							.getWorkbench()
							.getBrowserSupport()
							.createBrowser(IWorkbenchBrowserSupport.AS_EXTERNAL | IWorkbenchBrowserSupport.STATUS,
									"GitHub Personal Access Tokens", null, null); //$NON-NLS-1$ (just the browser name)
					browser.openURL(new URL("https://github.com/settings/tokens")); //$NON-NLS-1$
				}
				catch (Exception ex)
				{
					IdeLog.logError(GitUIPlugin.getDefault(), ex);
				}
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
		label.setText(StringUtil.makeFormLabel(Messages.GithubAccountPageProvider_User_LBL));
		userLabel = new Label(logoutComp, SWT.NONE);
		logoutButton = new Button(logoutComp, SWT.PUSH);
		logoutButton.setText(Messages.GithubAccountPageProvider_Logout_LBL);
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

	protected IGithubManager getGithubManager()
	{
		return GitPlugin.getDefault().getGithubManager();
	}

	private boolean isLoggedOut()
	{
		return getGithubManager().getUser() == null;
	}

	private void updateUserText()
	{
		IGithubUser user = getGithubManager().getUser();
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
				MessageDialog.openError(main.getShell(), Messages.GithubAccountPageProvider_InvalidInputTitle,
						Messages.GithubAccountPageProvider_EmptyUsername);
				return false;
			}
			return true;
		}
		if (StringUtil.isEmpty(password))
		{
			if (test)
			{
				MessageDialog.openError(main.getShell(), Messages.GithubAccountPageProvider_InvalidInputTitle,
						Messages.GithubAccountPageProvider_EmptyPassword);
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
					monitor.beginTask(Messages.GithubAccountPageProvider_ValidatingCredentials,
							IProgressMonitor.UNKNOWN);
					try
					{
						IStatus status = getGithubManager().login(username, password);
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
							fireLoggedInEvent();
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
				MessageDialog.openError(main.getShell(), Messages.GithubAccountPageProvider_LoginFailed, e
						.getTargetException().getMessage());
			}
			return false;
		}
		catch (Exception e)
		{
			IdeLog.logError(GitUIPlugin.getDefault(), e);
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
		getGithubManager().logout();
		usernameText.setText(StringUtil.EMPTY);
		passwordText.setText(StringUtil.EMPTY);
		usernameText.setFocus();
		layout();
		fireLoggedOutEvent();
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

	private void fireLoggedInEvent()
	{
		for (GithubListener listener : listeners)
		{
			listener.loggedIn();
		}
	}

	private void fireLoggedOutEvent()
	{
		for (GithubListener listener : listeners)
		{
			listener.loggedOut();
		}
	}

	private static int getButtonWidthHint(Button button)
	{
		PixelConverter converter = new PixelConverter(button);
		int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}
}
