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
import java.util.regex.Pattern;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.WhitespaceDetector;
import com.aptana.editor.common.text.rules.WordDetector;
import com.aptana.editor.html.parsing.lexer.HTMLTokenType;

public class HTMLDoctypeScanner extends RuleBasedScanner
{

	private static final class TagStartRule extends ExtendedWordRule
	{
		private Pattern pattern;

		private TagStartRule(IWordDetector detector, IToken defaultToken, boolean ignoreCase)
		{
			super(detector, defaultToken, ignoreCase);
		}

		@Override
		protected boolean wordOK(String word, ICharacterScanner scanner)
		{
			if (pattern == null)
			{
				pattern = Pattern.compile("<(/)?"); //$NON-NLS-1$
			}
			return pattern.matcher(word).matches();
		}
	}

	private static final class TagStartWordDetector implements IWordDetector
	{
		public boolean isWordStart(char c)
		{
			return c == '<';
		}

		public boolean isWordPart(char c)
		{
			return c == '/';
		}
	}

	public HTMLDoctypeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add rule for double quotes
		rules.add(new MultiLineRule(
				"\"", "\"", createToken("string.quoted.double.doctype.identifiers-and-DTDs.html"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Add a rule for single quotes
		rules.add(new MultiLineRule(
				"'", "'", createToken("string.quoted.single.doctype.identifiers-and-DTDs.html"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// Tags
		WordRule wordRule = new WordRule(new WordDetector(), createToken(StringUtil.EMPTY), true);
		wordRule.addWord("DOCTYPE", createToken("entity.name.tag.doctype.html")); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(wordRule);

		CharacterMapRule rule = new CharacterMapRule();
		rule.add('>', createToken(HTMLTokenType.TAG_END));
		rule.add('=', createToken(HTMLTokenType.EQUAL));
		rules.add(rule);
		// Tag start <(/)?
		rules.add(new TagStartRule(new TagStartWordDetector(), createToken(HTMLTokenType.TAG_START), false));

		setRules(rules.toArray(new IRule[rules.size()]));
		setDefaultReturnToken(createToken(StringUtil.EMPTY));
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
