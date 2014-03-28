/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * @author Max Stepanov
 *
 */
public class TagRule extends ResumableMultiLineRule {

	private static final IToken singleQuoteStringTOKEN = new Token("SQS"); //$NON-NLS-1$
	private static final IPredicateRule singleQuoteStringRule = new MultiLineRule("'", "'", singleQuoteStringTOKEN, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
	private static final IPredicateRule singleQuoteStringEOLRule = new EndOfLineRule("'", singleQuoteStringTOKEN, '\\'); //$NON-NLS-1$

	private static final IToken doubleQuoteStringTOKEN = new Token("DQS"); //$NON-NLS-1$
	private static final IPredicateRule doubleQuoteStringRule = new MultiLineRule("\"", "\"", doubleQuoteStringTOKEN, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
	private static final IPredicateRule doubleQuoteStringEOLRule = new EndOfLineRule("\"", doubleQuoteStringTOKEN, '\\'); //$NON-NLS-1$

	private final boolean fIgnoreCase;

	public TagRule(IToken token) {
		this("", token); //$NON-NLS-1$
	}

	public TagRule(String tag, IToken token) {
		this(tag, token, false);
	}

	public TagRule(String tag, IToken token, boolean ignoreCase) {
		this("<" + tag, ">", token, ignoreCase); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private TagRule(String startSequence, String endSequence, IToken token, boolean ignoreCase) {
		super(startSequence, endSequence, token, (char) 0, true);
		fIgnoreCase = ignoreCase;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.PatternRule#sequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner, char[], boolean)
	 */
	@Override
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		for (int i = 1; i < sequence.length; ++i) {
			int c = scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				break;
			}
			if ((fIgnoreCase && Character.toLowerCase(c) != Character.toLowerCase(sequence[i])) || (!fIgnoreCase && c != sequence[i])) {
				// Non-matching character detected, rewind the scanner back to
				// the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j = i - 1; j > 0; --j) {
					scanner.unread();
				}
				return false;
			}
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ResumableMultiLineRule#doDetectEndSequence(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean doDetectEndSequence(ICharacterScanner scanner) {
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (c == '\'') {
				scanner.unread();
				IToken token = singleQuoteStringRule.evaluate(scanner);
				if (token.isUndefined()) {
					token = singleQuoteStringEOLRule.evaluate(scanner);
				}
			} else if (c == '"') {
				scanner.unread();
				IToken token = doubleQuoteStringRule.evaluate(scanner);
				if (token.isUndefined()) {
					token = doubleQuoteStringEOLRule.evaluate(scanner);
				}
			} else if ((c == fEndSequence[0] && sequenceDetected(scanner, fEndSequence, fBreaksOnEOF))
					|| c == fStartSequence[0]) {
				if (c == fStartSequence[0]) {
					scanner.unread();
				}
				break;
			}
		}
		return true;
	}

}
