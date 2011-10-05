/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.dtd.text.rules.DTDNameDetector;
import com.aptana.editor.xml.parsing.lexer.XMLTokenType;
import com.aptana.editor.xml.text.rules.XMLAttributeRule;

class XMLAttributeScanner extends RuleBasedScanner
{
	/**
	 * XMLAttributeScanner
	 */
	XMLAttributeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// attribute values
		rules.add(new MultiLineRule("\"", "\"", createToken(XMLTokenType.VALUE), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("'", "'", createToken(XMLTokenType.VALUE), '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// attribute names
		rules.add(new XMLAttributeRule(createToken(XMLTokenType.ATTRIBUTE)));

		// tag name
		rules.add(new WordRule(new DTDNameDetector(), createToken(XMLTokenType.OTHER), true));

		// special characters
		CharacterMapRule characterRule = new CharacterMapRule();
		characterRule.add('<', createToken(XMLTokenType.OTHER));
		characterRule.add('>', createToken(XMLTokenType.OTHER));
		characterRule.add('=', createToken(XMLTokenType.OTHER));
		rules.add(characterRule);

		setRules(rules.toArray(new IRule[rules.size()]));

		setDefaultReturnToken(createToken(XMLTokenType.OTHER));
	}

	/**
	 * createToken
	 * 
	 * @param data
	 * @return
	 */
	protected IToken createToken(Object data)
	{
		return new Token(data);
	}

	/**
	 * getText
	 * 
	 * @return
	 */
	public String getText()
	{
		String result = null;

		try
		{
			result = this.fDocument.get(this.getTokenOffset(), this.getTokenLength());
		}
		catch (BadLocationException e)
		{
		}

		return result;
	}
}
