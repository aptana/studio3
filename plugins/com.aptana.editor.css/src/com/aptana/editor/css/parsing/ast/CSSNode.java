package com.aptana.editor.css.parsing.ast;

import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.parsing.ast.ParseBaseNode;

public class CSSNode extends ParseBaseNode
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
}
