/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.launchConfigurations;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.aptana.core.CoreStrings;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.debug.ui.internal.ActiveResourcePathGetterAdapter;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.ui.util.UIUtils;
import com.aptana.webserver.core.IServer;
import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.ui.IWebServerUIConstants;

/**
 * Launch settings tab
 */
public class LaunchBrowserSettingsTab extends AbstractLaunchConfigurationTab
{

	private Listener dirtyListener;
	private Image image;

	private Text fBrowserExeText;
	private Text fCommandArgsText;
	private Button rbCurrentPage;
	private Button rbSpecificPage;
	private Button bSpecificPageBrowse;
	private Text fSpecificPageText;
	private Button rbStartUrl;
	private Text fStartUrlText;
	private Button rbInternalServer;
	private Button rbCustomServer;
	private Text fbaseUrlText;
	private Button fAddProjectName;

	private Button rbManagedServer;
	private ComboViewer managedServersView;

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);

		composite.setLayout(GridLayoutFactory.swtDefaults().create());
		composite.setFont(parent.getFont());

		dirtyListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				updateEnablement();
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};

		createBrowserSection(composite);
		createStartActionSection(composite);
		createServerSection(composite);

		// hook up event handlers to update the configuration dialog when settings change
		hookListeners(true);
	}

	private void createBrowserSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchBrowserSettingsTab_WebBrowser);
		group.setFont(parent.getFont());

		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).hint(500, SWT.DEFAULT).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).margins(10, 3).create());

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.LaunchBrowserSettingsTab_BrowserExecutable);
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		fBrowserExeText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fBrowserExeText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());

		Button bBrowserExeBrowse = new Button(group, SWT.PUSH);
		bBrowserExeBrowse.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		bBrowserExeBrowse.setLayoutData(GridDataFactory.swtDefaults().create());

		label = new Label(group, SWT.NONE);
		label.setText(Messages.LaunchBrowserSettingsTab_Arguments);
		label.setLayoutData(GridDataFactory.swtDefaults().create());

		fCommandArgsText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fCommandArgsText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());

		bBrowserExeBrowse.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * Prompts the user to choose a location from the filesystem and sets the location as the full path of the
			 * selected file.
			 */
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
				fileDialog.setFileName(fBrowserExeText.getText());
				if (Platform.OS_WIN32.equals(Platform.getOS()))
				{
					fileDialog.setFilterExtensions(new String[] { "*.exe" }); //$NON-NLS-1$
					fileDialog.setFilterNames(new String[] { Messages.LaunchBrowserSettingsTab_ExecutableFiles });
				}
				String text = fileDialog.open();
				if (text != null)
				{
					fBrowserExeText.setText(text);
				}
			}
		});
	}

	private void createStartActionSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchBrowserSettingsTab_StartAction);
		group.setFont(parent.getFont());

		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).margins(10, 3).create());

		/* row 1 */
		rbCurrentPage = new Button(group, SWT.RADIO);
		rbCurrentPage.setText(Messages.LaunchBrowserSettingsTab_UseCurrentPage);
		rbCurrentPage.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(3, 1)
				.grab(true, false).create());

		/* row 2 */
		rbSpecificPage = new Button(group, SWT.RADIO);
		rbSpecificPage.setText(Messages.LaunchBrowserSettingsTab_SpecificPage);
		rbSpecificPage.setLayoutData(GridDataFactory.swtDefaults().create());

		fSpecificPageText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fSpecificPageText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
				.create());

		bSpecificPageBrowse = new Button(group, SWT.PUSH);
		bSpecificPageBrowse.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		bSpecificPageBrowse.setLayoutData(GridDataFactory.swtDefaults().create());

		/* row 3 */
		rbStartUrl = new Button(group, SWT.RADIO);
		rbStartUrl.setText(Messages.LaunchBrowserSettingsTab_StartURL);
		rbStartUrl.setLayoutData(GridDataFactory.swtDefaults().create());

		fStartUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fStartUrlText.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1)
				.grab(true, false).create());

		bSpecificPageBrowse.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				IResource resource = chooseWorkspaceLocation();
				if (resource != null)
				{
					fSpecificPageText.setText(resource.getFullPath().toPortableString());
				}
			}
		});
	}

	private void createServerSection(Composite parent)
	{
		Group group = new Group(parent, SWT.FLAT);
		group.setText(Messages.LaunchBrowserSettingsTab_Server);
		group.setFont(parent.getFont());

		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).margins(10, 3).create());

		rbInternalServer = new Button(group, SWT.RADIO);
		rbInternalServer.setText(Messages.LaunchBrowserSettingsTab_UseBuiltInWebServer);
		rbInternalServer.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).create());

		rbManagedServer = new Button(group, SWT.RADIO);
		rbManagedServer.setText(Messages.LaunchBrowserSettingsTab_Use_Selected_Server);
		rbManagedServer.setLayoutData(GridDataFactory.fillDefaults().create());

		managedServersView = new ComboViewer(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		managedServersView.getControl().setLayoutData(
				GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		managedServersView.setContentProvider(ArrayContentProvider.getInstance());
		managedServersView.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				if (element instanceof IServer)
				{
					return ((IServer) element).getName();
				}
				return super.getText(element);
			}
		});
		managedServersView.setInput(WebServerCorePlugin.getDefault().getServerManager().getServers());

		Link configureLink = new Link(group, SWT.NONE);
		configureLink.setText(MessageFormat.format("<a>{0}</a>", Messages.LaunchBrowserSettingsTab_Configure_Label)); //$NON-NLS-1$
		configureLink.setLayoutData(GridDataFactory.swtDefaults().create());
		configureLink.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				PreferenceDialog dlg = PreferencesUtil.createPreferenceDialogOn(UIUtils.getActiveShell(),
						IWebServerUIConstants.WEBSERVERS_PREFERENCE_PAGE_ID,
						new String[] { IWebServerUIConstants.WEBSERVERS_PREFERENCE_PAGE_ID }, null,
						PreferencesUtil.OPTION_FILTER_LOCKED);
				dlg.open();
				ISelection selection = managedServersView.getSelection();
				managedServersView.setInput(WebServerCorePlugin.getDefault().getServerManager().getServers());
				managedServersView.setSelection(selection);
			}
		});

		rbCustomServer = new Button(group, SWT.RADIO);
		rbCustomServer.setText(Messages.LaunchBrowserSettingsTab_UseExternalWebServer);
		rbCustomServer.setLayoutData(GridDataFactory.fillDefaults().create());

		fbaseUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		fbaseUrlText.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());

		fAddProjectName = new Button(group, SWT.CHECK);
		fAddProjectName.setText(Messages.LaunchBrowserSettingsTab_AppendProjectName);
		fAddProjectName.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());
	}

	private void hookListeners(boolean hook)
	{
		if (hook)
		{
			fBrowserExeText.addListener(SWT.Modify, dirtyListener);
			fCommandArgsText.addListener(SWT.Modify, dirtyListener);

			fSpecificPageText.addListener(SWT.Modify, dirtyListener);
			fStartUrlText.addListener(SWT.Modify, dirtyListener);
			rbCurrentPage.addListener(SWT.Selection, dirtyListener);
			rbSpecificPage.addListener(SWT.Selection, dirtyListener);
			rbStartUrl.addListener(SWT.Selection, dirtyListener);

			fbaseUrlText.addListener(SWT.Modify, dirtyListener);
			rbInternalServer.addListener(SWT.Selection, dirtyListener);
			rbCustomServer.addListener(SWT.Selection, dirtyListener);
			rbManagedServer.addListener(SWT.Selection, dirtyListener);
			managedServersView.getControl().addListener(SWT.Selection, dirtyListener);
			fAddProjectName.addListener(SWT.Selection, dirtyListener);
		}
		else
		{
			fBrowserExeText.removeListener(SWT.Modify, dirtyListener);
			fCommandArgsText.removeListener(SWT.Modify, dirtyListener);

			fSpecificPageText.removeListener(SWT.Modify, dirtyListener);
			fStartUrlText.removeListener(SWT.Modify, dirtyListener);
			rbCurrentPage.removeListener(SWT.Selection, dirtyListener);
			rbSpecificPage.removeListener(SWT.Selection, dirtyListener);
			rbStartUrl.removeListener(SWT.Selection, dirtyListener);

			fbaseUrlText.removeListener(SWT.Modify, dirtyListener);
			rbInternalServer.removeListener(SWT.Selection, dirtyListener);
			rbCustomServer.removeListener(SWT.Selection, dirtyListener);
			rbManagedServer.removeListener(SWT.Selection, dirtyListener);
			managedServersView.getControl().removeListener(SWT.Selection, dirtyListener);
			fAddProjectName.addListener(SWT.Selection, dirtyListener);
		}
	}

	private void updateEnablement()
	{
		fSpecificPageText.setEnabled(rbSpecificPage.getSelection());
		bSpecificPageBrowse.setEnabled(rbSpecificPage.getSelection());
		boolean startUrlEnabled = rbStartUrl.getSelection();
		fStartUrlText.setEnabled(startUrlEnabled);
		fbaseUrlText.setEnabled(!startUrlEnabled && rbCustomServer.getSelection());
		managedServersView.getControl().setEnabled(!startUrlEnabled && rbManagedServer.getSelection());
		managedServersView.getControl().setForeground(
				getShell().getDisplay().getSystemColor(
						(!startUrlEnabled && rbManagedServer.getSelection()) ? SWT.COLOR_LIST_FOREGROUND
								: SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
		rbInternalServer.setEnabled(!startUrlEnabled);
		rbManagedServer.setEnabled(!startUrlEnabled);
		rbCustomServer.setEnabled(!startUrlEnabled);
		fAddProjectName.setEnabled(rbCustomServer.getSelection()
				&& (rbCurrentPage.getSelection() || rbSpecificPage.getSelection()));
	}

	private IResource chooseWorkspaceLocation()
	{
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setMessage(Messages.LaunchBrowserSettingsTab_ChooseFile);
		if (dialog.open() == Window.OK)
		{
			return (IResource) dialog.getFirstResult();
		}

		return null;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		JSLaunchConfigurationHelper.setBrowserDefaults(configuration, null);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		hookListeners(false);
		try
		{
			fBrowserExeText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, StringUtil.EMPTY));
			fCommandArgsText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_BROWSER_COMMAND_LINE, StringUtil.EMPTY));

			int startActionType = configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE,
					ILaunchConfigurationConstants.DEFAULT_START_ACTION_TYPE);
			rbCurrentPage.setSelection(startActionType == ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE);
			rbSpecificPage.setSelection(startActionType == ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE);
			fSpecificPageText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, StringUtil.EMPTY));
			rbStartUrl.setSelection(startActionType == ILaunchConfigurationConstants.START_ACTION_START_URL);
			fStartUrlText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, StringUtil.EMPTY));
			fAddProjectName.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME, false));

			int serverType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
					ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
			rbInternalServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_INTERNAL);
			rbCustomServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_EXTERNAL);
			fbaseUrlText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY));
			rbManagedServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_MANAGED);
			IServer server = WebServerCorePlugin
					.getDefault()
					.getServerManager()
					.findServerByName(
							configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_NAME,
									StringUtil.EMPTY));
			if (server != null)
			{
				managedServersView.setSelection(new StructuredSelection(server));
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSDebugUIPlugin.getDefault(), "Reading launch configuration fails", e); //$NON-NLS-1$
		}
		finally
		{
			hookListeners(true);
			updateEnablement();
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		String value;
		value = fBrowserExeText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_EXECUTABLE, value);

		value = fCommandArgsText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_BROWSER_COMMAND_LINE, value);

		int startActionType = 0;
		if (rbCurrentPage.getSelection())
		{
			startActionType = ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE;
		}
		else if (rbSpecificPage.getSelection())
		{
			startActionType = ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE;
		}
		else if (rbStartUrl.getSelection())
		{
			startActionType = ILaunchConfigurationConstants.START_ACTION_START_URL;
		}
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, startActionType);

		value = fSpecificPageText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, value);
		value = fStartUrlText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, value);

		int serverType = 0;
		if (rbInternalServer.getSelection())
		{
			serverType = ILaunchConfigurationConstants.SERVER_INTERNAL;
		}
		else if (rbCustomServer.getSelection())
		{
			serverType = ILaunchConfigurationConstants.SERVER_EXTERNAL;
		}
		else if (rbManagedServer.getSelection())
		{
			serverType = ILaunchConfigurationConstants.SERVER_MANAGED;
		}
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE, serverType);

		value = fbaseUrlText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, value);

		IServer serverSelection = (IServer) ((IStructuredSelection) managedServersView.getSelection())
				.getFirstElement();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_NAME,
				(serverSelection != null) ? serverSelection.getName() : null);

		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_APPEND_PROJECT_NAME,
				fAddProjectName.getSelection());
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig)
	{
		setErrorMessage(null);
		setMessage(null);

		String value = fBrowserExeText.getText();
		if (!new File(value).exists())
		{
			setErrorMessage(Messages.LaunchBrowserSettingsTab_BrowserExecutableShouldBeSpecified);
			return false;
		}
		if (rbSpecificPage.getSelection())
		{
			value = fSpecificPageText.getText();
			if (value.length() == 0 || ResourcesPlugin.getWorkspace().getRoot().findMember(value) == null)
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_StartPageShouldBeSpecified);
				return false;
			}
		}
		else if (rbStartUrl.getSelection())
		{
			value = fStartUrlText.getText();
			try
			{
				new URL(value);
			}
			catch (MalformedURLException e)
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_ValidStartPageURLShouldBeSpecified);
				return false;
			}
		}

		if (rbCustomServer.getSelection())
		{
			value = fbaseUrlText.getText();
			try
			{
				new URL(value);
			}
			catch (MalformedURLException e)
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_ValidBaseURLShouldBeSpecified);
				return false;
			}
		}
		if (rbCurrentPage.getSelection())
		{
			Object activeResource = new ActiveResourcePathGetterAdapter().getActiveResourcePath();
			if (activeResource == null)
			{
				setMessage(Messages.LaunchBrowserSettingsTab_NoFilesOpenedInEditor);
				return false;
			}
		}

		if (rbManagedServer.getSelection())
		{
			IServer serverSelection = (IServer) ((IStructuredSelection) managedServersView.getSelection())
					.getFirstElement();
			if (serverSelection == null)
			{
				setErrorMessage(Messages.LaunchBrowserSettingsTab_ServerNotSelected);
				return false;
			}
		}

		return true;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName()
	{
		return Messages.LaunchBrowserSettingsTab_Main;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage()
	{
		if (image == null)
		{
			image = JSDebugUIPlugin.getImageDescriptor("icons/full/obj16/launch-main.gif").createImage(); //$NON-NLS-1$
		}
		return image;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#dispose()
	 */
	public void dispose()
	{
		if (image != null)
		{
			image.dispose();
		}
		super.dispose();
	}
}
