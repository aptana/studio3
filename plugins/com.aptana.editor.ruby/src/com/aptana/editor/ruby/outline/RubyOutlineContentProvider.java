package com.aptana.editor.ruby.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.parsing.ast.IParseNode;

public class RubyOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<CommonOutlineItem> list = new ArrayList<CommonOutlineItem>();
		IRubyElement element;
		for (IParseNode node : nodes)
		{
			if (node instanceof IRubyElement)
			{
				element = (IRubyElement) node;
				// filters out block elements
				if (element.getNodeType() != IRubyElement.BLOCK)
				{
					list.add(getOutlineItem(element));
				}
			}
		}
		return list.toArray(new CommonOutlineItem[list.size()]);
	}
}
