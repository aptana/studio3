/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.outline;

import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;

/**
 *	SVGOutlineContentProvider
 */
public class SVGOutlineContentProvider extends CompositeOutlineContentProvider
{
	/**
	 * SVGOutlineContentProvider
	 */
	public SVGOutlineContentProvider()
	{
		this.addSubLanguage(IJSParserConstants.LANGUAGE, new JSOutlineContentProvider());
		this.addSubLanguage(ICSSParserConstants.LANGUAGE, new CSSOutlineContentProvider());
	}
}
