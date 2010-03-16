package com.aptana.parsing.ast;

import com.aptana.parsing.lexer.IRange;


public interface IParseNode extends IRange, ILanguageNode
{

	public void addChild(IParseNode child);

	public IParseNode getChild(int index);

	public IParseNode[] getChildren();

	public int getChildrenCount();

	public int getIndex(IParseNode child);

	public INameNode getNameNode();

	public IParseNode getParent();

	public String getText();

	public short getType();

	public String getLanguage();

	public IParseNode getElementAt(int offset);
}
