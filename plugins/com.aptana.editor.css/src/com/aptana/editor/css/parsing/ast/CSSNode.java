/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.parsing.ast.ParseNode;

public class CSSNode extends ParseNode
{

	private short fType;

	protected CSSNode(short type)
	{
		this(type, 0, 0);
	}

	public CSSNode(short type, int start, int end)
	{
		super(ICSSParserConstants.LANGUAGE);
		fType = type;
		this.start = start;
		this.end = end;
	}

	@Override
	public String getText()
	{
		return toString();
	}

	@Override
	public short getNodeType()
	{
		return fType;
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CSSNode) && super.equals(obj);
	}
}
