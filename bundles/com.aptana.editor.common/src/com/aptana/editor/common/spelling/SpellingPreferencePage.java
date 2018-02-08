/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.spelling;

import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.texteditor.spelling.SpellingService;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;

/**
 * @author Max Stepanov
 */
public class SpellingPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

	private static final String GENERAL_SPELLING_PREF_ID = "org.eclipse.ui.editors.preferencePages.Spelling"; //$NON-NLS-1$

	private CheckboxTableViewer tableViewer;
	private Link globalPreferencesLink;

	/**
	 *
	 */
	public SpellingPreferencePage()
	{
		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().create());
		composite.setLayoutData(GridDataFactory.fillDefaults().create());

		globalPreferencesLink = new Link(composite, SWT.NONE);
		globalPreferencesLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());

		Label label = new Label(composite, SWT.NONE);
		label.setText(StringUtil.makeFormLabel(Messages.SpellingPreferencePage_label));
		label.setLayoutData(GridDataFactory.swtDefaults().indent(SWT.DEFAULT, 5).align(SWT.FILL, SWT.CENTER).create());

		tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
		tableViewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setLabelProvider(new LabelProvider()
		{
			@Override
			public String getText(Object element)
			{
				return ScopeDefinitions.DEFINITIONS.get(element);
			}
		});
		tableViewer.setInput(ScopeDefinitions.DEFINITIONS.keySet());
		tableViewer.setCheckedElements(SpellingPreferences.getEnabledScopes().toArray());

		globalPreferencesLink.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(GENERAL_SPELLING_PREF_ID, null);
			}
		});

		applyDialogFont(composite);
		updateStatus();
		return composite;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#setContainer(org.eclipse.jface.preference.IPreferencePageContainer)
	 */
	@Override
	public void setContainer(IPreferencePageContainer container)
	{
		super.setContainer(container);
		updateStatus();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults()
	{
		tableViewer.setCheckedElements(ScopeDefinitions.DEFINITIONS.keySet().toArray());
		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		Object[] checked = tableViewer.getCheckedElements();
		String[] scopes = new String[checked.length];
		System.arraycopy(checked, 0, scopes, 0, scopes.length);
		SpellingPreferences.setEnabledScopes(Arrays.asList(scopes));
		return super.performOk();
	}

	private void updateStatus()
	{
		boolean spellingEnabled = EditorsUI.getPreferenceStore()
				.getBoolean(SpellingService.PREFERENCE_SPELLING_ENABLED);
		if (globalPreferencesLink != null)
		{
			String spellingEnabledMessage = Messages.SpellingPreferencePage_EnabledMessage;
			String spellingDisabledMessage = Messages.SpellingPreferencePage_DisabledMessage;
			globalPreferencesLink.setText(spellingEnabled ? spellingEnabledMessage : spellingDisabledMessage);
		}
		if (tableViewer != null)
		{
			tableViewer.getControl().setEnabled(spellingEnabled);
		}
	}
}
