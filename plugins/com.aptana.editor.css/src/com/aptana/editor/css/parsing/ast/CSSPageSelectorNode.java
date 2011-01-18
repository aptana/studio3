/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

public class CSSPageSelectorNode extends CSSNode
{

	private String fText;

	public CSSPageSelectorNode(String text, int start, int end)
	{
		super(CSSNodeTypes.PAGE_SELECTOR, start, end);
		fText = text;
	}

	public String getText()
	{
		return fText;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSPageSelectorNode))
		{
			return false;
		}
		return fText.equals(((CSSPageSelectorNode) obj).fText);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + fText.hashCode();
	}

	@Override
	public String toString()
	{
		return getText();
	}
}
