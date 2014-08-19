/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.preferences;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.common.preferences.CommonEditorPreferencePage;
import com.aptana.editor.xml.XMLEditor;
import com.aptana.editor.xml.XMLPlugin;

public class XMLPreferencePage extends CommonEditorPreferencePage
{

	/**
	 * XMLPreferencePage
	 */

	public XMLPreferencePage()
	{
		super();
		setDescription(Messages.XMLPreferencePage_XML_Page_Title);
		setPreferenceStore(XMLPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IEclipsePreferences getPluginPreferenceStore()
	{
		return InstanceScope.INSTANCE.getNode(XMLPlugin.PLUGIN_ID);
	}

	@Override
	protected IPreferenceStore getChainedEditorPreferenceStore()
	{
		return XMLEditor.getChainedPreferenceStore();
	}

}
