/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.parsing.CompositeParser;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IDebugScopes;
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
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
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
			HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS,
			HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS, HTMLFormatterConstants.PRESERVED_LINES };

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected HTMLFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
	{
		super(preferences, mainContentType, lineSeparator);
	}

	/**
	 * Detects the indentation level.
	 */
	public int detectIndentationLevel(IDocument document, int offset, boolean isSelection,
			IFormattingContext formattingContext)
	{
		String source = document.get();
		int indent = 0;
		try
		{
			IParseState parseState = new HTMLParseState(source);
			IParseRootNode parseResult = ParserPoolFactory.parse(getMainContentType(), parseState).getRootNode();
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

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#format(java.lang.String, int, int, int, boolean,
	 * org.eclipse.jface.text.formatter.IFormattingContext, java.lang.String)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel, boolean isSelection,
			IFormattingContext context, String indentSufix) throws FormatterException
	{
		if (!ScriptFormatterManager.hasFormatterFor(getMainContentType()))
		{
			throw new FormatterException(FormatterMessages.Formatter_contentErrorMessage);
		}
		String input = source.substring(offset, offset + length);
		IParser parser = checkoutParser();
		String mainContentType = getMainContentType();
		if (!(parser instanceof HTMLParser) && !(parser instanceof CompositeParser))
		{
			// Check it back in and request a specific HTML parser.
			// This will happen when dealing with a master formatter that runs with a parser that does not extend from
			// HTNLParser (like PHPParser).
			checkinParser(parser, mainContentType);
			mainContentType = IHTMLConstants.CONTENT_TYPE_HTML;
			parser = checkoutParser(mainContentType);
		}
		try
		{
			IParseState parseState = new HTMLParseState(input);
			IParseNode parseResult = null;
			try
			{
				parseResult = parser.parse(parseState).getRootNode();
			}
			catch (Exception e)
			{
				StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
						ERROR_DISPLAY_TIMEOUT, true);
				IdeLog.logError(HTMLFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);

				// In case of a parse error (which is unlikely to HTML parsing), just try to indent the given source.
				return indent(source, input, offset, length, indentationLevel);
			}
			finally
			{
				checkinParser(parser, mainContentType);
			}
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
			StatusLineMessageTimerManager.setErrorMessage(
					NLS.bind(FormatterMessages.Formatter_formatterParsingErrorStatus, e.getMessage()),
					ERROR_DISPLAY_TIMEOUT, true);
			if (FormatterPlugin.getDefault().isDebugging())
			{
				IdeLog.logError(HTMLFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
		catch (Throwable t)
		{
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
					ERROR_DISPLAY_TIMEOUT, true);
			IdeLog.logError(HTMLFormatterPlugin.getDefault(), t, IDebugScopes.DEBUG);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE, 1);
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
		return getInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE, getEditorSpecificTabWidth());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#getEditorSpecificTabWidth()
	 */
	public int getEditorSpecificTabWidth()
	{
		return EditorUtil.getSpaceIndentSize(HTMLPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#isEditorInsertSpacesForTabs()
	 */
	public boolean isEditorInsertSpacesForTabs()
	{
		return FormatterUtils.isInsertSpacesForTabs(HTMLPlugin.getDefault().getPreferenceStore());
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
		final FormatterDocument document = createFormatterDocument(input);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		final HTMLFormatterNodeBuilder builder = new HTMLFormatterNodeBuilder(writer);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new HTMLFormatterNodeRewriter().rewrite(root);
		IFormatterContext context = new HTMLFormatterContext(indentationLevel);
		writer.setWrapLength(getInt(HTMLFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(HTMLFormatterConstants.PRESERVED_LINES));
		root.accept(context, writer);
		writer.flush(context);
		String output = writer.getOutput();
		List<IRegion> offOnRegions = builder.getOffOnRegions();
		if (offOnRegions != null && !offOnRegions.isEmpty())
		{
			// We re-parse the output to extract its On-Off regions, so we will be able to compute the offsets and
			// adjust it.
			List<IRegion> outputOnOffRegions = getOutputOnOffRegions(getString(HTMLFormatterConstants.FORMATTER_OFF),
					getString(HTMLFormatterConstants.FORMATTER_ON), new HTMLParseState(output));
			output = FormatterUtils.applyOffOnRegions(input, output, offOnRegions, outputOnOffRegions);
		}
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
		document.setBoolean(HTMLFormatterConstants.PLACE_COMMENTS_IN_SEPARATE_LINES,
				getBoolean(HTMLFormatterConstants.PLACE_COMMENTS_IN_SEPARATE_LINES));
		document.setSet(HTMLFormatterConstants.INDENT_EXCLUDED_TAGS,
				getSet(HTMLFormatterConstants.INDENT_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		document.setSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
				getSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		document.setBoolean(HTMLFormatterConstants.NEW_LINES_EXCLUSION_IN_EMPTY_TAGS,
				getBoolean(HTMLFormatterConstants.NEW_LINES_EXCLUSION_IN_EMPTY_TAGS));
		document.setBoolean(HTMLFormatterConstants.TRIM_SPACES, getBoolean(HTMLFormatterConstants.TRIM_SPACES));

		// Formatter OFF/ON
		document.setBoolean(HTMLFormatterConstants.FORMATTER_OFF_ON_ENABLED,
				getBoolean(HTMLFormatterConstants.FORMATTER_OFF_ON_ENABLED));
		document.setString(HTMLFormatterConstants.FORMATTER_ON, getString(HTMLFormatterConstants.FORMATTER_ON));
		document.setString(HTMLFormatterConstants.FORMATTER_OFF, getString(HTMLFormatterConstants.FORMATTER_OFF));

		for (int i = 0; i < BLANK_LINES.length; i++)
		{
			document.setInt(BLANK_LINES[i], getInt(BLANK_LINES[i]));
		}
		return document;
	}

}
