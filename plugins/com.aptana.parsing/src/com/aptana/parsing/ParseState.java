package com.aptana.parsing;

import com.aptana.parsing.ast.IParseNode;

public class ParseState implements IParseState
{

	private static final char[] NO_CHARS = new char[0];

	private char[] fSource;
	private char[] fInsertedText;
	private int fStartingOffset;
	private int fRemovedLength;

	// represents the root node of the parsing result
	private IParseNode fParseResult;

	public ParseState()
	{
		fSource = NO_CHARS;
		fInsertedText = NO_CHARS;
	}

	@Override
	public void clearEditState()
	{
		fInsertedText = NO_CHARS;
		fRemovedLength = 0;
	}

	public IParseNode getParseResult()
	{
		return fParseResult;
	}

	@Override
	public char[] getInsertedText()
	{
		return fInsertedText;
	}

	@Override
	public int getRemovedLength()
	{
		return fRemovedLength;
	}

	@Override
	public char[] getSource()
	{
		return fSource;
	}

	@Override
	public int getStartingOffset()
	{
		return fStartingOffset;
	}

	@Override
	public void setEditState(String source, String insertedText, int startingOffset, int removedLength)
	{
		fSource = (source != null) ? source.toCharArray() : NO_CHARS;
		fInsertedText = (insertedText != null) ? insertedText.toCharArray() : NO_CHARS;
		fStartingOffset = startingOffset;
		fRemovedLength = removedLength;
	}

	@Override
	public void setParseResult(IParseNode result)
	{
		fParseResult = result;
	}

	@Override
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
