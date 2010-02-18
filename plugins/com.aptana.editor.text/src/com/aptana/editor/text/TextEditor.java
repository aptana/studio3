package com.aptana.editor.text;

import com.aptana.editor.common.AbstractThemeableEditor;

public class TextEditor extends AbstractThemeableEditor
{

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setSourceViewerConfiguration(new TextSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new TextDocumentProvider());
	}

}
