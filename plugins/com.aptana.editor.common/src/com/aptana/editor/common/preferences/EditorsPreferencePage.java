/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.ui.preferences.AptanaPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plugin.
 */
public class EditorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{

	private String GENERAL_TEXT_EDITOR_PREF_ID = "org.eclipse.ui.preferencePages.GeneralTextEditor"; //$NON-NLS-1$
	private ComboFieldEditor editorsListCombo;

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

		// Typing
		Composite group = AptanaPreferencePage.createGroup(appearanceComposite, Messages.EditorsPreferencePage_Typing);
		addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING,
				Messages.EditorsPreferencePage_Colorize_Matching_Character_Pairs, group));
		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE,
				Messages.EditorsPreferencePage_Close_Matching_Character_Pairs, group));
		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_WRAP_SELECTION,
				Messages.EditorsPreferencePage_Wrap_Selection, group));
		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_SUB_WORD_NAVIGATION,
				Messages.EditorsPreferencePage_camelCaseSelection, group));

		// Save Actions
		group = AptanaPreferencePage.createGroup(appearanceComposite, Messages.EditorsPreferencePage_saveActionsGroup);
		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_REMOVE_TRAILING_WHITESPACE,
				Messages.EditorsPreferencePage_saveActionRemoveWhitespaceCharacters, group));

		// Syntax coloring
		group = AptanaPreferencePage.createGroup(appearanceComposite, Messages.EditorsPreferencePage_SyntaxColoring);
		group.setLayout(GridLayoutFactory.swtDefaults().create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		IntegerFieldEditor colEditor = new IntegerFieldEditor(IPreferenceConstants.EDITOR_MAX_COLORED_COLUMNS,
				StringUtil.makeFormLabel(Messages.EditorsPreferencePage_MaxColumnsLabel), group);
		colEditor.setValidRange(-1, Integer.MAX_VALUE);
		addField(colEditor);

		// Word Wrap
		addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_WORD_WRAP,
				Messages.EditorsPreferencePage_Enable_WordWrap, appearanceComposite));

		createOpenWithEditor(appearanceComposite);

		createTextEditorLink(appearanceComposite);
	}

	private void createOpenWithEditor(Composite appearanceComposite)
	{
		Map<String, String> editorsMap = EditorUtil.getAllRegistryEditors();

		// Prepare the input for the combo
		String[][] entryNamesAndValues = new String[editorsMap.size()][2];
		Set<Entry<String, String>> entries = editorsMap.entrySet();
		Iterator<Entry<String, String>> entriesIterator = entries.iterator();

		int i = 0;
		while (entriesIterator.hasNext())
		{
			Map.Entry<String, String> mapping = (Entry<String, String>) entriesIterator.next();
			entryNamesAndValues[i][0] = mapping.getValue();
			entryNamesAndValues[i][1] = mapping.getKey();
			i++;
		}

		editorsListCombo = new ComboFieldEditor(IPreferenceConstants.OPEN_WITH_EDITOR,
				Messages.EditorsPreferencePage_OpenWith_Editor, entryNamesAndValues, appearanceComposite);
		addField(editorsListCombo);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		super.propertyChange(event);

		// It's better to handle this functionality here rather than okPressed() and peformApply() considering these
		// functions will be called every time, propertyChange() will be invoked for the changes only in Editor
		// preferences.
		if (event.getSource() == editorsListCombo)
		{
			Object newValue = event.getNewValue();
			Object oldValue = event.getOldValue();
			if (!(newValue != null && oldValue != null && newValue.equals(oldValue)))
			{
				CommonUtil.handleOpenWithEditorPref();
			}
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	private void createTextEditorLink(Composite appearanceComposite)
	{
		// Link to general text editor prefs from Eclipse - they can set tabs/spaces/whitespace drawing, etc
		Link link = new Link(appearanceComposite, SWT.NONE);
		link.setText(Messages.EditorsPreferencePage_GeneralTextEditorPrefLink);
		link.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 2, 1));
		link.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(GENERAL_TEXT_EDITOR_PREF_ID, null);
			}
		});
	}
}
