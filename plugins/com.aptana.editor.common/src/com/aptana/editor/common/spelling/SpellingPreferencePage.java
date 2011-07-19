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
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.aptana.editor.common.CommonEditorPlugin;

/**
 * @author Max Stepanov
 *
 */
public class SpellingPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private CheckboxTableViewer tableViewer;
	
	/**
	 * 
	 */
	public SpellingPreferencePage() {
		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.swtDefaults().create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.SpellingPreferencePage_label);
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).create());
		
		tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
		tableViewer.getTable().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ScopeDefinitions.DEFINITIONS.get(element);
			}
			
		});
		tableViewer.setInput(ScopeDefinitions.DEFINITIONS.keySet());
		tableViewer.setCheckedElements(SpellingPreferences.getEnabledScopes().toArray());

		applyDialogFont(composite);
		return composite;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		tableViewer.setCheckedElements(ScopeDefinitions.DEFINITIONS.keySet().toArray());
		super.performDefaults();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		Object[] checked = tableViewer.getCheckedElements();
		String[] scopes = new String[checked.length];
		System.arraycopy(checked, 0, scopes, 0, scopes.length);
		SpellingPreferences.setEnabledScopes(Arrays.asList(scopes));
		return super.performOk();
	}

}
