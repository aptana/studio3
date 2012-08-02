/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.yaml.YAMLEditor;
import com.aptana.editor.yaml.YAMLPlugin;

public class YAMLPreferencePage extends CommonEditorPreferencePage
{

	/**
	 * YamlPreferencePage
	 */

	public YAMLPreferencePage()
	{
		super();
		setDescription(Messages.YAMLPreferencePage_YAML_Page_Title);
		setPreferenceStore(YAMLPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return EclipseUtil.instanceScope().getNode(YAMLPlugin.PLUGIN_ID);
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return YAMLEditor.getChainedPreferenceStore();
	}

	@Override
	protected IEclipsePreferences getDefaultPluginPreferenceStore()
	{
		return EclipseUtil.defaultScope().getNode(YAMLPlugin.PLUGIN_ID);
	}

	@Override
	protected boolean getDefaultSpacesForTabs()
	{
		return IYAMLPreferenceConstants.DEFAULT_YAML_SPACES_FOR_TABS;
	}

	@Override
	protected int getDefaultTabWidth()
	{
		return IYAMLPreferenceConstants.DEFAULT_YAML_TAB_WIDTH;
	}
}
