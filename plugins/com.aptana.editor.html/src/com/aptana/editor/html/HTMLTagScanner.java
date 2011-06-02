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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.core.util.FixedQueue;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.DelegatingRuleBasedScanner;
import com.aptana.editor.common.text.rules.MultiCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.css.CSSCodeScanner;
import com.aptana.editor.html.internal.text.rules.AttributeNameWordDetector;
import com.aptana.editor.html.internal.text.rules.TagNameWordDetector;
import com.aptana.editor.html.internal.text.rules.TagWordRule;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;
import com.aptana.editor.js.JSCodeScanner;

/**
 * @author Max Stepanov
 *
 */
public class HTMLTagScanner extends DelegatingRuleBasedScanner {

	// as per the html5 spec, these are elements that define "sections", but
	// we've added
	// the <html> tag itself to the list.
	// see http://dev.w3.org/html5/spec/Overview.html#sections
	@SuppressWarnings("nls")
	private static final String[] STRUCTURE_DOT_ANY = { "html", "head", "body", "header", "address", "nav", "section", "article", "footer", "aside", "hgroup", "h1", "h2", "h3", "h4",
			"h5", "h6" };

	@SuppressWarnings("nls")
	private static final String[] BLOCK_DOT_ANY = { "blockquote", "dd", "div", "dl", "dt", "fieldset", "form", "frame", "frameset", "iframe", "noframes", "object", "ol", "p", "ul",
			"applet", "center", "dir", "hr", "menu", "pre" };

	@SuppressWarnings("nls")
	private static final String[] TAG_INLINE_ANY = { "a", "abbr", "acronym", "area", "b", "base", "basefont", "bdo", "big", "br", "button", "caption", "cite", "code", "col", "colgroup",
			"del", "dfn", "em", "font", "i", "img", "input", "ins", "isindex", "kbd", "label", "legend", "li", "link", "map", "meta", "noscript", "optgroup", "option", "param",
			"q", "s", "samp", "script", "select", "small", "span", "strike", "strong", "style", "sub", "sup", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title",
			"tr", "tt", "u", "var", "canvas", "audio", "video" };
	
	private final IToken doubleQuotedStringToken = createToken(HTMLTokenType.DOUBLE_QUOTED_STRING);
	private final IToken singleQuotedStringToken = createToken(HTMLTokenType.SINGLE_QUOTED_STRING);
	
	private ITokenScanner cssTokenScanner = new CSSCodeScanner();
	private ITokenScanner jsTokenScanner = new JSCodeScanner();
	
	private FixedQueue<IToken> tokenHistory = new FixedQueue<IToken>(4);

	/**
	 * 
	 */
	public HTMLTagScanner() {
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for double quotes
		rules.add(new MultiLineRule("\"", "\"", doubleQuotedStringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add a rule for single quotes
		rules.add(new MultiLineRule("'", "'", singleQuotedStringToken, '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Tags
		WordRule tagWordRule = new TagWordRule(new TagNameWordDetector(), createToken(HTMLTokenType.META), true);
		tagWordRule.addWord("script", createToken(HTMLTokenType.SCRIPT)); //$NON-NLS-1$
		tagWordRule.addWord("style", createToken(HTMLTokenType.STYLE)); //$NON-NLS-1$
		IToken structureDotAnyToken = createToken(HTMLTokenType.STRUCTURE_TAG);
		for (String tag : STRUCTURE_DOT_ANY) {
			tagWordRule.addWord(tag, structureDotAnyToken);
		}
		IToken blockDotAnyToken = createToken(HTMLTokenType.BLOCK_TAG);
		for (String tag : BLOCK_DOT_ANY) {
			tagWordRule.addWord(tag, blockDotAnyToken);
		}
		IToken inlineAnyToken = createToken(HTMLTokenType.INLINE_TAG);
		for (String tag : TAG_INLINE_ANY) {
			tagWordRule.addWord(tag, inlineAnyToken);
		}
		rules.add(tagWordRule);
		
		WordRule attributeWordRule = new WordRule(new AttributeNameWordDetector(), createToken(HTMLTokenType.ATTRIBUTE), true);
		attributeWordRule.addWord("id", createToken(HTMLTokenType.ID)); //$NON-NLS-1$
		attributeWordRule.addWord("class", createToken(HTMLTokenType.CLASS)); //$NON-NLS-1$
		rules.add(attributeWordRule);

		rules.add(new MultiCharacterRule("</", createToken(HTMLTokenType.TAG_START))); //$NON-NLS-1$
		rules.add(new MultiCharacterRule("/>", createToken(HTMLTokenType.TAG_SELF_CLOSE))); //$NON-NLS-1$

		CharacterMapRule charsRule = new CharacterMapRule();
		charsRule.add('<', createToken(HTMLTokenType.TAG_START));
		charsRule.add('>', createToken(HTMLTokenType.TAG_END));
		charsRule.add('=', createToken(HTMLTokenType.EQUAL));
		rules.add(charsRule);
		
		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(createToken(HTMLTokenType.TEXT));
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.DelegatingRuleBasedScanner#setRange(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public void setRange(IDocument document, int offset, int length) {
		super.setRange(document, offset, length);
		tokenHistory.clear();
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.DelegatingRuleBasedScanner#nextToken()
	 */
	@Override
	public IToken nextToken() {
		IToken token = super.nextToken();
		if (doubleQuotedStringToken == token || singleQuotedStringToken == token) {
			
		}
		tokenHistory.add(token);
		return token;
	}

	/**
	 * createToken
	 * 
	 * @param type
	 * @return
	 */
	protected IToken createToken(HTMLTokenType type) {
		return createToken(type.getScope());
	}

	/**
	 * createToken
	 * 
	 * @param string
	 * @return
	 */
	protected IToken createToken(String string) {
		return new Token(string);
	}

}
