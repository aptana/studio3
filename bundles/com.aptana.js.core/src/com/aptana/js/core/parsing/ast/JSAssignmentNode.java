/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import beaver.Symbol;

import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.parsing.ast.IParseNode;

public class JSAssignmentNode extends JSNode
{
	private Symbol _operator;

	/**
	 * JSAssignmentNode - used when we only have the operator and need to descend to add the left and right expressions as children later
	 * 
	 * @param assignOperator
	 */
	public JSAssignmentNode(Symbol assignOperator)
	{
		this._operator = assignOperator;

		short type = DEFAULT_TYPE;
		JSTokenType token = JSTokenType.get((String) assignOperator.value);

		switch (token)
		{
			case EQUAL:
				type = IJSNodeTypes.ASSIGN;
				break;
			case PLUS_EQUAL:
				type = IJSNodeTypes.ADD_AND_ASSIGN;
				break;
			case GREATER_GREATER_GREATER_EQUAL:
				type = IJSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN;
				break;
			case AMPERSAND_EQUAL:
				type = IJSNodeTypes.BITWISE_AND_AND_ASSIGN;
				break;
			case PIPE_EQUAL:
				type = IJSNodeTypes.BITWISE_OR_AND_ASSIGN;
				break;
			case CARET_EQUAL:
				type = IJSNodeTypes.BITWISE_XOR_AND_ASSIGN;
				break;
			case FORWARD_SLASH_EQUAL:
				type = IJSNodeTypes.DIVIDE_AND_ASSIGN;
				break;
			case PERCENT_EQUAL:
				type = IJSNodeTypes.MOD_AND_ASSIGN;
				break;
			case STAR_EQUAL:
				type = IJSNodeTypes.MULTIPLY_AND_ASSIGN;
				break;
			case LESS_LESS_EQUAL:
				type = IJSNodeTypes.SHIFT_LEFT_AND_ASSIGN;
				break;
			case GREATER_GREATER_EQUAL:
				type = IJSNodeTypes.SHIFT_RIGHT_AND_ASSIGN;
				break;
			case MINUS_EQUAL:
				type = IJSNodeTypes.SUBTRACT_AND_ASSIGN;
				break;
			case STAR_STAR_EQUAL:
				type = IJSNodeTypes.EXPONENT_AND_ASSIGN;
				break;
		}

		this.setNodeType(type);
	}
	
	public JSAssignmentNode(JSNode left, Symbol assignOperator, JSNode right)
	{
		this(assignOperator);
		this.setChildren(new JSNode[] { left, right });
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
