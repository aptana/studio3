/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

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

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.internal.text.rules.EqualOperatorWordDetector;
import com.aptana.editor.css.internal.text.rules.IdentifierWithPrefixDetector;
import com.aptana.editor.css.internal.text.rules.KeywordIdentifierDetector;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

/**
 * CSSTokenScanner
 */
public class CSSTokenScanner extends RuleBasedScanner
{
	public CSSTokenScanner()
	{
		initRules();
	}

	/**
	 * initRules
	 */
	protected void initRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// multi-line comments
		rules.add(new MultiLineRule("/*", "*/", createToken(CSSTokenType.COMMENT), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$

		// strings
		rules.add(new SingleLineRule("\"", "\"", createToken(CSSTokenType.DOUBLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new SingleLineRule("\'", "\'", createToken(CSSTokenType.SINGLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// at-keywords
		WordRule atRule = new WordRule(new IdentifierWithPrefixDetector('@'), createToken(CSSTokenType.AT_RULE));
		atRule.addWord("@import", createToken(CSSTokenType.IMPORT));
		atRule.addWord("@page", createToken(CSSTokenType.PAGE));
		atRule.addWord("@media", createToken(CSSTokenType.MEDIA_KEYWORD));
		atRule.addWord("@charset", createToken(CSSTokenType.CHARSET));
		atRule.addWord("@font-face", createToken(CSSTokenType.FONTFACE));
		atRule.addWord("@namespace", createToken(CSSTokenType.NAMESPACE));
		rules.add(atRule);

		// units
		rules.addAll(createUnitRules());

		// numbers
		rules.add(createNumberRule());

		// hex colors
		// TODO: we need separate scanners for inside and outside of rules. This will erroneouly pick up some ids as
		// well
		rules.add(createHexColorRule());

		// classes;
		rules.add(new WordRule(new IdentifierWithPrefixDetector('.'), createToken(CSSTokenType.CLASS)));

		// ids
		rules.add(new WordRule(new IdentifierWithPrefixDetector('#'), createToken(CSSTokenType.ID)));

		// !important
		rules.add(createImportantRule());

		// url
		// FIXME Don't use a RegexpRule here!
		rules.add(new RegexpRule("url\\([^)]*\\)", createToken(CSSTokenType.URL), true)); //$NON-NLS-1$

		// TODO: functions

		// TODO: Unicode

		// identifiers
		rules.add(createIdentifierRule());

		// single character punctuators
		CharacterMapRule punctuatorRule = new CharacterMapRule();
		punctuatorRule.add(':', createToken(CSSTokenType.COLON));
		punctuatorRule.add(';', createToken(CSSTokenType.SEMICOLON));
		punctuatorRule.add('{', createToken(CSSTokenType.LCURLY));
		punctuatorRule.add('}', createToken(CSSTokenType.RCURLY));
		punctuatorRule.add('(', createToken(CSSTokenType.LPAREN));
		punctuatorRule.add(')', createToken(CSSTokenType.RPAREN));
		punctuatorRule.add('%', createToken(CSSTokenType.PERCENTAGE)); // ?
		punctuatorRule.add('[', createToken(CSSTokenType.LBRACKET));
		punctuatorRule.add(']', createToken(CSSTokenType.RBRACKET));
		punctuatorRule.add(',', createToken(CSSTokenType.COMMA));
		punctuatorRule.add('+', createToken(CSSTokenType.PLUS));
		punctuatorRule.add('*', createToken(CSSTokenType.STAR));
		punctuatorRule.add('>', createToken(CSSTokenType.GREATER));
		punctuatorRule.add('/', createToken(CSSTokenType.SLASH));
		punctuatorRule.add('=', createToken(CSSTokenType.EQUAL));
		punctuatorRule.add('-', createToken(CSSTokenType.MINUS));
		rules.add(punctuatorRule);

		// multi-character punctuators
		WordRule punctuatorRule2 = new WordRule(new EqualOperatorWordDetector(), Token.UNDEFINED);
		punctuatorRule2.addWord("~=", createToken(CSSTokenType.INCLUDES));
		punctuatorRule2.addWord("|=", createToken(CSSTokenType.DASHMATCH));
		punctuatorRule2.addWord("^=", createToken(CSSTokenType.BEGINS_WITH));
		punctuatorRule2.addWord("$=", createToken(CSSTokenType.ENDS_WITH));
		rules.add(punctuatorRule2);

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/**
	 * createImportantRule
	 * 
	 * @return
	 */
	protected IRule createImportantRule()
	{
		return new ExtendedWordRule(new IWordDetector()
		{

			public boolean isWordStart(char c)
			{
				return c == '!';
			}

			public boolean isWordPart(char c)
			{
				return isWordStart(c) || Character.isLetterOrDigit(c) || Character.isWhitespace(c);
			}
		}, createToken(CSSTokenType.IMPORTANT), false)
		{

			private Pattern pattern;

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (pattern == null)
				{
					pattern = Pattern.compile("!\\s*important"); //$NON-NLS-1$
				}
				return pattern.matcher(word).matches();
			}
		};
	}

	/**
	 * createNumberRule
	 * 
	 * @return
	 */
	protected IRule createNumberRule()
	{
		return new ExtendedWordRule(new IWordDetector()
		{
			public boolean isWordStart(char c)
			{
				return c == '-' || c == '+' || c == '.' || Character.isDigit(c);
			}

			public boolean isWordPart(char c)
			{
				return c == '.' || Character.isDigit(c);
			}
		}, createToken(CSSTokenType.NUMBER), false)
		{
			private Pattern pattern;

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (pattern == null)
				{
					pattern = Pattern.compile("[-+]?\\s*[0-9]+(\\.[0-9]+)?"); //$NON-NLS-1$
				}

				return pattern.matcher(word).matches();
			}
		};
	}

	protected IRule createHexColorRule()
	{
		return new ExtendedWordRule(new IdentifierWithPrefixDetector('#'), createToken(CSSTokenType.RGB), false)
		{
			private final Pattern HEX_COLOR = Pattern.compile("#[0-9a-fA-F]+");

			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				boolean result = false;

				if (word.length() == 4 || word.length() == 7)
				{
					result = HEX_COLOR.matcher(word).matches();
				}

				return result;
			}
		};
	}

	/**
	 * createIdentifierRule
	 * 
	 * @return
	 */
	protected IRule createIdentifierRule()
	{
		return new ExtendedWordRule(new KeywordIdentifierDetector(), createToken(CSSTokenType.IDENTIFIER), false)
		{
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (word == null || word.length() == 0)
				{
					return false;
				}
				if (word.charAt(0) == '-')
				{
					return word.length() > 1;
				}
				return true;
			}
		};
	}

	/**
	 * createUnitRules
	 * 
	 * @return
	 */
	protected Collection<? extends IRule> createUnitRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		
		// FIXME These are all really just numbers followed by measurements. Can't we modify scanner/parser to grab number and then a measurement
		// XXX: The number and the units have to be connected without intermediate whitespace. Alternately, we could
		// make the parser changes as suggested but we would need to make sure the validators point out the error
		// condition

		rules.add(createUnitRule("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)em", CSSTokenType.EMS)); //$NON-NLS-1$
		rules.add(createUnitRule("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(px|cm|mm|in|pt|pc)", CSSTokenType.LENGTH)); //$NON-NLS-1$
		rules.add(createUnitRule("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)%", CSSTokenType.PERCENTAGE)); //$NON-NLS-1$
		rules.add(createUnitRule("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)(deg|rad|grad)", CSSTokenType.ANGLE)); //$NON-NLS-1$
		rules.add(createUnitRule("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)ex", CSSTokenType.EXS)); //$NON-NLS-1$
		rules.add(createUnitRule("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)k?[Hh]z", CSSTokenType.FREQUENCY)); //$NON-NLS-1$
		rules.add(createUnitRule("[-+]?([0-9]+(\\.[0-9]+)?|\\.[0-9]+)m?s", CSSTokenType.TIME)); //$NON-NLS-1$

		return rules;
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
			public boolean isWordStart(char c)
			{
				return c == '-' || c == '+' || c == '.' || Character.isDigit(c);
			}

			public boolean isWordPart(char c)
			{
				return c == '.' || c == '%' ||Character.isLetterOrDigit(c);
			}
		}, createToken(tokenType), false)
		{
			
			private Pattern pattern;

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				if (pattern == null)
				{
					pattern = Pattern.compile(regex);
				}

				return pattern.matcher(word).matches();
			}
		};
	}

	protected IToken createToken(CSSTokenType type)
	{
		return new Token(type);
	}
}
