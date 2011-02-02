/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.sass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWordDetector;

import com.aptana.editor.common.text.rules.CharacterMapRule;
import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.css.CSSCodeScanner;

/**
 * @author Chris Williams
 */
public class SassCodeScanner extends CSSCodeScanner
{

	@Override
	protected List<IRule> createRules()
	{
		List<IRule> rules = super.createRules();
		// Stick in a rule that recognizes mixins and variables
		// FIXME This rule doesn't properly set the first char (!, =, or +) to it's own different punctuation token type
		ExtendedWordRule variableRule = new ExtendedWordRule(new VariableWordDetector(),
				createToken("variable.other.sass"), true) //$NON-NLS-1$
		{

			@Override
			protected boolean wordOK(String word, ICharacterScanner scanner)
			{
				return word.length() >= 2;
			}
		};
		rules.add(1, variableRule);
		return rules;
	}

	@Override
	protected CharacterMapRule createPunctuationRules()
	{
		CharacterMapRule rule = super.createPunctuationRules();
		// Override equals
		rule.add('=', createToken("punctuation.definition.entity.sass")); //$NON-NLS-1$
		return rule;
	}

	/**
	 * Here we override the array of static property names from CSS and make ones that have "namespaces" (as Sass calls
	 * them) also get split up so we recognize the second half (i.e. we recognize both "font-family" as well as "font"
	 * and "family" individually).
	 */
	@Override
	protected String[] getPropertyNames()
	{
		String[] origCSS = super.getPropertyNames();
		Set<String> namespaced = new HashSet<String>();
		for (String name : origCSS)
		{
			StringTokenizer tokenizer = new StringTokenizer(name, "-"); //$NON-NLS-1$
			while (tokenizer.hasMoreTokens())
				namespaced.add(tokenizer.nextToken());
			namespaced.add(name);
		}
		List<String> list = new ArrayList<String>(namespaced);
		Collections.sort(list, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o2.length() - o1.length();
			}
		});
		return list.toArray(new String[list.size()]);
	}

	private static class VariableWordDetector implements IWordDetector
	{

		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c) || c == '-' || c == '_';
		}

		public boolean isWordStart(char c)
		{
			return c == '!' || c == '=' || c == '+';
		}
	}
}
