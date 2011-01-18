/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSCommentNode extends CSSNode
{

	private String fText;

	public CSSCommentNode(String text, int start, int end)
	{
		super(CSSNodeTypes.COMMENT, start, end);
		fText = text;
	}
	
	@Override
	public String getText()
	{
		return fText;
	}
}
