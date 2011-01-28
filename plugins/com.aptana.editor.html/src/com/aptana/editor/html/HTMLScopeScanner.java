/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public class HTMLScopeScanner extends HTMLTagScanner
{
	/**
	 * HTMLScopeScanner
	 */
	public HTMLScopeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// add custom rules with higher precedence here

		// Add the rules created by the super class first so they have higher
		// precedence
		if (fRules != null)
		{
			rules.addAll(Arrays.asList(fRules));
		}

		// add custom rules with lower precedence here
		rules.add(new RegexpRule("/>", createToken(HTMLTokenType.TAG_SELF_CLOSE))); //$NON-NLS-1$

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(HTMLTokenType type)
	{
		return this.createToken(type.getScope());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.CSSCodeScanner#createToken(java.lang.String)
	 */
	@Override
	protected IToken createToken(String string)
	{
		// Simply use the token type string that is passed in as the data
		return new Token(string);
	}
}
