package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyElement;

public class RubyImport extends RubyElement
{

	private String fValue;

	public RubyImport(String value, int start, int end)
	{
		super(start, end);
		fValue = value;
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.IMPORT_DECLARATION;
	}

	@Override
	public String getName()
	{
		return fValue;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
