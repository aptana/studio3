/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.coffee.CoffeeEditor;
import com.aptana.editor.coffee.CoffeeScriptEditorPlugin;
import com.aptana.editor.common.preferences.CommonEditorPreferencePage;

public class CoffeePreferencePage extends CommonEditorPreferencePage
{

	/**
	 * CoffeePreferencePage
	 */
	public CoffeePreferencePage()
	{
		super();
		setDescription(Messages.CoffeePreferencePage_Coffee_Page_Title);
		setPreferenceStore(CoffeeScriptEditorPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return EclipseUtil.instanceScope().getNode(CoffeeScriptEditorPlugin.PLUGIN_ID);
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return CoffeeEditor.getChainedPreferenceStore();
	}

	@Override
	protected IEclipsePreferences getDefaultPluginPreferenceStore()
	{
		return EclipseUtil.defaultScope().getNode(CoffeeScriptEditorPlugin.PLUGIN_ID);
	}

	@Override
	protected boolean getDefaultSpacesForTabs()
	{
		return ICoffeePreferenceConstants.DEFAULT_SPACES_FOR_TABS;
	}

	@Override
	protected int getDefaultTabWidth()
	{
		return ICoffeePreferenceConstants.DEFAULT_TAB_WIDTH;
	}
}
