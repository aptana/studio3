/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing.ast;

import com.aptana.js.core.parsing.JSTokenType;

import beaver.Symbol;

public class JSBinaryArithmeticOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSArithmeticOperatorNode
	 * 
	 * @param operator
	 */
	public JSBinaryArithmeticOperatorNode(int start, int end, Symbol operator)
	{
		super(start, end, operator);

		JSTokenType token = JSTokenType.get((String) operator.value);
		short type;

		switch (token)
		{
			// additive operators
			case PLUS:
				type = IJSNodeTypes.ADD;
				break;

			case MINUS:
				type = IJSNodeTypes.SUBTRACT;
				break;

			// shift operators
			case LESS_LESS:
				type = IJSNodeTypes.SHIFT_LEFT;
				break;

			case GREATER_GREATER:
				type = IJSNodeTypes.SHIFT_RIGHT;
				break;

			case GREATER_GREATER_GREATER:
				type = IJSNodeTypes.ARITHMETIC_SHIFT_RIGHT;
				break;

			// bit operator
			case AMPERSAND:
				type = IJSNodeTypes.BITWISE_AND;
				break;

			case CARET:
				type = IJSNodeTypes.BITWISE_XOR;
				break;

			case PIPE:
				type = IJSNodeTypes.BITWISE_OR;
				break;

			// multiplicative operators
			case STAR:
				type = IJSNodeTypes.MULTIPLY;
				break;

			case STAR_STAR:
				type = IJSNodeTypes.EXPONENT;
				break;

			case FORWARD_SLASH:
				type = IJSNodeTypes.DIVIDE;
				break;

			case PERCENT:
				type = IJSNodeTypes.MOD;
				break;

			default:
				throw new IllegalArgumentException(Messages.JSBinaryArithmeticOperatorNode_0 + token);
		}

		this.setNodeType(type);
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
}
