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
package com.aptana.editor.common.text.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.TextUtils;

public class RegexpRule implements IPredicateRule
{

	private Pattern regexp;
	private IToken successToken;
	private char firstChar = Character.MIN_VALUE;

	public RegexpRule(String regexp, IToken successToken)
	{
		this(regexp, successToken, false);
	}

	/**
	 * This takes an additional flag to optimize the rule matching.
	 * 
	 * @param regexp
	 * @param successToken
	 * @param matchFirstCharFirst
	 *            - a value of true is a hint to match the first char of the regexp first before proceeding to to
	 *            Pattern match.
	 */
	public RegexpRule(String regexp, IToken successToken, boolean matchFirstCharFirst)
	{
		this.regexp = Pattern.compile(regexp);
		this.successToken = successToken;
		if (matchFirstCharFirst && regexp.length() > 0)
		{ // TODO always use this optimization if the first character is a non-special one? (i.e. not ".*([-\?+{" )
			switch (regexp.charAt(0))
			{ // FIXME We also need to see if there's any special character in same group! i.e. to handle "chris|sandip"
				case '*':
				case '.':
				case '+':
				case '|':
				case '(':
				case '[':
				case '{':
				case '-':
				case '^':
				case '$':
				case ')':
				case ']':
				case '}':
				case '?':
					break;
				case '\\':
					// Don't allow if next char is special too (like b,s,w,d,etc)
					switch (regexp.charAt(1))
					{
						case 'b':
						case 'B':
						case 'd':
						case 'D':
						case 's':
						case 'S':
						case 'w':
						case 'W':
						case 'A':
						case 'G':
						case 'Z':
						case 'z':
							break;
						default:
							firstChar = regexp.charAt(1);
							break;
					}
					break;
				default:
					firstChar = regexp.charAt(0);
					break;
			}
		}
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		// Should we try to match the first char first?
		if (firstChar != Character.MIN_VALUE)
		{
			int readChar = scanner.read();
			scanner.unread();
			if (readChar == ICharacterScanner.EOF)
			{
				return Token.EOF;
			}
			if (firstChar != readChar)
			{
				return Token.UNDEFINED;
			}
		}
		String line = readNextLine(scanner);
		if (line == null)
			return Token.EOF;
		Matcher matcher = regexp.matcher(line);
		if (matcher.find() && matcher.start() == 0)
		{
			// Unread back to end of regexp match!
			String match = matcher.group();
			if (match.length() < line.length())
			{
				int toUnread = line.length() - match.length();
				unread(scanner, toUnread);
			}
			return successToken;
		}
		unread(scanner, line.length());
		return Token.UNDEFINED;
	}

	private void unread(ICharacterScanner scanner, int toUnread)
	{
		while (toUnread > 0)
		{
			scanner.unread();
			toUnread--;
		}
	}

	private String readNextLine(ICharacterScanner scanner)
	{
		char[][] lineDelims = TextUtils.rsort(scanner.getLegalLineDelimiters());
		StringBuilder builder = new StringBuilder();
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF)
		{
			// FIXME We need to properly handle multi-char line delims!
			if (isLineDelim((char) c, lineDelims))
			{
				break;
			}
			builder.append((char) c);
		}
		if (c == ICharacterScanner.EOF && builder.length() == 0)
			return null;
		scanner.unread();
		return builder.toString();
	}

	private boolean isLineDelim(char c, char[][] lineDelims)
	{
		for (char[] lineDelim : lineDelims)
		{
			if (c == lineDelim[0])
				return true;
		}
		return false;
	}

	public IToken getSuccessToken()
	{
		return successToken;
	}

	public IToken evaluate(ICharacterScanner scanner)
	{
		return evaluate(scanner, false);
	}

}
