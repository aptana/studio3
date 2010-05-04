package com.aptana.parsing.lexer;

import org.eclipse.jface.text.rules.IToken;

public class Lexeme implements ITokenLexeme
{
	private final String text;
	private final int startingOffset;
	private final int endingOffset;
	private IToken token;

	/**
	 * Lexeme
	 * 
	 * @param text
	 * @param startingOffset
	 * @param endingOffset
	 * @param token
	 */
	public Lexeme(String text, int startingOffset, int endingOffset, IToken token)
	{
		this.text = text;
		this.startingOffset = startingOffset;
		this.endingOffset = endingOffset;
		this.token = token;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ILexeme#getText()
	 */
	@Override
	public String getText()
	{
		return text;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getEndingOffset()
	 */
	@Override
	public int getEndingOffset()
	{
		return endingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.IRange#getLength()
	 */
	@Override
	public int getLength()
	{
		if (text != null) 
		{
			return text.length();
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
		return startingOffset;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.parsing.lexer.ITokenLexeme#getToken()
	 */
	@Override
	public IToken getToken()
	{
		return token;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder buffer = new StringBuilder();
		Object data = this.getToken().getData();
		
		buffer.append("[").append(data).append("]~").append(this.getText()).append("~");
		
		return buffer.toString();
	}
}
