/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.outline;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aptana.editor.coffee.parsing.ast.CoffeeAssignNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeBlockNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeClassNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeCodeNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeIfNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeObjNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeOpNode;
import com.aptana.editor.coffee.parsing.ast.CoffeeValueNode;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.common.outline.CommonOutlinePageInput;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class CoffeeOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof CommonOutlinePageInput)
		{
			parentElement = ((CommonOutlinePageInput) parentElement).ast;
		}
		if (parentElement instanceof AbstractThemeableEditor)
		{
			IParseNode rootNode = ((AbstractThemeableEditor) parentElement).getAST();
			parentElement = rootNode;
		}
		if (parentElement instanceof IParseRootNode)
		{
			IParseNode rootNode = (IParseRootNode) parentElement;
			if (rootNode.hasChildren())
			{
				if (rootNode.hasChildren())
				{
					// Flatten toplevel block that is child of root!
					return filter(rootNode.getChild(0).getChildren());
				}
				else
				{
					return EMPTY;
				}
			}
			return EMPTY;
		}
		if (parentElement instanceof CoffeeValueNode)
		{
			IParseNode firstChild = ((CoffeeValueNode) parentElement).getChild(0);
			if (firstChild instanceof CoffeeObjNode)
			{
				getChildren(firstChild);
			}
			return EMPTY;
		}

		// Traverse into CodeNode and objects on right hand side of assignment
		if (parentElement instanceof CoffeeAssignNode)
		{
			CoffeeAssignNode node = (CoffeeAssignNode) parentElement;
			if (node.getChildCount() > 1)
			{
				IParseNode expression = node.getChild(1);
				// if (expression instanceof CoffeeCodeNode)
				// {
				// // expand out the function
				return getChildren(expression);
				// }
				// else if (expression instanceof CoffeeValueNode && expression.getChild(0) instanceof CoffeeObjNode)
				// {
				// // Expand out the assignment list
				// return getChildren(expression.getChild(0));
				// }
			}
			return EMPTY;
		}
		// Expand children of function's body block
		if (parentElement instanceof CoffeeCodeNode)
		{
			CoffeeCodeNode node = (CoffeeCodeNode) parentElement;
			return getChildren(node.getBlock());
		}
		// Expand object inside class
		if (parentElement instanceof CoffeeClassNode)
		{
			CoffeeClassNode node = (CoffeeClassNode) parentElement;
			CoffeeBlockNode block = node.getBlock();
			if (block.getChildCount() == 1 && block.getChild(0) instanceof CoffeeValueNode)
			{
				CoffeeValueNode value = (CoffeeValueNode) block.getChild(0);
				if (value.getChildCount() == 1 && value.getChild(0) instanceof CoffeeObjNode)
				{
					return getChildren(value.getChild(0));
				}
			}
			return getChildren(block);
		}
		return super.getChildren(parentElement);
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<CoffeeNode> list = new ArrayList<CoffeeNode>();
		for (IParseNode node : nodes)
		{
			if (!(node instanceof CoffeeNode))
			{
				continue;
			}
			CoffeeNode element = (CoffeeNode) node;
			// skip If nodes
			// FIXME Flatten ifs to include their body?
			if (element instanceof CoffeeIfNode || element instanceof CoffeeOpNode)
			{
				continue;
			}
			// TODO Filter out block elements, but retain their children!
			list.add(element);
		}
		// Sort within this level of the hierarchy
		Collections.sort(list, new Comparator<CoffeeNode>()
		{

			public int compare(CoffeeNode o1, CoffeeNode o2)
			{
				return sortPriority(o1) - sortPriority(o2);
			}

			private int sortPriority(CoffeeNode element)
			{
				switch (element.getNodeType())
				{
				// case CoffeeNodeTypes.SCRIPT:
				// return -2;
				// case CoffeeNodeTypes.GLOBAL:
				// return -1;
				// case CoffeeNodeTypes.IMPORT_CONTAINER:
				// return 0;
				// case CoffeeNodeTypes.IMPORT_DECLARATION:
				// return 1;
				// case CoffeeNodeTypes.TYPE:
				// return 2;
				// case CoffeeNodeTypes.CONSTANT:
				// return 3;
				// case CoffeeNodeTypes.CLASS_VAR:
				// return 4;
				// case CoffeeNodeTypes.INSTANCE_VAR:
				// case CoffeeNodeTypes.FIELD:
				// return 5;
				// case CoffeeNodeTypes.METHOD:
				// IRubyMethod method = (IRubyMethod) element;
				// if (method.isSingleton())
				// {
				// return 6;
				// }
				// if (method.isConstructor())
				// {
				// return 7;
				// }
				// return 8;
				// case CoffeeNodeTypes.LOCAL_VAR:
				// return 9;
				// case CoffeeNodeTypes.BLOCK:
				// case CoffeeNodeTypes.DYNAMIC_VAR:
				// return 10;
					default:
						return 5;
				}
			}
		});

		// Turn into outline items
		List<CommonOutlineItem> outlineItems = new ArrayList<CommonOutlineItem>(list.size());
		for (CoffeeNode element : list)
		{
			outlineItems.add(getOutlineItem(element));
		}

		return outlineItems.toArray(new CommonOutlineItem[outlineItems.size()]);
	}
}
