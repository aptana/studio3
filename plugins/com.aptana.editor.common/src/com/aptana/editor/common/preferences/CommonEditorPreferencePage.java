/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.ui.preferences.AptanaPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

@SuppressWarnings("restriction")
public abstract class CommonEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	/**
	 * Button for enabling occurrence highlighting
	 */
	private Composite appearanceComposite;
	private Composite advancedOptions;
	private BooleanFieldEditor markOccurences;
	private IntegerFieldEditor tabSize;
	private Combo tabSpaceCombo;
	private IPreferenceStore originalPref;

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
		originalPref = getPreferenceStore();
		setPreferenceStore(getChainedEditorPreferenceStore());
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

		createTextEditingOptions(appearanceComposite, Messages.CommonEditorPreferencePage_Text_Editing_Label);
		setPreferenceStore(originalPref);
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

	protected void createTextEditingOptions(Composite parent, String groupName)
	{
		Composite group = AptanaPreferencePage.createGroup(parent, groupName);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		tabSpaceCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		tabSpaceCombo.add(Messages.CommonEditorPreferencePage_UseSpacesOption);
		tabSpaceCombo.add(Messages.CommonEditorPreferencePage_UseTabOption);
		tabSpaceCombo.add(Messages.CommonEditorPreferencePage_UseDefaultOption);
		tabSpaceCombo.setLayoutData(GridDataFactory.fillDefaults().create());

		if (!originalPref.contains(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS))
		{
			tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseDefaultOption);
		}
		else
		{
			if (getPreferenceStore().getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS))
				tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseSpacesOption);
			else
				tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseTabOption);
		}

		tabSpaceCombo.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Object source = e.getSource();
				if (source == tabSpaceCombo)
				{
					IEclipsePreferences store = getPluginPreferenceStore();

					if (tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseSpacesOption))
					{
						store.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
					}
					else if (tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseTabOption))
					{
						store.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, false);
					}
					else
					{
						store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		Composite fildEditorGroup = new Composite(group, SWT.NONE);
		fildEditorGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		tabSize = new IntegerFieldEditor(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH,
				Messages.CommonEditorPreferencePage_Tab_Size_Label, fildEditorGroup, 5)
		{
			@Override
			protected void doLoadDefault()
			{
				Text text = getTextControl();
				if (text != null)
				{
					int value = getChainedEditorPreferenceStore().getInt(getPreferenceName());
					text.setText(Integer.toString(value));
				}
				valueChanged();
			}

			@Override
			protected void doLoad()
			{
		        Text text = getTextControl();
		        if (text != null) {
		            int value = getChainedEditorPreferenceStore().getInt(getPreferenceName());
		            text.setText(Integer.toString(value));
		            oldValue = Integer.toString(value); 
		        }
			}
			
			
		};
		tabSize.setEmptyStringAllowed(false);
		tabSize.setValidRange(1, 20);
		addField(tabSize);
	}

	/**
	 * Create any extra "Mark Occurrence" options if necessary.
	 * 
	 * @param parent
	 */
	protected abstract void createMarkOccurrenceOptions(Composite parent);

	protected abstract IPreferenceStore getChainedEditorPreferenceStore();

	protected abstract IEclipsePreferences getPluginPreferenceStore();

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

	@Override
	protected void performDefaults()
	{
		IEclipsePreferences store = getPluginPreferenceStore();
		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		super.performDefaults();
		tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseDefaultOption);
	}

}
