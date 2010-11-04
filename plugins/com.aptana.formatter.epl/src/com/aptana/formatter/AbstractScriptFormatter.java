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

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.util.DumpContentException;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * Abstract base class for the {@link IScriptFormatter} implementations.
 */
public abstract class AbstractScriptFormatter implements IScriptFormatter
{

	protected static final long ERROR_DISPLAY_TIMEOUT = 3000L;
	private final Map<String, ? extends Object> preferences;
	private boolean isSlave;
	private String mainContentType;

	/**
	 * @param preferences
	 */
	protected AbstractScriptFormatter(Map<String, ? extends Object> preferences, String mainContentType)
	{
		this.preferences = preferences;
		this.mainContentType = mainContentType;
	}

	/**
	 * Returns an {@link IParser} which was assigned to the mainContentType that was set on this formatter.
	 * 
	 * @return IParser (can be null in case there is no assigned parser for the given mainContentType)
	 * @see #checkinParser(IParser)
	 */
	protected IParser checkoutParser()
	{
		IParser parser = null;
		IParserPool pool = ParserPoolFactory.getInstance().getParserPool(getMainContentType());
		if (pool != null)
		{
			parser = pool.checkOut();
		}
		return parser;
	}

	/**
	 * Check in the parser back into the parser pool.
	 * 
	 * @param IParser
	 *            - The parser to check-in
	 * @see #checkoutParser()
	 */
	protected void checkinParser(IParser parser)
	{
		if (parser != null)
		{
			IParserPool pool = ParserPoolFactory.getInstance().getParserPool(getMainContentType());
			if (pool != null)
			{
				pool.checkIn(parser);
			}
		}
	}

	/**
	 * Returns an {@link IParser} for a given content-type (or language).<br>
	 * Note that in case you use this method to checkout a parser, you'll have to check it back in using the
	 * {@link #checkinParser(IParser, String)} function and provide it with the same content-type or language
	 * identifier.
	 * 
	 * @param contentTypeOrLanguage
	 * @return IParser (can be null in case there is no assigned parser for the given content-type or language)
	 * @see #checkinParser(IParser, String)
	 */
	protected IParser checkoutParser(String contentTypeOrLanguage)
	{
		IParser parser = null;
		IParserPool pool = ParserPoolFactory.getInstance().getParserPool(contentTypeOrLanguage);
		if (pool != null)
		{
			parser = pool.checkOut();
		}
		return parser;
	}

	/**
	 * Check in the parser back into the parser pool.
	 * 
	 * @param IParser
	 *            - The parser to check-in
	 * @param contentTypeOrLanguage
	 *            - The content-type or language that can identify the parser pool this parser will be pushed back into.
	 * @see #checkoutParser(String)
	 */
	protected void checkinParser(IParser parser, String contentTypeOrLanguage)
	{
		if (parser != null)
		{
			IParserPool pool = ParserPoolFactory.getInstance().getParserPool(contentTypeOrLanguage);
			if (pool != null)
			{
				pool.checkIn(parser);
			}
		}
	}

	/**
	 * Returns the main Content-Type that this formatter is formatting now. The value of this main Content-Type is
	 * mainly used when retrieving the parser .
	 * 
	 * @return The mainContentType this formatter is dealing with.
	 */
	public String getMainContentType()
	{
		return mainContentType;
	}

	protected boolean getBoolean(String key)
	{
		Object value = preferences.get(key);
		if (value != null)
		{
			if (value instanceof Boolean)
			{
				return ((Boolean) value).booleanValue();
			}
			if (value instanceof Number)
			{
				return ((Number) value).intValue() != 0;
			}
			return Boolean.valueOf(value.toString()).booleanValue();
		}
		return false;
	}

	/**
	 * Returns a Set of elements from a specific preference value. The elements will be delimited using the given
	 * delimiter.
	 * 
	 * @param key
	 * @param delimiter
	 *            - The delimiter to use in order the turn the preference value into a set.
	 * @return
	 */
	protected Set<String> getSet(String key, String delimiter)
	{
		Object value = preferences.get(key);
		if (value != null)
		{
			String[] elements = value.toString().split(delimiter);
			Set<String> set = new HashSet<String>();
			for (String str : elements)
			{
				set.add(str);
			}
			return set;
		}
		return Collections.emptySet();
	}

	protected int getInt(String key)
	{
		return toInt(preferences.get(key));
	}

