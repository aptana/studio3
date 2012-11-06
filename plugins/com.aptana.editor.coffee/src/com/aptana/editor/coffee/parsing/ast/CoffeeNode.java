/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

import com.aptana.editor.coffee.ICoffeeConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class CoffeeNode extends ParseNode
{

	protected short fType;
	protected boolean negated;

	/**
	 * CoffeeNode
	 * 
	 * @param type
	 */
	protected CoffeeNode(short type)
	{
		this(type, 0, 0);
	}

	public CoffeeNode(short type, int start, int end)
	{
		super();
		this.fType = type;
		setLocation(start, end);
	}

	public String getLanguage()
	{
		return ICoffeeConstants.CONTENT_TYPE_COFFEE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CoffeeNode) && super.equals(obj);
	}

	/**
	 * @return
	 */
	public IParseNode getContainingStatementNode()
	{
		// move up to nearest statement
		IParseNode result = this;
		IParseNode parent = result.getParent();

		while (parent != null)
		{
			// TODO: need to test for specific "statement" node types
			if (parent instanceof ParseRootNode)
			{
				break;
			}
			else
			{
				result = parent;
				parent = parent.getParent();
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getNodeType()
	 */
	@Override
	public short getNodeType()
	{
		return fType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getText()
	 */
	@Override
	public String getText()
	{
		return getClass().getName();
	}

	@Override
	public int getEndingOffset()
	{
		int end = super.getEndingOffset();
		if (end == 0 && getChildCount() > 0)
		{
			end = getChild(getChildCount() - 1).getEndingOffset();
		}
		return end;
	}

	public CoffeeNode invert()
	{
		return new CoffeeOpNode("!", this); //$NON-NLS-1$
	}

	protected boolean isStatement()
	{
		return false;
	}

	protected boolean jumps()
	{
		return false;
	}

	protected boolean isComplex()
	{
		return true;
	}

	protected boolean isChainable()
	{
		return false;
	}

	protected boolean isAssignable()
	{
		return false;
	}

	protected boolean unfoldSoak()
	{
		return false;
	}

	protected boolean assigns()
	{
		return false;
	}

	protected CoffeeNode unwrap()
	{
		return this;
	}
}
