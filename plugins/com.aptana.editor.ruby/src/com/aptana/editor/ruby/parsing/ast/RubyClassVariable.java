package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyElement;

public class RubyClassVariable extends RubyField
{

	public RubyClassVariable(String name, int start, int end)
	{
		super(name, start, end);
	}

	@Override
	public short getType()
	{
		return IRubyElement.CLASS_VAR;
	}
}
