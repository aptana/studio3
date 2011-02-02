/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.markdown.text.rules;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.CharacterMapRule;

public class MarkdownHeadingScanner extends RuleBasedScanner
{
	public MarkdownHeadingScanner()
	{
		CharacterMapRule rule = new CharacterMapRule();
		IToken token = getToken("punctuation.definition.heading.markdown"); //$NON-NLS-1$
		rule.add('#', token);
		rule.add('=', token);
		rule.add('-', token);
		IRule[] rules = new IRule[] { rule };
		setRules(rules);
		setDefaultReturnToken(getToken("entity.name.section.markdown")); //$NON-NLS-1$
	}

	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

}
