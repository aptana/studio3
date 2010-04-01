package com.aptana.parsing.lexer;

import org.eclipse.jface.text.rules.IToken;

public class TokenLexeme implements ITokenLexeme
{
	
	private final String text;
	private final int startingOffset;
	private final int endingOffset;
	private IToken token;

	public TokenLexeme(String text, int startingOffset, int endingOffset, IToken token)
	{
		this.text = text;
		this.startingOffset = startingOffset;
		this.endingOffset = endingOffset;
		this.token = token;
	}
	
	@Override
	public String getText()
	{
		return text;
	}

	@Override
	public int getEndingOffset()
	{
		return endingOffset;
	}

	@Override
	public int getLength()
	{
		if (text != null) 
		{
			return text.length();
		}
		return -1;
	}

	@Override
	public int getStartingOffset()
	{
		return startingOffset;
	}

	@Override
	public IToken getToken()
	{
		return token;
	}
}
