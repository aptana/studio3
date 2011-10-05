/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.MultiCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.html.internal.text.rules.AttributeNameWordDetector;
import com.aptana.editor.html.internal.text.rules.TagNameWordDetector;
import com.aptana.editor.html.internal.text.rules.TagWordRule;

class HTMLTagScanner extends RuleBasedScanner
{

	public enum TokenType
	{
		ATTR_NAME, ATTR_VALUE, OTHER
	}

	HTMLTagScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// whitespaces
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// attribute values
		IToken attrValueToken = createToken(TokenType.ATTR_VALUE);
		rules.add(new MultiLineRule("\"", "\"", attrValueToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("'", "'", attrValueToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		IToken otherToken = createToken(TokenType.OTHER);
		
		// tag name
		rules.add(new TagWordRule(new TagNameWordDetector(), otherToken, true));

		// attribute names
		rules.add(new WordRule(new AttributeNameWordDetector(), createToken(TokenType.ATTR_NAME), true));

		rules.add(new MultiCharacterRule("</", otherToken)); //$NON-NLS-1$
		rules.add(new MultiCharacterRule("/>", otherToken)); //$NON-NLS-1$

		// special characters
		CharacterMapRule rule = new CharacterMapRule();
		rule.add('<', otherToken);
		rule.add('>', otherToken);
		rule.add('=', otherToken);
		rules.add(rule);
		
		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(otherToken);
	}

	protected IToken createToken(Object data)
	{
		return new Token(data);
	}
}
