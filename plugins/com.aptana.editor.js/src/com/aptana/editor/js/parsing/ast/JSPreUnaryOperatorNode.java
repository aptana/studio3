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
		this.setChildren(new JSNode[] { expression });

		this.setNodeType(type);
	}

	/**
	 * JSUnaryOperatorNode
	 * 
	 * @param operator
	 * @param expression
	 */
	public JSPreUnaryOperatorNode(Symbol operator, JSNode expression)
	{
		this._operator = operator;
		this.setChildren(new JSNode[] { expression });

		short type;
		JSTokenType token = JSTokenType.get((String) operator.value);

		switch (token)
		{
			case DELETE:
				type = JSNodeTypes.DELETE;
				break;

			case EXCLAMATION:
				type = JSNodeTypes.LOGICAL_NOT;
				break;

			case MINUS:
				type = JSNodeTypes.NEGATIVE;
				break;

			case MINUS_MINUS:
				type = JSNodeTypes.PRE_DECREMENT;
				break;

			case PLUS:
				type = JSNodeTypes.POSITIVE;
				break;

			case PLUS_PLUS:
				type = JSNodeTypes.PRE_INCREMENT;
				break;

			case TILDE:
				type = JSNodeTypes.BITWISE_NOT;
				break;

			case TYPEOF:
				type = JSNodeTypes.TYPEOF;
				break;

			case VOID:
				type = JSNodeTypes.VOID;
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