	/**
	 * Logs an error and notify the user through a status line message and a beep.
	 * 
	 * @param input
	 * @param output
	 */
	protected void logError(String input, String output)
	{
		// Display a status error
		StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus, 3000L, true);
		if (FormatterPlugin.DEBUG)
		{
			// Log complete
			FormatterPlugin.log(new Status(IStatus.ERROR, FormatterPlugin.PLUGIN_ID, IStatus.ERROR,
					FormatterMessages.Formatter_formatterError, new DumpContentException(input
							+ "\n<!----------------------------!>\n" + output))); //$NON-NLS-1$
		}
		else
		{
			// Log basic
			FormatterPlugin.log(new Status(IStatus.ERROR, FormatterPlugin.PLUGIN_ID, IStatus.ERROR,
					FormatterMessages.Formatter_basicLogFormatterError, null));
		}
	}

	private static int toInt(Object value)
	{
		if (value != null)
		{
			if (value instanceof Number)
			{
				return ((Number) value).intValue();
			}
			try
			{
				return Integer.parseInt(value.toString());
			}
			catch (NumberFormatException e)
			{
				// ignore
			}
		}
		return 0;
	}

	protected String getString(String key)
	{
		Object value = preferences.get(key);
		if (value != null)
		{
			return value.toString();
		}
		return null;
	}

	/**
	 * @since 2.0
	 */
	protected IFormatterIndentGenerator createIndentGenerator()
	{
		final int tabSize = getTabSize();
		final int indentSize = getIndentSize();
		final String indentType = getIndentType();
		if (CodeFormatterConstants.SPACE.equals(indentType))
		{
			return new FormatterIndentGenerator(' ', indentSize, tabSize);
		}
		else if (CodeFormatterConstants.MIXED.equals(indentType))
		{
			return new FormatterMixedIndentGenerator(indentSize, tabSize);
		}
		else
		{
			return new FormatterIndentGenerator('\t', 1, tabSize);
		}
	}

	/**
	 * Check if the content that is read from the two given readers is equal in a way that it ignores the white-spaces
	 * at the beginning and the end of each line. Note that it does not ignore any changes in the spacing inside the
	 * lines. These changes will return false for this equality check. Same goes with new-lines that are added during
	 * the formatting process (if at all). In these cases, a custom check is needed, or even an AST comparison.
	 * 
	 * @param inputReader
	 * @param outputReader
	 * @return True if equal; False, otherwise.
	 * @see #equalsIgnoreWhitespaces(Reader, Reader)
	 */
	protected boolean equalLinesIgnoreBlanks(Reader inputReader, Reader outputReader)
	{
		LineNumberReader input = new LineNumberReader(inputReader);
		LineNumberReader output = new LineNumberReader(outputReader);
		for (;;)
		{
			final String inputLine = readLine(input);
			final String outputLine = readLine(output);
			if (inputLine == null)
			{
				return (outputLine == null);
			}
			else if (outputLine == null)
			{
				return false;
			}
			else if (!inputLine.equals(outputLine))
			{
				return false;
			}
		}
	}

	/**
	 * Check if the content that is read from the two given strings is equal in a way that it ignores <b>every</b> white
	 * space that exists in the content.
	 * 
	 * @param in
	 * @param out
	 * @return True if equal; False, otherwise.
	 */
	protected boolean equalsIgnoreWhitespaces(String in, String out)
	{
		if (in == null || out == null)
		{
			return in == out;
		}
		in = in.replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return in.equals(out);
	}

	private String readLine(LineNumberReader reader)
	{
		String line = null;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				if (line.length() > 0)
				{
					return line;
				}
			}
		}
		catch (IOException e)
		{
		}
		return null;
	}

	/**
	 * Returns the indentation level by looking at the previous line and the formatter settings for the tabs and spaces.
	 * This is the default way it's computed, unless a subclass override it. In case the subclass involves a parsing to
	 * get valid AST to compute the indentation, it might fail. Subclass that fail on the AST creation should call this
	 * method as a fall-back option.
	 * 
	 * @param document
	 * @param offset
	 * @return
	 */
	public int detectIndentationLevel(IDocument document, int offset)
	{
		try
		{
			int lineNumber = document.getLineOfOffset(offset + 1);
			if (lineNumber > 0)
			{
				IRegion previousLineRegion = document.getLineInformation(lineNumber - 1);
				String text = document.get(previousLineRegion.getOffset(), previousLineRegion.getLength());
				// grab the empty string at the beginning of the text.
				int spaceChars = 0;
				int tabChars = 0;
				for (int i = 0; i < text.length(); i++)
				{
					char c = text.charAt(i);
					if (!Character.isWhitespace(c))
					{
						break;
					}
					if (c == '\n' || c == '\r')
					{
						// ignore it
						continue;
					}
					if (c == ' ')
					{
						spaceChars++;
					}
					else if (c == '\t')
					{
						tabChars++;
					}
				}
				String indentType = getIndentType();
				int indentSize = getIndentSize();
				int tabSize = getTabSize();
				if (CodeFormatterConstants.TAB.equals(indentType))
				{
					if (tabSize == 0)
					{
						return 0;
					}
					// treat the whitespace-chars as tabs
					return (spaceChars / tabSize) + tabChars + 1;
				}
				else if (indentSize > 0)
				{
					if (CodeFormatterConstants.SPACE.equals(indentType))
					{
						// treat the tabs as spaces
						return (spaceChars + (tabSize * tabChars)) / indentSize + 1;
					}
					else
					{
						// it's Mixed
						return (spaceChars + tabChars) / indentSize + 1;
					}
				}

			}
		}
		catch (BadLocationException e)
		{
			FormatterPlugin.logError(e);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#setIsSlave(boolean)
	 */
	public void setIsSlave(boolean isSlave)
	{
		this.isSlave = isSlave;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#isSlave()
	 */
	public boolean isSlave()
	{
		return this.isSlave;
	}
}
