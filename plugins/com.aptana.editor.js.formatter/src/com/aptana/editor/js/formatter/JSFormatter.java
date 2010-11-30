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
package com.aptana.editor.js.formatter;

import java.util.Map;
import java.util.regex.Matcher;
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
 * Javascript code formatter.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class JSFormatter extends AbstractScriptFormatter implements IScriptFormatter
{

	/**
	 * Brace positions constants
	 */
	protected static final String[] BRACE_POSITIONS = { JSFormatterConstants.BRACE_POSITION_BLOCK,
			JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE, JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH,
			JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION };

	/**
	 * New-lines constants
	 */
	protected static final String[] NEW_LINES_POSITIONS = { JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT,
			JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT };

	/**
	 * Indentation constants
	 */
	protected static final String[] INDENTATIONS = { JSFormatterConstants.INDENT_BLOCKS,
			JSFormatterConstants.INDENT_CASE_BODY, JSFormatterConstants.INDENT_SWITCH_BODY,
			JSFormatterConstants.INDENT_FUNCTION_BODY, JSFormatterConstants.INDENT_GROUP_BODY };

	private static final Pattern JS_COMMENTS_PATTERN = Pattern.compile("((?s)(/\\*.*?\\*/))|(//.*)");//$NON-NLS-1$
	private static final Pattern COMMENTS_STRIPPING_PATTERN = Pattern.compile("\\s|\\*|//"); //$NON-NLS-1$

	private String lineSeparator;

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected JSFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
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
			// current
			// partition.
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
				final JSFormatterNodeBuilder builder = new JSFormatterNodeBuilder();
				final FormatterDocument formatterDocument = createFormatterDocument(source, offset);
				IFormatterContainerNode root = builder.build(parseResult, formatterDocument);
				new JSFormatterNodeRewriter(parseResult, formatterDocument).rewrite(root);
				IFormatterContext context = new JSFormatterContext(0);
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
	 * @see com.aptana.formatter.ui.IScriptFormatter#format(java.lang.String, int, int, int)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel, boolean isSelection,
			IFormattingContext context) throws FormatterException
	{
		String input = source.substring(offset, offset + length);
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
						if (equalContent(input, output))
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
		catch (FormatterException e)
		{
			StatusLineMessageTimerManager.setErrorMessage(NLS.bind(
					FormatterMessages.Formatter_formatterParsingErrorStatus, e.getMessage()), ERROR_DISPLAY_TIMEOUT,
					true);
		}
		catch (Exception e)
		{
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
					ERROR_DISPLAY_TIMEOUT, true);
			FormatterPlugin.logError(e);
		}
		return null;
	}

	/**
	 * @param input
	 * @param output
	 * @return
	 */
	private boolean equalContent(String input, String output)
	{
		// first, strip out all the comments from the input and the output.
		// save those comments for later comparison.
		StringBuilder inputBuffer = new StringBuilder(input.length());
		StringBuilder outputBuffer = new StringBuilder(output.length());
		StringBuilder inputComments = new StringBuilder();
		StringBuilder outputComments = new StringBuilder();
		Matcher inputCommentsMatcher = JS_COMMENTS_PATTERN.matcher(input);
		Matcher outputCommentsMatcher = JS_COMMENTS_PATTERN.matcher(output);
		int inputOffset = 0;
		int outputOffset = 0;
		while (inputCommentsMatcher.find())
		{
			inputComments.append(inputCommentsMatcher.group());
			inputBuffer.append(input.subSequence(inputOffset, inputCommentsMatcher.start()));
			inputOffset = inputCommentsMatcher.end() + 1;
		}
		inputBuffer.append(input.subSequence(inputOffset, input.length()));
		while (outputCommentsMatcher.find())
		{
			outputComments.append(outputCommentsMatcher.group());
			outputBuffer.append(output.subSequence(outputOffset, outputCommentsMatcher.start()));
			outputOffset = outputCommentsMatcher.end() + 1;

		}
		outputBuffer.append(output.subSequence(outputOffset, output.length()));
		return stripComment(inputComments.toString()).equals(stripComment(outputComments.toString()))
				&& equalsIgnoreWhitespaces(inputBuffer.toString(), outputBuffer.toString());
	}

	/**
	 * Remove any whitespace, '*' or '//' from a comment string
	 * 
	 * @param inputComment
	 */
	private String stripComment(String comment)
	{
		return COMMENTS_STRIPPING_PATTERN.matcher(comment).replaceAll(StringUtil.EMPTY);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(JSFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	public String getIndentType()
	{
		return getString(JSFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	public int getTabSize()
	{
		return getInt(JSFormatterConstants.FORMATTER_TAB_SIZE);
	}

	/**
	 * Do the actual formatting of the JavaScript.
	 * 
	 * @param input
	 *            The String input
	 * @param parseResult
	 *            A JavaScript parser result - {@link com.aptana.parsing.ast.IParseNode}
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
		final JSFormatterNodeBuilder builder = new JSFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input, offset);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new JSFormatterNodeRewriter(parseResult, document).rewrite(root);
		IFormatterContext context = new JSFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(JSFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(JSFormatterConstants.PRESERVED_LINES));
		root.accept(context, writer);
		writer.flush(context);
		// Unlike other formatters, we allow errors in the JS AST for now.
		// We just notify the user that there were errors in the JS file.
		if (builder.hasErrors())
		{
			StatusLineMessageTimerManager.setErrorMessage(
					FormatterMessages.Formatter_formatterErrorCompletedWithErrors, ERROR_DISPLAY_TIMEOUT, true);
		}
		String output = writer.getOutput();
		if (isSelection)
		{
			output = leftTrim(output, spacesCount);
		}
		return output;
	}

	private FormatterDocument createFormatterDocument(String input, int offset)
	{
		FormatterDocument document = new FormatterDocument(input);
		document.setInt(JSFormatterConstants.FORMATTER_TAB_SIZE, getInt(JSFormatterConstants.FORMATTER_TAB_SIZE));
		document.setBoolean(JSFormatterConstants.WRAP_COMMENTS, getBoolean(JSFormatterConstants.WRAP_COMMENTS));
		document.setInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION,
				getInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION));
		document.setInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION,
				getInt(JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION));
		document.setInt(ScriptFormattingContextProperties.CONTEXT_ORIGINAL_OFFSET, offset);

		// Set the indentation values
		for (String key : INDENTATIONS)
		{
			document.setBoolean(key, getBoolean(key));
		}
		// Set the new-lines values
		for (String key : NEW_LINES_POSITIONS)
		{
			document.setBoolean(key, getBoolean(key));
		}
		// Set the braces values
		for (String key : BRACE_POSITIONS)
		{
			document.setString(key, getString(key));
		}
		return document;
	}
}
