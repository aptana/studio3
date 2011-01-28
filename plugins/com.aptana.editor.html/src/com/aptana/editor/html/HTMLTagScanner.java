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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public class HTMLTagScanner extends RuleBasedScanner
{
	/**
	 * A key word detector.
	 */
	static class WordDetector implements IWordDetector
	{
		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c);
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}
	}

	// as per the html5 spec, these are elements that define "sections", but we've added
	// the <html> tag itself to the list.
	// see http://dev.w3.org/html5/spec/Overview.html#sections
	@SuppressWarnings("nls")
	private static String[] STRUCTURE_DOT_ANY = { "html", "head", "body", "header", "address", "nav", "section",
			"article", "footer", "aside", "hgroup", "h1", "h2", "h3", "h4", "h5", "h6" };

	@SuppressWarnings("nls")
	private static String[] BLOCK_DOT_ANY = { "blockquote", "dd", "div", "dl", "dt", "fieldset", "form", "frame",
			"frameset", "iframe", "noframes", "object", "ol", "p", "ul", "applet", "center", "dir", "hr", "menu", "pre" };

	@SuppressWarnings("nls")
	private static String[] TAG_INLINE_ANY = { "a", "abbr", "acronym", "area", "b", "base", "basefont", "bdo", "big",
			"br", "button", "caption", "cite", "code", "col", "colgroup", "del", "dfn", "em", "font", "i", "img",
			"input", "ins", "isindex", "kbd", "label", "legend", "li", "link", "map", "meta", "noscript", "optgroup",
			"option", "param", "q", "s", "samp", "script", "select", "small", "span", "strike", "strong", "style",
			"sub", "sup", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt", "u", "var",
			"canvas", "audio", "video" };

	/**
	 * HTMLTagScanner
	 */
	public HTMLTagScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for double quotes
		rules.add(new MultiLineRule("\"", "\"", createToken(HTMLTokenType.DOUBLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add a rule for single quotes
		rules.add(new MultiLineRule("'", "'", createToken(HTMLTokenType.SINGLE_QUOTED_STRING), '\\')); //$NON-NLS-1$ //$NON-NLS-2$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Attributes
		WordRule wordRule = new ExtendedWordRule(new IWordDetector()
		{

			public boolean isWordPart(char c)
			{
				return Character.isLetter(c) || c == '-' || c == ':';
			}

			public boolean isWordStart(char c)
			{
				return Character.isLetter(c);
			}

		}, createToken(HTMLTokenType.ATTRIBUTE), true)
		{
			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				int c = scanner.read();
				scanner.unread();
				return ((char) c) == '=';
			}
		};
		wordRule.addWord("id", createToken(HTMLTokenType.ID)); //$NON-NLS-1$
		wordRule.addWord("class", createToken(HTMLTokenType.CLASS)); //$NON-NLS-1$
		rules.add(wordRule);

		// Tags
		wordRule = new WordRule(new WordDetector(), createToken(HTMLTokenType.META), true);
		wordRule.addWord("script", createToken(HTMLTokenType.SCRIPT)); //$NON-NLS-1$
		wordRule.addWord("style", createToken(HTMLTokenType.STYLE)); //$NON-NLS-1$
		IToken structureDotAnyToken = createToken(HTMLTokenType.STRUCTURE_TAG);
		for (String tag : STRUCTURE_DOT_ANY)
		{
			wordRule.addWord(tag, structureDotAnyToken);
		}
		IToken blockDotAnyToken = createToken(HTMLTokenType.BLOCK_TAG);
		for (String tag : BLOCK_DOT_ANY)
		{
			wordRule.addWord(tag, blockDotAnyToken);
		}
		IToken inlineAnyToken = createToken(HTMLTokenType.INLINE_TAG);
		for (String tag : TAG_INLINE_ANY)
		{
			wordRule.addWord(tag, inlineAnyToken);
		}
		rules.add(wordRule);

		CharacterMapRule rule = new CharacterMapRule();
		rule.add('>', createToken(HTMLTokenType.TAG_END));
		rule.add('=', createToken(HTMLTokenType.EQUAL));
		rules.add(rule);
		// FIXME Use a word/extend word rule here to avoid slow regexp rule?
		rules.add(new RegexpRule("<(/)?", createToken(HTMLTokenType.TAG_START), true)); //$NON-NLS-1$

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(createToken(HTMLTokenType.TEXT));
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
