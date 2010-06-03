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
	@Override
	public boolean contains(int offset)
	{
		return getStartingOffset() <= offset && offset <= getEndingOffset();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getEndingOffset()
	 */
	@Override
	public int getEndingOffset()
	{
		return this._endingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	@Override
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
	@Override
	public int getStartingOffset()
	{
		return this._startingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ILexeme#getText()
	 */
	@Override
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
	@Override
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
		buffer.append(" [");
		buffer.append(this.getStartingOffset()).append("-").append(this.getEndingOffset());
		buffer.append(",").append(this.getText());
		buffer.append("]");

		return buffer.toString();
	}
}
