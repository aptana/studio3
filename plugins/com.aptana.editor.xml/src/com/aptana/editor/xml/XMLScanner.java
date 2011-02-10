/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.EntityRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;

public class XMLScanner extends RuleBasedScanner
{

	public XMLScanner()
	{
		IRule[] rules = new IRule[4];
		rules[0] = new WhitespaceRule(new WhitespaceDetector());
		rules[1] = new EntityRule(createToken("constant.character.entity.xml")); //$NON-NLS-1$
		// non-entity ampersands should be marked as invalid
		rules[2] = new SingleCharacterRule('&', createToken("invalid.illegal.bad-ampersand.xml")); //$NON-NLS-1$
		rules[3] = new WordRule(new WordDetector(), createToken("text")); //$NON-NLS-1$
		setRules(rules);
		setDefaultReturnToken(createToken("text")); //$NON-NLS-1$
	}

	protected IToken createToken(String string)
	{
		return new Token(string);
	}

	/**
	 * A key word detector.
	 */
	static class WordDetector implements IWordDetector
	{
		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c);
		}
	}
}
