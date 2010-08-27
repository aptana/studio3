package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyElement;
import com.aptana.editor.ruby.core.IRubyField;

public class RubyField extends NamedMember implements IRubyField
{

	public RubyField(String name, int start, int end)
	{
		super(name, start, end);
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.FIELD;
	}
}
