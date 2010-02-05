package com.aptana.editor.html.outline;

import java.util.ArrayList;
import java.util.List;

import com.aptana.editor.common.outline.CompositeOutlineContentProvider;
import com.aptana.editor.css.outline.CSSOutlineContentProvider;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
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
	protected Object[] getDefaultChildren(Object parent)
	{
		if (parent instanceof HTMLElementNode)
		{
			IParseNode[] children = ((HTMLElementNode) parent).getChildren();
			List<IParseNode> result = new ArrayList<IParseNode>();
			for (IParseNode child : children)
			{
				if (child instanceof HTMLElementNode && ((HTMLElementNode) child).getName().length() == 0)
				{
					continue;
				}
				result.add(child);
			}
			return result.toArray(new IParseNode[result.size()]);
		}
		return super.getDefaultChildren(parent);
	}
}
