/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter;

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
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.css.CSSPlugin;
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
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * CSS code formatter.
 */
public class CSSFormatter extends AbstractScriptFormatter implements IScriptFormatter
{

	private static final Pattern whiteSpaceAsterisk = Pattern.compile("[\\s\\*]"); //$NON-NLS-1$

	protected static final String[] SPACES = { CSSFormatterConstants.SPACES_AFTER_CHILD_COMBINATOR,
			CSSFormatterConstants.SPACES_AFTER_COMMAS, CSSFormatterConstants.SPACES_AFTER_PARENTHESES,
			CSSFormatterConstants.SPACES_AFTER_COLON, CSSFormatterConstants.SPACES_AFTER_SEMICOLON,
			CSSFormatterConstants.SPACES_BEFORE_CHILD_COMBINATOR, CSSFormatterConstants.SPACES_BEFORE_COMMAS,
			CSSFormatterConstants.SPACES_BEFORE_PARENTHESES, CSSFormatterConstants.SPACES_BEFORE_COLON,
			CSSFormatterConstants.SPACES_BEFORE_SEMICOLON };

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected CSSFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
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
				final CSSFormatterNodeBuilder builder = new CSSFormatterNodeBuilder();
				final FormatterDocument formatterDocument = createFormatterDocument(source, offset);
				IFormatterContainerNode root = builder.build(parseResult, formatterDocument);
				new CSSFormatterNodeRewriter(parseResult, formatterDocument).rewrite(root);
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
	 * @see com.aptana.formatter.IScriptFormatter#format(java.lang.String, int, int, int, boolean,
	 * org.eclipse.jface.text.formatter.IFormattingContext, java.lang.String)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel, boolean isSelection,
			IFormattingContext context, String indentSufix) throws FormatterException
	{
		String input = new String(source.substring(offset, offset + length));
		try
		{
			IParseRootNode parseResult = null;
			parseResult = ParserPoolFactory.parse(getMainContentType(), input).getRootNode();

			if (parseResult != null)
			{
				final String output = format(input, parseResult, indentationLevel, offset, isSelection, indentSufix,
						offset != 0, length != source.length());
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
			StatusLineMessageTimerManager.setErrorMessage(
					NLS.bind(FormatterMessages.Formatter_formatterParsingErrorStatus, e.getMessage()),
					ERROR_DISPLAY_TIMEOUT, true);
			if (FormatterPlugin.getDefault().isDebugging())
			{
				IdeLog.logError(CSSFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}

		}
		catch (Exception e)
		{
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
					ERROR_DISPLAY_TIMEOUT, true);
			IdeLog.logError(CSSFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(CSSFormatterConstants.FORMATTER_INDENTATION_SIZE, 1);
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
		return getInt(CSSFormatterConstants.FORMATTER_TAB_SIZE, getEditorSpecificTabWidth());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#getEditorSpecificTabWidth()
	 */
	public int getEditorSpecificTabWidth()
	{
		return EditorUtil.getSpaceIndentSize(CSSPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#isEditorInsertSpacesForTabs()
	 */
	public boolean isEditorInsertSpacesForTabs()
	{
		return FormatterUtils.isInsertSpacesForTabs(CSSPlugin.getDefault().getPreferenceStore());
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
	 * @param indentSufix
	 * @param prefixWithNewLine
	 * @param postfixWithNewLine
	 * @return A formatted string
	 * @throws Exception
	 */
	private String format(String input, IParseRootNode parseResult, int indentationLevel, int offset,
			boolean isSelection, String indentSufix, boolean prefixWithNewLine, boolean postfixWithNewLine)
			throws Exception
	{
		final CSSFormatterNodeBuilder builder = new CSSFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input, offset);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new CSSFormatterNodeRewriter(parseResult, document).rewrite(root);
		IFormatterContext context = new CSSFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(CSSFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(CSSFormatterConstants.PRESERVED_LINES));
		root.accept(context, writer);
		writer.flush(context);
		String output = writer.getOutput();
		List<IRegion> offOnRegions = builder.getOffOnRegions();
		if (offOnRegions != null && !offOnRegions.isEmpty())
		{
			// We re-parse the output to extract its On-Off regions, so we will be able to compute the offsets and
			// adjust it.
			List<IRegion> outputOnOffRegions = getOutputOnOffRegions(output,
					getString(CSSFormatterConstants.FORMATTER_OFF), getString(CSSFormatterConstants.FORMATTER_ON));
			output = FormatterUtils.applyOffOnRegions(input, output, offOnRegions, outputOnOffRegions);
		}
		if (output.length() > 0)
		{
			if (prefixWithNewLine && !output.startsWith(lineSeparator))
			{
				output = lineSeparator + output;
			}
			if (postfixWithNewLine && !output.endsWith(lineSeparator))
			{
				output += lineSeparator;
			}
			output += indentSufix;
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

		// Formatter OFF/ON
		document.setBoolean(CSSFormatterConstants.FORMATTER_OFF_ON_ENABLED,
				getBoolean(CSSFormatterConstants.FORMATTER_OFF_ON_ENABLED));
		document.setString(CSSFormatterConstants.FORMATTER_ON, getString(CSSFormatterConstants.FORMATTER_ON));
		document.setString(CSSFormatterConstants.FORMATTER_OFF, getString(CSSFormatterConstants.FORMATTER_OFF));

		// Set the spaces values
		for (String key : SPACES)
		{
			document.setInt(key, getInt(key));
		}
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
		boolean result = in.equals(out);
		if (!result && FormatterPlugin.getDefault().isDebugging())
		{
			FormatterUtils.logDiff(in, out);
		}
		return result;
	}

}
