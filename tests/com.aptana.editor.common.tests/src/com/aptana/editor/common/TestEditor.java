package com.aptana.editor.common.tests;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonEditorPlugin;

public class TestEditor extends AbstractThemeableEditor
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#initializeEditor()
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
//		setSourceViewerConfiguration(new TextSourceViewerConfiguration(getPreferenceStore(), this));
//		setDocumentProvider(TextEditorPlugin.getDefault().getTextDocumentProvider());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#getPluginPreferenceStore()
	 */
	@Override
	protected IPreferenceStore getPluginPreferenceStore()
	{
		return CommonEditorPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getContentType()
	{
		return "com.aptana.editor.test";
	}
}
