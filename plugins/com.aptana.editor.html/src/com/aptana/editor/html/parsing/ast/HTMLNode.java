package com.aptana.editor.html.parsing.ast;

import com.aptana.parsing.ast.ParseNode;

public class HTMLNode extends ParseNode
{

	public static final String LANGUAGE = "text/html"; //$NON-NLS-1$

	private short fType;

	public HTMLNode(short type, int start, int end)
	{
		super(LANGUAGE);
		fType = type;
		this.start = start;
		this.end = end;
	}

	public HTMLNode(short type, HTMLNode[] children, int start, int end)
	{
		this(type, start, end);
		setChildren(children);
	}

	public short getNodeType()
	{
		return fType;
	}
}
