package com.aptana.editor.common.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.editor.common.CommonEditorPlugin;

@SuppressWarnings("restriction")
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);

		prefs.putBoolean(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING, true);
		prefs.put(IPreferenceConstants.CHARACTER_PAIR_COLOR, "128,128,128"); //$NON-NLS-1$
		prefs.putBoolean(IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);

		IPreferenceStore store = EditorsPlugin.getDefault().getPreferenceStore();
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 2);
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER, true);
	}

}
