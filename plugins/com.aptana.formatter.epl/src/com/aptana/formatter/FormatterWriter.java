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
package com.aptana.formatter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IRegion;

import com.aptana.formatter.ExcludeRegionList.EXCLUDE_STRATEGY;
import com.aptana.formatter.util.TextUtils;

public class FormatterWriter implements IFormatterWriter
{

	private final StringBuilder writer = new StringBuilder();
	private final StringBuilder indent = new StringBuilder();
	/**
	 * @since 2.0
	 */
	private final StringBuilder callbackBuffer = new StringBuilder();
	private final StringBuilder emptyLines = new StringBuilder();

	private boolean lineStarted = false;
	private char lastChar = 0;
	private int lineNumber = 0;
	private final List<IFormatterCallback> newLineCallbacks = new ArrayList<IFormatterCallback>();

	private final String lineDelimiter;
	private final IFormatterDocument document;
	private final IFormatterIndentGenerator indentGenerator;
	private int linesPreserve = -1;
	private int wrapLength = -1;
	private boolean preserveSpaces = true;
	private boolean skipNextNewLine = false;
	private boolean canAppendToPreviousLine = false;
	private boolean trimTrailingSpaces = true;
	private boolean trimBlankLines = true;
	private boolean keepLines = false;

	/**
	 * @param lineDelimiter
	 * @since 2.0
	 */
	public FormatterWriter(IFormatterDocument document, String lineDelimiter, IFormatterIndentGenerator indentGenerator)
	{
		this.document = document;
		this.lineDelimiter = lineDelimiter;
		this.indentGenerator = indentGenerator;
	}

	public void ensureLineStarted(IFormatterContext context)
	{
		if (!lineStarted)
		{
			startLine(context);
		}
	}

