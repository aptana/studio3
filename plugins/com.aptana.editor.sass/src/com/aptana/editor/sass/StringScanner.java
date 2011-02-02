/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.sass;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.RegexpRule;

public class StringScanner extends BufferedRuleBasedScanner
{

	public StringScanner(String defaultTokenType)
	{
		List<IRule> rules = new ArrayList<IRule>();
		rules.add(new RegexpRule("\\.", getToken("constant.character.escape.sass"), true)); //$NON-NLS-1$ //$NON-NLS-2$
		setRules(rules.toArray(new IRule[rules.size()]));

		setDefaultReturnToken(getToken(defaultTokenType));
	}

	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}
}
