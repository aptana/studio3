package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;

public class CSSSelectorNode extends CSSNode
{

	public CSSSelectorNode(CSSRuleNode parent, CSSSimpleSelectorNode[] simpleSelectors, int start, int end)
	{
		super(start, end);
		setParent(parent);
		setChildren(simpleSelectors);
	}

	public CSSRuleNode getRule()
	{
		return (CSSRuleNode) getParent();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CSSSelectorNode))
		{
			return false;
		}
		CSSSelectorNode other = (CSSSelectorNode) obj;
		return Arrays.equals(getChildren(), other.getChildren());
	}

	@Override
	public int hashCode()
	{
		return Arrays.hashCode(getChildren());
	}
}
