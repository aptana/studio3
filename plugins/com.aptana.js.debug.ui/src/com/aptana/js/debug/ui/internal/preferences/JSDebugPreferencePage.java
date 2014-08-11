/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.ui.internal.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.js.debug.core.JSDebugPlugin;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.js.debug.ui.internal.IJSDebugUIConstants;

/**
 * @author Max Stepanov
 */
public class JSDebugPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
	private Button suspendOnFirstLine;
	private Button suspendOnExceptions;
	private Button suspendOnErrors;
	private Button suspendOnDebuggerKeyword;

	private Button confirmExitDebugger;

	/**
	 * 
	 */
	public JSDebugPreferencePage()
	{
		super();
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, JSDebugPlugin.PLUGIN_ID));
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group debugGroup = new Group(composite, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		debugGroup.setLayout(gridLayout);
		debugGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		debugGroup.setText(Messages.JSDebugPreferencePage_JavascriptDebugOptions);

		// Check boxes
		suspendOnFirstLine = new Button(debugGroup, SWT.CHECK);
		suspendOnFirstLine.setText(Messages.JSDebugPreferencePage_SuspendAtStart);
		suspendOnExceptions = new Button(debugGroup, SWT.CHECK);
		suspendOnExceptions.setText(Messages.JSDebugPreferencePage_SuspendOnExceptions);
		suspendOnErrors = new Button(debugGroup, SWT.CHECK);
		suspendOnErrors.setText(Messages.JSDebugPreferencePage_SuspendOnErrors);
		suspendOnDebuggerKeyword = new Button(debugGroup, SWT.CHECK);
		suspendOnDebuggerKeyword.setText(Messages.JSDebugPreferencePage_SuspendOnDebuggerKeyword);

		confirmExitDebugger = new Button(composite, SWT.CHECK);
		confirmExitDebugger.setText(Messages.JSDebugPreferencePage_ConfirmExitWhenDebuggerActive);

		setInitialValues();
		return composite;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		setDefaultValues();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		setValues();
		return super.performOk();
	}

	private void setInitialValues()
	{
		suspendOnFirstLine.setSelection(getPreferenceStore().getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE));
		suspendOnExceptions
				.setSelection(getPreferenceStore().getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS));
		suspendOnErrors.setSelection(getPreferenceStore().getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS));
		suspendOnDebuggerKeyword.setSelection(getPreferenceStore().getBoolean(
				IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD));

		IPreferenceStore uiStore = JSDebugUIPlugin.getDefault().getPreferenceStore();
		if (!uiStore.contains(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER))
		{
			confirmExitDebugger.setSelection(true); // for compatibility with
													// existing
													// workspace/preferences
		}
		else
		{
			confirmExitDebugger.setSelection(uiStore.getBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER));
		}
	}

	private void setDefaultValues()
	{
		suspendOnFirstLine.setSelection(getPreferenceStore().getDefaultBoolean(
				IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE));
		suspendOnExceptions.setSelection(getPreferenceStore().getDefaultBoolean(
				IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS));
		suspendOnErrors.setSelection(getPreferenceStore().getDefaultBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS));
		suspendOnDebuggerKeyword.setSelection(getPreferenceStore().getDefaultBoolean(
				IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD));

		IPreferenceStore uiStore = JSDebugUIPlugin.getDefault().getPreferenceStore();
		confirmExitDebugger.setSelection(uiStore.getDefaultBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER));
	}

	private void setValues()
	{
		getPreferenceStore().setValue(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE, suspendOnFirstLine.getSelection());
		getPreferenceStore()
				.setValue(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS, suspendOnExceptions.getSelection());
		getPreferenceStore().setValue(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS, suspendOnErrors.getSelection());
		getPreferenceStore().setValue(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD,
				suspendOnDebuggerKeyword.getSelection());

		IPreferenceStore uiStore = JSDebugUIPlugin.getDefault().getPreferenceStore();
		uiStore.setValue(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, confirmExitDebugger.getSelection());
		try
		{
			((IPersistentPreferenceStore) uiStore).save();
		}
		catch (IOException e)
		{
			IdeLog.logError(JSDebugUIPlugin.getDefault(), e);
		}
	}
}
