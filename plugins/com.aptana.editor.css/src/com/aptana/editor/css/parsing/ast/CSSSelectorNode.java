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
}
