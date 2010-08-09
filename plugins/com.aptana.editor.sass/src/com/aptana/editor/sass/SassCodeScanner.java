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
			@Override
			public int compare(String o1, String o2)
			{
				return o2.length() - o1.length();
			}
		});
		return list.toArray(new String[list.size()]);
	}

	private static class VariableWordDetector implements IWordDetector
	{

		@Override
		public boolean isWordPart(char c)
		{
			return Character.isLetterOrDigit(c) || c == '-' || c == '_';
		}

		@Override
		public boolean isWordStart(char c)
		{
			return c == '!' || c == '=' || c == '+';
		}
	}
}
