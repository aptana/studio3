package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.ILexeme;

public interface IParseNode extends ILexeme, ILanguageNode
{

	public void addChild(IParseNode child);

	public IParseNode getChild(int index);

	public IParseNode[] getChildren();

	public int getChildrenCount();

	public int getIndex(IParseNode child);

	public IParseNode getParent();

	public short getType();

	public String getLanguage();
}
