package com.aptana.editor.css.outline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
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
			CSSRuleNode rule = ((CSSSelectorNode) parentElement).getParent();
			return rule.getDeclarations();
		}
		return super.getChildren(parentElement);
	}

	@Override
	protected IParseNode[] filter(IParseNode[] nodes)
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
