/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.parsing.IHTMLParserConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{

	@SuppressWarnings("nls")
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = (new DefaultScope()).getNode(HTMLPlugin.PLUGIN_ID);

		prefs.putBoolean(com.aptana.editor.common.preferences.IPreferenceConstants.LINK_OUTLINE_WITH_EDITOR, true);
		prefs.putDouble(IPreferenceContants.HTML_INDEX_VERSION, 0);

		prefs = new DefaultScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		String[] filtered = new String[] { ".*canvas.*" };
		prefs.put(IHTMLParserConstants.LANGUAGE + ":"
				+ com.aptana.editor.common.preferences.IPreferenceConstants.FILTER_EXPRESSIONS,
				StringUtil.join("####", filtered));
	}
}
