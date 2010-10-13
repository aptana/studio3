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
package com.aptana.editor.js.sdoc.parsing;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

public class SDocTokenScanner extends RuleBasedScanner
{
	static class TagDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return (c == '@');
		}

		public boolean isWordPart(char c)
		{
			return Character.isLetter(c);
		}
	}

	static class OperatorDetector implements IWordDetector
	{
		private int fPosition;

		public boolean isWordPart(char c)
		{
			boolean result = false;

			fPosition++;

			if (fPosition == 1)
			{
				switch (c)
				{
					case '/':
					case '*':
						result = true;
						break;
				}
			}
			else if (fPosition == 2)
			{
				switch (c)
				{
					case '*':
						result = true;
						break;
				}
			}

			return result;
		}

		public boolean isWordStart(char c)
		{
			boolean result = false;

			fPosition = 0;

			switch (c)
			{
				case '/':
				case '*':
					result = true;
					break;
			}

			return result;
		}
	}

	static class TextDetector implements IWordDetector
	{
		public boolean isWordPart(char c)
		{
			// [^ \t{\[\]#]
			boolean result = true;

			switch (c)
			{
				case ' ':
				case '\t':
				case '{':
				case '[':
				case ']':
				case '#':
					result = false;
			}

			return result;
		}

		public boolean isWordStart(char c)
		{
			return this.isWordPart(c);
		}
	}

	/**
	 * SDocTokenScanner
	 */
	@SuppressWarnings("nls")
	public SDocTokenScanner()
	{
		List<IRule> rules = new LinkedList<IRule>();

		rules.add(new WordRule(new WhitespaceDetector(), getToken(SDocTokenType.WHITESPACE)));
		rules.add(createAlternateWhitespaceRule());

		WordRule tagRules = new WordRule(new TagDetector(), getToken(SDocTokenType.UNKNOWN));
		tagRules.addWord("@advanced", getToken(SDocTokenType.ADVANCED));
		tagRules.addWord("@alias", getToken(SDocTokenType.ALIAS));
		tagRules.addWord("@author", getToken(SDocTokenType.AUTHOR));
		tagRules.addWord("@classDescription", getToken(SDocTokenType.CLASS_DESCRIPTION));
		tagRules.addWord("@constructor", getToken(SDocTokenType.CONSTRUCTOR));
		tagRules.addWord("@example", getToken(SDocTokenType.EXAMPLE));
		tagRules.addWord("@exception", getToken(SDocTokenType.EXCEPTION));
		tagRules.addWord("@extends", getToken(SDocTokenType.EXTENDS));
		tagRules.addWord("@internal", getToken(SDocTokenType.INTERNAL));
		tagRules.addWord("@method", getToken(SDocTokenType.METHOD));
		tagRules.addWord("@namespace", getToken(SDocTokenType.NAMESPACE));
		tagRules.addWord("@overview", getToken(SDocTokenType.OVERVIEW));
		tagRules.addWord("@param", getToken(SDocTokenType.PARAM));
		tagRules.addWord("@private", getToken(SDocTokenType.PRIVATE));
		tagRules.addWord("@property", getToken(SDocTokenType.PROPERTY));
		tagRules.addWord("@return", getToken(SDocTokenType.RETURN));
		tagRules.addWord("@see", getToken(SDocTokenType.SEE));
		tagRules.addWord("@type", getToken(SDocTokenType.TYPE));
		rules.add(tagRules);
		
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('#', getToken(SDocTokenType.POUND));
		cmRule.add('[', getToken(SDocTokenType.LBRACKET));
		cmRule.add(']', getToken(SDocTokenType.RBRACKET));
		cmRule.add('\r', getToken(SDocTokenType.WHITESPACE));
		cmRule.add('\n', getToken(SDocTokenType.WHITESPACE));
		rules.add(cmRule);
		
		rules.add(new PatternRule("{", "}", getToken(SDocTokenType.TYPES), '\0', false));
		
		WordRule operatorRules = new WordRule(new OperatorDetector(), getToken(SDocTokenType.UNKNOWN));
		operatorRules.addWord("/**", getToken(SDocTokenType.START_DOCUMENTATION));
		operatorRules.addWord("*/", getToken(SDocTokenType.END_DOCUMENTATION));
		rules.add(operatorRules);

		rules.add(new WordRule(new TextDetector(), getToken(SDocTokenType.TEXT)));

		this.setDefaultReturnToken(getToken(SDocTokenType.ERROR));
		this.setRules(rules.toArray(new IRule[rules.size()]));
	}

	protected IRule createAlternateWhitespaceRule()
	{
		return new ExtendedWordRule(new IWordDetector()
		{
			public boolean isWordStart(char c)
			{
				return this.isWordPart(c);
			}

			public boolean isWordPart(char c)
			{
				return c == ' ' || c == '\t' || c == '*';
			}
		}, getToken(SDocTokenType.WHITESPACE), false)
		{
			private final Pattern PATTERN = Pattern.compile("^[ \\t]*\\*(?!/)[ \\t]*"); //$NON-NLS-1$

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				char c = (char) scanner.read();
				scanner.unread();

				if (word != null && word.length() > 0 & word.charAt(word.length() - 1) == '*' && c == '/')
				{
					return false;
				}
				else
				{
					Matcher m = PATTERN.matcher(word);

					return m.matches();
				}
			}
		};
	}

	/**
	 * getToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken getToken(SDocTokenType type)
	{
		return new Token(type);
	}
}
