/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.sass;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.parsing.FileService;

@SuppressWarnings("restriction")
public class SassSourceEditor extends AbstractThemeableEditor
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

		setSourceViewerConfiguration(new SassSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new SassDocumentProvider());
	}

	public static IPreferenceStore getChainedPreferenceStore()
	{
		return new ChainedPreferenceStore(new IPreferenceStore[] { Activator.getDefault().getPreferenceStore(),
				CommonEditorPlugin.getDefault().getPreferenceStore(), EditorsPlugin.getDefault().getPreferenceStore() });
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#createFileService()
	 */
	@Override
	protected FileService createFileService()
	{
		return new FileService(ISassConstants.LANGUAGE);
	}
}
