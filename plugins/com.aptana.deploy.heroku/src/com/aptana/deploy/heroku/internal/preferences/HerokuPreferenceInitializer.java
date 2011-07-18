/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.deploy.heroku.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.EclipseUtil;
import com.aptana.deploy.heroku.HerokuPlugin;
import com.aptana.deploy.heroku.preferences.IPreferenceConstants;

public class HerokuPreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = (EclipseUtil.defaultScope()).getNode(HerokuPlugin.getPluginIdentifier());
		prefs.putBoolean(IPreferenceConstants.HEROKU_AUTO_PUBLISH, true);
	}
}
