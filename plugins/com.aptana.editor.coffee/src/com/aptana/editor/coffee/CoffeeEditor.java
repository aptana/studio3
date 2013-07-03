/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.coffee.internal.text.CoffeeFoldingComputer;
import com.aptana.editor.coffee.outline.CoffeeOutlineContentProvider;
import com.aptana.editor.coffee.outline.CoffeeOutlineLabelProvider;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;

@SuppressWarnings("restriction")
public class CoffeeEditor extends AbstractThemeableEditor
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#initializeEditor()
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());

		setSourceViewerConfiguration(new CoffeeSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(CoffeeScriptEditorPlugin.getDefault().getCoffeeDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] {
				CoffeeScriptEditorPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new CoffeeOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new CoffeeOutlineLabelProvider();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getOutlinePreferenceStore()
	 */
	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return getPluginPreferenceStore();
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new CoffeeFoldingComputer(this, document);
	}

	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return CoffeeScriptEditorPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getContentType()
	{
		return ICoffeeConstants.CONTENT_TYPE_COFFEE;
	}
}
