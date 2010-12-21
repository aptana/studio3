package com.aptana.editor.yaml.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.parsing.ast.IParseNode;

public class YAMLOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<CommonOutlineItem> items = new ArrayList<CommonOutlineItem>();
		for (IParseNode node : nodes)
		{
			// if (node instanceof XMLElementNode)
			// {
			items.add(getOutlineItem(node));
			// }
		}
		return items.toArray(new CommonOutlineItem[items.size()]);
	}
}
