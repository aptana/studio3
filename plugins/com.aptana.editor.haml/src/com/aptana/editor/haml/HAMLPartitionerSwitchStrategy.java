/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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

package com.aptana.editor.haml;

import org.eclipse.jface.text.rules.ICharacterScanner;

import com.aptana.editor.common.IPartitionScannerSwitchStrategy.SequenceBypassHandler;
import com.aptana.editor.common.PartitionerSwitchStrategy;
import com.aptana.editor.common.TextUtils;

/**
 * @author Max Stepanov
 *
 */
public class HAMLPartitionerSwitchStrategy extends PartitionerSwitchStrategy {

	private static HAMLPartitionerSwitchStrategy instance;
	
	private static final String[][] HAML_PAIRS = new String[][] {
		{ "=", "\n" }, //$NON-NLS-1$ //$NON-NLS-2$
		{ "~", "\n" }, //$NON-NLS-1$ //$NON-NLS-2$
		{ "-", "\n" }, //$NON-NLS-1$ //$NON-NLS-2$
	};
	
	private static final char ESCAPE = '\\';
	private static final char COMMA = ',';
	private static final char VERTICAL = '|';
	
	private static final SequenceBypassHandler START_BYPASS_HANDLER = new SequenceBypassHandler() {
		public boolean bypassSequence(ICharacterScanner characterScanner, char[] sequenceFound) {
			if (characterScanner.getColumn() > 0) {
				characterScanner.unread();
				int c = characterScanner.read();
				if (ESCAPE == c) {
					return true;
				}
			}
			return false;
		}
	};

	private static final SequenceBypassHandler END_BYPASS_HANDLER = new SequenceBypassHandler() {
		public boolean bypassSequence(ICharacterScanner characterScanner, char[] sequenceFound) {
			if (characterScanner.getColumn() > 0) {
				characterScanner.unread();
				int c = characterScanner.read();
				if (COMMA == c) {
					return true;
				} else if (VERTICAL == c) {
					char[][] newLineSequences = TextUtils.rsort(characterScanner.getLegalLineDelimiters());
					int index = 0;
					try {
						// skip found sequence
						for (; index < sequenceFound.length; ++index) {
							characterScanner.read();
						}
						// search for newline, remember previous character to compare with vertical
						int previous = 0;
						while ((c = characterScanner.read()) != ICharacterScanner.EOF) {
							++index;
							for (char[] sequence : newLineSequences) {
								if (c == sequence[0] && TextUtils.sequenceDetected(characterScanner, sequence, false)) {
									return (VERTICAL == previous);
								}
							}
							previous = c;
						}
					} finally {
						for (int j = index; j > 0; --j) {
							characterScanner.unread();
						}
					}

				}
			}
			return false;
		}
	};

	/**
	 * 
	 */
	private HAMLPartitionerSwitchStrategy() {
		super(HAML_PAIRS, START_BYPASS_HANDLER, END_BYPASS_HANDLER);
	}
	
	public static HAMLPartitionerSwitchStrategy getDefault() {
		if (instance == null) {
			instance = new HAMLPartitionerSwitchStrategy();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.aptana.editor.common.IPartitionerSwitchStrategy#getSwitchTagPairs()
	 */
	public String[][] getSwitchTagPairs() {
		return HAML_PAIRS;
	}

}
