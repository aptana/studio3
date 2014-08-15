/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.CSSSourceEditor;

public class CSSPreferencePage extends CommonEditorPreferencePage
{

	private BooleanFieldEditor foldComments;
	private BooleanFieldEditor foldRules;
	private Composite foldingGroup;

	/**
	 * CSSPreferencePage
	 */

	public CSSPreferencePage()
	{
		super();
		setDescription(Messages.CSSPreferencePage_CSS_Page_Title);
		setPreferenceStore(CSSPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return CSSSourceEditor.getChainedPreferenceStore();
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return InstanceScope.INSTANCE.getNode(CSSPlugin.PLUGIN_ID);
	}

	@Override
	protected Composite createFoldingOptions(Composite parent)
	{
		this.foldingGroup = super.createFoldingOptions(parent);

		// Initially fold these elements:
		Label initialFoldLabel = new Label(foldingGroup, SWT.WRAP);
		initialFoldLabel.setText(Messages.CSSPreferencePage_initial_fold_options_label);

		// Comments
		foldComments = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_COMMENTS,
				Messages.CSSPreferencePage_fold_comments_label, foldingGroup);
		addField(foldComments);

		// Rules
		foldRules = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_RULES,
				Messages.CSSPreferencePage_fold_rules_label, foldingGroup);
		addField(foldRules);

		return foldingGroup;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource() == enableFolding)
		{
			Object newValue = event.getNewValue();
			if (Boolean.TRUE == newValue)
			{
				foldComments.setEnabled(true, foldingGroup);
				foldRules.setEnabled(true, foldingGroup);
			}
			else
			{
				foldComments.setEnabled(false, foldingGroup);
				foldRules.setEnabled(false, foldingGroup);
			}
		}
		super.propertyChange(event);
	}

}
