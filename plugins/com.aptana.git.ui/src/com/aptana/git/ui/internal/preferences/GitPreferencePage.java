/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.git.ui.internal.preferences;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.aptana.git.core.GitPlugin;
import com.aptana.git.core.IPreferenceConstants;
import com.aptana.git.core.model.GitExecutable;

public class GitPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private FileFieldEditor fileEditor;
	private BooleanFieldEditor pullIndicatorEditor;
	private BooleanFieldEditor autoAttachEditor;

	public GitPreferencePage()
	{
		super();
	}

	public void init(IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors()
	{
		// Git Executable location
		fileEditor = new FileFieldEditor(IPreferenceConstants.GIT_EXECUTABLE_PATH,
				Messages.GitExecutableLocationPage_LocationLabel, true, FileFieldEditor.VALIDATE_ON_KEY_STROKE,
				getFieldEditorParent())
		{
			@Override
			protected boolean checkState()
			{
				boolean ok = super.checkState();
				if (!ok)
					return ok;

				// Now check that the executable is ok
				String text = getTextControl().getText();
				if (text != null && text.trim().length() > 0)
				{
					if (!GitExecutable.acceptBinary(Path.fromOSString(text)))
					{
						showErrorMessage(NLS.bind(Messages.GitExecutableLocationPage_InvalidLocationErrorMessage,
								GitExecutable.MIN_GIT_VERSION));
						return false;
					}
				}

				clearErrorMessage();
				return true;
			}
		};
		// Git pull indicator
		pullIndicatorEditor = new BooleanFieldEditor(IPreferenceConstants.GIT_CALCULATE_PULL_INDICATOR,
				Messages.GitExecutableLocationPage_CalculatePullIndicatorLabel, getFieldEditorParent());
		addField(fileEditor);
		addField(pullIndicatorEditor);

		// Auto-attach to projects
		autoAttachEditor = new BooleanFieldEditor(IPreferenceConstants.AUTO_ATTACH_REPOS,
				Messages.GitExecutableLocationPage_AutoAttachProjectsLabel, getFieldEditorParent());
		addField(autoAttachEditor);
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore()
	{
		return new ScopedPreferenceStore(new InstanceScope(), GitPlugin.getPluginId());
	}
}
