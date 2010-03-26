package com.aptana.editor.ruby.parsing.ast;

import com.aptana.editor.ruby.core.IRubyMember;
import com.aptana.parsing.ast.INameNode;

public class NamedMember extends RubyElement implements IRubyMember
{
	private INameNode fNameNode;

	public NamedMember(String name, int start, int end)
	{
		super(start, end);
		fNameNode = new NameNode(name, start, end);
	}

	@Override
	public String getName()
	{
		return fNameNode.getName();
	}

	@Override
	public INameNode getNameNode()
	{
		return fNameNode;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
