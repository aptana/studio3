/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.launchConfigurations;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
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

/**
 * Launch settings tab
 */
public class LaunchServerSettingsTab extends AbstractLaunchConfigurationTab {
	private static final Pattern HOST_PATTERN = Pattern
			.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|([-\\w]+(\\.[-\\w+])*)(:\\d{4,7})?$"); //$NON-NLS-1$

	private Listener dirtyListener;
	private Image image;

	private Text fServerHostText;
	private Button rbCurrentPage;
	private Button rbSpecificPage;
	private Button bSpecificPageBrowse;
	private Text fSpecificPageText;
	private Button rbStartUrl;
	private Text fStartUrlText;
	private Button rbInternalServer;
	private Button rbCustomServer;
	private Text fbaseUrlText;

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Font font = parent.getFont();

		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		GridLayout topLayout = new GridLayout();
		topLayout.verticalSpacing = 5;
		composite.setLayout(topLayout);
		composite.setFont(font);

		dirtyListener = new Listener() {
			public void handleEvent(Event event) {
				updateEnablement();
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};

		createServerHostSection(composite);
		createStartActionSection(composite);
		createServerSection(composite);

		// hook up event handlers to update the configuration dialog when
		// settings change
		hookListeners(true);
	}

	private void createServerHostSection(Composite parent) {
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchServerSettingsTab_Server);
		group.setFont(font);

		// CHECKSTYLE:OFF
		GridData gd = new GridData(SWT.FILL, 20, true, false);
		group.setLayoutData(gd);

		FormLayout form = new FormLayout();
		group.setLayout(form);
		FormData data;
		form.marginTop = 10;
		form.marginBottom = 10;
		form.marginLeft = 10;
		form.marginRight = 10;

		int column1Offset = 0; // the left offset of the items placed in the
								// first column
		int column2Offset = 135; // the left offset of the items placed in the
									// second column

		Label hostLabel = new Label(group, SWT.NONE);
		hostLabel.setText(Messages.LaunchServerSettingsTab_Host);
		data = new FormData();
		data.left = new FormAttachment(0, column1Offset);
		hostLabel.setLayoutData(data);

