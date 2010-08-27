package com.aptana.editor.diff;

import com.aptana.editor.common.AbstractThemeableEditor;

public class DiffEditor extends AbstractThemeableEditor
{

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();

		setSourceViewerConfiguration(new DiffSourceViewerConfiguration(getPreferenceStore(), this));
		setDocumentProvider(new DiffDocumentProvider());
	}

}
