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
package com.aptana.editor.html.formatter;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.html.parsing.IHTMLParserConstants;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.ScriptFormatterManager;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.preferences.IPreferenceDelegate;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * HTML code formatter.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatter extends AbstractScriptFormatter implements IScriptFormatter
{

	/**
	 * Blank lines constants
	 */
	protected static final String[] BLANK_LINES = { HTMLFormatterConstants.LINES_AFTER_ELEMENTS,
			HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS, HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS };

	private String lineSeparator;

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected HTMLFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
	{
		super(preferences, mainContentType);
		this.lineSeparator = lineSeparator;
	}

	/**
	 * Detects the indentation level.
	 */
	public int detectIndentationLevel(IDocument document, int offset, boolean isSelection,
			IFormattingContext formattingContext)
	{
		IParser parser = checkoutParser();
		IParseState parseState = new HTMLParseState();
		String source = document.get();
		parseState.setEditState(source, null, 0, 0);
		int indent = 0;
		try
		{
			IParseNode parseResult = parser.parse(parseState);
			checkinParser(parser);
			if (parseResult != null)
			{
				final HTMLFormatterNodeBuilder builder = new HTMLFormatterNodeBuilder();
				final FormatterDocument formatterDocument = createFormatterDocument(source);
				IFormatterContainerNode root = builder.build(parseResult, formatterDocument);
				new HTMLFormatterNodeRewriter().rewrite(root);
				IFormatterContext context = new HTMLFormatterContext(0);
				FormatterIndentDetector detector = new FormatterIndentDetector(offset);
				try
				{
					root.accept(context, detector);
					return detector.getLevel();
				}
				catch (Exception e)
				{
					// ignore
				}
			}
		}
		catch (Throwable t)
		{
			return super.detectIndentationLevel(document, offset);
		}
		return indent;
	}

	public TextEdit format(String source, int offset, int length, int indentationLevel, boolean isSelection,
			IFormattingContext context) throws FormatterException
	{
		if (!ScriptFormatterManager.hasFormatterFor(getMainContentType()))
		{
			throw new FormatterException(FormatterMessages.Formatter_contentErrorMessage);
		}
		String input = source.substring(offset, offset + length);
		IParser parser = checkoutParser();
		String mainContentType = getMainContentType();
		if (!(parser instanceof HTMLParser))
		{
			// Check it back in and request a specific HTML parser.
			// This will happen when dealing with a master formatter that runs with a parser that does not extend from
			// HTNLParser (like PHPParser).
			checkinParser(parser, mainContentType);
			mainContentType = IHTMLParserConstants.LANGUAGE;
			parser = checkoutParser(mainContentType);
		}
		try
		{
			IParseState parseState = new HTMLParseState();
			parseState.setEditState(input, null, 0, 0);
			IParseNode parseResult = parser.parse(parseState);
			checkinParser(parser, mainContentType);
			if (parseResult != null)
			{
				final String output = format(input, parseResult, indentationLevel, isSelection);
				if (output != null)
				{
					if (!input.equals(output))
					{
						if (equalsIgnoreWhitespaces(input, output))
						{
							return new ReplaceEdit(offset, length, output);
						}
						else
						{
							logError(input, output);
						}
					}
					else
					{
						return new MultiTextEdit(); // NOP
					}
				}
			}
		}
		catch (beaver.Parser.Exception e)
		{
			StatusLineMessageTimerManager.setErrorMessage(NLS.bind(
					FormatterMessages.Formatter_formatterParsingErrorStatus, e.getMessage()), ERROR_DISPLAY_TIMEOUT,
					true);
			if (FormatterPlugin.DEBUG)
			{
				FormatterPlugin.logError(e);
			}
		}
		catch (Throwable t)
		{
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
					ERROR_DISPLAY_TIMEOUT, true);
			FormatterPlugin.logError(t);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	public String getIndentType()
	{
		return getString(HTMLFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	public int getTabSize()
	{
		return getInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE);
	}

	/**
	 * Do the actual formatting of the HTML.
	 * 
	 * @param input
	 *            The String input
	 * @param parseResult
	 *            An HTML parser result - {@link IParseNode}
	 * @param indentationLevel
	 *            The indentation level to start from
	 * @return A formatted string
	 * @throws Exception
	 */
	private String format(String input, IParseNode parseResult, int indentationLevel, boolean isSelection)
			throws Exception
	{
		int spacesCount = -1;
		if (isSelection)
		{
			spacesCount = countLeftWhitespaceChars(input);
		}
		final HTMLFormatterNodeBuilder builder = new HTMLFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new HTMLFormatterNodeRewriter().rewrite(root);
		IFormatterContext context = new HTMLFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(HTMLFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(HTMLFormatterConstants.PRESERVED_LINES));
		root.accept(context, writer);
		writer.flush(context);
		String output = writer.getOutput();
		if (isSelection)
		{
			output = leftTrim(output, spacesCount);
		}
		return output;
	}

	private FormatterDocument createFormatterDocument(String input)
	{
		FormatterDocument document = new FormatterDocument(input);
		document.setInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE, getInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE));
		document.setBoolean(HTMLFormatterConstants.WRAP_COMMENTS, getBoolean(HTMLFormatterConstants.WRAP_COMMENTS));
		document.setSet(HTMLFormatterConstants.INDENT_EXCLUDED_TAGS, getSet(
				HTMLFormatterConstants.INDENT_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		document.setSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, getSet(
				HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		for (int i = 0; i < BLANK_LINES.length; i++)
		{
			document.setInt(BLANK_LINES[i], getInt(BLANK_LINES[i]));
		}
		return document;
	}

}
