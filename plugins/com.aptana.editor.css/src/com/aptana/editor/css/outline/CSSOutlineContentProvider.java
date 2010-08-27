package com.aptana.editor.css.outline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.css.parsing.ast.CSSDeclarationNode;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.editor.css.parsing.ast.CSSSelectorNode;
import com.aptana.parsing.ast.IParseNode;

public class CSSOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof CSSSelectorNode)
		{
			CSSRuleNode rule = ((CSSSelectorNode) parentElement).getRule();
			return rule.getDeclarations();
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
									return selector;
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
									return declaration;
								}
							}
						}
						// by default returns the last selector
						return selectors[selectors.length - 1];
					}
				}
			}
		}
		return null;
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		// displays only the rules
		List<CSSSelectorNode> selectors = new ArrayList<CSSSelectorNode>();
		CSSRuleNode rule;
		for (IParseNode node : nodes)
		{
			if (node instanceof CSSRuleNode)
			{
				rule = (CSSRuleNode) node;
				selectors.addAll(Arrays.asList(rule.getSelectors()));
			}
		}
		return selectors.toArray(new CSSSelectorNode[selectors.size()]);
	}
}
