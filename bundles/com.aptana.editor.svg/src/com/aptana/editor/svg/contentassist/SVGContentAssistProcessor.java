package com.aptana.editor.svg.contentassist;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.svg.contentassist.index.SVGIndexConstants;
import com.aptana.editor.xml.contentassist.XMLContentAssistProcessor;

public class SVGContentAssistProcessor extends XMLContentAssistProcessor
{
	/**
	 * SVGContentAssistProcessor
	 * 
	 * @param editor
	 */
	public SVGContentAssistProcessor(AbstractThemeableEditor editor)
	{
		super(editor);
	}

	/**
	 * getCoreLocation
	 * 
	 * @return
	 */
	protected String getCoreLocation()
	{
		return SVGIndexConstants.CORE;
	}
}
