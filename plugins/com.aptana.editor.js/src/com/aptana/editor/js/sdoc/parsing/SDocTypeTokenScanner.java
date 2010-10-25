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

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

public class SDocTypeTokenScanner extends RuleBasedScanner
{
	static class IdentifierDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			boolean result = false;

			switch (c)
			{
				case '$':
				case '_':
					result = true;
					break;

				default:
					result = Character.isJavaIdentifierStart(c);
			}

			return result;
		}

		public boolean isWordPart(char c)
		{
			boolean result = false;

			switch (c)
			{
				case '$':
				case '_':
				case '.':
					result = true;
					break;

				default:
					result = Character.isJavaIdentifierPart(c);
			}

			return result;
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
					case '>':
					case '.':
						result = true;
						break;
				}
			}
			else if (fPosition == 2)
			{
				switch (c)
				{
					case '.':
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
				case '-':
				case '.':
					result = true;
					break;
			}

			return result;
		}
	}

	/**
	 * SDocTypeTokenScanner
	 */
	public SDocTypeTokenScanner()
	{
		List<IRule> rules = new LinkedList<IRule>();

		rules.add(new WordRule(new WhitespaceDetector(), getToken(SDocTokenType.WHITESPACE)));
		
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('(', getToken(SDocTokenType.LPAREN));
		cmRule.add(')', getToken(SDocTokenType.RPAREN));
		cmRule.add('{', getToken(SDocTokenType.LCURLY));
		cmRule.add('}', getToken(SDocTokenType.RCURLY));
		cmRule.add('[', getToken(SDocTokenType.LBRACKET));
		cmRule.add(']', getToken(SDocTokenType.RBRACKET));
		cmRule.add('<', getToken(SDocTokenType.LESS_THAN));
		cmRule.add('>', getToken(SDocTokenType.GREATER_THAN));
		cmRule.add(':', getToken(SDocTokenType.COLON));
		cmRule.add(',', getToken(SDocTokenType.COMMA));
		cmRule.add('|', getToken(SDocTokenType.PIPE));
		cmRule.add('\r', getToken(SDocTokenType.WHITESPACE));
		cmRule.add('\n', getToken(SDocTokenType.WHITESPACE));
		rules.add(cmRule);
		
		WordRule keywordRules = new WordRule(new IdentifierDetector(), getToken(SDocTokenType.IDENTIFIER));
		keywordRules.addWord("Array", getToken(SDocTokenType.ARRAY)); //$NON-NLS-1$
		keywordRules.addWord("Function", getToken(SDocTokenType.FUNCTION)); //$NON-NLS-1$
		keywordRules.addWord("Class", getToken(SDocTokenType.CLASS)); //$NON-NLS-1$
		rules.add(keywordRules);
		
		WordRule operatorRules = new WordRule(new OperatorDetector(), getToken(SDocTokenType.ERROR));
		operatorRules.addWord("...", getToken(SDocTokenType.ELLIPSIS)); //$NON-NLS-1$
		operatorRules.addWord("->", getToken(SDocTokenType.ARROW)); //$NON-NLS-1$
		rules.add(operatorRules);

		this.setDefaultReturnToken(getToken(SDocTokenType.ERROR));
		this.setRules(rules.toArray(new IRule[rules.size()]));
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
