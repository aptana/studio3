/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.beaver;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.aptana.editor.beaver.outline.BeaverOutlineContentProvider;
import com.aptana.editor.beaver.outline.BeaverOutlineLabelProvider;
import com.aptana.editor.common.AbstractThemeableEditor;

public class BeaverEditor extends AbstractThemeableEditor
{
	@Override
	public ITreeContentProvider getOutlineContentProvider()
	{
		return new BeaverOutlineContentProvider();
	}

	@Override
	public ILabelProvider getOutlineLabelProvider()
	{
		return new BeaverOutlineLabelProvider();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return BeaverPlugin.getDefault().getPreferenceStore();
	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		this.setSourceViewerConfiguration(new BeaverSourceViewerConfiguration(this.getPreferenceStore(), this));
		this.setDocumentProvider(BeaverPlugin.getDefault().getBeaverDocumentProvider());
	}

	@Override
	public String getContentType()
	{
		return IBeaverConstants.CONTENT_TYPE_BEAVER;
	}
}
