package com.aptana.editor.yaml;

import com.aptana.editor.common.AbstractThemeableEditor;

public class YAMLEditor extends AbstractThemeableEditor
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.AbstractThemeableEditor#initializeEditor()
	 */
	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setSourceViewerConfiguration(new YAMLSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new YAMLDocumentProvider());
	}

}
