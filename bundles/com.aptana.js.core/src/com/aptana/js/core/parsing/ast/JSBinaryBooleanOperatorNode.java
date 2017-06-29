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

public class JSBinaryBooleanOperatorNode extends JSBinaryOperatorNode
{
	/**
	 * JSBooleanOperatorNode
	 * 
	 * @param operator
	 */
	public JSBinaryBooleanOperatorNode(Symbol operator)
	{
		super(operator);

		JSTokenType token = JSTokenType.get((String) operator.value);
		short type;

		switch (token)
		{
			// equality operators
			case EQUAL_EQUAL:
				type = IJSNodeTypes.EQUAL;
				break;

			case EXCLAMATION_EQUAL:
				type = IJSNodeTypes.NOT_EQUAL;
				break;

			case EQUAL_EQUAL_EQUAL:
				type = IJSNodeTypes.IDENTITY;
				break;

			case EXCLAMATION_EQUAL_EQUAL:
				type = IJSNodeTypes.NOT_IDENTITY;
				break;

			// relational operators
			case LESS:
				type = IJSNodeTypes.LESS_THAN;
				break;

			case GREATER:
				type = IJSNodeTypes.GREATER_THAN;
				break;

			case LESS_EQUAL:
				type = IJSNodeTypes.LESS_THAN_OR_EQUAL;
				break;

			case GREATER_EQUAL:
				type = IJSNodeTypes.GREATER_THAN_OR_EQUAL;
				break;

			case INSTANCEOF:
				type = IJSNodeTypes.INSTANCE_OF;
				break;

			case IN:
				type = IJSNodeTypes.IN;
				break;

			// logical operators
			case AMPERSAND_AMPERSAND:
				type = IJSNodeTypes.LOGICAL_AND;
				break;

			case PIPE_PIPE:
				type = IJSNodeTypes.LOGICAL_OR;
				break;

			default:
				throw new IllegalArgumentException(Messages.JSBinaryBooleanOperatorNode_0 + token);
		}

		this.setNodeType(type);
	}

	public JSBinaryBooleanOperatorNode(JSNode left, Symbol operator, JSNode right)
	{
		this(operator);
		this.setLocation(left.getStart(), right.getEnd());
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
}
