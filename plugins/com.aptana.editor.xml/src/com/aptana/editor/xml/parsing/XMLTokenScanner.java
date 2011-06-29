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

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.TagRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.xml.internal.text.rules.DocTypeRule;
import com.aptana.editor.xml.parsing.lexer.XMLTokenType;

public class XMLTokenScanner extends RuleBasedScanner
{
	/**
	 * XMLTokenScanner
	 */
	public XMLTokenScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		rules.add(new MultiLineRule("<!--", "-->", createToken(XMLTokenType.COMMENT))); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new DocTypeRule(createToken(XMLTokenType.DOCTYPE), false));
		rules.add(new MultiLineRule("<![CDATA[", "]]>", createToken(XMLTokenType.CDATA))); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new TagRule("?xml", createToken(XMLTokenType.DECLARATION))); //$NON-NLS-1$
		rules.add(new TagRule("/", createToken(XMLTokenType.END_TAG))); //$NON-NLS-1$
		rules.add(new TagRule(createToken(XMLTokenType.START_TAG)));

		// text
		IToken token = createToken(XMLTokenType.TEXT);
		rules.add(new WordRule(new WordDetector(), token));

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(token);
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
}
