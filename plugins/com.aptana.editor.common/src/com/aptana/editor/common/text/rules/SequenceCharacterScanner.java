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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy;
import com.aptana.editor.common.IPartitionScannerSwitchStrategy.SequenceBypassHandler;
import com.aptana.editor.common.TextUtils;

/**
 * @author Max Stepanov
 *
 */
public class SequenceCharacterScanner implements ICharacterScanner {

	private ICharacterScanner characterScanner;
	private IPartitionScannerSwitchStrategy switchStrategy;
	private SequenceBypassHandler sequenceBypassHandler;
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
		try {
			return found;
		} finally {
			found = false;
		}
	}

	public void setSequenceIgnored(boolean ignored) {
		this.ignored = ignored;
	}

}
