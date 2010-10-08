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
package com.aptana.editor.markdown.text.rules;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;

public class HardWrapLineRule extends SingleLineRule
{
	/**
	 * Comparator that orders <code>char[]</code> in decreasing array lengths.
	 * 
	 * @since 3.1
	 */
	private static class DecreasingCharArrayLengthComparator implements Comparator<char[]>
	{
		public int compare(char[] o1, char[] o2)
		{
			return o2.length - o1.length;
		}
	}

	/**
	 * Line delimiter comparator which orders according to decreasing delimiter length.
	 * 
	 * @since 3.1
	 */
	private Comparator<char[]> fLineDelimiterComparator = new DecreasingCharArrayLengthComparator();

	private char[][] fSortedLineDelimiters;
	private char[][] fLineDelimiters;

	public HardWrapLineRule(String startSequence, String endSequence, IToken token)
	{
		super(startSequence, endSequence, token, (char) 0, true);
		setColumnConstraint(0);
	}

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner)
	{

		char[][] originalDelimiters = scanner.getLegalLineDelimiters();
		int count = originalDelimiters.length;
		if (fLineDelimiters == null || fLineDelimiters.length != count)
		{
			fSortedLineDelimiters = new char[count][];
		}
		else
		{
			while (count > 0 && Arrays.equals(fLineDelimiters[count - 1], originalDelimiters[count - 1]))
			{
				count--;
			}
		}
		if (count != 0)
		{
			fLineDelimiters = originalDelimiters;
			System.arraycopy(fLineDelimiters, 0, fSortedLineDelimiters, 0, fLineDelimiters.length);
			Arrays.sort(fSortedLineDelimiters, fLineDelimiterComparator);
		}

		int readCount = 1;
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF)
		{
			if (c == fEscapeCharacter)
			{
				// Skip escaped character(s)
				if (fEscapeContinuesLine)
				{
					c = scanner.read();
					for (int i = 0; i < fSortedLineDelimiters.length; i++)
					{
						if (c == fSortedLineDelimiters[i][0]
								&& sequenceDetected(scanner, fSortedLineDelimiters[i], true))
						{
							break;
						}
					}
				}
				else
				{
					scanner.read();
				}

			}
			else if (fEndSequence.length > 0 && c == fEndSequence[0])
			{
				// Check if the specified end sequence has been found.
				if (sequenceDetected(scanner, fEndSequence, true))
				{
					return true;
				}
			}
			else if (fBreaksOnEOL)
			{
				// Check for end of line since it can be used to terminate the pattern.
				for (int i = 0; i < fSortedLineDelimiters.length; i++)
				{
					if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true))
					{
						int lineDelimLength = fSortedLineDelimiters[i].length;
						// Read in all the line delim length
						for (int x = 0; x < lineDelimLength; x++)
						{
							scanner.read();
						}
						// Check if next char is space/tab
						c = scanner.read();
						if (!Character.isWhitespace(c))
						{
							// Next line is not indented, say we haven't hit the end yet!
							break;
						}

						for (int x = 0; x < lineDelimLength + 1; x++)
						{
							scanner.unread();
						}
						// unread the next char and line delim
						return true;
					}
				}
			}
			readCount++;
		}

		if (fBreaksOnEOF)
		{
			return true;
		}
		
		for (; readCount > 0; readCount--)
		{
			scanner.unread();
		}

		return false;
	}

	/*
	 * We continue to next line if it's not indented (tab or leading spaces) and ignore end sequence on current line.
	 */
}
