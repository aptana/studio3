package com.aptana.editor.html.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.parsing.ast.IParseNode;

public class HTMLOutlineContentProvider extends CommonOutlineContentProvider
{
	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof HTMLElementNode)
		{
			IParseNode[] children = ((HTMLElementNode) parentElement).getChildren();
			List<IParseNode> result = new ArrayList<IParseNode>();
			for (IParseNode child : children)
			{
				if (child instanceof HTMLElementNode && ((HTMLElementNode) child).getName().length() > 0)
				{
					result.add(child);
				}
			}
			return result.toArray(new IParseNode[result.size()]);
		}
		return super.getChildren(parentElement);
	}
}
