/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.reconciler.IFoldingComputer;
import com.aptana.editor.css.internal.text.CSSFoldingComputer;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.css.outline.CSSOutlineLabelProvider;
import com.aptana.parsing.ast.IParseRootNode;

@SuppressWarnings({ "restriction" })
public class CSSSourceEditor extends AbstractThemeableEditor
{
	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new CSSOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new CSSOutlineLabelProvider();
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setPreferenceStore(getChainedPreferenceStore());
		setSourceViewerConfiguration(new CSSSourceViewerConfiguration(getPreferenceStore(), this));

		setDocumentProvider(CSSPlugin.getDefault().getCSSDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { CSSPlugin.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	@Override
	protected Object getOutlineElementAt(int caret)
	{
		if (hasOutlinePageCreated())
		{
			return CSSOutlineContentProvider.getElementAt(getOutlinePage().getCurrentAst(), caret);
		}
		return null;
	}

	@Override
	protected IPreferenceStore getOutlinePreferenceStore()
	{
		return CSSPlugin.getDefault().getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return CSSPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IFoldingComputer createFoldingComputer(IDocument document)
	{
		return new CSSFoldingComputer(this, document);
	}

	@Override
	public String getContentType()
	{
		return ICSSConstants.CONTENT_TYPE_CSS;
	}

	@Override
	public void refreshOutline(final IParseRootNode ast)
	{
		outlineAutoExpanded = true; // Don't auto-expand it here.
		super.refreshOutline(ast);
	}
}
