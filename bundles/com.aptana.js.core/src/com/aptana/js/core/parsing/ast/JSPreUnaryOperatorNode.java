/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.parsing.ast.IParseNode;

import beaver.Symbol;

public class JSPreUnaryOperatorNode extends JSNode
{
	private Symbol _operator;

	/**
	 * Used by ANTLR AST
	 * 
	 * @param operator
	 */
	protected JSPreUnaryOperatorNode(short type)
	{
		this.setNodeType(type);
	}

	/**
	 * Used by ANTLR AST
	 * 
	 * @param start
	 * @param end
	 * @param operator
	 */
	public JSPreUnaryOperatorNode(int start, int end, Symbol operator)
	{
		super();
		this._operator = operator;
		this.setLocation(start, end);
		short type;
		JSTokenType token = JSTokenType.get((String) operator.value);

		switch (token)
		{
			case DELETE:
				type = IJSNodeTypes.DELETE;
				break;

			case EXCLAMATION:
				type = IJSNodeTypes.LOGICAL_NOT;
				break;

			case MINUS:
				type = IJSNodeTypes.NEGATIVE;
				break;

			case MINUS_MINUS:
				type = IJSNodeTypes.PRE_DECREMENT;
				break;

			case PLUS:
				type = IJSNodeTypes.POSITIVE;
				break;

			case PLUS_PLUS:
				type = IJSNodeTypes.PRE_INCREMENT;
				break;

			case TILDE:
				type = IJSNodeTypes.BITWISE_NOT;
				break;

			case TYPEOF:
				type = IJSNodeTypes.TYPEOF;
				break;

			case VOID:
				type = IJSNodeTypes.VOID;
				break;

			default:
				throw new IllegalArgumentException(Messages.JSPreUnaryOperatorNode_0 + token);
		}

		setNodeType(type);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.parsing.ast.JSNode#accept(com.aptana.editor.js.parsing.ast.JSTreeWalker)
	 */
	@Override
	public void accept(JSTreeWalker walker)
	{
		walker.visit(this);
	}

	/**
	 * getExpression
	 * 
	 * @return
	 */
	public IParseNode getExpression()
	{
		if (getChildCount() > 0)
		{
			return this.getChild(0);
		}
		return null;
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
}
