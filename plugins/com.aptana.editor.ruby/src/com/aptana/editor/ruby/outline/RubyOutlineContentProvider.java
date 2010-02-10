package com.aptana.editor.ruby.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CommonOutlineContentProvider;
import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.parsing.ast.IParseNode;

public class RubyOutlineContentProvider extends CommonOutlineContentProvider
{

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<IRubyElement> list = new ArrayList<IRubyElement>();
		IRubyElement element;
		for (IParseNode node : nodes)
		{
			if (node instanceof IRubyElement)
			{
				element = (IRubyElement) node;
				// filters out block elements
				if (element.getType() != IRubyElement.BLOCK)
				{
					list.add(element);
				}
			}
		}
		return list.toArray(new IRubyElement[list.size()]);
	}
}
