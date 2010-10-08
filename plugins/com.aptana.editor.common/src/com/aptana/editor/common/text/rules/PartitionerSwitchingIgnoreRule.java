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
	public IToken evaluate(ICharacterScanner scanner, boolean resume)
	{
		if (scanner instanceof SequenceCharacterScanner)
		{
			// when checking for the rule, do not search for potential sequence
			SequenceCharacterScanner seqScanner = (SequenceCharacterScanner) scanner;
			try
			{
				seqScanner.setSequenceIgnored(true);
				return rule.evaluate(scanner, resume);
			}
			finally
			{
				seqScanner.setSequenceIgnored(false);
			}
		}
		return rule.evaluate(scanner, resume);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IPredicateRule#getSuccessToken()
	 */
	public IToken getSuccessToken()
	{
		return rule.getSuccessToken();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner)
	{
		if (scanner instanceof SequenceCharacterScanner)
		{
			// when checking for the rule, do not search for potential sequence
			SequenceCharacterScanner seqScanner = (SequenceCharacterScanner) scanner;
			try
			{
				seqScanner.setSequenceIgnored(true);
				return rule.evaluate(scanner);
			}
			finally
			{
				seqScanner.setSequenceIgnored(false);
			}
		}
		return rule.evaluate(scanner);
	}
}
