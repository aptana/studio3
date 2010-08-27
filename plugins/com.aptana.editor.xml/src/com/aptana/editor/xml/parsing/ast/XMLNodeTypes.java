package com.aptana.editor.xml.parsing.ast;

public enum XMLNodeTypes
{
	UNKNOWN, DECLARATION, ELEMENT, ERROR;

	public short getIndex()
	{
		return (short) ordinal();
	}
}
