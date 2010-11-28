package com.aptana.editor.html.preferences;

import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.html.Activator;

public class HTMLPreferencePage extends CommonEditorPreferencePage
{
	/**
	 * HTMLPreferencePage
	 */
	public HTMLPreferencePage()
	{
		super();
		setDescription("Preferences for the Aptana HTML Editor");
		setPreferenceStore( Activator.getDefault().getPreferenceStore());
	}
}
