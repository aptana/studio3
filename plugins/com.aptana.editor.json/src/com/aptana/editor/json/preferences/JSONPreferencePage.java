/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.json.JSONEditor;
import com.aptana.editor.json.JSONPlugin;

public class JSONPreferencePage extends CommonEditorPreferencePage
{
	private BooleanFieldEditor foldObjects;
	private BooleanFieldEditor foldArrays;
	private Composite foldingGroup;

	/**
	 * JSONPreferencePage
	 */
	public JSONPreferencePage()
	{
		setDescription(Messages.JSONPreferencePage_JSON_Page_Title);
		setPreferenceStore(JSONPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return InstanceScope.INSTANCE.getNode(JSONPlugin.PLUGIN_ID);
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return JSONEditor.getChainedPreferenceStore();
	}

	@Override
	protected Composite createFoldingOptions(Composite parent)
	{
		foldingGroup = super.createFoldingOptions(parent);

		// Initially fold these elements:
		Label initialFoldLabel = new Label(foldingGroup, SWT.WRAP);
		initialFoldLabel.setText(Messages.JSONPreferencePage_initial_fold_options_label);

		// Objects
		foldObjects = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_OBJECTS,
				Messages.JSONPreferencePage_fold_objects_label, foldingGroup);
		addField(foldObjects);

		// Arrays
		foldArrays = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_ARRAYS,
				Messages.JSONPreferencePage_fold_arrays_label, foldingGroup);
		addField(foldArrays);

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
				foldObjects.setEnabled(true, foldingGroup);
				foldArrays.setEnabled(true, foldingGroup);
			}
			else
			{
				foldObjects.setEnabled(false, foldingGroup);
				foldArrays.setEnabled(false, foldingGroup);
			}
		}
		super.propertyChange(event);
	}
}
