package com.aptana.editor.css.parsing.ast;

import java.util.Arrays;
import java.util.List;

import com.aptana.parsing.ast.IParseNode;

public class CSSPageNode extends CSSNode
{

	private String fPageSelector;

	public CSSPageNode(int start, int end)
	{
		this(null, null, start, end);
	}

	public CSSPageNode(String pageSelector, int start, int end)
	{
		this(pageSelector, null, start, end);
	}

	public CSSPageNode(Object declarations, int start, int end)
	{
		this(null, declarations, start, end);
	}

	@SuppressWarnings("unchecked")
	public CSSPageNode(String pageSelector, Object declarations, int start, int end)
	{
		super(start, end);
		fPageSelector = pageSelector;
		if (declarations instanceof CSSDeclarationNode)
		{
			setChildren(new CSSDeclarationNode[] { (CSSDeclarationNode) declarations });
		}
		else if (declarations instanceof List<?>)
		{
			List<CSSDeclarationNode> list = (List<CSSDeclarationNode>) declarations;
			setChildren(list.toArray(new CSSDeclarationNode[list.size()]));
		}
	}

	public CSSDeclarationNode[] getDeclarations()
	{
		List<IParseNode> list = Arrays.asList(getChildren());
		return list.toArray(new CSSDeclarationNode[list.size()]);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof CSSPageNode))
		{
			return false;
		}
		CSSPageNode other = (CSSPageNode) obj;
		return (fPageSelector == null ? other.fPageSelector == null : fPageSelector.equals(other.fPageSelector))
				&& Arrays.equals(getDeclarations(), other.getDeclarations());
	}

	@Override
	public int hashCode()
	{
		return 31 * (fPageSelector == null ? 0 : fPageSelector.hashCode()) + Arrays.hashCode(getDeclarations());
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("@page "); //$NON-NLS-1$
		if (fPageSelector != null)
		{
			text.append(":").append(fPageSelector).append(" "); //$NON-NLS-1$ //$NON-NLS-2$
		}
		text.append("{"); //$NON-NLS-1$
		CSSDeclarationNode[] declarations = getDeclarations();
		int size = declarations.length;
		for (int i = 0; i < size; ++i)
		{
			text.append(declarations[i]);
			if (i < size - 1)
			{
				text.append(" "); //$NON-NLS-1$
			}
		}
		text.append("}"); //$NON-NLS-1$
		return text.toString();
	}
}
