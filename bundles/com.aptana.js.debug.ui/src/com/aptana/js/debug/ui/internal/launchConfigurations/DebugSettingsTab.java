/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.launchConfigurations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;
import com.aptana.js.debug.ui.JSDebugUIPlugin;

/**
 * @author Max Stepanov
 */
public class DebugSettingsTab extends AbstractLaunchConfigurationTab
{
	private Image image;

	private Button overridePrefs;
	private Button suspendOnFirstLine;
	private Button suspendOnAllExceptions;
	private Button suspendOnUncaughtExceptions;
	private Button suspendOnDebuggerKeyword;

	private ILaunchConfiguration launchConfiguration;

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		overridePrefs = new Button(composite, SWT.CHECK);
		overridePrefs.setText(Messages.DebugSettingsTab_UseLaunchSpecificOptions);

		Group suspendOptionsGroup = new Group(composite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		suspendOptionsGroup.setLayout(gridLayout);
		suspendOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		suspendOptionsGroup.setText(Messages.DebugSettingsTab_SuspendOptions);

		// Check boxes
		suspendOnFirstLine = new Button(suspendOptionsGroup, SWT.CHECK);
		suspendOnFirstLine.setText(Messages.DebugSettingsTab_SuspendAtStart);
		suspendOnAllExceptions = new Button(suspendOptionsGroup, SWT.CHECK);
		suspendOnAllExceptions.setText(com.aptana.js.debug.ui.internal.preferences.Messages.JSDebugPreferencePage_SuspendOnAllExceptions);
		suspendOnUncaughtExceptions = new Button(suspendOptionsGroup, SWT.CHECK);
		suspendOnUncaughtExceptions.setText(com.aptana.js.debug.ui.internal.preferences.Messages.JSDebugPreferencePage_SuspendOnUncaughtExceptions);
		suspendOnDebuggerKeyword = new Button(suspendOptionsGroup, SWT.CHECK);
		suspendOnDebuggerKeyword.setText(Messages.DebugSettingsTab_SuspendOnDebuggerKeyword);

		Listener dirtyListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		};
		overridePrefs.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				updateEnablement();
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
		suspendOnFirstLine.addListener(SWT.Selection, dirtyListener);
		suspendOnAllExceptions.addListener(SWT.Selection, dirtyListener);
		suspendOnUncaughtExceptions.addListener(SWT.Selection, dirtyListener);
		suspendOnDebuggerKeyword.addListener(SWT.Selection, dirtyListener);

		setControl(composite);
	}

	private void updateEnablement()
	{
		boolean enableOverride = overridePrefs.getSelection();
		suspendOnFirstLine.setEnabled(enableOverride);
		suspendOnAllExceptions.setEnabled(enableOverride);
		suspendOnUncaughtExceptions.setEnabled(enableOverride);
		suspendOnDebuggerKeyword.setEnabled(enableOverride);
		try
		{
			if (enableOverride)
			{
				setValuesFrom(launchConfiguration);
			}
			else
			{
				setValuesFrom(new ScopedPreferenceStore(InstanceScope.INSTANCE, JSDebugPlugin.PLUGIN_ID));
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
	}

	private void setValuesFrom(Object object) throws CoreException
	{
		if (object instanceof IPreferenceStore)
		{
			IPreferenceStore preferences = (IPreferenceStore) object;
			suspendOnFirstLine.setSelection(preferences.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE));
			suspendOnAllExceptions.setSelection(preferences.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ALL_EXCEPTIONS));
			suspendOnUncaughtExceptions.setSelection(preferences.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_UNCAUGHT_EXCEPTIONS));
			suspendOnDebuggerKeyword.setSelection(preferences
					.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD));
		}
		else if (object instanceof ILaunchConfiguration)
		{
			ILaunchConfiguration configuration = (ILaunchConfiguration) object;
			suspendOnFirstLine.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE, false));
			suspendOnAllExceptions.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ALL_EXCEPTIONS, false));
			suspendOnUncaughtExceptions.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_UNCAUGHT_EXCEPTIONS, false));
			suspendOnDebuggerKeyword.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS, false));
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration)
	{
		JSLaunchConfigurationHelper.setDebugDefaults(configuration);
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(ILaunchConfiguration configuration)
	{
		this.launchConfiguration = configuration;
		try
		{
			overridePrefs.setSelection(configuration.getAttribute(
					ILaunchConfigurationConstants.CONFIGURATION_OVERRIDE_DEBUG_PREFERENCES, false));
			setValuesFrom(configuration);
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSDebugUIPlugin.getDefault(), "Reading launch configuration fails", e); //$NON-NLS-1$
		}
		finally
		{
			updateEnablement();
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(ILaunchConfigurationWorkingCopy configuration)
	{
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_OVERRIDE_DEBUG_PREFERENCES,
				overridePrefs.getSelection());
		if (overridePrefs.getSelection())
		{
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE,
					suspendOnFirstLine.getSelection());
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ALL_EXCEPTIONS,
					suspendOnAllExceptions.getSelection());
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_UNCAUGHT_EXCEPTIONS,
					suspendOnUncaughtExceptions.getSelection());
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS,
					suspendOnDebuggerKeyword.getSelection());
		}
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName()
	{
		return Messages.DebugSettingsTab_Debug;
	}

	/**
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getImage()
	 */
	public Image getImage()
	{
		if (image == null)
		{
			image = JSDebugUIPlugin.getImageDescriptor("icons/full/obj16/launch-debug.gif").createImage(); //$NON-NLS-1$
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
