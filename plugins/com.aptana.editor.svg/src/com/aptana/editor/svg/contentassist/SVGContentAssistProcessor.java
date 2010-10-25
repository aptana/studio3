package com.aptana.editor.svg.contentassist;

import java.util.HashMap;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.svg.SVGSourceConfiguration;
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

	/* (non-Javadoc)
	 * @see com.aptana.editor.xml.contentassist.XMLContentAssistProcessor#buildLocationMap()
	 */
	@Override
	protected void buildLocationMap()
	{
		locationMap = new HashMap<String, LocationType>();
		locationMap.put(SVGSourceConfiguration.DEFAULT, LocationType.IN_TEXT);
		locationMap.put(SVGSourceConfiguration.COMMENT, LocationType.IN_COMMENT);
		locationMap.put(SVGSourceConfiguration.CDATA, LocationType.IN_TEXT);
		locationMap.put(SVGSourceConfiguration.PRE_PROCESSOR, LocationType.IN_TEXT);
		locationMap.put(SVGSourceConfiguration.DOCTYPE, LocationType.IN_DOCTYPE);
		locationMap.put(SVGSourceConfiguration.TAG, LocationType.IN_OPEN_TAG);

		locationMap.put(IDocument.DEFAULT_CONTENT_TYPE, LocationType.IN_TEXT);
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
