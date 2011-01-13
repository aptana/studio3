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
package com.aptana.js.debug.ui.internal.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.debug.core.DebugCorePlugin;
import com.aptana.js.debug.core.preferences.IJSDebugPreferenceNames;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.js.debug.ui.internal.IJSDebugUIConstants;

/**
 * @author Max Stepanov
 */
public class JSDebugPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private Preferences store;
	private Button suspendOnFirstLine;
	private Button suspendOnExceptions;
	private Button suspendOnErrors;
	private Button suspendOnDebuggerKeyword;

	private Button confirmExitDebugger;

	/**
	 * 
	 */
	public JSDebugPreferencePage() {
		super();
		store = getPreferences();
	}

	/**
	 * @param title
	 */
	public JSDebugPreferencePage(String title) {
		super(title);
		store = getPreferences();
	}

	/**
	 * @param title
	 * @param image
	 */
	public JSDebugPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		store = getPreferences();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
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
	public void init(IWorkbench workbench) {
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		setDefaultValues();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		setValues();
		return super.performOk();
	}

	/**
	 * getPreferences
	 * 
	 * @return Preferences
	 */
	protected Preferences getPreferences() {
		return DebugCorePlugin.getDefault().getPluginPreferences();
	}

	private void setInitialValues() {
		suspendOnFirstLine.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE));
		suspendOnExceptions.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS));
		suspendOnErrors.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS));
		suspendOnDebuggerKeyword.setSelection(store.getBoolean(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD));

		IPreferenceStore uiStore = JSDebugUIPlugin.getDefault().getPreferenceStore();
		if (!uiStore.contains(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER)) {
			confirmExitDebugger.setSelection(true); // for compatibility with
													// existing
													// workspace/preferences
		} else {
			confirmExitDebugger.setSelection(uiStore.getBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER));
		}
	}

	private void setDefaultValues() {
		suspendOnFirstLine.setSelection(store.getDefaultBoolean(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE));
		suspendOnExceptions.setSelection(store.getDefaultBoolean(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS));
		suspendOnErrors.setSelection(store.getDefaultBoolean(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS));
		suspendOnDebuggerKeyword.setSelection(store
				.getDefaultBoolean(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD));

		IPreferenceStore uiStore = JSDebugUIPlugin.getDefault().getPreferenceStore();
		if (uiStore.contains(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER)) {
			confirmExitDebugger.setSelection(uiStore.getDefaultBoolean(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER));
		} else {
			confirmExitDebugger.setSelection(true);
		}
	}

	private void setValues() {
		store.setValue(IJSDebugPreferenceNames.SUSPEND_ON_FIRST_LINE, suspendOnFirstLine.getSelection());
		store.setValue(IJSDebugPreferenceNames.SUSPEND_ON_EXCEPTIONS, suspendOnExceptions.getSelection());
		store.setValue(IJSDebugPreferenceNames.SUSPEND_ON_ERRORS, suspendOnErrors.getSelection());
		store.setValue(IJSDebugPreferenceNames.SUSPEND_ON_DEBUGGER_KEYWORD, suspendOnDebuggerKeyword.getSelection());
		DebugCorePlugin.getDefault().savePluginPreferences();

		IPreferenceStore uiStore = JSDebugUIPlugin.getDefault().getPreferenceStore();
		uiStore.setValue(IJSDebugUIConstants.PREF_CONFIRM_EXIT_DEBUGGER, confirmExitDebugger.getSelection());
		JSDebugUIPlugin.getDefault().savePluginPreferences();
	}
}
