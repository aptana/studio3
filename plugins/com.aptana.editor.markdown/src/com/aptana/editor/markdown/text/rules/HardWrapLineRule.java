/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
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
