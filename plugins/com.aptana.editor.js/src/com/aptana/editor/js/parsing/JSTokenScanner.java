/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.js.JSCodeScanner;
import com.aptana.editor.js.parsing.lexer.JSTokens;

/**
 * @author Michael Xia
 */
public class JSTokenScanner extends JSCodeScanner
{

	@SuppressWarnings("nls")
	private static String[] GRAMMAR_KEYWORDS = { "function", "var", "void", "true", "false", "null", "this" };

	private static String VAR_CONST = "const"; //$NON-NLS-1$

	public JSTokenScanner()
	{
	}

	@Override
	protected void initRules()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		// comments
		rules.add(new EndOfLineRule("//", createToken(getTokenName(JSTokens.SINGLELINE_COMMENT)))); //$NON-NLS-1$
		rules.add(new MultiLineRule("/*", "*/", createToken(getTokenName(JSTokens.MULTILINE_COMMENT)))); //$NON-NLS-1$ //$NON-NLS-2$
		// documentation
		rules.add(new MultiLineRule("/**", "*/", createToken(getTokenName(JSTokens.DOC)), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		// quoted strings
		IToken token = createToken(getTokenName(JSTokens.STRING));
		rules.add(new SingleLineRule("\"", "\"", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new SingleLineRule("\'", "\'", token, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		// regex
		rules.add(new RegexpRule("/([^/]|\\\\/)*?([^/\\\\]+|\\\\\\\\)/[igm]*", createToken(getTokenName(JSTokens.REGEX)), true)); //$NON-NLS-1$

		WordRule wordRule = new WordRule(new LettersAndDigitsWordDetector(), Token.UNDEFINED);
		for (String keyword : KEYWORD_OPERATORS)
		{
			wordRule.addWord(keyword, createToken(keyword));
		}
		String identifier = getTokenName(JSTokens.IDENTIFIER);
		addWordRules(wordRule, createToken(identifier), SUPPORT_FUNCTIONS);
		addWordRules(wordRule, createToken(identifier), EVENT_HANDLER_FUNCTIONS);
		addWordRules(wordRule, createToken(identifier), DOM_FUNCTIONS);
		rules.add(wordRule);

		// operators
		wordRule = new WordRule(new OperatorDetector(), Token.UNDEFINED);
		for (String operator : OPERATORS)
		{
			wordRule.addWord(operator, createToken(operator));
		}
		rules.add(wordRule);

		for (char operator : SINGLE_CHARACTER_OPERATORS)
		{
			rules.add(new SingleCharacterRule(operator, createToken(String.valueOf(operator))));
		}

		// other keywords, types, and constants
		wordRule = new WordRule(new WordDetector(), Token.UNDEFINED);
		for (String keyword : KEYWORD_CONTROL)
		{
			wordRule.addWord(keyword, createToken(keyword));
		}
		for (String keyword : GRAMMAR_KEYWORDS)
		{
			wordRule.addWord(keyword, createToken(keyword));
		}
		wordRule.addWord(VAR_CONST, createToken(getTokenName(JSTokens.VAR)));
		addWordRules(wordRule, createToken(identifier), SUPPORT_CLASSES);
		addWordRules(wordRule, createToken(identifier), SUPPORT_DOM_CONSTANTS);
		rules.add(wordRule);

		// punctuation
		rules.add(new SingleCharacterRule(';', createToken(getTokenName(JSTokens.SEMICOLON))));
		rules.add(new SingleCharacterRule('(', createToken(getTokenName(JSTokens.LPAREN))));
		rules.add(new SingleCharacterRule(')', createToken(getTokenName(JSTokens.RPAREN))));
		rules.add(new SingleCharacterRule('[', createToken(getTokenName(JSTokens.LBRACKET))));
		rules.add(new SingleCharacterRule(']', createToken(getTokenName(JSTokens.RBRACKET))));
		rules.add(new SingleCharacterRule('{', createToken(getTokenName(JSTokens.LCURLY))));
		rules.add(new SingleCharacterRule('}', createToken(getTokenName(JSTokens.RCURLY))));
		rules.add(new SingleCharacterRule(',', createToken(getTokenName(JSTokens.COMMA))));
		rules.add(new SingleCharacterRule(':', createToken(getTokenName(JSTokens.COLON))));
		rules.add(new SingleCharacterRule('.', createToken(getTokenName(JSTokens.DOT))));
		rules.add(new SingleCharacterRule('?', createToken(getTokenName(JSTokens.QUESTION))));

		// numbers
		rules.add(new RegexpRule("\\b((0(x|X)[0-9a-fA-F]+)|([0-9]+(\\.[0-9]+)?))\\b", //$NON-NLS-1$
				createToken(getTokenName(JSTokens.NUMBER))));

		// identifiers
		rules.add(new RegexpRule("[_a-zA-Z0-9$]+", createToken(identifier), true)); //$NON-NLS-1$

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	@Override
	protected IToken createToken(String string)
	{
		return new Token(string);
	}

	private static String getTokenName(short token)
	{
		return JSTokens.getTokenName(token);
	}
}
