package com.aptana.editor.ruby.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.parsing.ast.IParseNode;

public class RubyOutlineContentProvider extends CommonOutlineContentProvider
{

	public static RubyOutlineItem getOutlineItem(IParseNode node)
	{
		if (node == null)
		{
			return null;
		}
		return new RubyOutlineItem(node.getNameNode().getNameRange(), node);
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof RubyOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((RubyOutlineItem) parentElement).getReferenceNode());
		}
		return super.getChildren(parentElement);
	}

	@Override
	public Object getParent(Object element)
	{
		if (element instanceof RubyOutlineItem)
		{
			IParseNode node = ((RubyOutlineItem) element).getReferenceNode();
			return getOutlineItem(node.getParent());
		}
		return super.getParent(element);
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<RubyOutlineItem> list = new ArrayList<RubyOutlineItem>();
		IRubyElement element;
		for (IParseNode node : nodes)
		{
			if (node instanceof IRubyElement)
			{
				element = (IRubyElement) node;
				// filters out block elements
				if (element.getType() != IRubyElement.BLOCK)
				{
					list.add(getOutlineItem(element));
				}
			}
		}
		return list.toArray(new RubyOutlineItem[list.size()]);
	}
}
