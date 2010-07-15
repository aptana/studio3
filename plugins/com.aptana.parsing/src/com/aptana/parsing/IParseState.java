package com.aptana.parsing;

import com.aptana.parsing.ast.IParseNode;

public interface IParseState
{
	/**
	 * clearEditState
	 */
	public void clearEditState();

	/**
	 * getInsertedText
	 * 
	 * @return
	 */
	public char[] getInsertedText();

	/**
	 * getParseResult
	 * 
	 * @return
	 */
	public IParseNode getParseResult();

	/**
	 * getRemovedLength
	 * 
	 * @return
	 */
	public int getRemovedLength();

	/**
	 * getSource
	 * 
	 * @return
	 */
	public char[] getSource();

	/**
	 * getStartingOffset
	 * 
	 * @return
	 */
	public int getStartingOffset();

	/**
	 * setEditState
	 * 
	 * @param source
	 * @param insertedText
	 * @param startingOffset
	 * @param removedLength
	 */
	public void setEditState(String source, String insertedText, int startingOffset, int removedLength);

	/**
	 * setParseResult
	 * 
	 * @param result
	 */
	public void setParseResult(IParseNode result);
}
