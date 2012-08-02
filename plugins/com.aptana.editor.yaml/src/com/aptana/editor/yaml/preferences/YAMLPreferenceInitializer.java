/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.yaml.YAMLPlugin;

public class YAMLPreferenceInitializer extends AbstractPreferenceInitializer
{

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = EclipseUtil.defaultScope().getNode(YAMLPlugin.PLUGIN_ID);
		prefs.putBoolean(IPreferenceConstants.EDITOR_AUTO_INDENT, true);
		prefs.putBoolean(IPreferenceConstants.EDITOR_ENABLE_FOLDING, true);

		// mark occurrences
		// prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.EDITOR_MARK_OCCURRENCES, true);

		// Check if we previously set preference to use global defaults
		IEclipsePreferences instanceScopePref = EclipseUtil.instanceScope().getNode(YAMLPlugin.PLUGIN_ID);
		if (!instanceScopePref.getBoolean(IPreferenceConstants.USE_GLOBAL_DEFAULTS, false))
		{
			prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH,
					IYAMLPreferenceConstants.DEFAULT_YAML_TAB_WIDTH);
			prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS,
					IYAMLPreferenceConstants.DEFAULT_YAML_SPACES_FOR_TABS);
		}
	}
}
