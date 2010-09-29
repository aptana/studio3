/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing.ast;

import beaver.Symbol;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

public class JSBinaryArithmeticOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSArithmeticOperatorNode
	 * 
	 * @param left
	 * @param operator
	 * @param right
	 */
	public JSBinaryArithmeticOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		super(left, operator, right);

		JSTokenType token = JSTokenType.get((String) operator.value);
		short type;

		switch (token)
		{
			// additive operators
			case PLUS:
				type = JSNodeTypes.ADD;
				break;

			case MINUS:
				type = JSNodeTypes.SUBTRACT;
				break;

			// shift operators
			case LESS_LESS:
				type = JSNodeTypes.SHIFT_LEFT;
				break;

			case GREATER_GREATER:
				type = JSNodeTypes.SHIFT_RIGHT;
				break;

			case GREATER_GREATER_GREATER:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT;
				break;

			// bit operator
			case AMPERSAND:
				type = JSNodeTypes.BITWISE_AND;
				break;

			case CARET:
				type = JSNodeTypes.BITWISE_XOR;
				break;

			case PIPE:
				type = JSNodeTypes.BITWISE_OR;
				break;

			// multiplicative operators
			case STAR:
				type = JSNodeTypes.MULTIPLY;
				break;

			case FORWARD_SLASH:
				type = JSNodeTypes.DIVIDE;
				break;

			case PERCENT:
				type = JSNodeTypes.MOD;
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
