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

package com.aptana.editor.haml.internal.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * @author Max Stepanov
 *
 */
public class RubyEvaluationElementRule implements IPredicateRule {

	private final WordRule wordRule;
	private final IToken successToken;
	
	public RubyEvaluationElementRule(IToken token) {
		successToken = token;
		wordRule = new WordRule(new RubyEvaluationElementWordDetector(), Token.UNDEFINED);
		wordRule.addWord("-", token); //$NON-NLS-1$
		wordRule.addWord("~", token); //$NON-NLS-1$
		wordRule.addWord("=", token); //$NON-NLS-1$
		wordRule.addWord("&=", token); //$NON-NLS-1$
		wordRule.addWord("&==", token); //$NON-NLS-1$
		wordRule.addWord("!=", token); //$NON-NLS-1$
		wordRule.addWord("!==", token); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	public IToken getSuccessToken() {
		return successToken;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner, boolean)
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (!resume) {
			int index = 0;
			int c;
			while ((c = scanner.read()) != ICharacterScanner.EOF && isWhitespace(c)) {
				++index;
			}
			if (c != ICharacterScanner.EOF) {
				scanner.unread();
			}
			IToken token = wordRule.evaluate(scanner);
			if (token.isUndefined()) {
				for (int j = index; j > 0; --j) {
					scanner.unread();
				}
			}
			return token;
		}
		return Token.UNDEFINED;
	}
	
	private static boolean isWhitespace(int c) {
		return (c == ' ') || (c == '\t');
	}

}

/* package */ class RubyEvaluationElementWordDetector implements IWordDetector {

	private static final char EQUAL = '=';
	private static final char TILDA = '~';
	private static final char DASH = '-';
	private static final char AMPERSAND = '&';
	private static final char EXCLAMATION = '&';
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	public boolean isWordStart(char c) {
		return AMPERSAND == c || EXCLAMATION == c || EQUAL == c || TILDA == c || DASH == c;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	public boolean isWordPart(char c) {
		return EQUAL ==c;
	}

}