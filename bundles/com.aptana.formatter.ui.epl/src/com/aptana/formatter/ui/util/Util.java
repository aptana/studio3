/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.ui.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.aptana.core.util.IOUtil;

/**
 * Utilities library.
 */
public class Util
{
	// FIXME A number of these utilities should be re-using IOUtil, StringUtil, or moved there!
	private static char[] NO_CHAR = new char[0];
	private static final int DEFAULT_READING_SIZE = 8192;

	public static String concatenate(String[] lines, String delimiter)
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < lines.length; i++)
		{
			if (i > 0)
				buffer.append(delimiter);
			buffer.append(lines[i]);
		}
		return buffer.toString();
	}

	/*
	 * a character array. If a length is specified (ie. if length != -1), this represents the number of bytes in the
	 * stream. Note this doesn't close the stream. @throws IOException if a problem occured reading the stream.
	 */
	public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding) throws IOException
	{
		InputStreamReader reader = null;
		try
		{
			try
			{
				reader = encoding == null ? new InputStreamReader(toBufferedInputStream(stream))
						: new InputStreamReader(stream, encoding);
			}
			catch (UnsupportedEncodingException e)
			{
				// encoding is not supported
				reader = new InputStreamReader(toBufferedInputStream(stream));
			}
			char[] contents;
			int totalRead = 0;
			if (length == -1)
			{
				contents = NO_CHAR;
			}
			else
			{
				// length is a good guess when the encoding produces less or the
				// same amount of characters than the file length
				contents = new char[length]; // best guess
			}

			while (true)
			{
				int amountRequested;
				if (totalRead < length)
				{
					// until known length is met, reuse same array sized eagerly
					amountRequested = length - totalRead;
				}
				else
				{
					// reading beyond known length
					int current = reader.read();
					if (current < 0)
						break;

					amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE); // read at least 8K

					// resize contents if needed
					if (totalRead + 1 + amountRequested > contents.length)
						System.arraycopy(contents, 0, contents = new char[totalRead + 1 + amountRequested], 0,
								totalRead);

					// add current character
					contents[totalRead++] = (char) current; // coming from
					// totalRead==length
				}
				// read as many chars as possible
				int amountRead = reader.read(contents, totalRead, amountRequested);
				if (amountRead < 0)
					break;
				totalRead += amountRead;
			}

			// Do not keep first character for UTF-8 BOM encoding
			int start = 0;
			if (totalRead > 0 && IOUtil.UTF_8.equals(encoding))
			{
				if (contents[0] == 0xFEFF)
				{ // if BOM char then skip
					totalRead--;
					start = 1;
				}
			}

			// resize contents if necessary
			if (totalRead < contents.length)
				System.arraycopy(contents, start, contents = new char[totalRead], 0, totalRead);

			return contents;
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
	}

	private static InputStream toBufferedInputStream(InputStream stream)
	{
		if (stream instanceof BufferedInputStream)
		{
			return stream;
		}
		else
		{
			return new BufferedInputStream(stream, DEFAULT_READING_SIZE);
		}
	}

	/**
	 * Split this string around line boundaries (handles any line boundaries - "\n", "\r", "\r\n" so it is not
	 * equivalent to String#split("\n"))
	 * 
	 * @param content
	 * @return
	 */
	public static String[] splitLines(CharSequence content)
	{
		if (content == null)
		{
			return null;
		}
		final LineSplitter splitter = new LineSplitter(content);
		return splitter.split();
	}

	/**
	 * Split this string around line boundaries (handles any line boundaries - "\n", "\r", "\r\n" so it is not
	 * equivalent to String#split("\n"))
	 * 
	 * @param content
	 * @param limit
	 *            the maximal number of lines to return
	 * @return
	 */
	public static String[] splitLines(CharSequence content, int limit)
	{
		if (content == null)
		{
			return null;
		}
		final LineSplitter splitter = new LineSplitter(content);
		return splitter.split(limit);
	}

	/**
	 * Line splitter class.
	 */
	static class LineSplitter
	{

		private final CharSequence content;
		protected final int contentEnd;
		protected int contentPos;
		protected String lastLineDelimiter = null;

		private static final String DELIMITER_WINDOWS = "\r\n"; //$NON-NLS-1$
		private static final String DELIMITER_UNIX = "\n"; //$NON-NLS-1$
		private static final String DELIMITER_MAC = "\r"; //$NON-NLS-1$

		public LineSplitter(CharSequence content)
		{
			this.content = content;
			this.contentEnd = content.length();
		}

		/**
		 * @param lines
		 * @return
		 */
		public CharSequence selectHeadLines(int lines)
		{
			contentPos = 0;
			while (lines > 0 && contentPos < contentEnd)
			{
				findEndOfLine();
				--lines;
			}
			return content.subSequence(0, contentPos);
		}

		public String[] split()
		{
			final List<String> result = new ArrayList<String>();
			contentPos = 0;
			while (contentPos < contentEnd)
			{
				final int begin = contentPos;
				final int end = findEndOfLine();
				result.add(content.subSequence(begin, end).toString());
			}
			return result.toArray(new String[result.size()]);
		}

		public String[] split(int lines)
		{
			final List<String> result = new ArrayList<String>(lines);
			contentPos = 0;
			while (lines > 0 && contentPos < contentEnd)
			{
				final int begin = contentPos;
				final int end = findEndOfLine();
				result.add(content.subSequence(begin, end).toString());
				--lines;
			}
			return result.toArray(new String[result.size()]);
		}

		public int countLines()
		{
			contentPos = 0;
			int count = 0;
			while (contentPos < contentEnd)
			{
				findEndOfLine();
				++count;
			}
			return count;
		}

		protected final int findEndOfLine()
		{
			while (contentPos < contentEnd)
			{
				char charAt1 = content.charAt(contentPos);
				if (charAt1 == '\r')
				{
					final int endLine = contentPos;
					++contentPos;
					if (contentPos < contentEnd && content.charAt(contentPos) == '\n')
					{
						++contentPos;
						lastLineDelimiter = DELIMITER_WINDOWS;
					}
					else
					{
						lastLineDelimiter = DELIMITER_MAC;
					}
					return endLine;
				}
				else if (charAt1 == '\n')
				{
					final int endLine = contentPos;
					++contentPos;
					lastLineDelimiter = DELIMITER_UNIX;
					return endLine;
				}
				else
				{
					++contentPos;
				}
			}
			lastLineDelimiter = null;
			return contentPos;
		}

	}
}
