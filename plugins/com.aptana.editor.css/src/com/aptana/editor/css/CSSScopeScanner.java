/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.EmptyCommentRule;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;

public class CSSScopeScanner extends CSSCodeScanner
{
	/**
	 * CSSScopeScanner
	 */
	public CSSScopeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		
		// Add the rules created by the super class first so they have higher
		// precedence
		if (fRules != null)
		{
			rules.addAll(Arrays.asList(fRules));
		}

		// Add the rules for block comments, single and double quoted strings
		rules.add(new SingleLineRule("\"", "\"", createToken(CSSTokenType.DOUBLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ 
		rules.add(new SingleLineRule("\'", "\'", createToken(CSSTokenType.SINGLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new EmptyCommentRule(createToken(CSSTokenType.COMMENT)));
		rules.add(new MultiLineRule("/*", "*/", createToken(CSSTokenType.COMMENT), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$
		
		// Add rules for the start characters of classes and ids
		CharacterMapRule rule = new CharacterMapRule();		
		rule.add('#', createToken(CSSTokenType.ID));
		rule.add('.', createToken(CSSTokenType.CLASS));
		rule.add(',', createToken(CSSTokenType.COMMA));
		rule.add('/', createToken(CSSTokenType.SLASH));
		rule.add('*', createToken(CSSTokenType.STAR));
		rules.add(rule);
		
		setRules(rules.toArray(new IRule[rules.size()]));
	}
	
	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(CSSTokenType type)
	{
		return new Token(type);
	}
}
