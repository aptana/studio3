/**
 * Copyright (c) 2005-2006 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package com.aptana.editor.common.contentassist;

/***********************************************************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html Contributors: IBM Corporation - initial API and implementation
 **********************************************************************************************************************/
// copied directly from org.eclipse.jface.internal.text.link.contentassist.LineBreakingReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.text.BreakIterator;

import org.eclipse.swt.graphics.GC;

/**
 * Not a real reader. Could change if requested
 */
public class LineBreakingReader
{

	private BufferedReader fReader;
	private GC fGC;
	private int fMaxWidth;

	private String fLine;
	private int fOffset;

	private BreakIterator fLineBreakIterator;

	/**
	 * Creates a reader that breaks an input text to fit in a given width.
	 * 
	 * @param reader
	 *            Reader of the input text
	 * @param gc
	 *            The graphic context that defines the currently used font sizes
	 * @param maxLineWidth
	 *            The max width (in pixels) where the text has to fit in
	 */
	public LineBreakingReader(Reader reader, GC gc, int maxLineWidth)
	{
		fReader = new BufferedReader(reader);
		fGC = gc;
		fMaxWidth = maxLineWidth;
		fOffset = 0;
		fLine = null;
		fLineBreakIterator = BreakIterator.getLineInstance();
	}

	/**
	 * isFormattedLine
	 * 
	 * @return boolean
	 */
	public boolean isFormattedLine()
	{
		return fLine != null;
	}

	/**
	 * readLine
	 * 
	 * @return String
	 * @throws IOException
	 */
	public String readLine() throws IOException
	{
		if (fLine == null)
		{
			String line = fReader.readLine();
			if (line == null)
			{
				return null;
			}

			int lineLen = fGC.textExtent(line).x;
			if (lineLen < fMaxWidth)
			{
				return line;
			}
			fLine = line;
			fLineBreakIterator.setText(line);
			fOffset = 0;
		}
		int breakOffset = findNextBreakOffset(fOffset);
		String res;
		if (breakOffset != BreakIterator.DONE)
		{
			res = fLine.substring(fOffset, breakOffset);
			fOffset = findWordBegin(breakOffset);
			if (fOffset == fLine.length())
			{
				fLine = null;
			}
		}
		else
		{
			res = fLine.substring(fOffset);
			fLine = null;
		}
		return res;
	}

	private int findNextBreakOffset(int currOffset)
	{

		int tempOffset = currOffset;
		int currWidth = 0;
		int nextOffset = fLineBreakIterator.following(tempOffset);
		while (nextOffset != BreakIterator.DONE)
		{
			String word = fLine.substring(tempOffset, nextOffset);
			int wordWidth = fGC.textExtent(word).x;
			int nextWidth = wordWidth + currWidth;
			if (nextWidth > fMaxWidth)
			{
				if (currWidth > 0)
				{
					return tempOffset;
				}
				return nextOffset;
			}
			currWidth = nextWidth;
			tempOffset = nextOffset;
			nextOffset = fLineBreakIterator.next();
		}
		return nextOffset;
	}

	private int findWordBegin(int idx)
	{

		int index = idx;
		while (index < fLine.length() && Character.isWhitespace(fLine.charAt(index)))
		{
			index++;
		}
		return index;
	}
}
