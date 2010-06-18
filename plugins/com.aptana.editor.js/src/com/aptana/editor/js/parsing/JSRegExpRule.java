package com.aptana.editor.js.parsing;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JSRegExpRule implements IPredicateRule
{
	private enum State
	{
		ERROR,
		NORMAL,
		ESCAPE_SEQUENCE,
		OPTIONS;
	}
	
	IToken token;
	
	/**
	 * JSRegExpRule
	 * @param token
	 */
	public JSRegExpRule(IToken token)
	{
		this.token = token;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{
		State state = State.ERROR;
		StringBuffer buffer = new StringBuffer();
		int c = scanner.read();
		
		if (c == '/')
		{
			state = State.NORMAL;
			
			LOOP: while (c != ICharacterScanner.EOF)
			{
				c = scanner.read();
				buffer.append((char) c);
				
				switch (state)
				{
					case NORMAL:
						switch (c)
						{
							case '\\':
								state = State.ESCAPE_SEQUENCE;
								break;
								
							case '/':
								state = State.OPTIONS;
								break;
								
							case '\r':
							case '\n':
								state = State.ERROR;
								break LOOP;
						}
						break;
						
					case ESCAPE_SEQUENCE:
						switch (c)
						{
							case '\r':
							case '\n':
								state = State.ERROR;
								break LOOP;
								
							default:
								state = State.NORMAL;
								break;
						}
						break;
						
					case OPTIONS:
						switch (c)
						{
							case 'i':
							case 'm':
							case 'g':
								//state = State.OPTIONS;
								break;
								
							default:
								break LOOP;
						}
						break;
				}
			}
		}

		scanner.unread();
		
		if (state == State.OPTIONS && this.token != null && this.token.isUndefined() == false)
		{
			return this.token;
		}
		else
		{
			for (int i = 0; i < buffer.length(); i++)
			{
				scanner.unread();
			}
			
			return Token.UNDEFINED;
		}
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		return evaluate(scanner);
	}

	@Override
	public IToken getSuccessToken()
	{
		return token;
	}
}
