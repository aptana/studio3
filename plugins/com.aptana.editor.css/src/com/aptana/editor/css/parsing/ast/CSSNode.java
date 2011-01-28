/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing.ast;

import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class CSSNode extends ParseNode
{
	private short fType;

	/**
	 * CSSNode
	 * 
	 * @param type
	 */
	protected CSSNode(short type)
	{
		this(type, 0, 0);
	}

	/**
	 * CSSNode
	 * 
	 * @param type
	 * @param start
	 * @param end
	 */
	public CSSNode(short type, int start, int end)
	{
		super(ICSSParserConstants.LANGUAGE);
		
		fType = type;
		
		this.setLocation(start, end);
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
		return toString();
	}
}
