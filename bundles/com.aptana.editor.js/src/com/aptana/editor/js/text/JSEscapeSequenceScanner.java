/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.js.text.rules.JSEscapeSequenceRule;

public class JSEscapeSequenceScanner extends BufferedRuleBasedScanner
{
	/**
	 * JSEscapeSequenceScanner
	 */
	public JSEscapeSequenceScanner(String defaultScope)
	{
		IRule[] rules = new IRule[] { new JSEscapeSequenceRule(getToken("constant.character.escape.js")) }; //$NON-NLS-1$

		setRules(rules);

		setDefaultReturnToken(getToken(defaultScope));
	}

	/**
	 * getToken
	 * 
	 * @param tokenName
	 * @return
	 */
	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}
}
