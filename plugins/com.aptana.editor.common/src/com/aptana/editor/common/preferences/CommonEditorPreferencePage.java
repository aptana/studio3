/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.aptana.ui.preferences.AptanaPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public abstract class CommonEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * Button for enabling occurrence highlighting
	 */
	private Composite appearanceComposite;
	private Composite advancedOptions;
	private BooleanFieldEditor markOccurences;

	/**
	 * EditorsPreferencePage
	 */
	public CommonEditorPreferencePage()
	{
		super(GRID);
		setDescription(Messages.CommonEditorPreferencePage_Editor_Preferences);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		appearanceComposite = getFieldEditorParent();
		Composite group = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.EditorsPreferencePage_Formatting);

		markOccurences = new BooleanFieldEditor(IPreferenceConstants.EDITOR_MARK_OCCURRENCES,
				Messages.EditorsPreferencePage_MarkOccurrences, group);
		addField(markOccurences);

		// Perhaps need expand/collapse arrows
		advancedOptions = createAdvancedOccurrenceSection(group);

		// Link to general text annotation prefs from Eclipse
		Link link = new Link(group, SWT.NONE);
		link.setText(Messages.CommonEditorPreferencePage_Default_Editor_Preference_Link);
		link.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), e.text, null, null);
			}
		});
	}

	/**
	 * Creates the advanced group of occurrence options. If there are no options, we just delete the group.
	 * 
	 * @param parent
	 * @return
	 */
	private Composite createAdvancedOccurrenceSection(Composite parent)
	{
		Composite aOptions = new Composite(parent, SWT.NONE);

		aOptions.setLayout(GridLayoutFactory.fillDefaults().create());
		aOptions.setLayoutData(GridDataFactory.fillDefaults().indent(18, 0).create());

		createMarkOccurrenceOptions(aOptions);

		aOptions.setVisible(false);

		// Perhaps better way?
		if (aOptions.getChildren().length == 0)
		{
			aOptions.dispose();
			return null;
		}
		else
			return aOptions;
	}

	/**
	 * Create any extra "Mark Occurrence" options if necessary.
	 * 
	 * @param parent
	 */
	protected abstract void createMarkOccurrenceOptions(Composite parent);

	/**
	 * Listens for changes in showing/hiding advanced options
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource() == markOccurences && advancedOptions != null)
		{
			if (!(Boolean) event.getNewValue())
			{
				toggleAdvancedOccurrenceSection(false);
			}
			else
			{
				toggleAdvancedOccurrenceSection(true);
			}
		}
	}

	private void toggleAdvancedOccurrenceSection(boolean show)
	{
		advancedOptions.setVisible(show);
		if (advancedOptions.getLayoutData() != null)
		{
			((GridData) advancedOptions.getLayoutData()).exclude = !show;
		}
		appearanceComposite.layout(true, true);
	}

	public void init(IWorkbench workbench)
	{
	}

	protected void initialize()
	{
		super.initialize();

		if (advancedOptions != null)
		{
			boolean markOccurrences = getPreferenceStore().getBoolean(IPreferenceConstants.EDITOR_MARK_OCCURRENCES);
			toggleAdvancedOccurrenceSection(markOccurrences);
		}
	}
}
