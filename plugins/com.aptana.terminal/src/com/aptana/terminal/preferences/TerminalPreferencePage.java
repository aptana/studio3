/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.terminal.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.terminal.TerminalPlugin;

public class TerminalPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private BooleanFieldEditor closeOnExitEditor;
	private DirectoryFieldEditor workingDirectoryEditor;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	protected void createFieldEditors()
	{
		workingDirectoryEditor = new DirectoryFieldEditor(IPreferenceConstants.WORKING_DIRECTORY,
				Messages.TerminalPreferencePage_LBL_WorkingDirectory, getFieldEditorParent());
		closeOnExitEditor = new BooleanFieldEditor(IPreferenceConstants.CLOSE_VIEW_ON_EXIT,
				Messages.TerminalPreferencePage_Close_View_On_Exit, getFieldEditorParent());

		addField(workingDirectoryEditor);
		addField(closeOnExitEditor);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	protected IPreferenceStore doGetPreferenceStore()
	{
		return new ScopedPreferenceStore(InstanceScope.INSTANCE, TerminalPlugin.PLUGIN_ID);
	}
}
