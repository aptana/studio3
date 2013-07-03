/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.internal.text;

import org.eclipse.jface.text.IDocument;

import com.aptana.editor.coffee.parsing.ast.CoffeeArrNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeAssignNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeClassNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeCodeNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeIfNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeObjNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeValueNode;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.text.AbstractFoldingComputer;
import com.aptana.parsing.ast.IParseNode;

public class CoffeeFoldingComputer extends AbstractFoldingComputer
{

	public CoffeeFoldingComputer(AbstractThemeableEditor editor, IDocument document)
	{
		super(editor, document);
	}

	@Override
	public boolean isFoldable(IParseNode child)
	{
		if (child instanceof CoffeeAssignNode)
		{
			CoffeeAssignNode assign = (CoffeeAssignNode) child;
			IParseNode expression = assign.getChild(1);
			if (!(expression instanceof CoffeeValueNode))
			{
				return false;
			}
			IParseNode expressionFirstChild = expression.getChild(0);
			return expressionFirstChild instanceof CoffeeObjNode || expressionFirstChild instanceof CoffeeArrNode;
		}

		return (child instanceof CoffeeClassNode || child instanceof CoffeeCodeNode || child instanceof CoffeeIfNode);
	}

}
