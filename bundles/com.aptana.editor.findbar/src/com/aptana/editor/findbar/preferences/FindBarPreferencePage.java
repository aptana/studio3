/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.preferences;

import java.text.MessageFormat;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.findbar.FindBarPlugin;

/**
 * A preferences page for the find bar.
 * 
 * @author Fabio Zadrozny
 */
public final class FindBarPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	public void init(IWorkbench workbench)
	{
		setPreferenceStore(FindBarPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors()
	{
		Composite p = getFieldEditorParent();

		// Users can choose whether they want to use Aptana's find bar or the default find bar.
		addField(new BooleanFieldEditor(IPreferencesConstants.USE_CUSTOM_FIND_BAR, MessageFormat.format(
				Messages.FindBarPreferencesPage_AskUseCustomFindBar, EclipseUtil.getStudioPrefix()), p));

		// Users can choose whether to do incremental searches or not.
		addField(new BooleanFieldEditor(IPreferencesConstants.INCREMENTAL_SEARCH_ON_FIND_BAR,
				Messages.FindBarPreferencesPage_AskIncrementalSearchesOnFindBar, p));

		addField(new BooleanFieldEditor(IPreferencesConstants.CTRL_F_TWICE_OPENS_ECLIPSE_FIND_BAR,
				Messages.FindBarPreferencesPage_AskCtrlFTwiceOpensEclipseSearchOnFindBar, p));
	}
}
