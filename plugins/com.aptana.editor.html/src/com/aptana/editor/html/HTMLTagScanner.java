/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.WhitespaceDetector;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.theme.IThemeManager;

public class HTMLTagScanner extends RuleBasedScanner
{
	@SuppressWarnings("nls")
	private static String[] STRUCTURE_DOT_ANY = { "html", "head", "body" };

	@SuppressWarnings("nls")
	private static String[] BLOCK_DOT_ANY = { "address", "blockquote", "dd", "div", "dl", "dt", "fieldset", "form",
			"frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "iframe", "noframes", "object", "ol", "p", "ul",
			"applet", "center", "dir", "hr", "menu", "pre" };

	@SuppressWarnings("nls")
	private static String[] TAG_INLINE_ANY = { "a", "abbr", "acronym", "area", "b", "base", "basefont", "bdo", "big",
			"br", "button", "caption", "cite", "code", "col", "colgroup", "del", "dfn", "em", "font", "i", "img",
			"input", "ins", "isindex", "kbd", "label", "legend", "li", "link", "map", "meta", "noscript", "optgroup",
			"option", "param", "q", "s", "samp", "script", "select", "small", "span", "strike", "strong", "style",
			"sub", "sup", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "tt", "u", "var" };

	public HTMLTagScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for double quotes
		rules.add(new MultiLineRule("\"", "\"", createToken("string.quoted.double.html"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Add a rule for single quotes
		rules.add(new MultiLineRule("'", "'", createToken("string.quoted.single.html"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Attributes
		WordRule wordRule = new ExtendedWordRule(new IWordDetector()
		{

			@Override
			public boolean isWordPart(char c)
			{
				return Character.isLetter(c) || c == '-' || c == ':';
			}

			@Override
			public boolean isWordStart(char c)
			{
				return Character.isLetter(c);
			}

		}, createToken("entity.other.attribute-name.html"), true) {//$NON-NLS-1$
			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				int c = scanner.read();
				scanner.unread();
				return ((char) c) == '=';
			}
		};
		wordRule.addWord("id", createToken("entity.other.attribute-name.id.html")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("class", createToken("entity.other.attribute-name.class.html")); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(wordRule);

		// Tags
		wordRule = new WordRule(new WordDetector(), createToken("meta.tag.other.html"), true); //$NON-NLS-1$
		wordRule.addWord("script", createToken("entity.name.tag.script.html")); //$NON-NLS-1$ //$NON-NLS-2$
		wordRule.addWord("style", createToken("entity.name.tag.style.html")); //$NON-NLS-1$ //$NON-NLS-2$
		IToken structureDotAnyToken = createToken("entity.name.tag.structure.any.html"); //$NON-NLS-1$
		for (String tag : STRUCTURE_DOT_ANY)
		{
			wordRule.addWord(tag, structureDotAnyToken);
		}
		IToken blockDotAnyToken = createToken("entity.name.tag.block.any.html"); //$NON-NLS-1$
		for (String tag : BLOCK_DOT_ANY)
		{
			wordRule.addWord(tag, blockDotAnyToken);
		}
		IToken inlineAnyToken = createToken("entity.name.tag.inline.any.html"); //$NON-NLS-1$
		for (String tag : TAG_INLINE_ANY)
		{
			wordRule.addWord(tag, inlineAnyToken);
		}
		rules.add(wordRule);

		rules.add(new SingleCharacterRule('>', createToken("punctuation.definition.tag.end.html"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule('=', createToken("punctuation.separator.key-value.html"))); //$NON-NLS-1$
		rules.add(new RegexpRule("<(/)?", createToken("punctuation.definition.tag.begin.html"), true)); //$NON-NLS-1$ //$NON-NLS-2$

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(createToken("text")); //$NON-NLS-1$
	}

	protected IToken createToken(String string)
	{
		return getThemeManager().getToken(string);
	}

	protected IThemeManager getThemeManager()
	{
		return CommonEditorPlugin.getDefault().getThemeManager();
	}

	/**
	 * A key word detector.
	 */
	static class WordDetector implements IWordDetector
	{
		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordStart(char c)
		{
			return Character.isLetter(c);
		}

		/*
		 * (non-Javadoc) Method declared on IWordDetector
		 */
		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c);
		}
	}

}
