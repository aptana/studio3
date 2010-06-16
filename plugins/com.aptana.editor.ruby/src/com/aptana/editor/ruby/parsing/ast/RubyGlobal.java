package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyElement;

public class RubyGlobal extends RubyField
{

	public RubyGlobal(String name, int start, int end)
	{
		super(name, start, end);
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.GLOBAL;
	}
}
