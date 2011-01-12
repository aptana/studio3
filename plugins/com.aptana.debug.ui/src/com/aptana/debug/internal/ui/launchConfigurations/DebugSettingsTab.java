/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
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
package com.aptana.debug.internal.ui.launchConfigurations;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
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

import com.aptana.debug.core.DebugCorePlugin;
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.js.debug.core.ILaunchConfigurationConstants;
import com.aptana.js.debug.core.JSLaunchConfigurationHelper;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;

/**
 * @author Max Stepanov
 */
public class DebugSettingsTab extends AbstractLaunchConfigurationTab
{
	private Image image;

	private Button overridePrefs;
	private Button suspendOnFirstLine;
	private Button suspendOnExceptions;
	private Button suspendOnErrors;
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
		suspendOnExceptions = new Button(suspendOptionsGroup, SWT.CHECK);
		suspendOnExceptions.setText(Messages.DebugSettingsTab_SuspendOnExceptions);
		suspendOnErrors = new Button(suspendOptionsGroup, SWT.CHECK);
		suspendOnErrors.setText(Messages.DebugSettingsTab_SuspendOnErrors);
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
		suspendOnExceptions.addListener(SWT.Selection, dirtyListener);
		suspendOnErrors.addListener(SWT.Selection, dirtyListener);
		suspendOnDebuggerKeyword.addListener(SWT.Selection, dirtyListener);

		setControl(composite);
	}

	private void updateEnablement()
	{
		boolean enableOverride = overridePrefs.getSelection();
		suspendOnFirstLine.setEnabled(enableOverride);
		suspendOnExceptions.setEnabled(enableOverride);
		suspendOnErrors.setEnabled(enableOverride);
		suspendOnDebuggerKeyword.setEnabled(enableOverride);
		try
		{
			if (enableOverride)
			{
				setValuesFrom(launchConfiguration);
			}
			else
			{
				setValuesFrom(DebugCorePlugin.getDefault().getPluginPreferences());
			}
		}
		catch (CoreException ignore)
		{
		}
	}

	private void setValuesFrom(Object object) throws CoreException
	{
		if (object instanceof Preferences)
		{
			Preferences store = (Preferences) object;
			suspendOnFirstLine.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE));
			suspendOnExceptions.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS));
			suspendOnErrors.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS));
			suspendOnDebuggerKeyword.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD));
		}
		else if (object instanceof ILaunchConfiguration)
		{
			ILaunchConfiguration configuration = (ILaunchConfiguration) object;
			suspendOnFirstLine.setSelection(configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE, false));
			suspendOnExceptions.setSelection(configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS, false));
			suspendOnErrors.setSelection(configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS, false));
			suspendOnDebuggerKeyword.setSelection(configuration.getAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS, false));
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
			DebugUiPlugin.log("Reading launch configuration fails", e); //$NON-NLS-1$
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
		configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_OVERRIDE_DEBUG_PREFERENCES, overridePrefs.getSelection());
		if (overridePrefs.getSelection())
		{
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_FIRST_LINE, suspendOnFirstLine.getSelection());
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_EXCEPTIONS, suspendOnExceptions.getSelection());
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_ERRORS, suspendOnErrors.getSelection());
			configuration.setAttribute(ILaunchConfigurationConstants.CONFIGURATION_SUSPEND_ON_DEBUGGER_KEYWORDS, suspendOnDebuggerKeyword.getSelection());
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
			image = DebugUiPlugin.getImageDescriptor("icons/full/obj16/launch-debug.gif").createImage(); //$NON-NLS-1$
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
