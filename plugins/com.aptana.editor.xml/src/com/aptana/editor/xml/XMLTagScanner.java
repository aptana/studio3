/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.CommonUtil;
import com.aptana.editor.common.text.rules.BreakingMultiLineRule;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.MultiCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.xml.text.rules.AttributeNameWordDetector;
import com.aptana.editor.xml.text.rules.BrokenStringRule;
import com.aptana.editor.xml.text.rules.TagNameWordDetector;
import com.aptana.editor.xml.text.rules.TagWordRule;
import com.aptana.xml.core.IXMLScopes;
import com.aptana.xml.core.parsing.XMLTokenType;

public class XMLTagScanner extends RuleBasedScanner implements IXMLScopes
{

	@SuppressWarnings("nls")
	private static final String[] QUOTED_STRING_BREAKS = { "/>", ">" };

	private final IToken doubleQuotedStringToken = createToken(XMLTokenType.DOUBLE_QUOTED_STRING);
	private final IToken singleQuotedStringToken = createToken(XMLTokenType.SINGLE_QUOTED_STRING);
	private final IToken equalToken = createToken(XMLTokenType.EQUAL);

	private boolean hasTokens;

	public XMLTagScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for double quotes
		rules.add(new MultiLineRule("\"", "\"", doubleQuotedStringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new BreakingMultiLineRule("\"", "\"", QUOTED_STRING_BREAKS, doubleQuotedStringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add a rule for single quotes
		rules.add(new MultiLineRule("'", "'", singleQuotedStringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new BreakingMultiLineRule("'", "'", QUOTED_STRING_BREAKS, singleQuotedStringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Tags
		WordRule tagWordRule = new TagWordRule(new TagNameWordDetector(), createToken(XMLTokenType.TAG_NAME), true);
		rules.add(tagWordRule);

		// Attributes
		WordRule attributeWordRule = new WordRule(new AttributeNameWordDetector(), createToken(XMLTokenType.ATTRIBUTE),
				true);
		rules.add(attributeWordRule);

		rules.add(new MultiCharacterRule("</", createToken(XMLTokenType.START_TAG))); //$NON-NLS-1$
		rules.add(new MultiCharacterRule("/>", createToken(XMLTokenType.TAG_SELF_CLOSE))); //$NON-NLS-1$

		CharacterMapRule charsRule = new CharacterMapRule();
		charsRule.add('<', createToken(XMLTokenType.START_TAG));
		charsRule.add('>', createToken(XMLTokenType.END_TAG));
		charsRule.add('=', equalToken);
		rules.add(charsRule);

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(createToken(XMLTokenType.TEXT));
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(XMLTokenType type)
	{
		return createToken(type.getScope());
	}

	protected IToken createToken(String string)
	{
		return CommonUtil.getToken(string);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.DelegatingRuleBasedScanner#setRange(org.eclipse.jface.text.IDocument,
	 * int, int)
	 */
	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		super.setRange(document, offset, length);
		hasTokens = false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.DelegatingRuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken()
	{
		IToken token;
		if (!hasTokens)
		{
			hasTokens = true;
			token = findBrokenToken();
			if (!token.isUndefined())
			{
				return token;
			}
		}
		token = super.nextToken();
		return token;
	}

	private IToken findBrokenToken()
	{
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		return new BrokenStringRule(singleQuotedStringToken, doubleQuotedStringToken).evaluate(this);
	}
}
