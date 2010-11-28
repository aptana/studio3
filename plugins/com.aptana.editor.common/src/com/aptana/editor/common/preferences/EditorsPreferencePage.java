/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.ui.preferences.AptanaPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

public class EditorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private Button spaces;
	private Button tabs;

	private IPreferenceStore editorPreferenceStore;

	private IPropertyChangeListener prefListener = new IPropertyChangeListener()
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH)
					&& spaces != null && !spaces.isDisposed())
			{
				spaces.setText(StringUtil.format(Messages.EditorsPreferencePage_UseSpaces, event.getNewValue()));
			}

			if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS)
					&& spaces != null && !spaces.isDisposed() && tabs != null && !tabs.isDisposed())
			{
				Boolean val = (Boolean) event.getNewValue();
				spaces.setSelection(val);
				tabs.setSelection(!val);
			}

		}

	};

	/**
	 * EditorsPreferencePage
	 */
	@SuppressWarnings("restriction")
	public EditorsPreferencePage()
	{
		super(GRID);
		editorPreferenceStore = EditorsPlugin.getDefault().getPreferenceStore();
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
		Composite group = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.EditorsPreferencePage_Formatting);

		addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING,
				"Colorize matching character pairs", group));

		addField(new RadioGroupFieldEditor(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END,
				Messages.EditorsPreferencePage_HomeEndBehavior, 1, new String[][] {
						{ Messages.EditorsPreferencePage_ToggleBetween, "true" }, //$NON-NLS-1$
						{ Messages.EditorsPreferencePage_JumpsStartEnd, "false" } }, //$NON-NLS-1$
				appearanceComposite, true));

		Composite wsGroup = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.EditorsPreferencePage_TabInsertion);
		Composite wsComp = new Composite(wsGroup, SWT.NONE);

		GridLayout wsLayout = new GridLayout(3, false);
		wsLayout.marginWidth = 0;
		wsLayout.marginHeight = 0;
		wsComp.setLayout(wsLayout);
		wsComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		tabs = new Button(wsComp, SWT.RADIO);
		Composite spaceComp = new Composite(wsComp, SWT.NONE);
		wsLayout = new GridLayout(2, false);
		wsLayout.marginWidth = 0;
		wsLayout.marginHeight = 0;
		wsLayout.horizontalSpacing = 0;
		spaceComp.setLayout(wsLayout);
		spaceComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		spaces = new Button(spaceComp, SWT.RADIO);
		final Link currentTabSize = new Link(spaceComp, SWT.NONE);
		int size = editorPreferenceStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		spaces.setText(StringUtil.format(Messages.EditorsPreferencePage_UseSpaces, size));
		tabs.setText(Messages.EditorsPreferencePage_UseTabs);
		editorPreferenceStore.addPropertyChangeListener(prefListener);
		currentTabSize.setText(Messages.EditorsPreferencePage_EditLink);
		currentTabSize.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(
						"org.eclipse.ui.preferencePages.GeneralTextEditor", null); //$NON-NLS-1$
			}

		});
		boolean useSpaces = editorPreferenceStore
				.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		spaces.setSelection(useSpaces);
		tabs.setSelection(!useSpaces);
		tabs.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				spaces.setSelection(!tabs.getSelection());
			}

		});
		spaces.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				tabs.setSelection(!spaces.getSelection());
			}

		});
		// Link to general text editor prefs from Eclipse - they can set tabs/spaces/whitespace drawing, etc
		Link link = new Link(appearanceComposite, SWT.NONE);
		link.setText(Messages.EditorsPreferencePage_GeneralTextEditorPrefLink);
		link.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(
						"org.eclipse.ui.preferencePages.GeneralTextEditor", null); //$NON-NLS-1$
			}

		});
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#dispose()
	 */
	public void dispose()
	{
		editorPreferenceStore.removePropertyChangeListener(prefListener);
		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		boolean useSpaces = editorPreferenceStore
				.getDefaultBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		spaces.setSelection(useSpaces);
		tabs.setSelection(!useSpaces);
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		editorPreferenceStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS,
				spaces.getSelection());
		return super.performOk();
	}

}
