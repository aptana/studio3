package com.aptana.editor.js.parsing.ast;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;

public class JSNode extends ParseBaseNode
{

	protected static final short DEFAULT_TYPE = JSNodeTypes.ERROR;

	private short fType;

	private boolean fSemicolonIncluded;

	public JSNode()
	{
		fType = DEFAULT_TYPE;
	}

	public JSNode(short type, int start, int end)
	{
		this(type, start, end, false);
	}

	public JSNode(short type, int start, int end, boolean semicolon)
	{
		fType = type;
		this.start = start;
		this.end = end;
		fSemicolonIncluded = semicolon;
	}

	public short getType()
	{
		return fType;
	}

	public boolean getSemicolonIncluded()
	{
		return fSemicolonIncluded;
	}

	public void setSemicolonIncluded(boolean included)
	{
		fSemicolonIncluded = included;
	}

	public boolean isEmpty()
	{
		return getType() == JSNodeTypes.EMPTY;
	}

	@Override
	public String toString()
	{
		StringBuilder text = new StringBuilder();
		switch (getType())
		{
			case JSNodeTypes.EMPTY:
				break;
			case JSNodeTypes.ASSIGN:
				IParseNode[] children = getChildren();
				text.append(children[0]);
				text.append(" = "); //$NON-NLS-1$
				text.append(children[1]);
				break;
		}
		if (getSemicolonIncluded())
		{
			text.append(";"); //$NON-NLS-1$
		}
		return text.toString();
	}

	protected void setType(short type)
	{
		fType = type;
	}
}
