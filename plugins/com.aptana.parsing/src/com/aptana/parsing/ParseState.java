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

public class ParseState implements IParseState
{

	private static final char[] NO_CHARS = new char[0];

	private char[] fSource;
	private char[] fInsertedText;
	private int fStartingOffset;
	private int fRemovedLength;

	private IRange[] fSkippedRanges;

	// represents the root node of the parsing result
	private IParseNode fParseResult;

	public ParseState()
	{
		fSource = NO_CHARS;
		fInsertedText = NO_CHARS;
	}

	public void clearEditState()
	{
		fInsertedText = NO_CHARS;
		fRemovedLength = 0;
		fSkippedRanges = null;
	}

	public IParseNode getParseResult()
	{
		return fParseResult;
	}

	public char[] getInsertedText()
	{
		return fInsertedText;
	}

	public int getRemovedLength()
	{
		return fRemovedLength;
	}

	public char[] getSource()
	{
		return fSource;
	}

	public int getStartingOffset()
	{
		return fStartingOffset;
	}

	public IRange[] getSkippedRanges()
	{
		return fSkippedRanges;
	}

	public void setEditState(String source, String insertedText, int startingOffset, int removedLength)
	{
		fSource = (source != null) ? source.toCharArray() : NO_CHARS;
		fInsertedText = (insertedText != null) ? insertedText.toCharArray() : NO_CHARS;
		fStartingOffset = startingOffset;
		fRemovedLength = removedLength;
		fSkippedRanges = null;
	}

	public void setParseResult(IParseNode result)
	{
		fParseResult = result;
	}

	public void setSkippedRanges(IRange[] ranges)
	{
		fSkippedRanges = ranges;
	}

	public String toString()
	{
		StringBuilder text = new StringBuilder();
		text.append("@").append(fStartingOffset); //$NON-NLS-1$

		if (fRemovedLength > 0)
		{
			text.append(":r").append(fRemovedLength); //$NON-NLS-1$
		}

		int insertedLength = fInsertedText.length;
		if (insertedLength > 0)
		{
			text.append(":i").append(insertedLength).append(":").append(fInsertedText); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
		{
			// outputs closing delimiter for proper parsing of the offset
			text.append(":"); //$NON-NLS-1$
		}

		return text.toString();
	}
}
