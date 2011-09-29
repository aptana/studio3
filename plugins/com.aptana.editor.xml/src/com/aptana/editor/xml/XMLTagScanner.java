/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.xml.internal.IXMLScopes;
import com.aptana.editor.xml.text.rules.XMLTagStartRule;

public class XMLTagScanner extends RuleBasedScanner implements IXMLScopes
{

	public XMLTagScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for double quotes
		rules.add(new MultiLineRule("\"", "\"", createToken(STRING_QUOTED_DOUBLE_XML), '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add a rule for single quotes
		rules.add(new MultiLineRule("'", "'", createToken(STRING_QUOTED_SINGLE_XML), '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Attributes
		WordRule wordRule = new ExtendedWordRule(new IWordDetector()
		{

			public boolean isWordPart(char c)
			{
				return Character.isLetter(c) || c == '-' || c == ':';
			}

			public boolean isWordStart(char c)
			{
				return Character.isLetter(c);
			}

		}, createToken(ENTITY_OTHER_ATTRIBUTE_NAME_XML), true)
		{
			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				int c = scanner.read();
				scanner.unread();
				return ((char) c) == '=';
			}
		};
		rules.add(wordRule);

		CharacterMapRule rule = new CharacterMapRule();
		rule.add('>', createToken(PUNCTUATION_DEFINITION_TAG_XML));
		rule.add('=', createToken(PUNCTUATION_SEPARATOR_KEY_VALUE_XML));
		rules.add(rule);

		rules.add(new XMLTagStartRule(createToken(PUNCTUATION_DEFINITION_TAG_XML)));

		// Tag names
		rules.add(new WordRule(new WordDetector(), createToken(ENTITY_NAME_TAG_XML), true));

		setRules(rules.toArray(new IRule[rules.size()]));
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
