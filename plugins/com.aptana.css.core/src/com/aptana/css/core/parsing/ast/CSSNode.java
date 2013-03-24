/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing.ast;

import com.aptana.css.core.ICSSConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public abstract class CSSNode extends ParseNode
{

	/**
	 * CSSNode
	 */
	protected CSSNode()
	{
		this(0, 0);
	}

	/**
	 * CSSNode
	 * 
	 * @param start
	 * @param end
	 */
	public CSSNode(int start, int end)
	{
		this.setLocation(start, end);
	}

	public String getLanguage()
	{
		return ICSSConstants.CONTENT_TYPE_CSS;
	}

	/**
	 * CSSTreeWalker
	 * 
	 * @param walker
	 */
	public void accept(CSSTreeWalker walker)
	{
		// sub-classes must override this method so their types will be
		// recognized properly
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof CSSNode) && super.equals(obj);
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
	public abstract short getNodeType();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.ast.ParseNode#getText()
	 */
	@Override
	public String getText()
	{
		return toString();
	}
}
