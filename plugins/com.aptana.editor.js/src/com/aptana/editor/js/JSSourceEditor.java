/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.js.internal.text.JSFoldingComputer;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;

@SuppressWarnings("restriction")
public class JSSourceEditor extends AbstractThemeableEditor
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

		setSourceViewerConfiguration(new JSSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new JSDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { JSPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createFileService()
	 */
	@Override
	protected FileService createFileService()
	{
		return new FileService(IJSParserConstants.LANGUAGE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createOutlinePage()
	 */
	@Override
	protected CommonOutlinePage createOutlinePage()
	{
		CommonOutlinePage outline = super.createOutlinePage();
		outline.setContentProvider(new JSOutlineContentProvider());
		outline.setLabelProvider(new JSOutlineLabelProvider());

		return outline;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getOutlinePreferenceStore()
	 */
	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return JSPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new JSFoldingComputer(this, document);
	}
}
