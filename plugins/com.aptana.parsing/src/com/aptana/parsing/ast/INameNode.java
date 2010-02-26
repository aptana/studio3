package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.IRange;

public interface INameNode
{
	public String getName();

	public IRange getNameRange();
}
