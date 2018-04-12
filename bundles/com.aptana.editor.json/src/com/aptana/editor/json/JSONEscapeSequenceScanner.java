/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.json.text.rules.JSONEscapeSequenceRule;

public class JSONEscapeSequenceScanner extends BufferedRuleBasedScanner
{
	/**
	 * JSEscapeSequenceScanner
	 */
	public JSONEscapeSequenceScanner(String defaultScope)
	{
		IRule[] rules = new IRule[] { //
			new JSONEscapeSequenceRule(getToken("constant.character.escape.json")) //$NON-NLS-1$
		};

		setRules(rules);

		setDefaultReturnToken(getToken(defaultScope));
	}

	/**
	 * getToken
	 * 
	 * @param tokenName
	 * @return
	 */
	private static IToken getToken(String tokenName)
	{
		return CommonUtil.getToken(tokenName);
	}
}
