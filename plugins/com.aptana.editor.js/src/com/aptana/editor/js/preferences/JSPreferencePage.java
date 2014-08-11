/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.JSSourceEditor;

public class JSPreferencePage extends CommonEditorPreferencePage
{

	private BooleanFieldEditor foldComments;
	private BooleanFieldEditor foldFunctions;
	private BooleanFieldEditor foldObjects;
	private BooleanFieldEditor foldArrays;
	private Composite foldingGroup;

	/**
	 * JSPreferencePage
	 */
	public JSPreferencePage()
	{
		super();
		setDescription(Messages.JSPreferencePage_JS_Page_Title);
		setPreferenceStore(JSPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return InstanceScope.INSTANCE.getNode(JSPlugin.PLUGIN_ID);
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return JSSourceEditor.getChainedPreferenceStore();
	}

	@Override
	protected Composite createFoldingOptions(Composite parent)
	{
		this.foldingGroup = super.createFoldingOptions(parent);

		// Initially fold these elements:
		Label initialFoldLabel = new Label(foldingGroup, SWT.WRAP);
		initialFoldLabel.setText(Messages.JSPreferencePage_initial_fold_options_label);

		// Comments
		foldComments = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_COMMENTS,
				Messages.JSPreferencePage_fold_comments_label, foldingGroup);
		addField(foldComments);

		// Functions
		foldFunctions = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_FUNCTIONS,
				Messages.JSPreferencePage_fold_functions_label, foldingGroup);
		addField(foldFunctions);

		// Objects
		foldObjects = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_OBJECTS,
				Messages.JSPreferencePage_fold_objects_label, foldingGroup);
		addField(foldObjects);

		// Arrays
		foldArrays = new BooleanFieldEditor(IPreferenceConstants.INITIALLY_FOLD_ARRAYS,
				Messages.JSPreferencePage_fold_arrays_label, foldingGroup);
		addField(foldArrays);

		return foldingGroup;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event)
	{
		if (event.getSource() == enableFolding) // $codepro.audit.disable useEquals
		{
			Object newValue = event.getNewValue();
			if (Boolean.TRUE == newValue) // $codepro.audit.disable useEquals
			{
				foldComments.setEnabled(true, foldingGroup);
				foldFunctions.setEnabled(true, foldingGroup);
				foldObjects.setEnabled(true, foldingGroup);
				foldArrays.setEnabled(true, foldingGroup);
			}
			else
			{
				foldComments.setEnabled(false, foldingGroup);
				foldFunctions.setEnabled(false, foldingGroup);
				foldObjects.setEnabled(false, foldingGroup);
				foldArrays.setEnabled(false, foldingGroup);
			}
		}
		super.propertyChange(event);
	}

}
