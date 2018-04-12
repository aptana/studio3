/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.formatter;

import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK_IN_CASE;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_BLOCK_IN_SWITCH;
import static com.aptana.editor.js.formatter.JSFormatterConstants.BRACE_POSITION_FUNCTION_DECLARATION;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_INDENTATION_SIZE;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_OFF;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_OFF_ON_ENABLED;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_ON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_TAB_CHAR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.FORMATTER_TAB_SIZE;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_BLOCKS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_CASE_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_FUNCTION_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_GROUP_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.INDENT_SWITCH_BODY;
import static com.aptana.editor.js.formatter.JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION;
import static com.aptana.editor.js.formatter.JSFormatterConstants.LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_CATCH_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_DO_WHILE_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_ELSE_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_FINALLY_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BEFORE_NAME_VALUE_PAIRS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.NEW_LINES_BETWEEN_VAR_DECLARATIONS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.PRESERVED_LINES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_ARITHMETIC_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_ASSIGNMENT_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_CASE_COLON_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_COMMAS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_CONCATENATION_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_CONDITIONAL_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_FOR_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_KEY_VALUE_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_DECLARATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_INVOCATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_LOOP_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_OPENING_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_POSTFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_PREFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_RELATIONAL_OPERATORS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_AFTER_UNARY_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_ARITHMETIC_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_ASSIGNMENT_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CASE_COLON_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_LOOP_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CLOSING_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_COMMAS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CONCATENATION_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_CONDITIONAL_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_FOR_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_KEY_VALUE_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_LOOP_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_OPENING_PARENTHESES;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_POSTFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_PREFIX_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_RELATIONAL_OPERATORS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_SEMICOLON;
import static com.aptana.editor.js.formatter.JSFormatterConstants.SPACES_BEFORE_UNARY_OPERATOR;
import static com.aptana.editor.js.formatter.JSFormatterConstants.WRAP_COMMENTS;
import static com.aptana.editor.js.formatter.JSFormatterConstants.WRAP_COMMENTS_LENGTH;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IDebugScopes;
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
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
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
	protected static final String[] BRACE_POSITIONS = { BRACE_POSITION_BLOCK, BRACE_POSITION_BLOCK_IN_CASE,
			BRACE_POSITION_BLOCK_IN_SWITCH, BRACE_POSITION_FUNCTION_DECLARATION };

	/**
	 * New-lines constants
	 */
	protected static final String[] NEW_LINES_POSITIONS = { NEW_LINES_BEFORE_CATCH_STATEMENT,
			NEW_LINES_BEFORE_DO_WHILE_STATEMENT, NEW_LINES_BEFORE_ELSE_STATEMENT,
			NEW_LINES_BEFORE_IF_IN_ELSEIF_STATEMENT, NEW_LINES_BEFORE_FINALLY_STATEMENT,
			NEW_LINES_BEFORE_NAME_VALUE_PAIRS, NEW_LINES_BETWEEN_VAR_DECLARATIONS };

	/**
	 * Indentation constants
	 */
	protected static final String[] INDENTATIONS = { INDENT_BLOCKS, INDENT_CASE_BODY, INDENT_SWITCH_BODY,
			INDENT_FUNCTION_BODY, INDENT_GROUP_BODY };

	/**
	 * Spaces constants
	 */
	protected static final String[] SPACES = { SPACES_BEFORE_COMMAS, SPACES_AFTER_COMMAS, SPACES_BEFORE_UNARY_OPERATOR,
			SPACES_AFTER_UNARY_OPERATOR, SPACES_BEFORE_KEY_VALUE_OPERATOR, SPACES_AFTER_KEY_VALUE_OPERATOR,
			SPACES_BEFORE_ASSIGNMENT_OPERATOR, SPACES_AFTER_ASSIGNMENT_OPERATOR, SPACES_BEFORE_RELATIONAL_OPERATORS,
			SPACES_AFTER_RELATIONAL_OPERATORS, SPACES_BEFORE_CONCATENATION_OPERATOR,
			SPACES_AFTER_CONCATENATION_OPERATOR, SPACES_BEFORE_CONDITIONAL_OPERATOR, SPACES_AFTER_CONDITIONAL_OPERATOR,
			SPACES_BEFORE_POSTFIX_OPERATOR, SPACES_AFTER_POSTFIX_OPERATOR, SPACES_BEFORE_PREFIX_OPERATOR,
			SPACES_AFTER_PREFIX_OPERATOR, SPACES_BEFORE_ARITHMETIC_OPERATOR, SPACES_AFTER_ARITHMETIC_OPERATOR,
			SPACES_BEFORE_FOR_SEMICOLON, SPACES_AFTER_FOR_SEMICOLON, SPACES_BEFORE_SEMICOLON, SPACES_AFTER_SEMICOLON,
			SPACES_BEFORE_CASE_COLON_OPERATOR, SPACES_AFTER_CASE_COLON_OPERATOR, SPACES_BEFORE_OPENING_PARENTHESES,
			SPACES_AFTER_OPENING_PARENTHESES, SPACES_BEFORE_CLOSING_PARENTHESES,
			SPACES_BEFORE_OPENING_DECLARATION_PARENTHESES, SPACES_AFTER_OPENING_DECLARATION_PARENTHESES,
			SPACES_BEFORE_CLOSING_DECLARATION_PARENTHESES, SPACES_BEFORE_OPENING_INVOCATION_PARENTHESES,
			SPACES_AFTER_OPENING_INVOCATION_PARENTHESES, SPACES_BEFORE_CLOSING_INVOCATION_PARENTHESES,
			SPACES_BEFORE_OPENING_ARRAY_ACCESS_PARENTHESES, SPACES_AFTER_OPENING_ARRAY_ACCESS_PARENTHESES,
			SPACES_BEFORE_CLOSING_ARRAY_ACCESS_PARENTHESES, SPACES_BEFORE_OPENING_LOOP_PARENTHESES,
			SPACES_AFTER_OPENING_LOOP_PARENTHESES, SPACES_BEFORE_CLOSING_LOOP_PARENTHESES,
			SPACES_BEFORE_OPENING_CONDITIONAL_PARENTHESES, SPACES_AFTER_OPENING_CONDITIONAL_PARENTHESES,
			SPACES_BEFORE_CLOSING_CONDITIONAL_PARENTHESES };

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected JSFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
	{
		super(preferences, mainContentType, lineSeparator);
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
			String source = document.get();
			IParseRootNode parseResult = ParserPoolFactory.parse(getMainContentType(), source).getRootNode();
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
		String input = leftTrim(originalText, 0);
		if (indentationLevel > 0 && FormatterWriter.endsWithNewLine(input, lineSeparator))
		{
			String substring = input.substring(0, input.length() - lineSeparator.length());
			if (!FormatterWriter.endsWithNewLine(substring, lineSeparator))
			{
				input = substring;
			}
		}
		int inputOffset = offset + countLeftWhitespaceChars(originalText);
		IParseRootNode parseResult = null;
		try
		{
			parseResult = ParserPoolFactory.parse(getMainContentType(), input).getRootNode();
		}
		catch (Exception e)
		{
			StatusLineMessageTimerManager.setErrorMessage(e.getMessage()
					+ " - " + FormatterMessages.Formatter_formatterErrorStatus, //$NON-NLS-1$
					ERROR_DISPLAY_TIMEOUT, true);
			IdeLog.logError(JSFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
			// In this case, we probably have a parse error. To avoid any code shifting, we try to maintain the
			// indentation level as much as we can.
			return indent(source, input, inputOffset, length - (inputOffset - offset), indentationLevel);
		}
		try
		{
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
			IdeLog.logError(JSFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
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
		IParseState parseState = new ParseState(output);
		IParseRootNode outputParseResult = null;
		try
		{
			outputParseResult = parser.parse(parseState).getRootNode();
		}
		catch (Exception e)
		{
			IdeLog.logError(JSFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
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
		String flattenOutputAST = outputParseResult.toString();
		String flattenInputAST = inputParseResult.toString();
		boolean equals = flattenOutputAST.equals(flattenInputAST);
		if (!equals && FormatterPlugin.getDefault().isDebugging())
		{
			FormatterUtils.logDiff(flattenInputAST, flattenOutputAST);
		}
		return equals;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(FORMATTER_INDENTATION_SIZE, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	public String getIndentType()
	{
		return getString(FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	public int getTabSize()
	{
		return getInt(FORMATTER_TAB_SIZE, getEditorSpecificTabWidth());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#getEditorSpecificTabWidth()
	 */
	public int getEditorSpecificTabWidth()
	{
		return EditorUtil.getSpaceIndentSize(JSPlugin.getDefault().getBundle().getSymbolicName());
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
		final JSFormatterNodeBuilder builder = new JSFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input, inputOffset);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new JSFormatterNodeRewriter(parseResult, document).rewrite(root);
		IFormatterContext context = new JSFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(PRESERVED_LINES));
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
		List<IRegion> offOnRegions = builder.getOffOnRegions();
		if (offOnRegions != null && !offOnRegions.isEmpty())
		{
			// We re-parse the output to extract its On-Off regions, so we will be able to compute the offsets and
			// adjust it.
			List<IRegion> outputOnOffRegions = getOutputOnOffRegions(output,
					getString(JSFormatterConstants.FORMATTER_OFF), getString(JSFormatterConstants.FORMATTER_ON));
			output = FormatterUtils.applyOffOnRegions(input, output, offOnRegions, outputOnOffRegions);
		}
		output = processNestedOutput(output, lineSeparator, indentSufix, prefixWithNewLine, postfixWithNewLine);
		return output;
	}

	private FormatterDocument createFormatterDocument(String input, int offset)
	{
		FormatterDocument document = new FormatterDocument(input);
		document.setInt(FORMATTER_TAB_SIZE, getInt(FORMATTER_TAB_SIZE));
		document.setBoolean(WRAP_COMMENTS, getBoolean(WRAP_COMMENTS));
		document.setInt(LINES_AFTER_FUNCTION_DECLARATION, getInt(LINES_AFTER_FUNCTION_DECLARATION));
		document.setInt(LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION,
				getInt(LINES_AFTER_FUNCTION_DECLARATION_IN_EXPRESSION));
		document.setInt(ScriptFormattingContextProperties.CONTEXT_ORIGINAL_OFFSET, offset);

		// Formatter OFF/ON
		document.setBoolean(FORMATTER_OFF_ON_ENABLED, getBoolean(FORMATTER_OFF_ON_ENABLED));
		document.setString(FORMATTER_ON, getString(FORMATTER_ON));
		document.setString(FORMATTER_OFF, getString(FORMATTER_OFF));

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
		// Set the spaces values
		for (String key : SPACES)
		{
			document.setInt(key, getInt(key));
		}
		return document;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatter#getOutputOnOffRegions(java.lang.String, java.lang.String,
	 * java.lang.String, com.aptana.parsing.IParseState)
	 */
	@Override
	protected List<IRegion> getOutputOnOffRegions(String formatterOffPattern, String formatterOnPattern,
			IParseState parseState)
	{
		String output = parseState.getSource();
		IParser parser = checkoutParser();
		List<IRegion> onOffRegions = null;
		try
		{
			IParseRootNode parseResult = parser.parse(parseState).getRootNode();
			checkinParser(parser);
			if (parseResult != null)
			{
				IParseNode[] commentNodes = parseResult.getCommentNodes();
				if (commentNodes != null)
				{
					LinkedHashMap<Integer, String> commentsMap = new LinkedHashMap<Integer, String>(commentNodes.length);
					for (IParseNode comment : commentNodes)
					{
						int start = comment.getStartingOffset();
						int end = comment.getEndingOffset() + 1;
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
}
