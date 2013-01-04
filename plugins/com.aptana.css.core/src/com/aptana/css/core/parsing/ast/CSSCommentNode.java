/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSCommentNode extends CSSNode
{
	private String fText;

	/**
	 * CSSCommentNode
	 * 
	 * @param text
	 * @param start
	 * @param end
	 */
	public CSSCommentNode(String text, int start, int end)
	{
		super(start, end);

		fText = text;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.COMMENT;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#accept(com.aptana.editor.css.parsing.ast.CSSTreeWalker)
	 */
	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#getText()
	 */
	@Override
	public String getText()
	{
		return fText;
	}
}
