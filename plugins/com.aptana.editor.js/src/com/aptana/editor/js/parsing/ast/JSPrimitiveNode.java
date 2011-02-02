/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing.ast;

public abstract class JSPrimitiveNode extends JSNode
{
	private String fText;

	/**
	 * JSPrimitiveNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 * @param text
	 */
	public JSPrimitiveNode(short type, String text)
	{
		super(type);

		fText = text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(obj instanceof JSPrimitiveNode))
		{
			return false;
		}

		return fText.equals(((JSPrimitiveNode) obj).fText);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseBaseNode#getText()
	 */
	@Override
	public String getText()
	{
		return fText;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return 31 * super.hashCode() + fText.hashCode();
	}
}