	public void write(IFormatterContext context, int startOffset, int endOffset)
	{
		if (!excludes.isExcluded(startOffset, endOffset))
		{
			if (endOffset > startOffset)
			{
				write(context, document.get(startOffset, endOffset));
			}
		}
		else
		{
			// Check if we need to write the region as is, or just skip it.
			EXCLUDE_STRATEGY excludeAction = excludes.getExcludeStrategy(startOffset, endOffset);
			if (excludeAction != null && EXCLUDE_STRATEGY.WRITE_AS_IS.equals(excludeAction))
			{
				// Write directly
				writer.append(document.get(startOffset, endOffset));
			}
			else
			{
				final IRegion[] regions = excludes.selectValidRanges(startOffset, endOffset);
				for (int i = 0; i < regions.length; ++i)
				{
					write(context, document.get(regions[i]));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterWriter#writeText(com.aptana.formatter.IFormatterContext, java.lang.String)
	 */
	public void writeText(IFormatterContext context, String text)
	{
		writeText(context, text, true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterWriter#writeText(com.aptana.formatter.IFormatterContext, java.lang.String,
	 * boolean)
	 */
	public void writeText(IFormatterContext context, String text, boolean removePreviousSpaces)
	{
		if (text.trim().length() != 0)
		{
			skipNextNewLine = false;
		}
		if (lineStarted && removePreviousSpaces)
		{
			trimTrailingSpaces();
		}
		write(context, text);
	}

	private void trimTrailingSpaces()
	{
		while (writer.length() > 0 && FormatterUtils.isSpace(writer.charAt(writer.length() - 1)))
		{
			writer.setLength(writer.length() - 1);
		}
	}

	/*
	 * @see IFormatterWriter#writeLineBreak(IFormatterContext)
	 */
	public void writeLineBreak(IFormatterContext context)
	{
		if (lineStarted && !keepLines)
		{
			write(context, lineDelimiter);
			assert (!lineStarted);
			skipNextNewLine = true;
		}
	}

	public void skipNextLineBreaks(IFormatterContext context)
	{
		if (!keepLines)
		{
			skipNextNewLine = true;
		}
	}

	public void appendToPreviousLine(IFormatterContext context, String text)
	{
		if (!lineStarted && canAppendToPreviousLine)
		{
			skipNextNewLine = false;
			emptyLines.setLength(0);
			indent.setLength(0);
			int len = writer.length();
			if (len > 0)
			{
				if (writer.charAt(len - 1) == '\n')
				{
					--len;
					if (len > 0 && writer.charAt(len - 1) == '\r')
					{
						--len;
					}
				}
				else if (writer.charAt(len - 1) == '\r')
				{
					--len;
				}
				writer.setLength(len);
				writer.append(text);
				lineStarted = true;
			}
		}
	}

	public void disableAppendToPreviousLine()
	{
		canAppendToPreviousLine = false;
	}

	private void write(IFormatterContext context, String text)
	{
		if (!context.isWrapping())
		{
			for (int i = 0; i < text.length(); ++i)
			{
				write(context, text.charAt(i));
			}
		}
		else
		{
			int offset;
			int start = findLineStart();
			if (lineStarted)
			{
				offset = calculateOffset(start);
			}
			else
			{
				offset = 0;
			}
			int savedLineNumber = lineNumber;
			for (int i = 0; i < text.length(); ++i)
			{
				final char ch = text.charAt(i);
				if (lineStarted && !FormatterUtils.isSpace(ch) && !FormatterUtils.isLineSeparator(ch))
				{
					if (savedLineNumber != lineNumber)
					{
						start = findLineStart();
						offset = calculateOffset(start);
						savedLineNumber = lineNumber;
					}
					if (wrapLength > 0 && offset > wrapLength)
					{
						int begin = start;
						while (begin < writer.length() && FormatterUtils.isSpace(writer.charAt(begin)))
						{
							++begin;
						}
						if (begin < writer.length())
						{
							begin += context.getCommentStartLength(writer, begin);
						}
						while (begin < writer.length() && FormatterUtils.isSpace(writer.charAt(begin)))
						{
							++begin;
						}
						int wordBegin = writer.length();
						while (wordBegin > begin && !FormatterUtils.isSpace(writer.charAt(wordBegin - 1)))
						{
							--wordBegin;
						}
						int prevWordEnd = wordBegin;
						while (prevWordEnd > begin && FormatterUtils.isSpace(writer.charAt(prevWordEnd - 1)))
						{
							--prevWordEnd;
						}
						if (prevWordEnd > begin)
						{
							StringBuilder tempIndentBuffer = new StringBuilder();
							indentGenerator.generateIndent(context.getIndent(), tempIndentBuffer);
							writer.replace(prevWordEnd, wordBegin,
									lineDelimiter + tempIndentBuffer + context.getWrappingCommentPrefix(text));
							start = prevWordEnd + lineDelimiter.length();
							offset = calculateOffset(start);
						}
					}
				}
				write(context, ch);
				++offset;
			}
		}
	}

	private int calculateOffset(int pos)
	{
		int offset = 0;
		while (pos < writer.length())
		{
			char ch = writer.charAt(pos++);
			if (ch == '\t')
			{
				final int tabSize = indentGenerator.getTabSize();
				offset = (offset + tabSize - 1) / tabSize * tabSize;
			}
			else
			{
				++offset;
			}
		}
		return offset;
	}

	private int findLineStart()
	{
		int pos = writer.length();
		while (pos > 0 && !FormatterUtils.isLineSeparator(writer.charAt(pos - 1)))
		{
			--pos;
		}
		return pos;
	}

	/**
	 * @param context
	 * @param charAt
	 */
	private void write(IFormatterContext context, char ch)
	{
		if (ch == '\n' || ch == '\r')
		{
			if (lineStarted)
			{
				if (trimTrailingSpaces)
				{
					trimTrailingSpaces();
				}
				writer.append(ch);
				lineStarted = false;
				if (!newLineCallbacks.isEmpty())
				{
					executeNewLineCallbacks(context);
					assert newLineCallbacks.isEmpty();
				}
			}
			else if (ch == '\n' && lastChar == '\r')
			{
				if (emptyLines.length() == 0)
				{
					writer.append(ch); // windows EOL = "\r\n"
				}
				else
				{
					emptyLines.append(ch);
				}
			}
			else
			{
				if (!trimBlankLines)
				{
					emptyLines.append(indent);
				}
				indent.setLength(0);
				emptyLines.append(ch);
			}
		}
		else if (!lineStarted)
		{
			if (Character.isWhitespace(ch))
			{
				indent.append(ch);
			}
			else
			{
				startLine(context);
				writer.append(ch);
			}
		}
		else
		{
			if (!preserveSpaces && context.isIndenting() && !context.isComment() && FormatterUtils.isSpace(ch))
			{
				if (writer.charAt(writer.length() - 1) != ' ')
				{
					writer.append(' ');
				}
			}
			else
			{
				writer.append(ch);
			}
		}
		lastChar = ch;
	}

	private void executeNewLineCallbacks(IFormatterContext context)
	{
		final IFormatterRawWriter callbackWriter = new IFormatterRawWriter()
		{

			public void writeIndent(IFormatterContext context)
			{
				FormatterWriter.this.writeIndent(context, callbackBuffer);
			}

			public void writeText(IFormatterContext context, String text)
			{
				callbackBuffer.append(text);
			}

		};
		IFormatterCallback[] copy = newLineCallbacks.toArray(new IFormatterCallback[newLineCallbacks.size()]);
		newLineCallbacks.clear();
		for (IFormatterCallback callback : copy)
		{
			callback.call(context, callbackWriter);
		}
	}

	private void startLine(IFormatterContext context)
	{
		if (callbackBuffer.length() != 0)
		{
			writer.append(callbackBuffer);
			callbackBuffer.setLength(0);
		}
		if (context.getBlankLines() >= 0)
		{
			if (writer.length() != 0)
			{
				for (int i = 0; i < context.getBlankLines(); ++i)
				{
					writer.append(lineDelimiter);
				}
			}
			context.resetBlankLines();
		}
		else if (emptyLines.length() != 0)
		{
			writeEmptyLines();
		}
		skipNextNewLine = false;
		emptyLines.setLength(0);
		if (context.isIndenting())
		{
			writeIndent(context);
		}
		else
		{
			writer.append(indent);
		}
		indent.setLength(0);
		lineStarted = true;
		++lineNumber;
		canAppendToPreviousLine = true;
	}

	private void writeEmptyLines()
	{
		if (skipNextNewLine)
		{
			int i = 0;
			if (emptyLines.charAt(i) == '\r')
			{
				++i;
				if (i < emptyLines.length() && emptyLines.charAt(i) == '\n')
				{
					++i;
				}
			}
			else if (emptyLines.charAt(i) == '\n')
			{
				++i;
			}
			if (i > 0)
			{
				emptyLines.delete(0, i);
			}
		}
		if (linesPreserve >= 0 && linesPreserve < Integer.MAX_VALUE && TextUtils.countLines(emptyLines) > linesPreserve)
		{
			writer.append(TextUtils.selectHeadLines(emptyLines, linesPreserve));
		}
		else
		{
			writer.append(emptyLines);
		}
	}

	/**
	 * @param context
	 */
	public void writeIndent(IFormatterContext context)
	{
		writeIndent(context, writer);
	}

	/**
	 * @since 2.0
	 */
	private void writeIndent(IFormatterContext context, StringBuilder buffer)
	{
		indentGenerator.generateIndent(context.getIndent(), buffer);
	}

	public String getOutput()
	{
		return writer.toString();
	}

	private final ExcludeRegionList excludes = new ExcludeRegionList();

	public void excludeRegion(IRegion region, EXCLUDE_STRATEGY strategy)
	{
		excludes.excludeRegion(region, strategy);
	}

	/**
	 * Exclude a list of regions.
	 * 
	 * @param regions
	 * @param strategy
	 * @see #excludeRegion(IRegion, EXCLUDE_STRATEGY)
	 */
	public void excludeRegions(List<IRegion> regions, EXCLUDE_STRATEGY strategy)
	{
		if (regions != null)
		{
			for (IRegion region : regions)
			{
				excludes.excludeRegion(region, strategy);
			}
		}
	}

	public void addNewLineCallback(IFormatterCallback callback)
	{
		newLineCallbacks.add(callback);
	}

	public void flush(IFormatterContext context)
	{
		if (!newLineCallbacks.isEmpty())
		{
			if (lineStarted)
			{
				writer.append(lineDelimiter);
				lineStarted = false;
			}
			executeNewLineCallbacks(context);
			assert newLineCallbacks.isEmpty();
		}
		if (callbackBuffer.length() != 0)
		{
			writer.append(callbackBuffer);
			callbackBuffer.setLength(0);
		}
		if (emptyLines.length() != 0)
		{
			writeEmptyLines();
			emptyLines.setLength(0);
		}
	}

	/**
	 * @since 2.0
	 */
	public int getLinesPreserve()
	{
		return linesPreserve;
	}

	/**
	 * @param value
	 */
	public void setLinesPreserve(int value)
	{
		this.linesPreserve = value;
	}

	/**
	 * @return the wrapLength
	 */
	public int getWrapLength()
	{
		return wrapLength;
	}

	/**
	 * @param wrapLength
	 *            the wrapLength to set
	 */
	public void setWrapLength(int wrapLength)
	{
		this.wrapLength = wrapLength;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterWriter#isPreserveSpaces()
	 */
	public boolean isPreserveSpaces()
	{
		return preserveSpaces;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterWriter#setPreserveSpaces(boolean)
	 */
	public void setPreserveSpaces(boolean preserveSpaces)
	{
		this.preserveSpaces = preserveSpaces;
	}

	/**
	 * @since 2.0
	 */
	public boolean isTrimTrailingSpaces()
	{
		return trimTrailingSpaces;
	}

	/**
	 * @since 2.0
	 */
	public void setTrimTrailingSpaces(boolean trimTrailingSpaces)
	{
		this.trimTrailingSpaces = trimTrailingSpaces;
	}

	/**
	 * @since 2.0
	 */
	public boolean isTrimEmptyLines()
	{
		return trimBlankLines;
	}

	/**
	 * @since 2.0
	 */
	public void setTrimEmptyLines(boolean trimEmptyLines)
	{
		this.trimBlankLines = trimEmptyLines;
	}

	/**
	 * @param keepLines
	 *            the keepLines to set
	 * @since 2.0
	 */
	public void setKeepLines(boolean keepLines)
	{
		this.keepLines = keepLines;
	}

	/**
	 * @return the keepLines
	 * @since 2.0
	 */
	public boolean isKeepLines()
	{
		return keepLines;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterWriter#endsWithNewLine()
	 */
	public boolean endsWithNewLine()
	{
		int writerLength = writer.length();
		if (writerLength < lineDelimiter.length())
		{
			return false;
		}
		if (lineDelimiter.length() == 1)
		{
			return writer.charAt(writerLength - 1) == lineDelimiter.charAt(0);
		}
		else if (lineDelimiter.length() == 2)
		{
			return writer.charAt(writerLength - 2) == lineDelimiter.charAt(0)
					&& writer.charAt(writerLength - 1) == lineDelimiter.charAt(1);
		}
		return false;
	}

	/**
	 * Returns true in case the given source ends with the given new-line delimiter; false otherwise.
	 * 
	 * @param source
	 * @param lineDelimiter
	 */
	public static boolean endsWithNewLine(String source, String lineDelimiter)
	{
		int sourceLength = source.length();
		if (sourceLength < lineDelimiter.length())
		{
			return false;
		}
		if (lineDelimiter.length() == 1)
		{
			return source.charAt(sourceLength - 1) == lineDelimiter.charAt(0);
		}
		else if (lineDelimiter.length() == 2)
		{
			return source.charAt(sourceLength - 2) == lineDelimiter.charAt(0)
					&& source.charAt(sourceLength - 1) == lineDelimiter.charAt(1);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IFormatterWriter#isInBlankLine()
	 */
	public boolean isInBlankLine()
	{
		for (int i = writer.length() - 1; i >= 0; i--)
		{
			char c = writer.charAt(i);
			if (c == '\n' || c == '\r')
			{
				return true;
			}
			if (!Character.isWhitespace(c))
			{
				return false;
			}
		}
		return true;
	}
}
