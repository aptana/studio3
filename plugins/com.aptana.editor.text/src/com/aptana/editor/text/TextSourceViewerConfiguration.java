package com.aptana.editor.text;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonSourceViewerConfiguration;

public class TextSourceViewerConfiguration extends CommonSourceViewerConfiguration
{

	public TextSourceViewerConfiguration(IPreferenceStore preferenceStore, AbstractThemeableEditor editor)
	{
		super(preferenceStore, editor);
	}

	@Override
	public String[][] getTopContentTypes()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
