/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;

class HTMLTagScanner extends RuleBasedScanner
{

	public enum TokenType
	{
		ATTR_NAME, ATTR_VALUE, OTHER
	}

	public HTMLTagScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// whitespaces
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// attribute values
		IToken token = createToken(TokenType.ATTR_VALUE);
		rules.add(new MultiLineRule("\"", "\"", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("'", "'", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// attribute names
		WordRule wordRule = new ExtendedWordRule(new IWordDetector()
		{

			public boolean isWordPart(char c)
			{
				return Character.isLetter(c) || c == '-' || c == ':';
			}

			public boolean isWordStart(char c)
			{
				return Character.isLetter(c);
			}

		}, createToken(TokenType.ATTR_NAME), true)
		{
			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				int c = scanner.read();
				scanner.unread();
				return ((char) c) == '=';
			}
		};
		rules.add(wordRule);

		token = createToken(TokenType.OTHER);
		// tag name
		rules.add(new WordRule(new WordDetector(), token, true));
		// special characters
		CharacterMapRule rule = new CharacterMapRule();
		rule.add('<', token);
		rule.add('>', token);
		rule.add('=', token);
		rules.add(rule);
		
		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(token);
	}

	protected IToken createToken(Object data)
	{
		return new Token(data);
	}

	private static class WordDetector implements IWordDetector
	{

		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c);
		}

		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}
	}
}
