/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.editor.js.JSPlugin;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterUtils;
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
	 * @see com.aptana.formatter.IScriptFormatter#format(java.lang.String, int, int, int, boolean,
	 * org.eclipse.jface.text.formatter.IFormattingContext, java.lang.String)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel, boolean isSelection,
			IFormattingContext context, String indentSufix) throws FormatterException
	{
		String originalText = source.substring(offset, offset + length);
		String input = originalText.trim();
		int inputOffset = offset + countLeftWhitespaceChars(originalText);
		IParser parser = checkoutParser();
		IParseState parseState = new ParseState();
		parseState.setEditState(input, null, 0, 0);
		try
		{
			IParseRootNode parseResult = parser.parse(parseState);
			checkinParser(parser);
			if (parseResult != null)
			{
				final String output = format(input, parseResult, indentationLevel, inputOffset, isSelection,
						indentSufix, offset != 0, length != source.length());
				if (output != null)
				{
					if (!originalText.equals(output))
					{
						if (equalContent(parseResult, output))
						{
							return new ReplaceEdit(offset, length, output);
						}
						else
						{
							logError(originalText, output);
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
			StatusLineMessageTimerManager.setErrorMessage(
					NLS.bind(FormatterMessages.Formatter_formatterParsingErrorStatus, e.getMessage()),
					ERROR_DISPLAY_TIMEOUT, true);
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
	 * @param inputParseResult
	 * @param output
	 * @return
	 */
	private boolean equalContent(IParseRootNode inputParseResult, String output)
	{
		if (output == null)
		{
			return false;
		}
		output = output.trim();
		IParser parser = checkoutParser();
		IParseState parseState = new ParseState();
		parseState.setEditState(output, null, 0, 0);
		IParseRootNode outputParseResult = null;
		try
		{
			outputParseResult = parser.parse(parseState);
		}
		catch (Exception e)
		{
			return false;
		}
		checkinParser(parser);
		if (outputParseResult == null)
		{
			return false;
		}
		// Flatten the AST's and do a string compare
		// The toString() of the JSParseRootNode calls the JSFormatWalker,
		// which should generate the same string for the input and the output.
		return outputParseResult.toString().equals(inputParseResult.toString());
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#getEditorSpecificTabWidth()
	 */
	public int getEditorSpecificTabWidth()
	{
		return FormatterUtils.getEditorTabWidth(JSPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#isEditorInsertSpacesForTabs()
	 */
	public boolean isEditorInsertSpacesForTabs()
	{
		return FormatterUtils.isInsertSpacesForTabs(JSPlugin.getDefault().getPreferenceStore());
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
	 * @param indentSufix
	 * @param prefixWithNewLine
	 *            Prefix the output with a line terminator
	 * @param postfixWithNewLine
	 *            Terminate the output with a line terminator and append the 'indentSuffix' to it.
	 * @return A formatted string
	 * @throws Exception
	 */
	private String format(String input, IParseRootNode parseResult, int indentationLevel, int inputOffset,
			boolean isSelection, String indentSufix, boolean prefixWithNewLine, boolean postfixWithNewLine)
			throws Exception
	{
		int spacesCount = -1;
		if (isSelection)
		{
			spacesCount = countLeftWhitespaceChars(input);
		}
		final JSFormatterNodeBuilder builder = new JSFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input, inputOffset);
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
		else
		{
			output = processNestedOutput(output, lineSeparator, indentSufix, prefixWithNewLine, postfixWithNewLine);
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
