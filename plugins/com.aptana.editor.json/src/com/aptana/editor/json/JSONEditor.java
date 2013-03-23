/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.json.internal.text.JSONFoldingComputer;
import com.aptana.editor.json.outline.JSONOutlineContentProvider;
import com.aptana.editor.json.outline.JSONOutlineLabelProvider;
import com.aptana.json.core.IJSONConstants;

@SuppressWarnings("restriction")
public class JSONEditor extends AbstractThemeableEditor
{
	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new JSONOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new JSONOutlineLabelProvider();
	}

	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());

		this.setSourceViewerConfiguration(new JSONSourceViewerConfiguration(this.getPreferenceStore(), this));
		this.setDocumentProvider(JSONPlugin.getDefault().getJSONDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { JSONPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new JSONFoldingComputer(this, document);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return JSONPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getContentType()
	{
		return IJSONConstants.CONTENT_TYPE_JSON;
	}
}
