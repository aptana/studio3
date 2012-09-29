/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.PartitionerSwitchingIgnoreRule;
import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;

public class HTMLTokenScanner extends RuleBasedScanner
{

	private IRule generalTagRule;

	public HTMLTokenScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		// generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector(), createToken(getTokenName(HTMLTokens.TEXT))));
		// comments
		rules.add(new PartitionerSwitchingIgnoreRule(new MultiLineRule(
				"<!--", "-->", createToken(getTokenName(HTMLTokens.COMMENT)), (char) 0, true))); //$NON-NLS-1$ //$NON-NLS-2$
		// DOCTYPE
		IToken token = createToken(getTokenName(HTMLTokens.DOCTYPE));
		rules.add(new MultiLineRule("<!DOCTYPE ", ">", token)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("<!doctype ", ">", token)); //$NON-NLS-1$ //$NON-NLS-2$
		// CDATA
		rules.add(new MultiLineRule("<![CDATA[", "]]>", createToken(getTokenName(HTMLTokens.CDATA)))); //$NON-NLS-1$ //$NON-NLS-2$
		// script
		rules.add(new TagRule("script", createToken(getTokenName(HTMLTokens.SCRIPT)), true)); //$NON-NLS-1$
		rules.add(new TagRule("/script", createToken(getTokenName(HTMLTokens.SCRIPT_END)), true)); //$NON-NLS-1$
		// style
		rules.add(new TagRule("style", createToken(getTokenName(HTMLTokens.STYLE)), true)); //$NON-NLS-1$
		rules.add(new TagRule("/style", createToken(getTokenName(HTMLTokens.STYLE_END)), true)); //$NON-NLS-1$
		// xml declaration
		rules.add(new TagRule("?xml", createToken(getTokenName(HTMLTokens.XML_DECL)))); //$NON-NLS-1$
		// tags
		rules.add(new TagRule("/", createToken(getTokenName(HTMLTokens.END_TAG)))); //$NON-NLS-1$
		rules.add(generalTagRule = new HTMLParserTagRule(createToken(getTokenName(HTMLTokens.START_TAG))));

		// text
		token = createToken(getTokenName(HTMLTokens.TEXT));
		rules.add(new WordRule(new WordDetector(), token));

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(token);
	}

	public void setInsideSpecialTag(boolean special)
	{
		List<IRule> rules = new ArrayList<IRule>();
		rules.addAll(Arrays.asList(fRules));
		if (special)
		{
			rules.remove(generalTagRule);
		}
		else
		{
			rules.add(generalTagRule);
		}
		setRules(rules.toArray(new IRule[rules.size()]));
	}

	protected IToken createToken(String string)
	{
		return new Token(string);
	}

	private static String getTokenName(short token)
	{
		return HTMLTokens.getTokenName(token);
	}
}
