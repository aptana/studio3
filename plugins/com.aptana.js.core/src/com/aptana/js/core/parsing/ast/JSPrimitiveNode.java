/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.core.util.ObjectUtil;
import com.aptana.parsing.ast.IParseNodeAttribute;
import com.aptana.parsing.ast.ParseNodeAttribute;

abstract class JSPrimitiveNode extends JSNode
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
	protected JSPrimitiveNode(short type, String text)
	{
		super(type);

		fText = text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getAttributes()
	 */
	@Override
	public IParseNodeAttribute[] getAttributes()
	{
		return new IParseNodeAttribute[] { new ParseNodeAttribute(this, "value", getText()) }; //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof JSPrimitiveNode))
		{
			return false;
		}
		return ObjectUtil.areEqual(getText(), ((JSPrimitiveNode) obj).getText());
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
		String text = getText();
		return 31 * super.hashCode() + ((text == null) ? 0 : text.hashCode());
	}
}