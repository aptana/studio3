package com.aptana.parsing.lexer;

public class Range implements IRange
{
	public static final Range EMPTY = new Range(0, -1);
	
	private int fStart;
	private int fEnd;

	/**
	 * Range
	 * 
	 * @param offset
	 */
	public Range(int offset)
	{
		this(offset, offset);
	}
	
	/**
	 * Range
	 * 
	 * @param start
	 * @param end
	 */
	public Range(int start, int end)
	{
		fStart = start;
		fEnd = end;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#contains(int)
	 */
	@Override
	public boolean contains(int offset)
	{
		return getStartingOffset() <= offset && offset <= getEndingOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getEndingOffset()
	 */
	@Override
	public int getEndingOffset()
	{
		return fEnd;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	@Override
	public int getLength()
	{
		return getEndingOffset() - getStartingOffset() + 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getStartingOffset()
	 */
	@Override
	public int getStartingOffset()
	{
		return fStart;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return 31 * getStartingOffset() + getLength();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return fEnd < fStart;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "[" + fStart + "," + fEnd + "]";
	}
}
