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
		return getEndingOffset() - getStartingOffset() + 1;
	}

	@Override
	public int getStartingOffset()
	{
		return fStart;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Range))
		{
			return false;
		}
		Range otherRange = (Range) obj;
		return getStartingOffset() == otherRange.getStartingOffset() && getLength() == otherRange.getLength();
	}

	@Override
	public int hashCode()
	{
		return 13 * Integer.valueOf(getStartingOffset()).hashCode() + Integer.valueOf(getLength()).hashCode();
	}
}
