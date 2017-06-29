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
	 * JSUnaryOperatorNode
	 * 
	 * @param type
	 * @param expression
	 */
	protected JSPreUnaryOperatorNode(short type, JSNode expression)
	{
		this(type);
		this.setChildren(new JSNode[] { expression });
	}

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
	 * @param operator
	 */
	public JSPreUnaryOperatorNode(Symbol operator)
	{
		super();
		this._operator = operator;
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

	/**
	 * JSUnaryOperatorNode
	 * 
	 * @param operator
	 * @param expression
	 */
	public JSPreUnaryOperatorNode(Symbol operator, JSNode expression)
	{
		this(operator);
		this.setChildren(new JSNode[] { expression });
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
}
