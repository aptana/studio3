package com.aptana.editor.css.parsing.ast;

import java.util.List;

import beaver.Symbol;

public class CSSRuleNode extends CSSNode
{

	private CSSSelectorNode[] fSelectors;
	private CSSDeclarationNode[] fDeclarations;

	public CSSRuleNode(Symbol[] selectors, int end)
	{
		this(selectors, null, end);
	}

	@SuppressWarnings("unchecked")
	public CSSRuleNode(Symbol[] selectors, Object declarations, int end)
	{
		fSelectors = new CSSSelectorNode[selectors.length];
		List<CSSSimpleSelectorNode> simpleSelectors;
		for (int i = 0; i < selectors.length; ++i)
		{
			simpleSelectors = (List<CSSSimpleSelectorNode>) selectors[i].value;
			fSelectors[i] = new CSSSelectorNode(this, simpleSelectors.toArray(new CSSSimpleSelectorNode[simpleSelectors
					.size()]), selectors[i].getStart(), selectors[i].getEnd());
		}
		if (selectors.length > 0)
		{
			this.start = selectors[0].getStart();
		}

		if (declarations instanceof CSSDeclarationNode)
		{
			fDeclarations = new CSSDeclarationNode[1];
			fDeclarations[0] = (CSSDeclarationNode) declarations;
		}
		else if (declarations instanceof List<?>)
		{
			List<CSSDeclarationNode> list = (List<CSSDeclarationNode>) declarations;
			int size = list.size();
			fDeclarations = new CSSDeclarationNode[size];
			for (int i = 0; i < size; ++i)
			{
				fDeclarations[i] = list.get(i);
			}
		}
		else
		{
			fDeclarations = new CSSDeclarationNode[0];
		}
		if (fSelectors.length > 0)
		{
			for (CSSDeclarationNode declaration : fDeclarations)
			{
				declaration.setParent(fSelectors[0]);
			}
		}
		this.end = end;
	}

	public CSSSelectorNode[] getSelectors()
	{
		return fSelectors;
	}

	public CSSDeclarationNode[] getDeclarations()
	{
		return fDeclarations;
	}

	@Override
	public void addOffset(int offset)
	{
		super.addOffset(offset);
		for (CSSSelectorNode node : fSelectors)
		{
			node.addOffset(offset);
		}
		for (CSSDeclarationNode node : fDeclarations)
		{
			node.addOffset(offset);
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!super.equals(obj) || !(obj instanceof CSSRuleNode))
		{
			return false;
		}
		CSSRuleNode other = (CSSRuleNode) obj;
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode()
	{
		return super.hashCode() * 31 + toString().hashCode();
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		CSSSelectorNode[] selectors = getSelectors();
		for (int i = 0; i < selectors.length; ++i)
		{
			text.append(selectors[i]);
			if (i < selectors.length - 1)
			{
				text.append(", "); //$NON-NLS-1$
			}
		}

		CSSDeclarationNode[] declarations = getDeclarations();
		text.append(" {"); //$NON-NLS-1$
		for (int i = 0; i < declarations.length; ++i)
		{
			text.append(declarations[i]);
			if (i < declarations.length - 1)
			{
				text.append(" "); //$NON-NLS-1$
			}
		}
		text.append("}"); //$NON-NLS-1$

		return text.toString();
	}
}
