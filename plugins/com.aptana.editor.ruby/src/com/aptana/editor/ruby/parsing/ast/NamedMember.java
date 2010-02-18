package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyMember;

public class NamedMember extends RubyElement implements IRubyMember
{
	private String fName;

	public NamedMember(String name, int start, int end)
	{
		super(start, end);
		fName = name;
	}

	@Override
	public String getName()
	{
		return fName;
	}

	@Override
	public String toString()
	{
		return fName;
	}
}
