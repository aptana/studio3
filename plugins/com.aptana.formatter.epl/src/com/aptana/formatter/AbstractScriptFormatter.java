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

import org.eclipse.jface.text.IDocument;

import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.IScriptFormatter;
import com.aptana.formatter.ui.internal.FormatterIndentGenerator;
import com.aptana.formatter.ui.internal.FormatterMixedIndentGenerator;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParserPoolFactory;

/**
 * Abstract base class for the {@link IScriptFormatter} implementations.
 */
public abstract class AbstractScriptFormatter implements IScriptFormatter
{

	private final Map<String, ? extends Object> preferences;
	private boolean isSlave;

	/**
	 * @param preferences
	 */
	protected AbstractScriptFormatter(Map<String, ? extends Object> preferences)
	{
		this.preferences = preferences;
	}

	/**
	 * Returns an {@link IParser} that is assigned to the given language.
	 * 
	 * @param language
	 *            The language identifier. For example, text/html.
	 * @return IParser (can be null in case there is no assigned parser for the given language)
	 */
	protected IParser getParser(String language)
	{
		IParser parser = null;
		IParserPool pool = ParserPoolFactory.getInstance().getParserPool(language);
		if (pool != null)
		{
			parser = pool.checkOut();
			pool.checkIn(parser);
		}
		return parser;
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
				if (outputLine == null)
				{
					return true;
				}
				else
				{
					return false;
				}
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
		if (in == null)
		{
			return out == null;
		}
		if (out == null)
		{
			return false;
		}
		in = in.replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
		out = out.replaceAll("\\s", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return in.equals(out);
	}

	private String readLine(LineNumberReader reader)
	{
		String line;
		do
		{
			try
			{
				line = reader.readLine();
			}
			catch (IOException e)
			{
				// should not happen
				return null;
			}
			if (line == null)
			{
				return line;
			}
			line = line.trim();
		}
		while (line.length() == 0);
		return line;
	}

	public int detectIndentationLevel(IDocument document, int offset)
	{
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
