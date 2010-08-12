package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;

/**
 * A partitioner rule wrapper that allow ignoring switching rules in sequence characters.<br>
 * This rule is useful, for example, in ignoring PHP close tags inside strings and multi-line comments.
 * 
 * @author Max, Shalom
 */
public class PartitionerSwitchingIgnoreRule implements IPredicateRule
{

	private final IPredicateRule rule;

	/**
	 * Constructs a new PartitionerIgnoreSwitchingRule by wrapping a given rule.
	 * 
	 * @param rule
	 *            The rule to be wrapped.
	 */
	public PartitionerSwitchingIgnoreRule(IPredicateRule rule)
	{
		this.rule = rule;

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner,
	 * boolean)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		if (scanner instanceof SequenceCharacterScanner)
		{
			// when checking for the rule, do not search for potential sequence
			SequenceCharacterScanner seqScanner = (SequenceCharacterScanner) scanner;
			IToken token = null;
			try
			{
				seqScanner.setSequenceIgnored(true);
				token = rule.evaluate(scanner, resume);
			}
			finally
			{
				seqScanner.setSequenceIgnored(false);
			}
			return token;
		}
		return rule.evaluate(scanner, resume);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	@Override
	public IToken getSuccessToken()
	{
		return rule.getSuccessToken();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	public IToken evaluate(ICharacterScanner scanner)
	{
		if (scanner instanceof SequenceCharacterScanner)
		{
			// when checking for the rule, do not search for potential sequence
			SequenceCharacterScanner seqScanner = (SequenceCharacterScanner) scanner;
			IToken token = null;
			try
			{
				seqScanner.setSequenceIgnored(true);
				token = rule.evaluate(scanner);
			}
			finally
			{
				seqScanner.setSequenceIgnored(false);
			}
			return token;
		}
		return rule.evaluate(scanner);
	}
}
