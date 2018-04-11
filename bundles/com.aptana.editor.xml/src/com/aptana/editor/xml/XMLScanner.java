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
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.text.rules.EntityRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.xml.core.IXMLScopes;

public class XMLScanner extends RuleBasedScanner implements IXMLScopes
{

	public XMLScanner()
	{
		IRule[] rules = new IRule[4];
		rules[0] = new WhitespaceRule(new WhitespaceDetector());
		rules[1] = new EntityRule(createToken(CONSTANT_CHARACTER_ENTITY_XML));
		// non-entity ampersands should be marked as invalid
		rules[2] = new SingleCharacterRule('&', createToken(INVALID_ILLEGAL_BAD_AMPERSAND_XML));
		rules[3] = new WordRule(new WordDetector(), createToken(TEXT));
		setRules(rules);
		setDefaultReturnToken(createToken(TEXT));
	}

	protected IToken createToken(String string)
	{
		return CommonUtil.getToken(string);
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
