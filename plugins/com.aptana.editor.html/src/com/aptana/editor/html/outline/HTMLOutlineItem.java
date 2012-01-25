/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

public class HTMLOutlineItem extends CommonOutlineItem
{

	public HTMLOutlineItem(IRange sourceRange, IParseNode referenceNode)
	{
		super(sourceRange, referenceNode);
	}
}
