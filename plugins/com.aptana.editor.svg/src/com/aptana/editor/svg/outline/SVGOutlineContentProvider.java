/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.svg.outline;

import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.outline.JSOutlineContentProvider;

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
		this.addSubLanguage(IJSConstants.CONTENT_TYPE_JS, new JSOutlineContentProvider());
		this.addSubLanguage(ICSSConstants.CONTENT_TYPE_CSS, new CSSOutlineContentProvider());
	}
}
