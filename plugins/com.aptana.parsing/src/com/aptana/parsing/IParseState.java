/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

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
	 * Returns the list of ranges in the source to skip.
	 * 
	 * @return the list of ranges in an array
	 */
	public IRange[] getSkippedRanges();

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
