/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.RegexpRule;
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
		CharacterMapRule cmRule = new CharacterMapRule();
		cmRule.add('(', getToken("punctuation.definition.metadata.markdown")); //$NON-NLS-1$
		cmRule.add(')', getToken("punctuation.definition.metadata.markdown")); //$NON-NLS-1$
		rules.add(cmRule);

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
			if (tokenIs(token, "constant.other.reference.link.markdown")) //$NON-NLS-1$
			{
				nextMayBeLink = true;
			}
			else if (nextMayBeLink)
			{
				if (token.equals(Token.WHITESPACE) || tokenIs(token, "markup.underline.link.markdown")) //$NON-NLS-1$
				{
					return token;
				}
				else if (tokenIs(token, "") && getTokenLength() == 1) // is it the ':'? //$NON-NLS-1$
				{
					// OK, ignore once
				}
				else
				{
					nextMayBeLink = false;
				}
			}

			// Parens are special, but only as part of links
			if (tokenIs(token, "punctuation.definition.metadata.markdown") //$NON-NLS-1$
					&& !lastTokenIs("string.other.link.description.title.markdown", "string.other.link.title.markdown", //$NON-NLS-1$ //$NON-NLS-2$
							"markup.underline.link.markdown")) //$NON-NLS-1$
			{
				token = getToken(""); //$NON-NLS-1$
			}
			// URLS are special, but only inside links
			else if (tokenIs(token, "markup.underline.link.markdown")) //$NON-NLS-1$
			{
				if (!lastTokenIs("punctuation.definition.metadata.markdown")) //$NON-NLS-1$
				{
					token = getToken(""); //$NON-NLS-1$
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
