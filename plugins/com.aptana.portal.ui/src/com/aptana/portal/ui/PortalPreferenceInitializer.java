/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
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
		// TODO - Change this to true once the Toolbox server-side is ready!
		preferenceStore.setDefault(IPortalPreferences.SHOULD_OPEN_DEV_TOOLBOX, false);
	}
}
