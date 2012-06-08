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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * Abstract base class for the {@link IScriptFormatter} implementations.
 */
public abstract class AbstractScriptFormatter implements IScriptFormatter
{

	protected static final long ERROR_DISPLAY_TIMEOUT = 3000L;
	private final Map<String, String> preferences;
	private boolean isSlave;
	private String mainContentType;
	protected String lineSeparator;

	/**
	 * @param preferences
	 * @param lineSeparator
	 */
	protected AbstractScriptFormatter(Map<String, String> preferences, String mainContentType, String lineSeparator)
	{
		this.preferences = preferences;
		this.mainContentType = mainContentType;
		this.lineSeparator = lineSeparator;
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

	protected int getInt(String key, int minValue)
	{
		return Math.max(minValue, toInt(preferences.get(key)));
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
		// Log a basic error. It's up to the subclassing formatter implementation to log any advance information
		// regarding the failure.
		IdeLog.logError(FormatterPlugin.getDefault(), FormatterMessages.Formatter_basicLogFormatterError,
				IDebugScopes.DEBUG);
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
	 * Parse the output and look for the formatter On-Off regions.<br>
	 * We do that to compare it later to the original input, and then adjust the formatting result to have the original
	 * content in those regions.<br>
	 * We can assume at this point that the formatter On-Off is enabled and valid in the preferences.<br>
	 * This method is a generic one that retrieves the comments from the {@link IParseRootNode}. A formatter that does
	 * not provide comment nodes through that mechanism should override this method, or not use it.
	 * 
	 * @param output
	 *            The formatter output
	 * @param formatterOffPattern
	 * @param formatterOnPattern
	 * @return The formatter On-Off regions that we have in the output source.
	 * @see #getOutputOnOffRegions(String, String, String, IParseState)
	 */
	protected List<IRegion> getOutputOnOffRegions(String output, String formatterOffPattern, String formatterOnPattern)
	{
		return getOutputOnOffRegions(formatterOffPattern, formatterOnPattern, new ParseState(output));
	}

	/**
	 * Parse the output and look for the formatter On-Off regions.<br>
	 * We do that to compare it later to the original input, and then adjust the formatting result to have the original
	 * content in those regions.<br>
	 * We can assume at this point that the formatter On-Off is enabled and valid in the preferences.<br>
	 * This method is a generic one that retrieves the comments from the {@link IParseRootNode}. A formatter that does
	 * not provide comment nodes through that mechanism should override this method, or not use it.
	 * 
	 * @param output
	 *            The formatter output
	 * @param formatterOffPattern
	 * @param formatterOnPattern
	 * @param parseState
	 *            An {@link IParseState} that will be used when parsing the output.
	 * @return The formatter On-Off regions that we have in the output source.
	 */
	protected List<IRegion> getOutputOnOffRegions(String formatterOffPattern, String formatterOnPattern,
			IParseState parseState)
	{
		String output = parseState.getSource();
		List<IRegion> onOffRegions = null;
		try
		{
			IParseRootNode parseResult = null;
			IParser parser = checkoutParser();
			try
			{
				if (parser != null)
				{
					parseResult = parser.parse(parseState).getRootNode();
				}
			}
			finally
			{
				checkinParser(parser);
			}
			if (parseResult != null)
			{
				IParseNode[] commentNodes = parseResult.getCommentNodes();
				if (commentNodes != null)
				{
					LinkedHashMap<Integer, String> commentsMap = new LinkedHashMap<Integer, String>(commentNodes.length);
					for (IParseNode comment : commentNodes)
					{
						int start = comment.getStartingOffset();
						int end = comment.getEndingOffset();
						String commentStr = output.substring(start, end);
						commentsMap.put(start, commentStr);
					}
					// Generate the OFF/ON regions
					if (!commentsMap.isEmpty())
					{
						Pattern onPattern = Pattern.compile(Pattern.quote(formatterOnPattern));
						Pattern offPattern = Pattern.compile(Pattern.quote(formatterOffPattern));
						onOffRegions = FormatterUtils.resolveOnOffRegions(commentsMap, onPattern, offPattern,
								output.length() - 1);
					}
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(FormatterPlugin.getDefault(),
					"Error while computing the formatter's output OFF/ON regions", e, IDebugScopes.DEBUG); //$NON-NLS-1$
		}
		return onOffRegions;
	}

	/**
	 * Perform an indentation-only formatting, which does not involve any parsing.<br>
	 * This method can be called when a parsing error prevents us from applying an accurate formatting. To avoid any
	 * de-dented code appearing in the source, we just indent the source into the desired location.
	 * 
	 * @param completeSource
	 *            full source module content
	 * @param toFormat
	 *            the source to format
	 * @param offset
	 *            the offset of the region to format
	 * @param length
	 *            the length of the region to format
	 * @param indentationLevel
	 *            the indent level
	 * @return A {@link TextEdit} for the indented code.
	 */
	protected TextEdit indent(String completeSource, String toFormat, int offset, int length, int indentationLevel)
	{
		// Only indent when the source to format is located at the beginning of a line, or when there are only
		// white-space characters to its left.
		if (!canIndent(completeSource, offset - 1))
		{
			return null;
		}
		// push the first line of the code
		IFormatterIndentGenerator indentGenerator = createIndentGenerator();
		StringBuilder builder = new StringBuilder();
		indentGenerator.generateIndent(indentationLevel, builder);
		int leftWhitespaceChars = countLeftWhitespaceChars(toFormat);
		// replace the left chars with the indent
		builder.append(toFormat.substring(leftWhitespaceChars));
		return new ReplaceEdit(offset, length, builder.toString());
	}

	/**
	 * Returns true only when the source to format is located at the beginning of a line, or when there are only
	 * white-space characters to its left.
	 * 
	 * @param completeSource
	 * @param toFormat
	 * @param offset
	 * @return True, when an indentation is permitted; False, otherwise.
	 */
	private boolean canIndent(String completeSource, int offset)
	{
		// input validation
		if (StringUtil.isEmpty(completeSource))
		{
			return true;
		}
		if (offset >= completeSource.length())
		{
			return false;
		}
		// check for white-spaces
		for (int i = offset; i >= 0; i--)
		{
			char c = completeSource.charAt(i);
			if (c == ' ' || c == '\t')
			{
				continue;
			}
			if (c == '\n' || c == '\r')
			{
				// we are done
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * @since 2.0
	 */
	protected IFormatterIndentGenerator createIndentGenerator()
	{
		int tabSize = getTabSize();
		int indentSize = getIndentSize();
		final String indentType = getIndentType();
		if (CodeFormatterConstants.EDITOR.equals(indentType))
		{
			// Since the editor tab-width setting can be changed at any time outside of
			// the formatter's preferences, we have to retrieve it from the editor's preferences.
			tabSize = getEditorSpecificTabWidth();
			indentSize = tabSize;
			if (isEditorInsertSpacesForTabs())
			{
				return new FormatterIndentGenerator(' ', indentSize, tabSize);
			}
			return new FormatterMixedIndentGenerator(indentSize, tabSize);
		}
		if (CodeFormatterConstants.SPACE.equals(indentType))
		{
			return new FormatterIndentGenerator(' ', indentSize, tabSize);
		}
		if (CodeFormatterConstants.MIXED.equals(indentType))
		{
			return new FormatterMixedIndentGenerator(indentSize, tabSize);
		}
		return new FormatterIndentGenerator('\t', 1, tabSize);
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
		String inputLine = null;
		String outputLine = null;
		boolean result = true;
		while (result)
		{
			inputLine = readLine(input);
			outputLine = readLine(output);
			if (inputLine == null)
			{
				result = (outputLine == null);
				break;
			}
			else if (outputLine == null)
			{
				result = false;
			}
			else if (!inputLine.equals(outputLine))
			{
				result = false;
			}
		}
		if (!result && FormatterPlugin.getDefault().isDebugging())
		{
			if (inputLine != null && outputLine != null)
			{
				FormatterUtils.logDiff(inputLine, outputLine);
			}
			else
			{
				IdeLog.logError(FormatterPlugin.getDefault(),
						"Formatter Error - Input line does not match output line:\nINPUT:\n" + inputLine + "\nOUTPUT\n" //$NON-NLS-1$ //$NON-NLS-2$
								+ outputLine);
			}
		}
		return result;
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
		boolean result = in.equals(out);
		if (!result && FormatterPlugin.getDefault().isDebugging())
		{
			FormatterUtils.logDiff(in, out);
		}
		return result;
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
		if (document.getLength() <= offset + 1)
		{
			return 0;
		}
		try
		{
			String lineDelimiter = document.getLineDelimiter(document.getLineOfOffset(offset));
			if (lineDelimiter == null)
			{
				lineDelimiter = StringUtil.EMPTY;
			}
			int lineNumber = document.getLineOfOffset(Math.min(document.getLength(), offset + lineDelimiter.length()));
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
				if (CodeFormatterConstants.EDITOR.equals(indentType))
				{
					tabSize = getEditorSpecificTabWidth();
					indentSize = tabSize;
				}
				if (indentSize > 0)
				{
					if (CodeFormatterConstants.SPACE.equals(indentType)
							|| (CodeFormatterConstants.EDITOR.equals(indentType)))
					{
						// treat the tabs as spaces
						return (spaceChars + (tabSize * tabChars)) / indentSize + 1;
					}
					else
					{
						// it's 'Mixed'
						return (spaceChars + tabChars) / indentSize + 1;
					}
				}

			}
		}
		catch (BadLocationException e)
		{
			IdeLog.logError(FormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
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

	/**
	 * Left trim the String output.
	 * 
	 * @param str
	 * @param keptChars
	 *            The number of whitespace chars to keep.
	 * @return The output without the white-spaces at its beginning.
	 */
	protected static String leftTrim(String str, int keptChars)
	{
		int whitespaceChars = countLeftWhitespaceChars(str);
		if (whitespaceChars >= keptChars)
		{
			whitespaceChars -= keptChars;
		}
		return str.substring(whitespaceChars);
	}

	/**
	 * Count the number of whitespace characters that appear in the start of the given string.
	 * 
	 * @param str
	 * @return The number of prefix whitespace chars in the string.
	 */
	protected static int countLeftWhitespaceChars(String str)
	{
		int i = 0;
		int length = str.length();
		while (i < length && Character.isWhitespace(str.charAt(i)))
		{
			i++;
		}
		return i;
	}

	/**
	 * Process an output string to determine if it needs wrapping new-line chars, indent-suffix addition, or just
	 * trimming and adding a suffix when needed.
	 * 
	 * @param output
	 * @param lineSeparator
	 * @param suffix
	 *            A suffix to be added to the output. Note that only the 'indentSufix' will be used when
	 *            'postfixWithNewLine' is set to <code>true</code>.
	 * @param indentSufix
	 *            A suffix to be added to the output as an <b>indentation</b> addition when a 'postfixWithNewLine' is
	 *            set to <code>true</code>.
	 * @param prefixWithNewLine
	 *            Prefix the output with a line terminator
	 * @param postfixWithNewLine
	 *            Terminate the output with a line terminator and append the 'suffix' to it.
	 * @return A processed output string.
	 * @see #processNestedOutput(String, String, String, boolean, boolean)
	 */
	protected String processNestedOutput(String output, String lineSeparator, String suffix, String indentSufix,
			boolean prefixWithNewLine, boolean postfixWithNewLine)
	{
		// In case the output contains multiple lines, make sure it starts and ends with a new-line char
		if (output.split(lineSeparator, 2).length > 1)
		{
			StringBuilder wrappedOutput = new StringBuilder(output);
			if (prefixWithNewLine)
			{
				wrappedOutput.insert(0, lineSeparator);
			}
			if (postfixWithNewLine)
			{
				if (!output.endsWith(lineSeparator))
				{
					wrappedOutput.append(lineSeparator);
				}
				// Add the indentSufix that we may have.
				wrappedOutput.append(indentSufix);
			}
			else
			{
				wrappedOutput.append(suffix);
			}
			output = wrappedOutput.toString();
		}
		else
		{
			// Trim the output. Disregard any indentSufix that we have.
			output = output.trim() + suffix;
		}
		return output;
	}

	/**
	 * Process an output string to determine if it needs wrapping new-line chars, indent-suffix addition, or just
	 * trimming.
	 * 
	 * @param output
	 * @param lineSeparator
	 * @param indentSufix
	 *            A suffix to be added to the output as an <b>indentation</b> addition when a 'postfixWithNewLine' is
	 *            set to <code>true</code>.
	 * @param prefixWithNewLine
	 *            Prefix the output with a line terminator
	 * @param postfixWithNewLine
	 *            Terminate the output with a line terminator and append the 'suffix' to it.
	 * @return A processed output string.
	 * @see #processNestedOutput(String, String, String, String, boolean, boolean)
	 */
	protected String processNestedOutput(String output, String lineSeparator, String indentSufix,
			boolean prefixWithNewLine, boolean postfixWithNewLine)
	{
		return processNestedOutput(output, lineSeparator, StringUtil.EMPTY, indentSufix, prefixWithNewLine,
				postfixWithNewLine);
	}
}
