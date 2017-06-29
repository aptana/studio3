/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public abstract class JSBinaryOperatorNode extends JSNode
{
	private Symbol _operator;

	
	/**
	 * Used when building ANTLR AST. Children get added later!
	 * @param operator
	 */
	protected JSBinaryOperatorNode(Symbol operator)
	{
		this._operator = operator;
	}

	/**
	 * JSBinaryOperatorNode
	 * 
	 * @param left
	 * @param operator
	 * @param right
	 */
	protected JSBinaryOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		this(operator);

		this.setLocation(left.getStart(), right.getEnd());
		this.setChildren(new JSNode[] { left, right });
	}

	/**
	 * getLeftHandSide
	 * 
	 * @return
	 */
	public IParseNode getLeftHandSide()
	{
		return this.getChild(0);
	}

	/**
	 * getOperator
	 * 
	 * @return
	 */
	public Symbol getOperator()
	{
		return this._operator;
	}

	/**
	 * getRightHandSide
	 * 
	 * @return
	 */
	public IParseNode getRightHandSide()
	{
		return this.getChild(1);
	}
}
