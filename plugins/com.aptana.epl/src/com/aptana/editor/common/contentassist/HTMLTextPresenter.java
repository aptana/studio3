/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
// copied directly from org.eclipse.jface.internal.text.link.contentassist.HTMLTextPresenter;

package com.aptana.editor.common.contentassist;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.DefaultInformationControl.IInformationPresenter;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

/**
 * Note: apparently we eventually can switch to using the browser widget here
 */
public class HTMLTextPresenter implements IInformationPresenter
{

	private static final String LINE_DELIM = System.getProperty("line.separator", "\n"); //$NON-NLS-1$ //$NON-NLS-2$

	private int fCounter;
	private boolean fEnforceUpperLineLimit;

	/**
	 * HTMLTextPresenter
	 * 
	 * @param enforceUpperLineLimit
	 */
	public HTMLTextPresenter(boolean enforceUpperLineLimit)
	{
		super();
		fEnforceUpperLineLimit = enforceUpperLineLimit;
	}

	/**
	 * HTMLTextPresenter
	 */
	public HTMLTextPresenter()
	{
		this(true);
	}

	/**
	 * adaptTextPresentation
	 * 
	 * @param presentation
	 * @param offset
	 * @param insertLength
	 */
	protected void adaptTextPresentation(TextPresentation presentation, int offset, int insertLength)
	{

		int yoursStart = offset;
		int yoursEnd = offset + insertLength - 1;
		yoursEnd = Math.max(yoursStart, yoursEnd);

		Iterator<?> e = presentation.getAllStyleRangeIterator();
		while (e.hasNext())
		{
			StyleRange range = (StyleRange) e.next();

			int myStart = range.start;
			int myEnd = range.start + range.length - 1;
			myEnd = Math.max(myStart, myEnd);

			if (myEnd < yoursStart)
			{
				continue;
			}

			if (myStart < yoursStart)
			{
				range.length += insertLength;
			}
			else
			{
				range.start += insertLength;
			}
		}
	}

	/**
	 * Creates the specified text reader for the presentation
	 * 
	 * @param hoverInfo
	 * @param presentation
	 * @param display
	 * @return a new HTMLTextReader object
	 */
	protected Reader createReader(String hoverInfo, TextPresentation presentation, Display display)
	{
		return new HTML2TextReader(new StringReader(hoverInfo), presentation, display);
	}

	private void append(StringBuffer buffer, String string, TextPresentation presentation)
	{

		int length = string.length();
		buffer.append(string);

		if (presentation != null)
		{
			adaptTextPresentation(presentation, fCounter, length);
		}

		fCounter += length;
	}

	/**
	 * see org.eclipse.jface.text.DefaultInformationControl$IInformationPresenter#updatePresentation(org.eclipse.swt.widgets.Display, java.lang.String, org.eclipse.jface.text.TextPresentation, int, int)
	 * 
	 * @param display 
	 * @param hoverInfo 
	 * @param presentation 
	 * @param maxWidth 
	 * @param maxHeight 
	 * @return int
	 */
	public String updatePresentation(Display display, String hoverInfo, TextPresentation presentation, int maxWidth,
			int maxHeight)
	{

		if (hoverInfo == null)
		{
			return null;
		}

		/*
		 * if(false) { StringBuffer sb = new StringBuffer(); sb.append("<head><style>h2 { font-size:11px; margin:0px;
		 * padding: 0px;} </style></head>"); sb.append("<body bgcolor=\"#ffffe1\" style=\"font-size:9px; font-family:
		 * Verdana, sans-serif; margin:0px;\">"); sb.append(hoverInfo); sb.append("</body>"); return sb.toString(); }
		 */

		GC gc = new GC(display);
		try
		{

			StringBuffer buffer = new StringBuffer();
			int maxNumberOfLines = Math.round(maxHeight / gc.getFontMetrics().getHeight());

			fCounter = 0;
			LineBreakingReader reader = new LineBreakingReader(createReader(hoverInfo, presentation, display), gc,
					maxWidth);

			boolean lastLineFormatted = false;
			String lastLineIndent = null;

			String line = reader.readLine();
			boolean lineFormatted = reader.isFormattedLine();
			boolean firstLineProcessed = false;

			while (line != null)
			{

				if (fEnforceUpperLineLimit && maxNumberOfLines <= 0)
				{
					break;
				}

				if (firstLineProcessed)
				{
					if (!lastLineFormatted)
					{
						append(buffer, LINE_DELIM, null);
					}
					else
					{
						append(buffer, LINE_DELIM, presentation);
						if (lastLineIndent != null)
						{
							append(buffer, lastLineIndent, presentation);
						}
					}
				}

				append(buffer, line, null);
				firstLineProcessed = true;

				lastLineFormatted = lineFormatted;
				if (!lineFormatted)
				{
					lastLineIndent = null;
				}
				// else if (lastLineIndent == null)
				// lastLineIndent= getIndent(line);

				line = reader.readLine();
				lineFormatted = reader.isFormattedLine();

				maxNumberOfLines--;
			}

			if (line != null)
			{
				append(buffer, LINE_DELIM, lineFormatted ? presentation : null);
				append(buffer, System.getProperty("HTMLTextPresenter.ellipse", "..."), presentation); //$NON-NLS-1$
			}

			return trim(buffer, presentation);

		}
		catch (IOException e)
		{

			// ignore TODO do something else?
			return null;

		}
		finally
		{
			gc.dispose();
		}
	}

	private String trim(StringBuffer buffer, TextPresentation presentation)
	{

		int length = buffer.length();

		int end = length - 1;
		while (end >= 0 && Character.isWhitespace(buffer.charAt(end)))
		{
			--end;
		}

		if (end == -1)
		{
			return ""; 
		}

		if (end < length - 1)
		{
			buffer.delete(end + 1, length);
		}
		else
		{
			end = length;
		}

		int start = 0;
		while (start < end && Character.isWhitespace(buffer.charAt(start)))
		{
			++start;
		}

		buffer.delete(0, start);
		presentation.setResultWindow(new Region(start, buffer.length()));
		return buffer.toString();
	}
}
