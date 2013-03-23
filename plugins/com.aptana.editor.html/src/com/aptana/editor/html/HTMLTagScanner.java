/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.BreakingMultiLineRule;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.MultiCharacterRule;
import com.aptana.editor.common.text.rules.QueuedRuleBasedScanner;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.CSSCodeScannerFlex;
import com.aptana.editor.html.parsing.HTMLUtils;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.editor.js.text.JSCodeScanner;
import com.aptana.editor.xml.text.rules.AttributeNameWordDetector;
import com.aptana.editor.xml.text.rules.BrokenStringRule;
import com.aptana.editor.xml.text.rules.TagNameWordDetector;
import com.aptana.editor.xml.text.rules.TagWordRule;

/**
 * @author Max Stepanov
 */
public class HTMLTagScanner extends QueuedRuleBasedScanner
{

	// as per the html5 spec, these are elements that define "sections", but
	// we've added
	// the <html> tag itself to the list.
	// see http://dev.w3.org/html5/spec/Overview.html#sections
	@SuppressWarnings("nls")
	private static final String[] STRUCTURE_DOT_ANY = { "html", "head", "body", "header", "address", "nav", "section",
			"article", "footer", "aside", "hgroup", "h1", "h2", "h3", "h4", "h5", "h6" };

	@SuppressWarnings("nls")
	private static final String[] BLOCK_DOT_ANY = { "blockquote", "dd", "div", "dl", "dt", "fieldset", "form", "frame",
			"frameset", "iframe", "noframes", "object", "ol", "p", "ul", "applet", "center", "dir", "hr", "menu", "pre" };

	@SuppressWarnings("nls")
	private static final String[] TAG_INLINE_ANY = { "a", "abbr", "acronym", "area", "b", "base", "basefont", "bdo",
			"big", "br", "button", "caption", "cite", "code", "col", "colgroup", "del", "dfn", "em", "font", "i",
			"img", "input", "ins", "isindex", "kbd", "label", "legend", "li", "link", "map", "meta", "noscript",
			"optgroup", "option", "param", "q", "s", "samp", "script", "select", "small", "span", "strike", "strong",
			"style", "sub", "sup", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt",
			"u", "var", "canvas", "audio", "video" };

	@SuppressWarnings("nls")
	private static final String[] QUOTED_STRING_BREAKS = { "/>", ">" };

	private final IToken doubleQuotedStringToken = createToken(HTMLTokenType.DOUBLE_QUOTED_STRING);
	private final IToken singleQuotedStringToken = createToken(HTMLTokenType.SINGLE_QUOTED_STRING);
	private final IToken attributeStyleToken = createToken(HTMLTokenType.ATTR_STYLE);
	private final IToken attributeScriptToken = createToken(HTMLTokenType.ATTR_SCRIPT);
	private final IToken equalToken = createToken(HTMLTokenType.EQUAL);

	private ITokenScanner cssTokenScanner = new CSSCodeScannerFlex();
	private ITokenScanner jsTokenScanner = new JSCodeScanner();

	private Stack<IToken> tokenHistory = new Stack<IToken>();
	private String tagName;
	private boolean hasTokens;
	private boolean rescanNestedLanguages;

	/**
	 * HTMLTagScanner
	 */
	public HTMLTagScanner()
	{
		this(true);
	}

	/**
	 * HTMLTagScanner
	 * 
	 * @param rescanNestedLanguges
	 *            A flag indicating if nested languages in attributes should be rescanned in their source language. When
	 *            this is set to false, the entire attribute value is treated as a single lexeme
	 */
	public HTMLTagScanner(boolean rescanNestedLanguges)
	{
		this.rescanNestedLanguages = rescanNestedLanguges;

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
		WordRule tagWordRule = new TagWordRule(new TagNameWordDetector(), createToken(HTMLTokenType.META), true)
		{
			@Override
			protected IToken getWordToken(String word)
			{
				tagName = word;
				return null;
			}
		};
		tagWordRule.addWord("script", createToken(HTMLTokenType.SCRIPT)); //$NON-NLS-1$
		tagWordRule.addWord("style", createToken(HTMLTokenType.STYLE)); //$NON-NLS-1$
		IToken structureDotAnyToken = createToken(HTMLTokenType.STRUCTURE_TAG);
		for (String tag : STRUCTURE_DOT_ANY)
		{
			tagWordRule.addWord(tag, structureDotAnyToken);
		}
		IToken blockDotAnyToken = createToken(HTMLTokenType.BLOCK_TAG);
		for (String tag : BLOCK_DOT_ANY)
		{
			tagWordRule.addWord(tag, blockDotAnyToken);
		}
		IToken inlineAnyToken = createToken(HTMLTokenType.INLINE_TAG);
		for (String tag : TAG_INLINE_ANY)
		{
			tagWordRule.addWord(tag, inlineAnyToken);
		}
		rules.add(tagWordRule);

		WordRule attributeWordRule = new ExtendedWordRule(new AttributeNameWordDetector(),
				createToken(HTMLTokenType.ATTRIBUTE), true)
		{
			@Override
			protected IToken getWordToken(String word)
			{
				return HTMLUtils.isJSAttribute(tagName, word) ? attributeScriptToken : null;
			}

		};
		attributeWordRule.addWord("id", createToken(HTMLTokenType.ATTR_ID)); //$NON-NLS-1$
		attributeWordRule.addWord("class", createToken(HTMLTokenType.ATTR_CLASS)); //$NON-NLS-1$
		attributeWordRule.addWord("style", attributeStyleToken); //$NON-NLS-1$
		rules.add(attributeWordRule);

		rules.add(new MultiCharacterRule("</", createToken(HTMLTokenType.TAG_START))); //$NON-NLS-1$
		rules.add(new MultiCharacterRule("/>", createToken(HTMLTokenType.TAG_SELF_CLOSE))); //$NON-NLS-1$

		CharacterMapRule charsRule = new CharacterMapRule();
		charsRule.add('<', createToken(HTMLTokenType.TAG_START));
		charsRule.add('>', createToken(HTMLTokenType.TAG_END));
		charsRule.add('=', equalToken);
		rules.add(charsRule);

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(createToken(HTMLTokenType.TEXT));
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
		tokenHistory.clear();
		tagName = null;
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
		if (rescanNestedLanguages && (doubleQuotedStringToken == token || singleQuotedStringToken == token))
		{
			IToken attributeToken = getAttributeToken();
			ITokenScanner tokenScanner = null;
			if (attributeScriptToken == attributeToken)
			{
				tokenScanner = jsTokenScanner;
			}
			else if (attributeStyleToken == attributeToken)
			{
				tokenScanner = cssTokenScanner;
			}
			tokenHistory.clear();
			int offset = getTokenOffset();
			int length = getTokenLength() - 2;
			if (tokenScanner != null && length > 0)
			{
				queueToken(token, offset, 1);
				queueDelegate(tokenScanner, offset + 1, length);
				queueToken(token, offset + length + 1, 1);
				return super.nextToken();
			}
		}
		if (!token.isWhitespace())
		{
			tokenHistory.push(token);
		}
		return token;
	}

	private IToken findBrokenToken()
	{
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		return new BrokenStringRule(singleQuotedStringToken, doubleQuotedStringToken).evaluate(this);
	}

	private IToken getAttributeToken()
	{
		if (tokenHistory.size() < 2 || equalToken != tokenHistory.pop())
		{
			return null;
		}
		return tokenHistory.pop();
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(HTMLTokenType type)
	{
		return createToken(type.getScope());
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(String string)
	{
		return new Token(string);
	}

}
