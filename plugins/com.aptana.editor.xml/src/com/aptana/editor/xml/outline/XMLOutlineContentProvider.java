package com.aptana.editor.xml.outline;

import java.util.LinkedList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.parsing.ast.IParseNode;

public class XMLOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		// only shows the element node
		List<XMLElementNode> elements = new LinkedList<XMLElementNode>();
		for (IParseNode node : nodes)
		{
			if (node instanceof XMLElementNode)
			{
				elements.add((XMLElementNode) node);
			}
		}
		return elements.toArray(new XMLElementNode[elements.size()]);
	}
}
