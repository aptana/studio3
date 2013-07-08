/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.outline;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.coffee.CoffeeScriptEditorPlugin;
import com.aptana.editor.coffee.ICoffeeUIConstants;
import com.aptana.editor.coffee.parsing.ast.CoffeeAccessNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeArrNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeAssignNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeClassNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeCodeNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeLiteralNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeObjNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeParamNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeValueNode;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CommonOutlineLabelProvider;
import com.aptana.parsing.ast.IParseNode;

public class CoffeeOutlineLabelProvider extends CommonOutlineLabelProvider
{
	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getImage(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof CoffeeCodeNode)
		{
			return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.FUNCTION_ICON);
		}
		if (element instanceof CoffeeObjNode)
		{
			return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.OBJECT_ICON);
		}
		if (element instanceof CoffeeClassNode)
		{
			return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.CLASS_ICON);
		}
		if (element instanceof CoffeeArrNode)
		{
			return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.ARRAY_ICON);
		}
		if (element instanceof CoffeeAssignNode)
		{

			CoffeeAssignNode assign = (CoffeeAssignNode) element;
			if (assign.isFunctionDeclaration())
			{
				return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.FUNCTION_ICON);
			}
			if (assign.isObjectProperty())
			{
				return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.PROPERTY_ICON);
			}

			// If the lefthand side is a valuenode starting with "@", use instance var image!
			IParseNode assignable = assign.getChild(0);
			// if assignable is a value with two children, first being "this", then it's an instance variable
			if (assignable.getChildCount() == 2)
			{
				String text = getText(assignable.getChild(0));
				if (text.equals("this")) //$NON-NLS-1$
				{
					return CoffeeScriptEditorPlugin.getDefault().getImageRegistry()
							.get(ICoffeeUIConstants.PROPERTY_ICON);
				}
			}
			// Pass through to show the image associated with the righthand (value) of the assignment
			return getImage(assign.getChild(1));
		}
		if (element instanceof CoffeeValueNode)
		{
			// Pass through to grab image of the literal we're holding onto
			return getImage(((CoffeeValueNode) element).getChild(0));
		}
		if (element instanceof CoffeeLiteralNode)
		{
			// Come up with the "type" of literal this is
			CoffeeLiteralNode literal = (CoffeeLiteralNode) element;
			String literalValue = literal.getText();
			if (literalValue != null && (literalValue.startsWith("\"") || literalValue.startsWith("'"))) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.STRING_ICON);
			}
			if ("null".equals(literalValue) || "undefined".equals(literalValue)) //$NON-NLS-1$ //$NON-NLS-2$
			{
				return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.NULL_ICON);
			}

			if ("true".equals(literalValue) || "false".equals(literalValue) || "yes".equals(literalValue) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					|| "on".equals(literalValue) || "off".equals(literalValue) || "no".equals(literalValue)) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			{
				return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.BOOLEAN_ICON);
			}
			boolean isNumber = false;
			try
			{
				Float.parseFloat(literalValue);
				isNumber = true;
			}
			catch (Exception e)
			{
				// ignore
			}
			if (isNumber)
			{
				return CoffeeScriptEditorPlugin.getDefault().getImageRegistry().get(ICoffeeUIConstants.NUMBER_ICON);
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			return getText(((CommonOutlineItem) element).getReferenceNode());
		}
		if (element instanceof CoffeeClassNode)
		{
			CoffeeClassNode node = (CoffeeClassNode) element;
			return node.determineName();
		}
		if (element instanceof CoffeeAccessNode)
		{
			// Spit out the variable name as the label
			CoffeeAccessNode node = (CoffeeAccessNode) element;
			return getText(node.getChild(0));
		}
		if (element instanceof CoffeeAssignNode)
		{
			// Spit out the assignable as the label
			CoffeeAssignNode node = (CoffeeAssignNode) element;
			IParseNode assignable = node.getChild(0);
			String text = getText(assignable);
			if ("this".equals(text)) //$NON-NLS-1$
			{
				// instance variable, print "@" and then name of var
				text = "@" + getText(assignable.getChild(1)); //$NON-NLS-1$
			}
			else if (node.getChildCount() > 1)
			{
				IParseNode expression = node.getChild(1);
				if (expression instanceof CoffeeCodeNode)
				{ // print out rest of function signature
					text += getText(expression);
				}
			}
			return text;
		}
		if (element instanceof CoffeeValueNode)
		{
			// For value nodes, show the literal's text
			CoffeeValueNode node = (CoffeeValueNode) element;
			return getText(node.getChild(0));
		}
		if (element instanceof CoffeeCodeNode)
		{
			// For functions nodes function name is lefthand of the parent AssignNode, we only get here to piece it
			// together, so...
			// parens and any params
			CoffeeCodeNode node = (CoffeeCodeNode) element;
			StringBuilder builder = new StringBuilder("("); //$NON-NLS-1$
			// Go through params. Last child is function body...
			for (int i = 0; i < node.getChildCount() - 1; i++)
			{
				builder.append(getText(node.getChild(i))); // recurse into params
				if (i != node.getChildCount() - 2)
				{
					builder.append(", "); //$NON-NLS-1$
				}
			}
			builder.append(")"); //$NON-NLS-1$
			return builder.toString();
		}
		if (element instanceof CoffeeParamNode)
		{
			// Use var name for parameter text
			// TODO Also show if it has assignments/defaults/splat.
			CoffeeParamNode param = (CoffeeParamNode) element;
			return getText(param.getChild(0));
		}
		if (element instanceof CoffeeNode)
		{
			CoffeeNode node = (CoffeeNode) element;
			return node.getText();
		}
		return super.getText(element);
	}
}
