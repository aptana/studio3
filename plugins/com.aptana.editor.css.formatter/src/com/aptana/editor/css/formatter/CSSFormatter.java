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
package com.aptana.editor.css.formatter;

import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.util.StringUtil;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * CSS code formatter.
 */
public class CSSFormatter extends AbstractScriptFormatter implements IScriptFormatter
{

	private static final Pattern whiteSpaceAsterisk = Pattern.compile("[\\s\\*]"); //$NON-NLS-1$
	private String lineSeparator;

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected CSSFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
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

		int indent = 0;
		try
		{
			// detect the indentation offset with the parser, only if the given offset is not the first one in the
			// current partition.
			ITypedRegion partition = document.getPartition(offset);
			if (partition != null && partition.getOffset() == offset)
			{
				return super.detectIndentationLevel(document, offset);
			}

			IParser parser = checkoutParser();
			IParseState parseState = new ParseState();
			String source = document.get();
			parseState.setEditState(source, null, 0, 0);

			IParseRootNode parseResult = parser.parse(parseState);
			checkinParser(parser);
			if (parseResult != null)
			{
				final CSSFormatterNodeBuilder builder = new CSSFormatterNodeBuilder();
				final FormatterDocument formatterDocument = createFormatterDocument(source, offset);
				IFormatterContainerNode root = builder.build(parseResult, formatterDocument);
				new CSSFormatterNodeRewriter(parseResult).rewrite(root);
				IFormatterContext context = new CSSFormatterContext(0);
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#format(java.lang.String, int, int, int, boolean)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel, boolean isSelection,
			IFormattingContext context) throws FormatterException
	{
		String input = new String(source.substring(offset, offset + length));
		IParser parser = checkoutParser();
		IParseState parseState = new ParseState();
		parseState.setEditState(input, null, 0, 0);
		try
		{
			IParseRootNode parseResult = parser.parse(parseState);
			checkinParser(parser);
			if (parseResult != null)
			{
				final String output = format(input, parseResult, indentationLevel, offset, isSelection);
				if (output != null)
				{
					if (!input.equals(output))
					{
						if (equalsIgnoreWhiteSpaceAndAsterisk(input, output))
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
		catch (Exception e)
		{
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
					ERROR_DISPLAY_TIMEOUT, true);
			FormatterPlugin.logError(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(CSSFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	public String getIndentType()
	{
		return getString(CSSFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	public int getTabSize()
	{
		return getInt(CSSFormatterConstants.FORMATTER_TAB_SIZE);
	}

	/**
	 * Do the actual formatting of the CSS.
	 * 
	 * @param input
	 *            The String input
	 * @param parseResult
	 *            An CSS parser result - {@link IParseRootNode}
	 * @param indentationLevel
	 *            The indentation level to start from
	 * @return A formatted string
	 * @throws Exception
	 */
	private String format(String input, IParseRootNode parseResult, int indentationLevel, int offset,
			boolean isSelection) throws Exception
	{
		int spacesCount = -1;
		if (isSelection)
		{
			spacesCount = countLeftWhitespaceChars(input);
		}
		final CSSFormatterNodeBuilder builder = new CSSFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input, offset);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new CSSFormatterNodeRewriter(parseResult).rewrite(root);
		IFormatterContext context = new CSSFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(CSSFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(CSSFormatterConstants.PRESERVED_LINES));
		root.accept(context, writer);
		writer.flush(context);
		String output = writer.getOutput();
		if (isSelection)
		{
			if (isSelection)
			{
				output = leftTrim(output, spacesCount);
			}
		}
		return output;
	}

	private FormatterDocument createFormatterDocument(String input, int offset)
	{
		FormatterDocument document = new FormatterDocument(input);
		document.setInt(CSSFormatterConstants.FORMATTER_TAB_SIZE, getInt(CSSFormatterConstants.FORMATTER_TAB_SIZE));
		document.setBoolean(CSSFormatterConstants.WRAP_COMMENTS, getBoolean(CSSFormatterConstants.WRAP_COMMENTS));
		document.setString(CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS,
				getString(CSSFormatterConstants.NEW_LINES_BEFORE_BLOCKS));
		document.setInt(CSSFormatterConstants.LINES_AFTER_ELEMENTS, getInt(CSSFormatterConstants.LINES_AFTER_ELEMENTS));
		document.setInt(CSSFormatterConstants.LINES_AFTER_DECLARATION,
				getInt(CSSFormatterConstants.LINES_AFTER_DECLARATION));
		document.setInt(ScriptFormattingContextProperties.CONTEXT_ORIGINAL_OFFSET, offset);

		return document;
	}

	private boolean equalsIgnoreWhiteSpaceAndAsterisk(String in, String out)
	{
		if (in == null || out == null)
		{
			return in == out;
		}

		in = whiteSpaceAsterisk.matcher(in).replaceAll(StringUtil.EMPTY);
		out = whiteSpaceAsterisk.matcher(out).replaceAll(StringUtil.EMPTY);
		return in.equals(out);
	}

}
