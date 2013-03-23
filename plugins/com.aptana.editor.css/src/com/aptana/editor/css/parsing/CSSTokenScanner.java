/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.internal.text.rules.CSSHexColorRule;
import com.aptana.editor.css.internal.text.rules.CSSIdentifierRule;
import com.aptana.editor.css.internal.text.rules.CSSImportantRule;
import com.aptana.editor.css.internal.text.rules.CSSNumberRule;
import com.aptana.editor.css.internal.text.rules.CSSURLRule;
import com.aptana.editor.css.internal.text.rules.EqualOperatorWordDetector;
import com.aptana.editor.css.internal.text.rules.IdentifierWithPrefixDetector;

/**
 * CSSTokenScanner
 */
@SuppressWarnings("nls")
public class CSSTokenScanner extends RuleBasedScanner
{
	private static Pattern CLASS_IS_NUMBER_PATTERN = Pattern.compile("\\.[0-9]+");
	private boolean _inMediaRule;
	private int _curlyBraceCount;

	/**
	 * CSSTokenScanner
	 */
	public CSSTokenScanner()
	{
		List<IRule> rules = createRules();

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/**
	 * createAtWordsRule
	 * 
	 * @return
	 */
	private WordRule createAtWordsRule()
	{
		WordRule atRule = new WordRule(new IdentifierWithPrefixDetector('@'), createToken(CSSTokenType.AT_RULE), true);

		atRule.addWord("@import", createToken(CSSTokenType.IMPORT));
		atRule.addWord("@page", createToken(CSSTokenType.PAGE));
		atRule.addWord("@media", createToken(CSSTokenType.MEDIA_KEYWORD));
		atRule.addWord("@charset", createToken(CSSTokenType.CHARSET));
		atRule.addWord("@font-face", createToken(CSSTokenType.FONTFACE));
		atRule.addWord("@namespace", createToken(CSSTokenType.NAMESPACE));

		return atRule;
	}

	/**
	 * createPuncturatorsRule
	 * 
	 * @return
	 */
	protected CharacterMapRule createPunctuatorsRule()
	{
		CharacterMapRule punctuatorsRule = new CharacterMapRule();

		punctuatorsRule.add(':', createToken(CSSTokenType.COLON));
		punctuatorsRule.add(';', createToken(CSSTokenType.SEMICOLON));
		punctuatorsRule.add('{', createToken(CSSTokenType.LCURLY));
		punctuatorsRule.add('}', createToken(CSSTokenType.RCURLY));
		punctuatorsRule.add('(', createToken(CSSTokenType.LPAREN));
		punctuatorsRule.add(')', createToken(CSSTokenType.RPAREN));
		punctuatorsRule.add('%', createToken(CSSTokenType.PERCENTAGE)); // ?
		punctuatorsRule.add('[', createToken(CSSTokenType.LBRACKET));
		punctuatorsRule.add(']', createToken(CSSTokenType.RBRACKET));
		punctuatorsRule.add(',', createToken(CSSTokenType.COMMA));
		punctuatorsRule.add('+', createToken(CSSTokenType.PLUS));
		punctuatorsRule.add('*', createToken(CSSTokenType.STAR));
		punctuatorsRule.add('>', createToken(CSSTokenType.GREATER));
		punctuatorsRule.add('/', createToken(CSSTokenType.SLASH));
		punctuatorsRule.add('=', createToken(CSSTokenType.EQUAL));
		punctuatorsRule.add('-', createToken(CSSTokenType.MINUS));

		return punctuatorsRule;
	}

	/**
	 * createRules
	 * 
	 * @return
	 */
	protected List<IRule> createRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// multi-line comments
		rules.add(new MultiLineRule("/*", "*/", createToken(CSSTokenType.COMMENT), (char) 0, true));

		// strings
		rules.addAll(createStringRules());

		// at-keywords
		rules.add(createAtWordsRule());

		// units
		rules.addAll(createUnitRules());

		// numbers
		rules.add(new CSSNumberRule(createToken(CSSTokenType.NUMBER)));

		// hex colors
		// TODO: we need separate scanners for inside and outside of rules. This will erroneously pick up some ids as
		// well
		rules.add(new CSSHexColorRule(createToken(CSSTokenType.RGB)));

		// classes;
		rules.add(new WordRule(new IdentifierWithPrefixDetector('.'), createToken(CSSTokenType.CLASS)));

		// ids
		rules.add(new WordRule(new IdentifierWithPrefixDetector('#'), createToken(CSSTokenType.ID)));

		// !important
		rules.add(new CSSImportantRule(createToken(CSSTokenType.IMPORTANT)));

		// url
		rules.add(new CSSURLRule(createToken(CSSTokenType.URL)));

		// TODO: functions

		// TODO: Unicode

		// identifiers
		rules.add(new CSSIdentifierRule(createToken(CSSTokenType.IDENTIFIER)));

		// single character punctuators
		rules.add(createPunctuatorsRule());

		// multi-character punctuators
		WordRule punctuatorRule2 = new WordRule(new EqualOperatorWordDetector(), Token.UNDEFINED);
		punctuatorRule2.addWord("~=", createToken(CSSTokenType.INCLUDES));
		punctuatorRule2.addWord("|=", createToken(CSSTokenType.DASHMATCH));
		punctuatorRule2.addWord("^=", createToken(CSSTokenType.BEGINS_WITH));
		punctuatorRule2.addWord("$=", createToken(CSSTokenType.ENDS_WITH));
		rules.add(punctuatorRule2);

		return rules;
	}

