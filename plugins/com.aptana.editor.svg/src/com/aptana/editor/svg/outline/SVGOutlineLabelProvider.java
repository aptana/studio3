/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.outline;

import org.eclipse.swt.graphics.Image;

import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CompositeOutlineLabelProvider;
import com.aptana.editor.css.outline.CSSOutlineLabelProvider;
import com.aptana.editor.js.outline.JSOutlineLabelProvider;
import com.aptana.editor.svg.SVGPlugin;
import com.aptana.js.core.IJSConstants;
import com.aptana.xml.core.parsing.ast.XMLNode;

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
		this.addSubLanguage(IJSConstants.CONTENT_TYPE_JS, new JSOutlineLabelProvider());
		this.addSubLanguage(ICSSConstants.CONTENT_TYPE_CSS, new CSSOutlineLabelProvider());
	}

	@Override
	protected Image getDefaultImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getDefaultImage(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof XMLNode)
		{
			return ELEMENT;
		}
		return super.getDefaultImage(element);
	}

	@Override
	protected String getDefaultText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getDefaultText(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof XMLNode)
		{
			return ((XMLNode) element).getText();
		}
		return super.getDefaultText(element);
	}
}
