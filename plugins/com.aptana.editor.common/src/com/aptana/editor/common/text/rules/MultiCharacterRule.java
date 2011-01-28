/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * Optimized rule to match a a sequence of characters. Faster than a RegexpRule for fixed sequence.
 * 
 * @author Max Stepanov
 */
public class MultiCharacterRule implements IPredicateRule {

	private IToken successToken;
	private char[] sequence;

	public MultiCharacterRule(String sequence, IToken successToken) {
		this(sequence.toCharArray(), successToken);
	}

	public MultiCharacterRule(char[] sequence, IToken successToken) {
		this.sequence = sequence;
		this.successToken = successToken;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		int index = 0;
		for (char c : sequence) {
			++index;
			if (c != scanner.read()) {
				for (int j = index; j > 0; --j) {
					scanner.unread();
				}
				return Token.UNDEFINED;
			}
		}
		return getSuccessToken();
	}

	public IToken getSuccessToken() {
		return successToken;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}
}
