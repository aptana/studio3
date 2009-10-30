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

package com.aptana.radrails.editor.common;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

/**
 * @author Max Stepanov
 *
 */
public class SequenceRule extends MultiLineRule {

	/**
	 * Line delimiter comparator which orders according to decreasing delimiter length.
	 */
	private Comparator<Object> fLineDelimiterComparator = new Comparator<Object>() {
		public int compare(Object o1, Object o2) {
			return ((char[]) o2).length - ((char[]) o1).length;
		}
	};

	/**
	 * Cached line delimiters.
	 */
	private char[][] fLineDelimiters;

	/**
	 * Cached sorted {@linkplain #fLineDelimiters}.
	 */
	private char[][] fSortedLineDelimiters;

	/**
	 * Cached escape pairs;
	 */
	private char[][][] fEscapePairs;
		
	/**
	 * @param startSequence
	 * @param endSequence
	 * @param token
	 */
	public SequenceRule(String startSequence, String endSequence, IToken token, IPartitionerSwitchStrategy langSpec) {
		super(startSequence, endSequence, token, (char) 0, true);
		
		String[][] pairs = langSpec.getEscapePairs();
		fEscapePairs = new char[pairs.length][][];
		for (int i = 0; i < pairs.length; ++i) {
			fEscapePairs[i] = new char[][] {
					pairs[i][0].toCharArray(),
					pairs[i][1] != null ? pairs[i][1].toCharArray() : null
			};
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		char[][] originalDelimiters = scanner.getLegalLineDelimiters();
		int count = originalDelimiters.length;
		if (fLineDelimiters == null || originalDelimiters.length != count) {
			fSortedLineDelimiters = new char[count][];
		} else {
			while (count > 0 && fLineDelimiters[count-1] == originalDelimiters[count-1]) {
				count--;
			}
		}
		if (count != 0) {
			fLineDelimiters = originalDelimiters;
			System.arraycopy(fLineDelimiters, 0, fSortedLineDelimiters, 0, fLineDelimiters.length);
			Arrays.sort(fSortedLineDelimiters, fLineDelimiterComparator);
		}

		int readCount = 1;
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip escaped character(s)
				if (fEscapeContinuesLine) {
					c = scanner.read();
					for (int i = 0; i < fSortedLineDelimiters.length; ++i) {
						if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true)) {
							break;
						}
					}
				} else {
					scanner.read();
				}
			} else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
				// Check if the specified end sequence has been found.
				if (sequenceDetected(scanner, fEndSequence, true)) {
					return true;
				}
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for (int i = 0; i < fSortedLineDelimiters.length; ++i) {
					if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true)) {
						return true;
					}
				}
			} else {
				for (char[][] pair : fEscapePairs) {
					char[] start = pair[0];
					if (c == start[0] && sequenceDetected(scanner, start, false)) {
						readCount += start.length-1;
						char[] end = pair[1];
						while ((c = scanner.read()) != ICharacterScanner.EOF) {
							++readCount;
							if (end != null) {
								if (c == end[0] && sequenceDetected(scanner, end, true)) {
									readCount += end.length-1;
									break;
								}
							} else {
								boolean endOfSequence = false;
								for (int i = 0; i < fSortedLineDelimiters.length; ++i) {
									if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true)) {
										readCount += fSortedLineDelimiters[i].length-1;
										endOfSequence = true;
										break;
									}
								}
								if (endOfSequence) {
									break;
								}
							}
						}
						break;
					}
				}
				
			}
			++readCount;
		}

		if (fBreaksOnEOF)
			return true;

		for (; readCount > 0; --readCount)
			scanner.unread();

		return false;
	}
	
}
