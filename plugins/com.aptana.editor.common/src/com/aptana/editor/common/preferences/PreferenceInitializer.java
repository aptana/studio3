package com.aptana.editor.common.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.editor.common.CommonEditorPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING, true);
		prefs.put(IPreferenceConstants.CHARACTER_PAIR_COLOR, "128,128,128"); //$NON-NLS-1$
		prefs.putBoolean(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
	}

}
