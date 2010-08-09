package com.aptana.portal.ui;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initialize the portal (dev-toolbox) preferences.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class PortalPreferenceInitializer extends AbstractPreferenceInitializer
{
	@Override
	public void initializeDefaultPreferences()
	{
		IPreferenceStore preferenceStore = PortalUIPlugin.getDefault().getPreferenceStore();
		preferenceStore.setDefault(IPortalPreferences.SHOULD_OPEN_DEV_TOOLBOX, true);
	}
}
