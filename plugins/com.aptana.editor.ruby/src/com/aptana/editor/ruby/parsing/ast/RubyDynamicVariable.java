package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyElement;

public class RubyDynamicVariable extends RubyField
{

	public RubyDynamicVariable(String name, int start, int end)
	{
		super(name, start, end);
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.DYNAMIC_VAR;
	}
}
