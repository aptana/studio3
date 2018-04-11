/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.dtd.core.IDTDConstants;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;

@SuppressWarnings("restriction")
public class DTDEditor extends AbstractThemeableEditor
{

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());

		this.setSourceViewerConfiguration(new DTDSourceViewerConfiguration(this.getPreferenceStore(), this));
		this.setDocumentProvider(DTDPlugin.getDefault().getDTDDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { DTDPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return DTDPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getContentType()
	{
		return IDTDConstants.CONTENT_TYPE_DTD;
	}
}