		fServerHostText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(hostLabel, 0, SWT.TOP);
		data.left = new FormAttachment(0, column2Offset);
		data.right = new FormAttachment(100, 0);
		fServerHostText.setLayoutData(data);
		// CHECKSTYLE:ON

	}

	private void createStartActionSection(Composite parent) {
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.FLAT | SWT.SHADOW_IN);
		group.setText(Messages.LaunchBrowserSettingsTab_StartAction);
		group.setFont(font);

		// CHECKSTYLE:OFF
		GridData gd = new GridData(SWT.FILL, 20, true, false);
		group.setLayoutData(gd);

		FormLayout form = new FormLayout();
		group.setLayout(form);
		FormData data;
		form.marginTop = 10;
		form.marginBottom = 10;
		form.marginLeft = 10;
		form.marginRight = 10;

		int column1Offset = 0; // the left offset of the items placed in the
								// first column
		int column2Offset = 135; // the left offset of the items placed in the
									// second column

		rbCurrentPage = new Button(group, SWT.RADIO);
		rbCurrentPage.setText(Messages.LaunchBrowserSettingsTab_UseCurrentPage);
		data = new FormData();
		data.left = new FormAttachment(0, column1Offset);
		rbCurrentPage.setLayoutData(data);

		rbSpecificPage = new Button(group, SWT.RADIO);
		rbSpecificPage.setText(Messages.LaunchBrowserSettingsTab_SpecificPage);
		data = new FormData();
		data.top = new FormAttachment(rbCurrentPage, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column1Offset);
		rbSpecificPage.setLayoutData(data);

		bSpecificPageBrowse = new Button(group, SWT.PUSH);
		bSpecificPageBrowse.setText(StringUtil.ellipsify(CoreStrings.BROWSE));
		data = new FormData();
		data.top = new FormAttachment(rbSpecificPage, -1, SWT.TOP);
		data.right = new FormAttachment(100, 0);
		bSpecificPageBrowse.setLayoutData(data);

		fSpecificPageText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(rbSpecificPage, 0, SWT.TOP);
		data.left = new FormAttachment(0, column2Offset);
		data.right = new FormAttachment(bSpecificPageBrowse, -5, SWT.LEFT);
		fSpecificPageText.setLayoutData(data);

		rbStartUrl = new Button(group, SWT.RADIO);
		rbStartUrl.setText(Messages.LaunchBrowserSettingsTab_StartURL);
		data = new FormData();
		data.top = new FormAttachment(rbSpecificPage, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column1Offset);
		rbStartUrl.setLayoutData(data);

		fStartUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(rbStartUrl, 0, SWT.TOP);
		data.left = new FormAttachment(0, column2Offset);
		data.right = new FormAttachment(100, 0);
		fStartUrlText.setLayoutData(data);
		// CHECKSTYLE:ON

		bSpecificPageBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IResource resource = chooseWorkspaceLocation();
				if (resource != null) {
					fSpecificPageText.setText(resource.getFullPath().toPortableString());
				}
			}
		});
	}

	private void createServerSection(Composite parent) {
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.FLAT);
		group.setText(Messages.LaunchBrowserSettingsTab_Server);
		group.setFont(font);

		// CHECKSTYLE:OFF
		GridData gd = new GridData(SWT.FILL, 20, true, false);
		group.setLayoutData(gd);

		FormLayout form = new FormLayout();
		group.setLayout(form);
		FormData data;
		form.marginTop = 10;
		form.marginBottom = 10;
		form.marginLeft = 10;
		form.marginRight = 10;

		int column1Offset = 0; // the left offset of the items placed in the
								// first column
		int column3Offset = 135; // the left offset of the items placed in the
									// second column

		rbInternalServer = new Button(group, SWT.RADIO);
		rbInternalServer.setText(Messages.LaunchBrowserSettingsTab_UseBuiltInWebServer);
		data = new FormData();
		data.left = new FormAttachment(0, column1Offset);
		rbInternalServer.setLayoutData(data);

		rbCustomServer = new Button(group, SWT.RADIO);
		rbCustomServer.setText(Messages.LaunchBrowserSettingsTab_UseExternalWebServer);
		data = new FormData();
		data.top = new FormAttachment(rbInternalServer, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column1Offset);
		rbCustomServer.setLayoutData(data);

		fbaseUrlText = new Text(group, SWT.SINGLE | SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(rbCustomServer, 10, SWT.BOTTOM);
		data.left = new FormAttachment(0, column3Offset);
		data.right = new FormAttachment(100, 0);
		fbaseUrlText.setLayoutData(data);

		Label baseUrlLabel = new Label(group, SWT.NONE);
		baseUrlLabel.setText(Messages.LaunchBrowserSettingsTab_BaseURL);
		baseUrlLabel.setAlignment(SWT.RIGHT);
		data = new FormData();
		data.right = new FormAttachment(fbaseUrlText, -8, SWT.LEFT);
		data.top = new FormAttachment(fbaseUrlText, 0, SWT.TOP);
		baseUrlLabel.setLayoutData(data);
		// CHECKSTYLE:ON
	}

	private void hookListeners(boolean hook) {
		if (hook) {
			fServerHostText.addListener(SWT.Modify, dirtyListener);

			fSpecificPageText.addListener(SWT.Modify, dirtyListener);
			fStartUrlText.addListener(SWT.Modify, dirtyListener);
			rbCurrentPage.addListener(SWT.Selection, dirtyListener);
			rbSpecificPage.addListener(SWT.Selection, dirtyListener);
			rbStartUrl.addListener(SWT.Selection, dirtyListener);

			fbaseUrlText.addListener(SWT.Modify, dirtyListener);
			rbInternalServer.addListener(SWT.Selection, dirtyListener);
			rbCustomServer.addListener(SWT.Selection, dirtyListener);
		} else {
			fServerHostText.removeListener(SWT.Modify, dirtyListener);

			fSpecificPageText.removeListener(SWT.Modify, dirtyListener);
			fStartUrlText.removeListener(SWT.Modify, dirtyListener);
			rbCurrentPage.removeListener(SWT.Selection, dirtyListener);
			rbSpecificPage.removeListener(SWT.Selection, dirtyListener);
			rbStartUrl.removeListener(SWT.Selection, dirtyListener);

			fbaseUrlText.removeListener(SWT.Modify, dirtyListener);
			rbInternalServer.removeListener(SWT.Selection, dirtyListener);
			rbCustomServer.removeListener(SWT.Selection, dirtyListener);
		}
	}

	private void updateEnablement() {
		fSpecificPageText.setEnabled(rbSpecificPage.getSelection());
		bSpecificPageBrowse.setEnabled(rbSpecificPage.getSelection());
		fStartUrlText.setEnabled(rbStartUrl.getSelection());
		rbInternalServer.setEnabled(!rbStartUrl.getSelection());
		rbCustomServer.setEnabled(!rbStartUrl.getSelection());
		fbaseUrlText.setEnabled(rbCustomServer.isEnabled() && rbCustomServer.getSelection());
	}

	private IResource chooseWorkspaceLocation() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		dialog.setMessage(Messages.LaunchBrowserSettingsTab_ChooseFile);
		dialog.open();
		Object result = dialog.getFirstResult();

		return (IResource) result;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		JSLaunchConfigurationHelper.setServerDefaults(configuration);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		hookListeners(false);
		try {
			fServerHostText.setText(configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_HOST,
					StringUtil.EMPTY));

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

			int serverType = configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE,
					ILaunchConfigurationConstants.DEFAULT_SERVER_TYPE);
			rbInternalServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_INTERNAL);
			rbCustomServer.setSelection(serverType == ILaunchConfigurationConstants.SERVER_EXTERNAL);
			fbaseUrlText.setText(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, StringUtil.EMPTY));

		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), "Reading launch configuration fails", e); //$NON-NLS-1$
		} finally {
			hookListeners(true);
			updateEnablement();
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		String value;
		value = fServerHostText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_HOST, value);

		int startActionType = 0;
		if (rbCurrentPage.getSelection()) {
			startActionType = ILaunchConfigurationConstants.START_ACTION_CURRENT_PAGE;
		} else if (rbSpecificPage.getSelection()) {
			startActionType = ILaunchConfigurationConstants.START_ACTION_SPECIFIC_PAGE;
		} else if (rbStartUrl.getSelection()) {
			startActionType = ILaunchConfigurationConstants.START_ACTION_START_URL;
		}
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_ACTION_TYPE, startActionType);

		value = fSpecificPageText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_PATH, value);
		value = fStartUrlText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_START_PAGE_URL, value);

		int serverType = 0;
		if (rbInternalServer.getSelection()) {
			serverType = ILaunchConfigurationConstants.SERVER_INTERNAL;
		} else if (rbCustomServer.getSelection()) {
			serverType = ILaunchConfigurationConstants.SERVER_EXTERNAL;
		}
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SERVER_TYPE, serverType);

		value = fbaseUrlText.getText();
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_EXTERNAL_BASE_URL, value);

	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);

		String value = fServerHostText.getText();
		if (!HOST_PATTERN.matcher(value).matches()) {
			setErrorMessage(Messages.LaunchServerSettingsTab_Error_ValidServerRequired);
			return false;
		}
		if (rbSpecificPage.getSelection()) {
			value = fSpecificPageText.getText();
			if (value.length() == 0 || ResourcesPlugin.getWorkspace().getRoot().findMember(value) == null) {
				setErrorMessage(Messages.LaunchBrowserSettingsTab_StartPageShouldBeSpecified);
				return false;
			}
		} else if (rbStartUrl.getSelection()) {
			value = fStartUrlText.getText();
			try {
				new URL(value);
			} catch (MalformedURLException e) {
				setErrorMessage(Messages.LaunchBrowserSettingsTab_ValidStartPageURLShouldBeSpecified);
				return false;
			}
		}

		if (rbCustomServer.getSelection()) {
			value = fbaseUrlText.getText();
			try {
				new URL(value);
			} catch (MalformedURLException e) {
				setErrorMessage(Messages.LaunchBrowserSettingsTab_ValidBaseURLShouldBeSpecified);
				return false;
			}
		}
		if (rbCurrentPage.getSelection()) {
			Object activeResource = new ActiveResourcePathGetterAdapter().getActiveResourcePath();
			if (activeResource == null) {
				setMessage(Messages.LaunchBrowserSettingsTab_NoFilesOpenedInEditor);
				return false;
			}
		}
		return true;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return Messages.LaunchBrowserSettingsTab_Main;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		if (image == null) {
			image = JSDebugUIPlugin.getImageDescriptor("icons/full/obj16/launch-httpServer.gif").createImage(); //$NON-NLS-1$
		}
		return image;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#dispose()
	 */
	public void dispose() {
		if (image != null) {
			image.dispose();
		}
		super.dispose();
	}
}
