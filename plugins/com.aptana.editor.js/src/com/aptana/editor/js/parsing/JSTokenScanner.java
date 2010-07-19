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
package com.aptana.editor.js.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.js.IJSTokenScanner;
import com.aptana.editor.js.JSCodeScanner;
import com.aptana.editor.js.parsing.lexer.JSTokenType;

/**
 * @author Michael Xia
 */
public class JSTokenScanner extends JSCodeScanner implements IJSTokenScanner
{
	@SuppressWarnings("nls")
	private static String[] GRAMMAR_KEYWORDS = { "function", "var", "void", "true", "false", "null", "this" };
	private static String VAR_CONST = "const"; //$NON-NLS-1$

	private IToken fToken;

	/**
	 * JSTokenScanner
	 */
	public JSTokenScanner()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.JSCodeScanner#initRules()
	 */
	@Override
	protected void initRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// comments and documentation
		rules.add(new EndOfLineRule("///", createToken(JSTokenType.VSDOC))); //$NON-NLS-1$
		rules.add(new EndOfLineRule("//", createToken(JSTokenType.SINGLELINE_COMMENT))); //$NON-NLS-1$
		rules.add(new MultiLineRule("/**", "*/", createToken(JSTokenType.SDOC), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("/*", "*/", createToken(JSTokenType.MULTILINE_COMMENT))); //$NON-NLS-1$ //$NON-NLS-2$

		// quoted strings
		IToken token = createToken(JSTokenType.STRING);
		rules.add(new SingleLineRule("\"", "\"", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new SingleLineRule("\'", "\'", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// regex
		rules.add(new JSRegExpRule(createToken(JSTokenType.REGEX)));

		WordRule wordRule = new WordRule(new LettersAndDigitsWordDetector(), Token.UNDEFINED);
		for (String keyword : KEYWORD_OPERATORS)
		{
			JSTokenType type = JSTokenType.get(keyword);

			wordRule.addWord(keyword, createToken(type));
		}
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), SUPPORT_FUNCTIONS);
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), EVENT_HANDLER_FUNCTIONS);
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), DOM_FUNCTIONS);
		rules.add(wordRule);

		// operators
		wordRule = new WordRule(new OperatorDetector(), Token.UNDEFINED);
		for (String operator : OPERATORS)
		{
			JSTokenType type = JSTokenType.get(operator);

			wordRule.addWord(operator, createToken(type));
		}
		rules.add(wordRule);

		for (char operator : SINGLE_CHARACTER_OPERATORS)
		{
			JSTokenType type = JSTokenType.get(Character.toString(operator));

			rules.add(new SingleCharacterRule(operator, createToken(type)));
		}

		// other keywords, types, and constants
		wordRule = new WordRule(new WordDetector(), Token.UNDEFINED);
		for (String keyword : KEYWORD_CONTROL)
		{
			JSTokenType type = JSTokenType.get(keyword);

			wordRule.addWord(keyword, createToken(type));
		}
		for (String keyword : GRAMMAR_KEYWORDS)
		{
			JSTokenType type = JSTokenType.get(keyword);

			wordRule.addWord(keyword, createToken(type));
		}
		wordRule.addWord(VAR_CONST, createToken(JSTokenType.VAR));
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), SUPPORT_CLASSES);
		addWordRules(wordRule, createToken(JSTokenType.IDENTIFIER), SUPPORT_DOM_CONSTANTS);
		rules.add(wordRule);

		// punctuation
		rules.add(new SingleCharacterRule(';', createToken(JSTokenType.SEMICOLON)));
		rules.add(new SingleCharacterRule('(', createToken(JSTokenType.LPAREN)));
		rules.add(new SingleCharacterRule(')', createToken(JSTokenType.RPAREN)));
		rules.add(new SingleCharacterRule('[', createToken(JSTokenType.LBRACKET)));
		rules.add(new SingleCharacterRule(']', createToken(JSTokenType.RBRACKET)));
		rules.add(new SingleCharacterRule('{', createToken(JSTokenType.LCURLY)));
		rules.add(new SingleCharacterRule('}', createToken(JSTokenType.RCURLY)));
		rules.add(new SingleCharacterRule(',', createToken(JSTokenType.COMMA)));
		rules.add(new SingleCharacterRule(':', createToken(JSTokenType.COLON)));
		rules.add(new SingleCharacterRule('.', createToken(JSTokenType.DOT)));
		rules.add(new SingleCharacterRule('?', createToken(JSTokenType.QUESTION)));

		// numbers
		rules.add(new JSNumberRule(createToken(JSTokenType.NUMBER)));

		// identifiers
		rules.add(new WordRule(new JSIdentifierDetector(), createToken(JSTokenType.IDENTIFIER)));

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.IJSTokenScanner#hasDivisionStart()
	 */
	@Override
	public boolean hasDivisionStart()
	{
		if (fToken == null || fToken.getData() == null)
		{
			return false;
		}

		JSTokenType tokenType = (JSTokenType) fToken.getData();
		switch (tokenType)
		{
			case IDENTIFIER:
			case NUMBER:
			case REGEX:
			case STRING:
			case RPAREN:
			case PLUS_PLUS:
			case MINUS_MINUS:
			case RBRACKET:
			case RCURLY:
			case FALSE:
			case NULL:
			case THIS:
			case TRUE:
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public void setRange(final IDocument document, int offset, int length)
	{
		fToken = null;
		super.setRange(document, offset, length);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.RuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken()
	{
		fToken = super.nextToken();
		return fToken;
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(JSTokenType type)
	{
		return new Token(type);
	}

	protected IToken createToken(String name)
	{
		System.out.println(name);
		return super.createToken(name);
	}

	private static class JSNumberRule extends ExtendedWordRule
	{
		private static final String REGEXP = "(0(x|X)[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?(?:[eE]\\d+)?)"; //$NON-NLS-1$
		private static Pattern pattern;

		JSNumberRule(IToken token)
		{
			super(new JSNumberDetector(), token, false);
		}

		@Override
		protected boolean wordOK(String word, ICharacterScanner scanner)
		{
			return getPattern().matcher(word).matches();
		}

		private synchronized static Pattern getPattern()
		{
			if (pattern == null)
			{
				pattern = Pattern.compile(REGEXP);
			}
			return pattern;
		}
	}

	private static class JSNumberDetector implements IWordDetector
	{
		@Override
		public boolean isWordStart(char c)
		{
			return Character.isDigit(c);
		}

		@Override
		public boolean isWordPart(char c)
		{
			if (isWordStart(c) || c == '.')
				return true;
			char lower = Character.toLowerCase(c);
			return lower == 'a' || lower == 'b' || lower == 'c' || lower == 'd' || lower == 'e' || lower == 'f' || lower == 'x';
		}
	}

	private static class JSIdentifierDetector implements IWordDetector
	{

		@Override
		public boolean isWordStart(char c)
		{
			return c == '_' || c == '$' || Character.isLetter(c);
		}

		@Override
		public boolean isWordPart(char c)
		{
			return isWordStart(c) || Character.isDigit(c);
		}
	}
}
