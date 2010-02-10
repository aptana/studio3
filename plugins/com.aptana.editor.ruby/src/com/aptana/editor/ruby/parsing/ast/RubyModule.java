package com.aptana.editor.ruby.parsing.ast;

public class RubyModule extends RubyType
{

	public RubyModule(String name, int start, int end)
	{
		super(name, start, end);
	}

	@Override
	public boolean isClass()
	{
		return false;
	}

	@Override
	public boolean isModule()
	{
		return true;
	}
}
