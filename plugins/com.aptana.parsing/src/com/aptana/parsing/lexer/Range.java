package com.aptana.parsing.lexer;

public class Range implements IRange
{

	private int fStart;
	private int fEnd;

	public Range(int start, int end)
	{
		fStart = start;
		fEnd = end;
	}

	@Override
	public int getEndingOffset()
	{
		return fEnd;
	}

	@Override
	public int getLength()
	{
		return fEnd - fStart;
	}

	@Override
	public int getStartingOffset()
	{
		return fStart;
	}
}
