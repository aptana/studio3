/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;

public class ToggleWordWrapHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IEclipsePreferences prefs = (new InstanceScope()).getNode(CommonEditorPlugin.PLUGIN_ID);
		boolean currentSetting = Platform.getPreferencesService().getBoolean(CommonEditorPlugin.PLUGIN_ID,
				IPreferenceConstants.ENABLE_WORD_WRAP, false, null);
		prefs.putBoolean(IPreferenceConstants.ENABLE_WORD_WRAP, !currentSetting);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
		return null;
	}
}
