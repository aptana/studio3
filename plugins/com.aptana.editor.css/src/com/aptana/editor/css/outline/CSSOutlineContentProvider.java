/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.css.core.parsing.ast.CSSCommentNode;
import com.aptana.css.core.parsing.ast.CSSDeclarationNode;
import com.aptana.css.core.parsing.ast.CSSFunctionNode;
import com.aptana.css.core.parsing.ast.CSSRuleNode;
import com.aptana.css.core.parsing.ast.CSSSelectorNode;
import com.aptana.css.core.parsing.ast.CSSTermListNode;
import com.aptana.css.core.parsing.ast.CSSTermNode;
import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.parsing.ast.IParseNode;

public class CSSOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof CSSSelectorNode)
		{
			CSSRuleNode rule = ((CSSSelectorNode) parentElement).getRule();
			return filter(rule.getDeclarations());
		}
		return super.getChildren(parentElement);
	}

	public static Object getElementAt(IParseNode root, int offset)
	{
		if (root == null)
		{
			return null;
		}
		IParseNode[] children = root.getChildren();
		CSSRuleNode rule;
		CSSSelectorNode[] selectors;
		for (IParseNode child : children)
		{
			if (child instanceof CSSRuleNode)
			{
				rule = (CSSRuleNode) child;
				if (offset <= rule.getEnd())
				{
					// the offset is in the current rule
					selectors = rule.getSelectors();
					// should always be the case, but checking nonetheless
					if (selectors.length > 0)
					{
						if (offset <= selectors[selectors.length - 1].getEnd())
						{
							// the offset is in the selectors
							for (CSSSelectorNode selector : selectors)
							{
								if (offset <= selector.getEnd())
								{
									return getItem(selector);
								}
							}
						}
						else
						{
							// the offset is in the declarations
							CSSDeclarationNode[] declarations = rule.getDeclarations();
							for (CSSDeclarationNode declaration : declarations)
							{
								if (offset <= declaration.getEnd())
								{
									return getItem(declaration);
								}
							}
						}
						// by default returns the last selector
						return getItem(selectors[selectors.length - 1]);
					}
				}
			}
		}
		return null;
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<CommonOutlineItem> items = new ArrayList<CommonOutlineItem>();
		for (IParseNode node : nodes)
		{
			if (node instanceof CSSRuleNode)
			{
				// displays the selectors of each rule at the top level
				CSSSelectorNode[] selectors = ((CSSRuleNode) node).getSelectors();
				for (CSSSelectorNode selector : selectors)
				{
					items.add(getOutlineItem(selector));
				}
			}
			else if (!(node instanceof CSSCommentNode) && !(node instanceof CSSTermNode)
					&& !(node instanceof CSSTermListNode) && !(node instanceof CSSFunctionNode))
			{
				items.add(getOutlineItem(node));
			}
		}
		return items.toArray(new CommonOutlineItem[items.size()]);
	}

	private static CommonOutlineItem getItem(IParseNode node)
	{
		return new CommonOutlineItem(node.getNameNode().getNameRange(), node);
	}
}
