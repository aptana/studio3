package com.aptana.editor.xml.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.parsing.ast.IParseNode;

public class XMLOutlineContentProvider extends CommonOutlineContentProvider
{

	public static XMLOutlineItem getOutlineItem(IParseNode node)
	{
		if (node == null)
		{
			return null;
		}
		return new XMLOutlineItem(node.getNameNode().getNameRange(), node);
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof XMLOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((XMLOutlineItem) parentElement).getReferenceNode());
		}
		return super.getChildren(parentElement);
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof XMLOutlineItem)
		{
			IParseNode node = ((XMLOutlineItem) element).getReferenceNode();
			return getOutlineItem(node.getParent());
		}
		return super.getParent(element);
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<XMLOutlineItem> items = new ArrayList<XMLOutlineItem>();
		for (IParseNode node : nodes)
		{
			if (node instanceof XMLElementNode)
			{
				items.add(getOutlineItem(node));
			}
		}
		return items.toArray(new XMLOutlineItem[items.size()]);
	}
}
