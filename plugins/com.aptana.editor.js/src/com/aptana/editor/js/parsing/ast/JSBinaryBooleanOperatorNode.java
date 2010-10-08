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

public class JSBinaryBooleanOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSBooleanOperatorNode
	 * 
	 * @param left
	 * @param operator
	 * @param right
	 */
	public JSBinaryBooleanOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		super(left, operator, right);

		JSTokenType token = JSTokenType.get((String) operator.value);
		short type;

		switch (token)
		{
			// equality operators
			case EQUAL_EQUAL:
				type = JSNodeTypes.EQUAL;
				break;

			case EXCLAMATION_EQUAL:
				type = JSNodeTypes.NOT_EQUAL;
				break;

			case EQUAL_EQUAL_EQUAL:
				type = JSNodeTypes.IDENTITY;
				break;

			case EXCLAMATION_EQUAL_EQUAL:
				type = JSNodeTypes.NOT_IDENTITY;
				break;

			// relational operators
			case LESS:
				type = JSNodeTypes.LESS_THAN;
				break;

			case GREATER:
				type = JSNodeTypes.GREATER_THAN;
				break;

			case LESS_EQUAL:
				type = JSNodeTypes.LESS_THAN_OR_EQUAL;
				break;

			case GREATER_EQUAL:
				type = JSNodeTypes.GREATER_THAN_OR_EQUAL;
				break;

			case INSTANCEOF:
				type = JSNodeTypes.INSTANCE_OF;
				break;

			case IN:
				type = JSNodeTypes.IN;
				break;

			// logical operators
			case AMPERSAND_AMPERSAND:
				type = JSNodeTypes.LOGICAL_AND;
				break;

			case PIPE_PIPE:
				type = JSNodeTypes.LOGICAL_OR;
				break;

			default:
				throw new IllegalArgumentException(Messages.JSBinaryBooleanOperatorNode_0 + token);
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
