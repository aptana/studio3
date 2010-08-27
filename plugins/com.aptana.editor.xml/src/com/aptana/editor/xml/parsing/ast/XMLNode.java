package com.aptana.editor.xml.parsing.ast;

import com.aptana.editor.xml.parsing.IXMLParserConstants;
import com.aptana.parsing.ast.ParseNode;

public class XMLNode extends ParseNode
{

	private short fType;

	public XMLNode(short type, int start, int end)
	{
		super(IXMLParserConstants.LANGUAGE);
		fType = type;
		this.start = start;
		this.end = end;
	}

	public XMLNode(short type, XMLNode[] children, int start, int end)
	{
		this(type, start, end);
		setChildren(children);
	}

	@Override
	public short getNodeType()
	{
		return fType;
	}
}
