/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.outline;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CompositeOutlineLabelProvider;
import com.aptana.editor.css.outline.CSSOutlineLabelProvider;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.editor.svg.SVGPlugin;
import com.aptana.editor.xml.parsing.ast.XMLNode;

/**
 * SVGOutlineLabelProvider
 */
public class SVGOutlineLabelProvider extends CompositeOutlineLabelProvider
{
	private static final Image ELEMENT = SVGPlugin.getImage("icons/element.png"); //$NON-NLS-1$

	/**
	 * SVGOutlineLabelProvider
	 */
	public SVGOutlineLabelProvider()
	{
		this.addSubLanguage(IJSParserConstants.LANGUAGE, new JSOutlineLabelProvider());
		this.addSubLanguage(ICSSParserConstants.LANGUAGE, new CSSOutlineLabelProvider());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.outline.CompositeOutlineLabelProvider#getDefaultImage(java.lang.Object)
	 */
	@Override
	protected Image getDefaultImage(Object element)
	{
		if (element instanceof XMLNode)
		{
			return ELEMENT;
		}
		else
		{
			return super.getDefaultImage(element);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.outline.CompositeOutlineLabelProvider#getDefaultText(java.lang.Object)
	 */
	@Override
	protected String getDefaultText(Object element)
	{
		if (element instanceof XMLNode)
		{
			return ((XMLNode) element).getText();
		}
		else
		{
			return super.getDefaultText(element);
		}
	}
}
