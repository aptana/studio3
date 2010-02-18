package com.aptana.parsing;

import com.aptana.parsing.ast.IParseNode;

public interface IParseState
{

	public void clearEditState();

	public IParseNode getParseResult();

	public char[] getSource();

	public char[] getInsertedText();

	public int getStartingOffset();

	public int getRemovedLength();

	public void setEditState(String source, String insertedText, int startingOffset, int removedLength);

	public void setParseResult(IParseNode result);
}
