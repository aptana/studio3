/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.parsing.ast;

public class CoffeeAssignNode extends CoffeeNode
{

	private String compoundAssignmentText;

	public CoffeeAssignNode(CoffeeNode assignable, CoffeeNode expression)
	{
		this(assignable, expression, null);
	}

	/**
	 * @param coffeeValueNode
	 * @param expression
	 * @param compoundAssignmentText
	 *            might be the compound assignment text...
	 */
	public CoffeeAssignNode(CoffeeNode assignable, CoffeeNode expression, String string)
	{
		super(CoffeeNodeTypes.ASSIGN);
		addChild(assignable);
		if (expression != null)
		{
			addChild(expression);
		}
		this.compoundAssignmentText = string;
	}

	@Override
	public String getText()
	{
		return "Assign"; //$NON-NLS-1$
	}

	public boolean isObjectProperty()
	{
		return "object".equals(compoundAssignmentText); //$NON-NLS-1$
	}

	public boolean isFunctionDeclaration()
	{
		return getChildCount() > 1 && getChild(1) instanceof CoffeeCodeNode;
	}

}
