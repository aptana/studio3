package com.aptana.editor.html.parsing.ast;

import com.aptana.parsing.ast.ParseBaseNode;

public class HTMLNode extends ParseBaseNode
{

	private short fType;

	public HTMLNode(short type, int start, int end)
	{
		fType = type;
		this.start = start;
		this.end = end;
	}

	public HTMLNode(short type, HTMLNode[] children, int start, int end)
	{
		this(type, start, end);
		setChildren(children);
	}

	public short getType()
	{
		return fType;
	}
}
