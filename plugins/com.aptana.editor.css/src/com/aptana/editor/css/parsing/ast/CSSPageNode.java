package com.aptana.editor.css.parsing.ast;

import java.util.List;

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
			setChildren(new CSSNode[] { (CSSDeclarationNode) declarations });
		}
		else if (declarations instanceof List<?>)
		{
			List<CSSDeclarationNode> list = (List<CSSDeclarationNode>) declarations;
			setChildren(list.toArray(new CSSDeclarationNode[list.size()]));
		}
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
		text.append(super.toString());
		text.append("}"); //$NON-NLS-1$
		return text.toString();
	}
}
