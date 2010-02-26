package com.aptana.editor.html.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.outline.JSOutlineContentProvider;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.ast.IParseNode;

public class HTMLOutlineContentProvider extends CompositeOutlineContentProvider
{

	public HTMLOutlineContentProvider()
	{
		addSubLanguage(ICSSParserConstants.LANGUAGE, new CSSOutlineContentProvider());
		addSubLanguage(IJSParserConstants.LANGUAGE, new JSOutlineContentProvider());
	}

	@Override
	public Object[] getChildren(Object parentElement)
	{
		if (parentElement instanceof HTMLOutlineItem)
		{
			// delegates to the parse node it references to
			return getChildren(((HTMLOutlineItem) parentElement).getReferenceNode());
		}
		if (parentElement instanceof HTMLSpecialNode)
		{
			// HTMLSpecialNode always has the root node of the nested language as its child; we want to skip that and
			// get the content below
			return getChildren(((HTMLSpecialNode) parentElement).getChild(0));
		}
		return super.getChildren(parentElement);
	}

	@Override
	protected Object[] filter(IParseNode[] nodes)
	{
		List<HTMLOutlineItem> items = new ArrayList<HTMLOutlineItem>();
		HTMLElementNode element;
		for (IParseNode node : nodes)
		{
			if (node instanceof HTMLElementNode)
			{
				// for HTML node, only takes the element node
				element = (HTMLElementNode) node;
				if (element.getName().length() > 0)
				{
					items.add(new HTMLOutlineItem(element.getNameNode().getNameRange(), element));
				}
			}
			else
			{
				// includes all non-HTML nodes and let the nested language handle its own filtering
				items.add(new HTMLOutlineItem(node, node));
			}
		}
		return items.toArray(new HTMLOutlineItem[items.size()]);
	}
}
