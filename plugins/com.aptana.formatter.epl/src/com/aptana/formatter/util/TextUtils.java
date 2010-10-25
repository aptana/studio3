/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.formatter.util;


public abstract class TextUtils
{
	private TextUtils()
	{
		throw new AssertionError("Cannot instantiate utility class"); //$NON-NLS-1$
	}

	/**
	 * Counts the number of lines in the specified string. Lines are counter by the separators ("\n", "\r", "\r\n")
	 * 
	 * @param content
	 * @return
	 */
	public static int countLines(CharSequence content)
	{
		return new LineSplitter(content).countLines();
	}

	/**
	 * @param content
	 * @param lines
	 * @return
	 */
	public static CharSequence selectHeadLines(CharSequence content, int lines)
	{
		return new LineSplitter(content).selectHeadLines(lines);
	}

	private static class LineSplitter
	{

		private final CharSequence content;
		protected final int contentEnd;
		protected int contentPos;
		@SuppressWarnings("unused")
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
