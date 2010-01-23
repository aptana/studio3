package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.ILexeme;

public interface IParseNode extends ILexeme
{

	public IParseNode getChild(int index);

	public IParseNode[] getChildren();

	public int getChildrenCount();

	public IParseNode getParent();
}
