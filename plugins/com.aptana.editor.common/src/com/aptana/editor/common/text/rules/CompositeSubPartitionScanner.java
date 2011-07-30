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

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;

/**
 * @author Max Stepanov
 *
 */
public abstract class CompositeSubPartitionScanner implements ISubPartitionScanner {

	protected static final int TYPE_DEFAULT = 0;

	protected ISubPartitionScanner[] subPartitionScanners;
	protected IPartitionScannerSwitchStrategy[] switchStrategies;
	protected SequenceCharacterScanner[] sequenceCharacterScanners;
	protected SequenceCharacterScanner parentSequenceCharacterScanner;
	protected int current = 0;
	private IToken lastToken;
	protected IToken resumeToken;
	
	/**
	 * 
	 */
	protected CompositeSubPartitionScanner(ISubPartitionScanner[] subPartitionScanners, IPartitionScannerSwitchStrategy[] switchStrategies) {
		this.subPartitionScanners = subPartitionScanners;
		this.switchStrategies = switchStrategies;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#initCharacterScanner(org.eclipse.jface.text.rules.ICharacterScanner, com.aptana.editor.common.IPartitionScannerSwitchStrategy)
	 */
	public void initCharacterScanner(ICharacterScanner baseCharacterScanner, IPartitionScannerSwitchStrategy switchStrategy) {
		parentSequenceCharacterScanner = new SequenceCharacterScanner(baseCharacterScanner, switchStrategy, true);
		sequenceCharacterScanners = new SequenceCharacterScanner[subPartitionScanners.length];
		sequenceCharacterScanners[0] = parentSequenceCharacterScanner;
		for (int i = 0; i < switchStrategies.length; ++i) {
			sequenceCharacterScanners[1+i] = new SequenceCharacterScanner(parentSequenceCharacterScanner, switchStrategies[i], true);
		}
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#getRules()
	 */
	public IPredicateRule[] getRules() {
		return subPartitionScanners[current].getRules();
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#getDefaultToken()
	 */
	public IToken getDefaultToken() {
		return subPartitionScanners[current].getDefaultToken();
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#getCharacterScanner()
	 */
	public ICharacterScanner getCharacterScanner() {
		return sequenceCharacterScanners[current];
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#foundSequence()
	 */
	public boolean foundSequence() {
		return parentSequenceCharacterScanner.foundSequence();
	}

	protected boolean foundSequence(boolean reset) {
		return parentSequenceCharacterScanner.foundSequence(reset);
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#doResetRules()
	 */
	public boolean doResetRules() {
		if (current != TYPE_DEFAULT && sequenceCharacterScanners[current].foundSequence()) {
			current = TYPE_DEFAULT;
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#hasContentType(java.lang.String)
	 */
	public boolean hasContentType(String contentType) {
		for (ISubPartitionScanner i : subPartitionScanners) {
			if (i.hasContentType(contentType)) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.ISubPartitionScanner#setLastToken(org.eclipse.jface.text.rules.IToken)
	 */
	public void setLastToken(IToken token) {
		lastToken = foundSequence(false) ? token : null;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ISubPartitionScanner#getLastToken()
	 */
	public final IToken getLastToken() {
		return lastToken;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.text.rules.ISubPartitionScanner#getResumeToken()
	 */
	public IToken getResumeToken() {
		try {
			return resumeToken;
		} finally {
			resumeToken = null;
		}
	}

}
