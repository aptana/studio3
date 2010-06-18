package com.aptana.editor.css.parsing.ast;

import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.parsing.ast.ParseNode;

public class CSSNode extends ParseNode
{

	protected CSSNode()
	{
		this(0, 0);
	}

	public CSSNode(int start, int end)
	{
		super(ICSSParserConstants.LANGUAGE);
		this.start = start;
		this.end = end;
	}
	
	@Override
	public String getText()
	{
		return toString();
	}
}
