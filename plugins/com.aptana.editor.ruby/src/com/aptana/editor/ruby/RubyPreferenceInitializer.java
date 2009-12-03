package com.aptana.editor.ruby;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.osgi.service.prefs.BackingStoreException;

public class RubyPreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(Activator.PLUGIN_ID);
		prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 2);
		prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, true);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			Activator.log(e);
		}
	}

}
