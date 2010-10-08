/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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

import com.aptana.editor.common.text.rules.ExtendedWordRule;
import com.aptana.editor.common.text.rules.SingleCharacterRule;
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
	protected List<IRule> createPunctuationRules()
	{
		List<IRule> rules = super.createPunctuationRules();
		rules.remove(rules.size() - 1);
		rules.add(new SingleCharacterRule('=', createToken("punctuation.definition.entity.sass"))); //$NON-NLS-1$
		return rules;
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
