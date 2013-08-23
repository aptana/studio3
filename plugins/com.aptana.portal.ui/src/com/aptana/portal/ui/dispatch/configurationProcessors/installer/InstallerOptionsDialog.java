/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.configurationProcessors.installer;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.portal.ui.dispatch.configurationProcessors.Messages;

/**
 * A generic implementation for an installation dialog. Through this dialog, the user can input arbitrary data that is
 * needed for the specific installer.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class InstallerOptionsDialog extends TitleAreaDialog
{
	public static final String INSTALL_DIR_ATTR = "install_dir"; //$NON-NLS-1$
	protected Map<String, Object> attributes;
	protected Text path;
	private String installerName;
	private boolean createInstallDir;

	/**
	 * Constructs a new InstallerOptionsDialog
	 * 
	 * @param parentShell
	 * @param installerName
	 */
	public InstallerOptionsDialog(Shell parentShell, String installerName)
	{
		this(parentShell, installerName, false);
	}

	/**
	 * Constructs a new InstallerOptionsDialog.
	 * 
	 * @param parentShell
	 * @param installerName
	 * @param createInstallDir
	 *            - In case it's <code>true</code>, an input directory that does not exist will be created when the user
	 *            clicks OK.
	 */
	public InstallerOptionsDialog(Shell parentShell, String installerName, boolean createInstallDir)
	{
		super(Display.getDefault().getActiveShell());
		this.installerName = installerName;
		setBlockOnOpen(true);
		setHelpAvailable(false);
		attributes = new HashMap<String, Object>();
		setAttributes();
		this.createInstallDir = createInstallDir;
	}

	/**
	 * Returns an unmodifiable Map of the attributes this install dialog is holding.
	 * 
	 * @return
	 */
	public Map<String, Object> getAttributes()
	{
		return Collections.unmodifiableMap(attributes);
	}

	/**
	 * Set attributes that can later be used when creating the dialog area.
	 * 
	 * @param attributeName
	 * @param value
	 */
	protected abstract void setAttributes();

	/**
	 * Configure the shell to display a title.
	 */
	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.InstallProcessor_installerShellTitle);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		// Create a inner composite so we can control the margins
		Composite inner = new Composite(composite, SWT.NONE);
		inner.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginLeft = 4;
		layout.marginRight = 4;
		layout.marginTop = 4;
		layout.marginBottom = 4;
		inner.setLayout(layout);

		// TODO - Split this to a method.
		Group group = new Group(inner, SWT.NONE);
		group.setText(Messages.InstallProcessor_installerGroupTitle);
		group.setLayout(new GridLayout());
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		group.setLayoutData(layoutData);

		createInstallerGroupControls(group);
		createExtendedControls(inner);
		setTitle(NLS.bind(Messages.InstallProcessor_installerTitle, installerName));
		return composite;
	}

	/**
	 * Returns the message that will be displayed in the installer dialog.
	 * 
	 * @return An installer message.
	 */
	protected String getInstallerMessage()
	{
		return NLS.bind(Messages.InstallProcessor_installerMessage, installerName);
	}

	/**
	 * Creates the components inside the 'Installer' group. <br>
	 * The default creation is only for the installation path. This can be overwritten, or extended, by a subclass.
	 * 
	 * @param group
	 * @return A composite.
	 */
	protected Composite createInstallerGroupControls(Composite group)
	{
		Label l = new Label(group, SWT.WRAP);
		l.setText(getInstallerMessage());
		Composite installLocation = new Composite(group, SWT.NONE);
		installLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		installLocation.setLayout(new GridLayout(2, false));
		path = new Text(installLocation, SWT.SINGLE | SWT.BORDER);
		path.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		path.setText(attributes.get(INSTALL_DIR_ATTR).toString());
		path.addKeyListener(new KeyListener()
		{
			public void keyReleased(org.eclipse.swt.events.KeyEvent e)
			{
				attributes.put(INSTALL_DIR_ATTR, PlatformUtil.expandEnvironmentStrings(path.getText().trim()));
				validatePath();
			}

			public void keyPressed(org.eclipse.swt.events.KeyEvent e)
			{
				attributes.put(INSTALL_DIR_ATTR, PlatformUtil.expandEnvironmentStrings(path.getText().trim()));
				validatePath();
			}
		});
		Button browse = new Button(installLocation, SWT.PUSH);
		browse.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		browse.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				DirectoryDialog dirDialog = new DirectoryDialog(getParentShell());
				String dir = dirDialog.open();
				if (dir != null)
				{
					path.setText(dir);
					attributes.put(INSTALL_DIR_ATTR, dir);
					validatePath();
				}
			}
		});
		validatePath();
		return group;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		validatePath();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed()
	{
		if (createInstallDir)
		{
			File f = new File(PlatformUtil.expandEnvironmentStrings(path.getText()));
			if (!f.exists() && !f.mkdirs())
			{
				// Display an error message about the problem and return here to prevent a dialog close.
				MessageDialog.openError(getParentShell(),
						Messages.InstallerOptionsDialog_creatingDirectoriesErrorTitle,
						Messages.InstallerOptionsDialog_creatingDirectoriesErrorMessage);
				return;
			}
		}
		super.okPressed();
	}

	/**
	 * Validate the path
	 */
	protected void validatePath()
	{
		String pathText = path.getText().trim();
		if (pathText.length() == 0)
		{
			// empty path
			setErrorMessage(Messages.InstallerOptionsDialog_emptyPathError);
			return;
		}
		pathText = PlatformUtil.expandEnvironmentStrings(pathText);
		if (!new File(pathText).exists())
		{
			if (createInstallDir)
			{
				setMessage(Messages.InstallerOptionsDialog_inputDirectoryWillBeCreated, IMessageProvider.INFORMATION);
			}
			else
			{
				// non-existing path
				setErrorMessage(Messages.InstallerOptionsDialog_nonExistingPathError);
				return;
			}

		}
		setErrorMessage(null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#setErrorMessage(java.lang.String)
	 */
	@Override
	public void setErrorMessage(String newErrorMessage)
	{
		super.setErrorMessage(newErrorMessage);
		Button button = getButton(IDialogConstants.OK_ID);
		if (button != null)
		{
			button.setEnabled(newErrorMessage == null);
		}
	}

	/**
	 * Create extended controls that will appear under the 'Installer' group.<br>
	 * The default implementation is empty, and can be sub-classed.
	 * 
	 * @param parent
	 * @return A composite.
	 */
	protected Composite createExtendedControls(Composite parent)
	{
		// Does nothing special here
		return parent;
	}

	/**
	 * Capitalize the word by upper-casing the first letter.
	 * 
	 * @param word
	 * @return A capitalized word.
	 */
	protected static String capitalize(String word)
	{
		if (word != null && word.length() > 0)
		{
			return Character.toUpperCase(word.charAt(0)) + word.substring(1);
		}
		return word;
	}
}
