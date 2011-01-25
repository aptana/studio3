package com.aptana.editor.html.preferences;

import org.eclipse.swt.widgets.Composite;

import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.html.HTMLPlugin;

public class HTMLPreferencePage extends CommonEditorPreferencePage
{
	/**
	 * HTMLPreferencePage
	 */
	public HTMLPreferencePage()
	{
		super();
		setDescription(Messages.HTMLPreferencePage_LBL_Description);
		setPreferenceStore( HTMLPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected void createMarkOccurrenceOptions(Composite parent)
	{
	}
}
