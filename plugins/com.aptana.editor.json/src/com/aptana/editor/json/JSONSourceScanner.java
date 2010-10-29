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
package com.aptana.editor.json;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.json.parsing.lexer.JSONTokenType;
import com.aptana.editor.json.text.rules.JSONNumberRule;
import com.aptana.editor.json.text.rules.JSONPropertyRule;

@SuppressWarnings("nls")
public class JSONSourceScanner extends RuleBasedScanner
{
	public class KeywordDetector implements IWordDetector
	{
		public boolean isWordPart(char c)
		{
			return isWordStart(c);
		}

		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}
	}

	/**
	 * JSONSourceScanner
	 */
	public JSONSourceScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		rules.add(new EndOfLineRule("//", createToken(JSONTokenType.COMMENT)));
		rules.add(new MultiLineRule("/*", "*/", createToken(JSONTokenType.COMMENT)));

		rules.add( //
			new JSONPropertyRule( //
				createToken(JSONTokenType.STRING_SINGLE), //
				createToken(JSONTokenType.STRING_DOUBLE), //
				createToken(JSONTokenType.PROPERTY) //
			) //
		);

		WordRule keywordRule = new WordRule(new KeywordDetector(), Token.UNDEFINED);
		keywordRule.addWord("true", createToken(JSONTokenType.TRUE));
		keywordRule.addWord("false", createToken(JSONTokenType.FALSE));
		keywordRule.addWord("null", createToken(JSONTokenType.NULL));
		rules.add(keywordRule);

		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('{', createToken(JSONTokenType.LCURLY));
		cmRule.add('}', createToken(JSONTokenType.RCURLY));
		cmRule.add('[', createToken(JSONTokenType.LBRACKET));
		cmRule.add(']', createToken(JSONTokenType.RBRACKET));
		cmRule.add(',', createToken(JSONTokenType.COMMA));
		cmRule.add(':', createToken(JSONTokenType.COLON));
		rules.add(cmRule);

		// Numbers
		rules.add(new JSONNumberRule(createToken(JSONTokenType.NUMBER)));

		this.setRules(rules.toArray(new IRule[rules.size()]));
		// this.setDefaultReturnToken(this.createToken("text"));
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(JSONTokenType type)
	{
		return new Token(type.getScope());
	}
}
