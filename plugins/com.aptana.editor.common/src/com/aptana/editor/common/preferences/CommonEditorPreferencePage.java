/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.CommonEditorPlugin;
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
	protected void createFieldEditors()
	{
		originalPref = getPreferenceStore();
		setPreferenceStore(getChainedEditorPreferenceStore());
		appearanceComposite = getFieldEditorParent();

		createMarkOccurrenceOptions(appearanceComposite);

		createTextEditingOptions(appearanceComposite, Messages.CommonEditorPreferencePage_Text_Editing_Label);
		setPreferenceStore(originalPref);

	}

	protected void createTextEditingOptions(Composite parent, String groupName)
	{
		Composite group = AptanaPreferencePage.createGroup(parent, groupName);
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Label label = new Label(group, SWT.NONE);
		label.setText(Messages.CommonEditorPreferencePage_LBL_TabPolicy);
		label.setLayoutData(GridDataFactory.fillDefaults().create());

		tabSpaceCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		tabSpaceCombo.add(Messages.CommonEditorPreferencePage_UseSpacesOption);
		tabSpaceCombo.add(Messages.CommonEditorPreferencePage_UseTabOption);
		tabSpaceCombo.add(Messages.CommonEditorPreferencePage_UseDefaultOption);
		tabSpaceCombo.setLayoutData(GridDataFactory.fillDefaults().create());

		setTabSpaceCombo();

		final Composite fildEditorGroup = new Composite(group, SWT.NONE);
		fildEditorGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		tabSpaceCombo.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Object source = e.getSource();
				if (source == tabSpaceCombo)
				{
					tabSize.setEnabled(
							!tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseDefaultOption),
							fildEditorGroup);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

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
				setEnabled(!tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseDefaultOption),
						fildEditorGroup);
			}

			@Override
			protected void doLoad()
			{
				Text text = getTextControl();
				if (text != null)
				{
					int value = getChainedEditorPreferenceStore().getInt(getPreferenceName());
					text.setText(Integer.toString(value));
					oldValue = Integer.toString(value);
				}
			}

			@Override
			protected void doStore()
			{
				// This is called only when Apply or OK are clicked on the dialog, so we are OK to store to the
				// preferences here.
				Text text = getTextControl();
				if (text != null)
				{
					Integer i = new Integer(text.getText());
					int globalEditorValue = new ChainedPreferenceStore(new IPreferenceStore[] {
							CommonEditorPlugin.getDefault().getPreferenceStore(),
							EditorsPlugin.getDefault().getPreferenceStore() })
							.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);

					if (i.intValue() == globalEditorValue
							|| tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseDefaultOption))
					{
						// Remove preference from plugin preference store if it has the same value as the global editor
						// value, or if the tab-policy is set to use the global settings.
						getPluginPreferenceStore().remove(
								AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
					}
					else
					{
						getPreferenceStore().setValue(getPreferenceName(), i.intValue());
					}
				}

			}

		};
		tabSize.setEmptyStringAllowed(false);
		tabSize.setValidRange(1, 20);
		tabSize.setEnabled(!tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseDefaultOption),
				fildEditorGroup);
		addField(tabSize);

		createAutoIndentOptions(group);

	}

	private void setTabSpaceCombo()
	{
		if (!originalPref.contains(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS))
		{
			tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseDefaultOption);
		}
		else
		{
			if (getPreferenceStore().getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS))
			{
				tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseSpacesOption);
			}
			else
			{
				tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseTabOption);
			}
		}
	}

	/**
	 * Create the Mark Occurrences group and options if there are any for this language/editor.
	 * 
	 * @param parent
	 */
	protected abstract void createMarkOccurrenceOptions(Composite parent);

	protected abstract IPreferenceStore getChainedEditorPreferenceStore();

	protected abstract IEclipsePreferences getPluginPreferenceStore();

	public void init(IWorkbench workbench)
	{
	}

	public boolean performOk()
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
			store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		}
		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		IEclipsePreferences store = getPluginPreferenceStore();
		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		setTabSpaceCombo();
		super.performDefaults();
		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			CommonEditorPlugin.logError(e);
		}
	}

	protected void createAutoIndentOptions(Composite parent)
	{
		Composite autoIndentGroup = new Composite(parent, SWT.NONE);
		autoIndentGroup.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).create());

		FieldEditor autoIndentTag = new BooleanFieldEditor(IPreferenceConstants.EDITOR_AUTO_INDENT,
				Messages.CommonEditorPreferencePage_auto_indent_label, autoIndentGroup);
		addField(autoIndentTag);
	}

}