	/**
	 * createStringRules
	 * 
	 * @return
	 */
	private List<IRule> createStringRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new SingleLineRule("\"", "\"", createToken(CSSTokenType.DOUBLE_QUOTED_STRING), '\\'));
		rules.add(new SingleLineRule("\'", "\'", createToken(CSSTokenType.SINGLE_QUOTED_STRING), '\\'));

		return rules;
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(CSSTokenType type)
	{
		return new Token(type);
	}

	/**
	 * createMeasurementRule
	 * 
	 * @param regex
	 * @param tokenType
	 * @return
	 */
	private IRule createUnitRule(final String regex, CSSTokenType tokenType)
	{
		return new ExtendedWordRule(new IWordDetector()
		{
			public boolean isWordPart(char c)
			{
				return c == '.' || c == '%' || Character.isLetterOrDigit(c);
			}

			public boolean isWordStart(char c)
			{
				return c == '-' || c == '+' || c == '.' || Character.isDigit(c);
			}
		}, createToken(tokenType), true)
		{
			private Pattern pattern;

			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (pattern == null)
				{
					pattern = Pattern.compile("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(" + regex + ")",
							Pattern.CASE_INSENSITIVE);
				}

				return pattern.matcher(word).matches();
			}
		};
	}

	/**
	 * createUnitRules
	 * 
	 * @return
	 */
	protected List<IRule> createUnitRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// FIXME These are all really just numbers followed by measurements. Can't we modify scanner/parser to grab
		// number and then a measurement
		// XXX: The number and the units have to be connected without intermediate whitespace. Alternately, we could
		// make the parser changes as suggested but we would need to make sure the validators point out the error
		// condition

		rules.add(createUnitRule("em", CSSTokenType.EMS));
		rules.add(createUnitRule("px|cm|mm|in|pt|pc", CSSTokenType.LENGTH));
		rules.add(createUnitRule("%", CSSTokenType.PERCENTAGE));
		rules.add(createUnitRule("deg|rad|grad", CSSTokenType.ANGLE));
		rules.add(createUnitRule("ex", CSSTokenType.EXS));
		rules.add(createUnitRule("k?[Hh]z", CSSTokenType.FREQUENCY));
		rules.add(createUnitRule("m?s", CSSTokenType.TIME));

		return rules;
	}

	/**
	 * isOutsideRule
	 * 
	 * @return
	 */
	private boolean isOutsideRule()
	{
		return this._curlyBraceCount < (this._inMediaRule ? 2 : 1);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken()
	{
		IToken token = super.nextToken();
		Object data = token.getData();

		if (data instanceof CSSTokenType)
		{
			switch ((CSSTokenType) data)
			{
				case MEDIA_KEYWORD:
					this._inMediaRule = true;
					break;

				case LCURLY:
					this._curlyBraceCount++;
					break;

				case RCURLY:
					this._curlyBraceCount--;

					if (this._curlyBraceCount == 0 && this._inMediaRule)
					{
						this._inMediaRule = false;
					}
					break;

				case RGB:
					// fixup colors in selectors
					if (isOutsideRule())
					{
						token = createToken(CSSTokenType.ID);
					}
					break;

				case CLASS:
					// potentially fixup a class inside of a ruleset to be a number
					if (!isOutsideRule())
					{
						try
						{
							String text = fDocument.get(getTokenOffset(), getTokenLength());

							if (CLASS_IS_NUMBER_PATTERN.matcher(text).matches())
							{
								token = createToken(CSSTokenType.NUMBER);
							}
						}
						catch (BadLocationException e)
						{
							// ignore
						}
					}
					break;
			}
		}

		return token;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		super.setRange(document, offset, length);

		this._inMediaRule = false;
		this._curlyBraceCount = 0;
	}
}
