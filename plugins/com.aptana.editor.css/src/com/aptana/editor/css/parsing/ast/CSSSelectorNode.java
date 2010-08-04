package com.aptana.editor.css.parsing.ast;

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
		if (!super.equals(obj) || !(obj instanceof CSSSelectorNode))
		{
			return false;
		}
		CSSSelectorNode other = (CSSSelectorNode) obj;
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}
}
