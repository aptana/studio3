package com.aptana.parsing.lexer;

public interface IRange
{
	/**
	 * Determines if the specified offset is contained within this range
	 * 
	 * @param offset
	 * @return
	 */
	boolean contains(int offset);

	/**
	 * Gets the starting offset for this range.
	 * 
	 * @return the starting offset for this range
	 */
	int getStartingOffset();

	/**
	 * Gets the ending offset for this range.
	 * 
	 * @return the ending offset for this range
	 */
	int getEndingOffset();

	/**
	 * Gets the total length between the starting offset and the ending offset in this range.
	 * 
	 * @return the length of this range
	 */
	int getLength();
	
	/**
	 * Determines if this range is empty. This is equivalent to having a zero or negative length
	 * @return
	 */
	boolean isEmpty();
}
