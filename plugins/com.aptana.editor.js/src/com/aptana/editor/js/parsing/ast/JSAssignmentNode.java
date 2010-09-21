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
import com.aptana.parsing.ast.IParseNode;

public class JSAssignmentNode extends JSNode
{
	private Symbol _operator;

	/**
	 * JSAssignmentNode
	 * 
	 * @param left
	 * @param assignOperator
	 * @param right
	 */
	public JSAssignmentNode(JSNode left, Symbol assignOperator, JSNode right)
	{
		this._operator = assignOperator;

		short type = DEFAULT_TYPE;
		JSTokenType token = JSTokenType.get((String) assignOperator.value);

		switch (token)
		{
			case EQUAL:
				type = JSNodeTypes.ASSIGN;
				break;
			case PLUS_EQUAL:
				type = JSNodeTypes.ADD_AND_ASSIGN;
				break;
			case GREATER_GREATER_GREATER_EQUAL:
				type = JSNodeTypes.ARITHMETIC_SHIFT_RIGHT_AND_ASSIGN;
				break;
			case AMPERSAND_EQUAL:
				type = JSNodeTypes.BITWISE_AND_AND_ASSIGN;
				break;
			case PIPE_EQUAL:
				type = JSNodeTypes.BITWISE_OR_AND_ASSIGN;
				break;
			case CARET_EQUAL:
				type = JSNodeTypes.BITWISE_XOR_AND_ASSIGN;
				break;
			case FORWARD_SLASH_EQUAL:
				type = JSNodeTypes.DIVIDE_AND_ASSIGN;
				break;
			case PERCENT_EQUAL:
				type = JSNodeTypes.MOD_AND_ASSIGN;
				break;
			case STAR_EQUAL:
				type = JSNodeTypes.MULTIPLY_AND_ASSIGN;
				break;
			case LESS_LESS_EQUAL:
				type = JSNodeTypes.SHIFT_LEFT_AND_ASSIGN;
				break;
			case GREATER_GREATER_EQUAL:
				type = JSNodeTypes.SHIFT_RIGHT_AND_ASSIGN;
				break;
			case MINUS_EQUAL:
				type = JSNodeTypes.SUBTRACT_AND_ASSIGN;
				break;
		}

		this.setNodeType(type);
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
