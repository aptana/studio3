/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

public class CSSPageSelectorNode extends CSSNode
{
	private String fText;

	/**
	 * CSSPageSelectorNode
	 * 
	 * @param identifier
	 */
	public CSSPageSelectorNode(String identifier)
	{
		fText = identifier;
	}

	@Override
	public short getNodeType()
	{
		return ICSSNodeTypes.PAGE_SELECTOR;
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
	 * @see com.aptana.editor.css.parsing.ast.CSSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSPageSelectorNode))
		{
			return false;
		}
		return fText.equals(((CSSPageSelectorNode) obj).fText);
	}

	/**
	 * getText
	 */
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
		return getText();
	}
}
