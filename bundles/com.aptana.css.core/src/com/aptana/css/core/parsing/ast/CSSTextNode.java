/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSTextNode extends CSSNode
{
	private String fText;

	/**
	 * CSSTextNode
	 * 
	 * @param text
	 */
	public CSSTextNode(String text)
	{
		fText = text;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.TEXT;
	}

	@Override
	public void accept(CSSTreeWalker walker)
	{
		walker.visit(this);
	}

	@Override
	public boolean equals(Object obj)
	{
		return super.equals(obj) && (obj instanceof CSSTextNode) && fText.equals(((CSSTextNode) obj).fText);
	}

	@Override
	public String getText()
	{
		return fText;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + fText.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#toString()
	 */
	@Override
	public String toString()
	{
		return fText;
	}
}
