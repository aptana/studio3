/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.editor.common.text.rules;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;
import com.aptana.editor.common.IPartitionScannerSwitchStrategy.ISequenceBypassHandler;
import com.aptana.editor.common.TextUtils;

/**
 * @author Max Stepanov
 *
 */
public class SequenceCharacterScanner implements ICharacterScanner {

	private ICharacterScanner characterScanner;
	private IPartitionScannerSwitchStrategy switchStrategy;
	private ISequenceBypassHandler sequenceBypassHandler;
	private char[][] switchSequences;
	private boolean found = false;
	private boolean eof = false;
	private boolean ignored;

	private boolean ignoreCase;

	/**
	 * @param baseCharacterScanner
	 */
	public SequenceCharacterScanner(ICharacterScanner characterScanner, IPartitionScannerSwitchStrategy switchStrategy) {
		this(characterScanner, switchStrategy, false);
	}

	public SequenceCharacterScanner(ICharacterScanner characterScanner, IPartitionScannerSwitchStrategy switchStrategy, boolean ignoreCase) {
		this.characterScanner = characterScanner;
		this.switchStrategy = switchStrategy;
		this.ignoreCase = ignoreCase;
		this.sequenceBypassHandler = switchStrategy.getSequenceBypassHandler();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
	 */
	public int getColumn() {
		return characterScanner.getColumn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
	 */
	public char[][] getLegalLineDelimiters() {
		return characterScanner.getLegalLineDelimiters();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
	 */
	public int read() {
		eof = false;
		int c = characterScanner.read();
		if (c != ICharacterScanner.EOF && !ignored) {
			if (switchSequences == null) {
				switchSequences = TextUtils.replace(switchStrategy.getSwitchSequences(), '\n', TextUtils.rsort(characterScanner.getLegalLineDelimiters()));
			}
			for (char[] sequence : switchSequences) {
				if (c == sequence[0] && TextUtils.sequenceDetected(characterScanner, sequence, ignoreCase)) {
					characterScanner.unread();
					if (sequenceBypassHandler == null || !sequenceBypassHandler.bypassSequence(characterScanner, sequence)) {
						found = true;
						eof = true;
						return ICharacterScanner.EOF;
					}
					Assert.isTrue(c == characterScanner.read());
				}
			}
		}
		return c;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
	 */
	public void unread() {
		if (eof) {
			eof = false;
			return;
		}
		found = false;
		characterScanner.unread();
	}

	public boolean foundSequence() {
		return foundSequence(true);
	}

	public boolean foundSequence(boolean reset) {
		try {
			return found;
		} finally {
			if (reset) {
				found = false;
			}
		}
	}

	public void setSequenceIgnored(boolean ignored) {
		this.ignored = ignored;
	}

}
