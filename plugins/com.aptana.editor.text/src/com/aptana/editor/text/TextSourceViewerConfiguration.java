package com.aptana.editor.text;

import org.eclipse.jface.preference.IPreferenceStore;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.ICommonConstants;
import com.aptana.editor.common.QualifiedContentType;
import com.aptana.editor.common.tmp.ContentTypeTranslation;

public class TextSourceViewerConfiguration extends CommonSourceViewerConfiguration
{

	static
	{
		ContentTypeTranslation.getDefault().addTranslation(
				new QualifiedContentType(ICommonConstants.CONTENT_TYPE_UKNOWN), new QualifiedContentType("text")); //$NON-NLS-1$
	}

	public TextSourceViewerConfiguration(IPreferenceStore preferenceStore, AbstractThemeableEditor editor)
	{
		super(preferenceStore, editor);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.ITopContentTypesProvider#getTopContentTypes()
	 */
	public String[][] getTopContentTypes()
	{
		return new String[][] { { ICommonConstants.CONTENT_TYPE_UKNOWN } };
	}

}
