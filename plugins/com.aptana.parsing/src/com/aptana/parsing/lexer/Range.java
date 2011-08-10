/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.lexer;

import java.text.MessageFormat;

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
		if (this == obj)
		{
			return true;
		}

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
	public int getEndingOffset()
	{
		return fEnd;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		return getEndingOffset() - getStartingOffset() + 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return fStart;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return 31 * getStartingOffset() + getLength();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#isEmpty()
	 */
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
		return MessageFormat.format("[{0},{1}]", fStart, fEnd); //$NON-NLS-1$
	}
}
