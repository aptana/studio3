/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.findbar.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.internal.texteditor.TextEditorPlugin;

/**
 * Helpers (based on org.eclipse.ui.texteditor.FindNextAction) to get the last settings used in a search. i.e.: Find
 * Next (Ctrl+K) or Find dialog. The idea is that when the find bar is initialized it reads these configurations and
 * whenever a change is done in it, it'll change the configuration and write the change so that other find actions work.
 * 
 * @author fabioz
 */
@SuppressWarnings("restriction")
public class EclipseFindSettings
{

	// Should be FindReplaceDialog.class.getName(), but it's not visible.
	public static final String FIND_REPLACE_DIALOG_CLASS_NAME = "org.eclipse.ui.texteditor.FindReplaceDialog"; //$NON-NLS-1$

	private IDialogSettings fDialogSettings;

	/* default */boolean fWrap;
	/* default */boolean fCase;
	/* default */boolean fWholeWord;
	/* default */boolean fRegExSearch;
	/* default */String fSelection;
	/* default */final List<String> fFindHistory = new ArrayList<String>();

	/**
	 * Returns the dialog settings object used to share state between several find/replace dialogs.
	 * 
	 * @return the dialog settings to be used
	 */
	private IDialogSettings getDialogSettings()
	{
		IDialogSettings settings = TextEditorPlugin.getDefault().getDialogSettings();
		fDialogSettings = settings.getSection(FIND_REPLACE_DIALOG_CLASS_NAME);
		if (fDialogSettings == null)
			fDialogSettings = settings.addNewSection(FIND_REPLACE_DIALOG_CLASS_NAME);
		return fDialogSettings;
	}

	/**
	 * Initializes itself from the dialog settings with the same state as at the previous invocation.
	 */
	/* default */void readConfiguration()
	{
		IDialogSettings s = getDialogSettings();

		fWrap = s.get("wrap") == null || s.getBoolean("wrap"); //$NON-NLS-1$ //$NON-NLS-2$
		fCase = s.getBoolean("casesensitive"); //$NON-NLS-1$
		fWholeWord = s.getBoolean("wholeword"); //$NON-NLS-1$
		fRegExSearch = s.getBoolean("isRegEx"); //$NON-NLS-1$
		fSelection = s.get("selection"); //$NON-NLS-1$

		String[] findHistory = s.getArray("findhistory"); //$NON-NLS-1$
		if (findHistory != null)
		{
			fFindHistory.clear();
			for (int i = 0; i < findHistory.length; i++)
			{
				fFindHistory.add(findHistory[i]);
			}
		}
	}

	/**
	 * Stores its current configuration in the dialog store.
	 */
	/* default */void writeConfiguration()
	{
		IDialogSettings s = getDialogSettings();
		s.put("selection", fSelection); //$NON-NLS-1$

		while (fFindHistory.size() > 8)
		{
			fFindHistory.remove(8);
		}
		String[] names = new String[fFindHistory.size()];
		fFindHistory.toArray(names);
		s.put("findhistory", names); //$NON-NLS-1$

		s.put("wrap", fWrap); //$NON-NLS-1$
		s.put("casesensitive", fCase); //$NON-NLS-1$
		s.put("wholeword", fWholeWord); //$NON-NLS-1$
		s.put("isRegEx", fRegExSearch); //$NON-NLS-1$

	}

	public void addEntry(String selection)
	{
		fSelection = selection;
		int index = fFindHistory.indexOf(selection);
		if (index != -1)
		{
			fFindHistory.remove(index);
		}
		fFindHistory.add(0, selection);

		while (fFindHistory.size() > 8)
		{
			fFindHistory.remove(8);
		}
		String[] names = new String[fFindHistory.size()];
		fFindHistory.toArray(names);
		writeConfiguration();
	}

}
