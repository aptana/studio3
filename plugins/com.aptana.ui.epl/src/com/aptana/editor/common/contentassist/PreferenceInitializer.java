package com.aptana.editor.common.contentassist;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.ui.epl.UIEplPlugin;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = new DefaultScope().getNode(UIEplPlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.USER_AGENT_PREFERENCE, "IE,Mozilla"); //$NON-NLS-1$
	}
}
