/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable packageNamingConvention

package com.aptana.js.debug.ui.internal.launchConfigurations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.aptana.core.logging.IdeLog;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public class AdvancedSettingsTab extends AbstractLaunchConfigurationTab {
	private Image image;
	private Button advancedRunEnabled;

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		advancedRunEnabled = new Button(composite, SWT.CHECK);
		advancedRunEnabled.setText(Messages.AdvancedSettingsTab_EnableDebuggingInRunMode);

		Listener dirtyListener = new Listener() {
			public void handleEvent(Event event) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};

		advancedRunEnabled.addListener(SWT.Selection, dirtyListener);

		setControl(composite);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		JSLaunchConfigurationHelper.setAdvancedDefaults(configuration);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			advancedRunEnabled.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_ADVANCED_RUN_ENABLED, false));
		} catch (CoreException e) {
			IdeLog.logError(JSDebugUIPlugin.getDefault(), "Reading launch configuration fails", e); //$NON-NLS-1$
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_ADVANCED_RUN_ENABLED,
				advancedRunEnabled.getSelection());
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return Messages.AdvancedSettingsTab_Advanced;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		if (image == null) {
			image = JSDebugUIPlugin.getImageDescriptor("icons/full/obj16/launch-rundebug.gif").createImage(); //$NON-NLS-1$
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
