/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;
import com.aptana.editor.common.PartitionScannerSwitchStrategy;

/**
 * A rule for detecting patterns which begin with a given
 * sequence and may end with a given sequence thereby spanning
 * multiple lines. In addition, the rule ends on the provided break sequences
 * as if if would a end-of-file.
 * 
 * @author Max Stepanov
 */
public class BreakingMultiLineRule extends MultiLineRule {

	private final IPartitionScannerSwitchStrategy scannerSwitchStrategy;
	
	/**
	 * @param startSequence
	 * @param endSequence
	 * @param token
	 */
	public BreakingMultiLineRule(String startSequence, String endSequence, String[] breakSequences, IToken token) {
		this(startSequence, endSequence, breakSequences, token, (char) 0);
	}

	/**
	 * @param startSequence
	 * @param endSequence
	 * @param token
	 * @param escapeCharacter
	 */
	public BreakingMultiLineRule(String startSequence, String endSequence, String[] breakSequences, IToken token, char escapeCharacter) {
		super(startSequence, endSequence, token, escapeCharacter, true);
		scannerSwitchStrategy = new PartitionScannerSwitchStrategy(breakSequences);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		return super.endSequenceDetected(new SequenceCharacterScanner(scanner, scannerSwitchStrategy));
	}

}
