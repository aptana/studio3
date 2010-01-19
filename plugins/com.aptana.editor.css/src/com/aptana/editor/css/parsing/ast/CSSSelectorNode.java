package com.aptana.editor.css.parsing.ast;

public class CSSSelectorNode extends CSSNode
{

	private CSSSimpleSelectorNode[] fSimpleSelectors;

	public CSSSelectorNode(CSSRuleNode parent, CSSSimpleSelectorNode[] simpleSelectors, int start, int end)
	{
		setParent(parent);
		fSimpleSelectors = simpleSelectors;
		this.start = start;
		this.end = end;
	}

	public CSSRuleNode getRule()
	{
		return (CSSRuleNode) getParent();
	}

	public CSSSimpleSelectorNode[] getSimpleSelectors()
	{
		return fSimpleSelectors;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < fSimpleSelectors.length; ++i)
		{
			text.append(fSimpleSelectors[i]);
			if (i < fSimpleSelectors.length - 1)
			{
				text.append(" "); //$NON-NLS-1$
			}
		}
		return text.toString();
	}
}
