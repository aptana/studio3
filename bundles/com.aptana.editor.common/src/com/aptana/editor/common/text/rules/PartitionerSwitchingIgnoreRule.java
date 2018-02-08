/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;

/**
 * A partitioner rule wrapper that allow ignoring switching rules in sequence
 * characters.<br>
 * This rule is useful, for example, in ignoring PHP close tags inside strings
 * and multi-line comments.
 * 
 * @author Max Stepanov
 * @author Shalom Gibly
 */
public class PartitionerSwitchingIgnoreRule implements IPredicateRule {

	private final IPredicateRule rule;

	/**
	 * Constructs a new PartitionerIgnoreSwitchingRule by wrapping a given rule.
	 * 
	 * @param rule
	 *            The rule to be wrapped.
	 */
	public PartitionerSwitchingIgnoreRule(IPredicateRule rule) {
		this.rule = rule;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.rules.IPredicateRule#evaluate(org.eclipse.jface
	 * .text.rules.ICharacterScanner,
	 * boolean)
	 */
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		if (scanner instanceof SequenceCharacterScanner) {
			// when checking for the rule, do not search for potential sequence
			SequenceCharacterScanner seqScanner = (SequenceCharacterScanner) scanner;
			try {
				seqScanner.setSequenceIgnored(true);
				return rule.evaluate(scanner, resume);
			} finally {
				seqScanner.setSequenceIgnored(false);
			}
		}
		return rule.evaluate(scanner, resume);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	public IToken getSuccessToken() {
		return rule.getSuccessToken();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules
	 * .ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		if (scanner instanceof SequenceCharacterScanner) {
			// when checking for the rule, do not search for potential sequence
			SequenceCharacterScanner seqScanner = (SequenceCharacterScanner) scanner;
			try {
				seqScanner.setSequenceIgnored(true);
				return rule.evaluate(scanner);
			} finally {
				seqScanner.setSequenceIgnored(false);
			}
		}
		return rule.evaluate(scanner);
	}
}
