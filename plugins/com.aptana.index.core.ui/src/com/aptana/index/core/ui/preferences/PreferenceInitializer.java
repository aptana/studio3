package com.aptana.index.core.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.index.core.ui.IndexUiActivator;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = (new DefaultScope()).getNode(IndexUiActivator.PLUGIN_ID);

		prefs.put(IPreferenceConstants.FILTERED_INDEX_URIS, IPreferenceConstants.NO_ITEMS); //$NON-NLS-1$
	}
}
