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
package com.aptana.editor.markdown.text.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.editor.common.text.rules.RegexpRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;

public class MarkdownScanner extends RuleBasedScanner
{

	private IToken fLastToken;
	private boolean nextMayBeLink;

	public MarkdownScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Inline Code
		rules.add(new SingleLineRule("`", "`", getToken("markup.raw.inline.markdown"), '\\')); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		// Links
		rules.add(new RegexpRule(
				"\\[([^\\]]+?)\\](?=\\s*\\[([^\\]]+?)\\])", getToken("string.other.link.title.markdown"))); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new RegexpRule(
				"\\[([^\\]]+?)\\](?=\\s*\\(([^\\)]+?)\\))", getToken("string.other.link.title.markdown"))); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new RegexpRule("\\[([^\\]]+?)\\]", getToken("constant.other.reference.link.markdown"))); //$NON-NLS-1$ //$NON-NLS-2$

		// Link URLs
		rules.add(new RegexpRule("(http:/)?/[^\\)\\(\\s]+", getToken("markup.underline.link.markdown"))); //$NON-NLS-1$ //$NON-NLS-2$

		// Link titles
		rules.add(new PatternRule("\"", "\"", getToken("string.other.link.description.title.markdown"), '\\', false)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new PatternRule(
				"'", "'", getToken("string.other.link.description.title.markdown"), '\\', false, false)); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$

		// Parens. Report what they are in links, add special hack in nextToken to set it to "" when it's not in the
		// right place
		rules.add(new SingleCharacterRule('(', getToken("punctuation.definition.metadata.markdown"))); //$NON-NLS-1$
		rules.add(new SingleCharacterRule(')', getToken("punctuation.definition.metadata.markdown"))); //$NON-NLS-1$

		// Bold
		rules.add(new SingleLineRule("**", "**", getToken("markup.bold.markdown"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new SingleLineRule("__", "__", getToken("markup.bold.markdown"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Italic
		rules.add(new SingleLineRule("*", "*", getToken("markup.italic.markdown"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new SingleLineRule("_", "_", getToken("markup.italic.markdown"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Character escapes
		rules.add(new EscapeCharacterRule(getToken("constant.character.escape.markdown"))); //$NON-NLS-1$

		// Underline link
		// What the heck is this?
		WordRule rule = new WordRule(new IWordDetector()
		{

			public boolean isWordStart(char c)
			{
				return c == '#';
			}

			public boolean isWordPart(char c)
			{
				return Character.isJavaIdentifierPart(c);
			}
		}, getToken("markup.underline.link.markdown")); //$NON-NLS-1$C
		rules.add(rule);

		// Normal words
		rule = new WordRule(new WordDetector(), getToken("")); //$NON-NLS-1$C
		rules.add(rule);

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(getToken("")); //$NON-NLS-1$
	}

	protected IToken getToken(String tokenName)
	{
		return new Token(tokenName);
	}

	@Override
	public IToken nextToken()
	{
		IToken token = super.nextToken();
		try
		{
			// [id]: http://example.com "optional title"
			if (tokenIs(token, "constant.other.reference.link.markdown"))
			{
				nextMayBeLink = true;
			}
			else if (nextMayBeLink)
			{
				if (token.equals(Token.WHITESPACE) || tokenIs(token, "markup.underline.link.markdown"))
				{
					return token;
				}
				else if (tokenIs(token, "") && getTokenLength() == 1) // is it the ':'?
				{
					// OK, ignore once
				}
				else
				{
					nextMayBeLink = false;
				}
			}

			// Parens are special, but only as part of links
			if (tokenIs(token, "punctuation.definition.metadata.markdown")
					&& !lastTokenIs("string.other.link.description.title.markdown", "string.other.link.title.markdown",
							"markup.underline.link.markdown"))
			{
				token = getToken("");
			}
			// URLS are special, but only inside links
			else if (tokenIs(token, "markup.underline.link.markdown"))
			{
				if (!lastTokenIs("punctuation.definition.metadata.markdown"))
				{
					token = getToken("");
				}
			}
		}
		finally
		{
			fLastToken = token;
		}
		return token;
	}

	private boolean lastTokenIs(String... precedingTokens)
	{
		return tokenIs(fLastToken, precedingTokens);
	}

	private boolean tokenIs(IToken token, String... tokenScope)
	{
		if (token == null)
		{
			return false;
		}
		Object data = token.getData();
		if (data == null)
		{
			return false;
		}
		for (String precedingToken : tokenScope)
		{
			if (precedingToken.equals(data))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		reset();
		super.setRange(document, offset, length);
	}

	protected void reset()
	{
		nextMayBeLink = false;
		fLastToken = null;
	}
}
