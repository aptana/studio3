// $codepro.audit.disable platformSpecificLineSeparator
/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.js.IRegexpDivisionDisambiguator;

public class JSRegExpRule implements IPredicateRule
{
	private enum State
	{
		ERROR, NORMAL, ESCAPE_SEQUENCE, OPTIONS;
	}

	IToken token;

	/**
	 * JSRegExpRule
	 * 
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
	public IToken evaluate(ICharacterScanner scanner)
	{
		if (scanner instanceof IRegexpDivisionDisambiguator)
		{
			if (((IRegexpDivisionDisambiguator) scanner).isValidDivisionStart())
			{
				return Token.UNDEFINED;
			}
		}

		State state = State.ERROR;
		boolean inCharacterClass = false; // use flag instead of new state to simplify state machine
		int c = scanner.read();
		int unreadCount = 0;

		if (c == '/')
		{
			state = State.NORMAL;

			LOOP: while (c != ICharacterScanner.EOF)
			{
				c = scanner.read();
				unreadCount++;

				switch (state)
				{
					case NORMAL:
						switch (c)
						{
							case '\\':
								state = State.ESCAPE_SEQUENCE;
								break;

							case '/':
								// We allow stray slashes inside of character classes
								if (!inCharacterClass)
								{
									if (unreadCount > 1)
									{
										state = State.OPTIONS;
									}
									else
									{
										state = State.ERROR;
										break LOOP;
									}
								}
								break;

							case '[':
								inCharacterClass = true;
								break;

							case ']':
								inCharacterClass = false;
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
								// state = State.OPTIONS;
								break;

							default:
								break LOOP;
						}
						break;
				}
			}
		}

		// we always read at least one character too many, so push that back
		scanner.unread();

		if (state == State.OPTIONS && this.token != null && !this.token.isUndefined())
		{
			return this.token;
		}
		else
		{
			for (int i = 0; i < unreadCount; i++)
			{
				scanner.unread();
			}

			return Token.UNDEFINED;
		}
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		return evaluate(scanner);
	}

	public IToken getSuccessToken()
	{
		return token;
	}
}
