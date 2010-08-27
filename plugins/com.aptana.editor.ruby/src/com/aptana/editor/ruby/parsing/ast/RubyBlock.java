package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyElement;

public class RubyBlock extends RubyElement
{

	public RubyBlock(int start, int end)
	{
		super(start, end);
	}

	@Override
	public short getNodeType()
	{
		return IRubyElement.BLOCK;
	}
}
