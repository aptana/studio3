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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

import com.aptana.core.logging.IdeLog;
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
	protected BooleanFieldEditor enableFolding;
	private Combo tabSpaceCombo;
	private IPropertyChangeListener tabSizeListener;

	/**
	 * EditorsPreferencePage
	 */
	protected CommonEditorPreferencePage()
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
		appearanceComposite = getFieldEditorParent();
		createMarkOccurrenceOptions(appearanceComposite);
		createTextEditingOptions(appearanceComposite, Messages.CommonEditorPreferencePage_Text_Editing_Label);
		Composite group = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.CommonEditorPreferencePage_Folding);
		group.setLayout(GridLayoutFactory.swtDefaults().create());
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		createFoldingOptions(group);

		Composite caGroup = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.CommonEditorPreferencePage_ContentAssist);
		caGroup.setLayout(GridLayoutFactory.swtDefaults().create());
		caGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite caOptions = createContentAssistOptions(caGroup);
		if (caOptions.getChildren().length == 1)
		{
			caGroup.getParent().setVisible(false);
		}
	}

	private void createTextEditingOptions(Composite parent, String groupName)
	{
		Composite group = AptanaPreferencePage.createGroup(parent, groupName);
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
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

		final Composite fieldEditorGroup = new Composite(group, SWT.NONE);
		fieldEditorGroup.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		tabSpaceCombo.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Object source = e.getSource();
				if (source == tabSpaceCombo)
				{
					tabSize.setEnabled(
							!tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseDefaultOption),
							fieldEditorGroup);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
			}
		});

		tabSize = new IntegerFieldEditor(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH,
				Messages.CommonEditorPreferencePage_Tab_Size_Label, fieldEditorGroup, 5)
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
						fieldEditorGroup);
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
						removePluginDefaults();
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
				fieldEditorGroup);
		addField(tabSize);

		createAutoIndentOptions(group);

		tabSizeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(event.getProperty()))
				{
					// Update the tab-size control
					setTabSpaceCombo();
					tabSize.load();
				}
			}
		};
		// Listen to any external changes to the tab-size. The code-formatter preference page may change this value, so
		// we need to track it here, as long as this page is not disposed.
		getPreferenceStore().addPropertyChangeListener(tabSizeListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#dispose()
	 */
	@Override
	public void dispose()
	{
		if (tabSizeListener != null)
		{
			getPreferenceStore().removePropertyChangeListener(tabSizeListener);
			tabSizeListener = null;
		}
		super.dispose();
	}

	private void setTabSpaceCombo()
	{
		IEclipsePreferences store = getPluginPreferenceStore();

		if (store.getBoolean(IPreferenceConstants.USE_GLOBAL_DEFAULTS, false))
		{
			tabSpaceCombo.setText(Messages.CommonEditorPreferencePage_UseDefaultOption);
		}
		else
		{
			boolean useSpaces = store.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS,
					true);
			tabSpaceCombo.setText(useSpaces ? Messages.CommonEditorPreferencePage_UseSpacesOption
					: Messages.CommonEditorPreferencePage_UseTabOption);
		}
	}

	public boolean performOk()
	{
		IEclipsePreferences store = getPluginPreferenceStore();

		if (tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseSpacesOption))
		{
			store.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
			store.putBoolean(IPreferenceConstants.USE_GLOBAL_DEFAULTS, false);
		}
		else if (tabSpaceCombo.getText().equals(Messages.CommonEditorPreferencePage_UseTabOption))
		{
			store.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, false);
			store.putBoolean(IPreferenceConstants.USE_GLOBAL_DEFAULTS, false);
		}
		else
		{
			removePluginDefaults();
			store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
			store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
			store.putBoolean(IPreferenceConstants.USE_GLOBAL_DEFAULTS, true);
		}

		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults()
	{
		IEclipsePreferences store = getPluginPreferenceStore();

		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);

		setPluginDefaults();
		setTabSpaceCombo();
		super.performDefaults();
		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	private void createAutoIndentOptions(Composite parent)
	{
		Composite autoIndentGroup = new Composite(parent, SWT.NONE);
		autoIndentGroup.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).create());

		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_AUTO_INDENT,
				Messages.CommonEditorPreferencePage_auto_indent_label, autoIndentGroup));
	}

	protected Composite createFoldingOptions(Composite parent)
	{
		Composite foldingGroup = new Composite(parent, SWT.NONE);
		foldingGroup.setLayoutData(GridDataFactory.fillDefaults().span(1, 1).create());

		addField(enableFolding = new BooleanFieldEditor(IPreferenceConstants.EDITOR_ENABLE_FOLDING,
				Messages.CommonEditorPreferencePage_enable_folding_label, foldingGroup));

		return foldingGroup;
	}

	/**
	 * Create the Content Assist group and options if there are any for this language/editor.
	 * 
	 * @param parent
	 */
	protected Composite createContentAssistOptions(Composite parent)
	{
		IPreferenceStore s = getChainedEditorPreferenceStore();

		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.CommonEditorPreferencePage_OnTypingCharacters);
		label.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());

		if (s.contains(com.aptana.editor.common.contentassist.IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS))
		{
			addField(new StringFieldEditor(
					com.aptana.editor.common.contentassist.IPreferenceConstants.COMPLETION_PROPOSAL_ACTIVATION_CHARACTERS,
					Messages.CommonEditorPreferencePage_DisplayProposals, parent));
		}

		if (s.contains(com.aptana.editor.common.contentassist.IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS))
		{
			addField(new StringFieldEditor(
					com.aptana.editor.common.contentassist.IPreferenceConstants.CONTEXT_INFORMATION_ACTIVATION_CHARACTERS,
					Messages.CommonEditorPreferencePage_DisplayContextualInfo, parent));
		}

		if (s.contains(com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS))
		{
			addField(new StringFieldEditor(
					com.aptana.editor.common.contentassist.IPreferenceConstants.PROPOSAL_TRIGGER_CHARACTERS,
					Messages.CommonEditorPreferencePage_InsertProposal, parent));
		}

		return parent;
	}

	/**
	 * This method re-applies the plugin defaults for the spaces for tabs and tab width preferences from the default
	 * scope of the plugin preference store. The default values are taken from getDefaultTabWidth() and
	 * getDefaultSpacesForTabs(). The default scope getDefaultPluginPreferenceStore() is used.
	 */
	private void setPluginDefaults()
	{
		IEclipsePreferences store = getDefaultPluginPreferenceStore();
		if (store == null)
		{
			return;
		}

		store.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS,
				getDefaultSpacesForTabs());
		store.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, getDefaultTabWidth());
		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	/**
	 * This method removes the spaces for tabs and tab width preferences from the default scope of the plugin preference
	 * store.
	 */
	private void removePluginDefaults()
	{
		IEclipsePreferences store = getDefaultPluginPreferenceStore();
		if (store == null)
		{
			return;
		}

		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		store.remove(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		try
		{
			store.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CommonEditorPlugin.getDefault(), e);
		}
	}

	/**
	 * This class returns the default scope of the plugin preference store. This method should be overwritten when we
	 * want to specify other default preferences for a particular editor rather than "Use Global Editor Defaults". NOTE:
	 * When specifying other default preferences, we also need to overwrite getDefaultSpacesForTabs() and
	 * getDefaultTabWidth()
	 * 
	 * @return
	 */
	protected IEclipsePreferences getDefaultPluginPreferenceStore()
	{
		return null;
	}

	/**
	 * The default value for spaces for tabs preference. (Used with getDefaultPluginPreferenceStore() )
	 * 
	 * @return
	 */

	protected boolean getDefaultSpacesForTabs()
	{
		return getChainedEditorPreferenceStore().getBoolean(
				AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
	}

	/**
	 * The default value used for tab width preference. (Used with getDefaultPluginPreferenceStore() )
	 * 
	 * @return
	 */

	protected int getDefaultTabWidth()
	{
		return getChainedEditorPreferenceStore()
				.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * Create the Mark Occurrences group and options if there are any for this language/editor.
	 * 
	 * @param parent
	 */
	protected void createMarkOccurrenceOptions(Composite parent)
	{
		Composite group = AptanaPreferencePage.createGroup(parent, "Mark Occurrences"); //$NON-NLS-1$

		// @formatter:off
		addField(
			new BooleanFieldEditor(
				IPreferenceConstants.EDITOR_MARK_OCCURRENCES,
				Messages.EditorsPreferencePage_MarkOccurrences,
				group
			)
		);
		// @formatter:on
	}

	protected abstract IPreferenceStore getChainedEditorPreferenceStore();

	protected abstract IEclipsePreferences getPluginPreferenceStore();

}
