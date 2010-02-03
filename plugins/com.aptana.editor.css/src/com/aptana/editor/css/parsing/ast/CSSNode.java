package com.aptana.editor.css.parsing.ast;

import com.aptana.parsing.ast.ParseBaseNode;

public class CSSNode extends ParseBaseNode
{

	public static final String LANGUAGE = "text/css"; //$NON-NLS-1$

	protected CSSNode()
	{
		this(0, 0);
	}

	public CSSNode(int start, int end)
	{
		super(LANGUAGE);
		this.start = start;
		this.end = end;
	}
}
