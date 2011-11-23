/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.ui.preferences.AptanaPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public class EditorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private String GENERAL_TEXT_EDITOR_PREF_ID = "org.eclipse.ui.preferencePages.GeneralTextEditor"; //$NON-NLS-1$

	/**
	 * EditorsPreferencePage
	 */
	public EditorsPreferencePage()
	{
		super(GRID);

		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.EditorsPreferencePage_PreferenceDescription);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		Composite appearanceComposite = getFieldEditorParent();
		Composite group = AptanaPreferencePage.createGroup(appearanceComposite, Messages.EditorsPreferencePage_Typing);

		addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING,
				Messages.EditorsPreferencePage_Colorize_Matching_Character_Pairs, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE,
				Messages.EditorsPreferencePage_Close_Matching_Character_Pairs, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_WRAP_SELECTION,
				Messages.EditorsPreferencePage_Wrap_Selection, group));

		// In Studio 2.0, commenting out until requested, or it's determined we have enough available space
		// addField(new RadioGroupFieldEditor(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END,
		// Messages.EditorsPreferencePage_HomeEndBehavior, 1, new String[][] {
		//								{ Messages.EditorsPreferencePage_ToggleBetween, "true" }, //$NON-NLS-1$
		//								{ Messages.EditorsPreferencePage_JumpsStartEnd, "false" } }, //$NON-NLS-1$
		// appearanceComposite, true));

		addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_WORD_WRAP,
				Messages.EditorsPreferencePage_Enable_WordWrap, appearanceComposite));
		createTextEditorLink(appearanceComposite);
	}

	private void createTextEditorLink(Composite appearanceComposite)
	{

		// Link to general text editor prefs from Eclipse - they can set tabs/spaces/whitespace drawing, etc
		Link link = new Link(appearanceComposite, SWT.NONE);
		link.setText(Messages.EditorsPreferencePage_GeneralTextEditorPrefLink);
		link.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(GENERAL_TEXT_EDITOR_PREF_ID, null);
			}

		});
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}
}
