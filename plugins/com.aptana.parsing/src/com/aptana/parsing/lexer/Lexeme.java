/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.lexer;

public class Lexeme<T> implements ILexeme
{
	private String _text;
	private int _startingOffset;
	private int _endingOffset;
	private T _type;

	/**
	 * Lexeme
	 * 
	 * @param startingOffset
	 * @param endingOffset
	 * @param text
	 */
	public Lexeme(T type, int startingOffset, int endingOffset, String text)
	{
		this._type = type;
		this._startingOffset = startingOffset;
		this._endingOffset = endingOffset;
		this._text = text;
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
	 * @see com.aptana.parsing.lexer.IRange#getEndingOffset()
	 */
	public int getEndingOffset()
	{
		return this._endingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	public int getLength()
	{
		if (this._text != null)
		{
			return this._text.length();
		}

		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getStartingOffset()
	 */
	public int getStartingOffset()
	{
		return this._startingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ILexeme#getText()
	 */
	public String getText()
	{
		return this._text;
	}

	/**
	 * getType
	 * 
	 * @return
	 */
	public T getType()
	{
		return this._type;
	}

	/**
	 * areContiguous
	 * 
	 * @param firstLexeme
	 * @param secondLexeme
	 * @return
	 */
	public boolean isContiguousWith(Lexeme<T> secondLexeme)
	{
		boolean result = true;

		if (secondLexeme != null)
		{
			result = this.getEndingOffset() + 1 == secondLexeme.getStartingOffset();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this._endingOffset < this._startingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		String type = this._type.toString();

		buffer.append(type);
		buffer.append(" ["); //$NON-NLS-1$
		buffer.append(this.getStartingOffset()).append('-').append(this.getEndingOffset());
		buffer.append(',').append(this.getText());
		buffer.append(']');

		return buffer.toString();
	}
}
