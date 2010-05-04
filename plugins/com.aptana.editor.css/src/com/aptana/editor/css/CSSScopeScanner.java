package com.aptana.editor.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.css.CSSSourceConfiguration.WordPredicateRule;

public class CSSScopeScanner extends CSSCodeScanner
{
	/**
	 * CSSScopeScanner
	 */
	public CSSScopeScanner()
	{
		List<IRule> rules = new ArrayList<IRule>();

		// Add the rules for block comments, single and double quoted strings
		rules.add(new SingleLineRule("\"", "\"", createToken("string.quoted.double.css"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new SingleLineRule("\'", "\'", createToken("string.quoted.single.css"), '\\')); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		rules.add(new WordPredicateRule(createToken("comment.block.css"))); //$NON-NLS-1$
		rules.add(new MultiLineRule("/*", "*/", createToken("comment.block.css"), (char) 0, true)); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$

		if (fRules != null)
		{
			// Add the rules created by the super class
			rules.addAll(Arrays.asList(fRules));
		}

		setRules(rules.toArray(new IRule[rules.size()]));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.css.CSSCodeScanner#createToken(java.lang.String)
	 */
	@Override
	protected IToken createToken(String string)
	{
		// Simply use the token type string that is passed in as the data
		return new Token(string);
	}
}
