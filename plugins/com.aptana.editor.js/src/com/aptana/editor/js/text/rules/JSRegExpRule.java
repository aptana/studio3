/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.js.IJSTokenScanner;

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
		if (scanner instanceof IJSTokenScanner)
		{
			if (((IJSTokenScanner) scanner).hasDivisionStart())
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
								if (inCharacterClass == false)
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

		if (state == State.OPTIONS && this.token != null && this.token.isUndefined() == false)
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
