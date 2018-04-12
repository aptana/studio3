/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter;

import java.util.List;
import java.util.Map;

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
import com.aptana.editor.xml.XMLPlugin;
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
import com.aptana.formatter.preferences.IPreferenceDelegate;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * XML code formatter.
 */
public class XMLFormatter extends AbstractScriptFormatter implements IScriptFormatter
{

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected XMLFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
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
				final XMLFormatterNodeBuilder builder = new XMLFormatterNodeBuilder();
				final FormatterDocument formatterDocument = createFormatterDocument(source, offset);
				IFormatterContainerNode root = builder.build(parseResult, formatterDocument);
				new XMLFormatterNodeRewriter().rewrite(root);
				IFormatterContext context = new XMLFormatterContext(0);
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
			try
			{
				parseResult = ParserPoolFactory.parse(getMainContentType(), input).getRootNode();
			}
			catch (Exception e)
			{
				// In case of a parse error, just try to indent the given source.
				return indent(source, input, offset, length, indentationLevel);
			}
			if (parseResult != null)
			{
				final String output = format(input, parseResult, indentationLevel, offset, isSelection);
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
				IdeLog.logError(XMLFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
			}
		}
		catch (Exception e)
		{
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
					ERROR_DISPLAY_TIMEOUT, true);
			IdeLog.logError(XMLFormatterPlugin.getDefault(), e, IDebugScopes.DEBUG);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(XMLFormatterConstants.FORMATTER_INDENTATION_SIZE, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	public String getIndentType()
	{
		return getString(XMLFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	public int getTabSize()
	{
		return getInt(XMLFormatterConstants.FORMATTER_TAB_SIZE, getEditorSpecificTabWidth());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#getEditorSpecificTabWidth()
	 */
	public int getEditorSpecificTabWidth()
	{
		return EditorUtil.getSpaceIndentSize(XMLPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#isEditorInsertSpacesForTabs()
	 */
	public boolean isEditorInsertSpacesForTabs()
	{
		return FormatterUtils.isInsertSpacesForTabs(XMLPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Do the actual formatting of the XML.
	 * 
	 * @param input
	 *            The String input
	 * @param parseResult
	 *            An XML parser result - {@link IParseRootNode}
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
		final XMLFormatterNodeBuilder builder = new XMLFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input, offset);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new XMLFormatterNodeRewriter().rewrite(root);
		IFormatterContext context = new XMLFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(XMLFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(XMLFormatterConstants.PRESERVED_LINES));
		root.accept(context, writer);
		writer.flush(context);
		String output = writer.getOutput();
		List<IRegion> offOnRegions = builder.getOffOnRegions();
		if (offOnRegions != null && !offOnRegions.isEmpty())
		{
			// We re-parse the output to extract its On-Off regions, so we will be able to compute the offsets and
			// adjust it.
			List<IRegion> outputOnOffRegions = getOutputOnOffRegions(output,
					getString(XMLFormatterConstants.FORMATTER_OFF), getString(XMLFormatterConstants.FORMATTER_ON));
			output = FormatterUtils.applyOffOnRegions(input, output, offOnRegions, outputOnOffRegions);
		}
		if (isSelection)
		{
			output = leftTrim(output, spacesCount);
		}
		return output;
	}

	private FormatterDocument createFormatterDocument(String input, int offset)
	{
		FormatterDocument document = new FormatterDocument(input);
		document.setInt(XMLFormatterConstants.FORMATTER_TAB_SIZE, getInt(XMLFormatterConstants.FORMATTER_TAB_SIZE));
		document.setBoolean(XMLFormatterConstants.WRAP_COMMENTS, getBoolean(XMLFormatterConstants.WRAP_COMMENTS));
		document.setBoolean(XMLFormatterConstants.NEW_LINES_EXCLUDED_ON_TEXT_NODES,
				getBoolean(XMLFormatterConstants.NEW_LINES_EXCLUDED_ON_TEXT_NODES));
		document.setInt(XMLFormatterConstants.LINES_AFTER_ELEMENTS, getInt(XMLFormatterConstants.LINES_AFTER_ELEMENTS));
		document.setSet(XMLFormatterConstants.INDENT_EXCLUDED_TAGS,
				getSet(XMLFormatterConstants.INDENT_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		document.setSet(XMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS,
				getSet(XMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		document.setInt(ScriptFormattingContextProperties.CONTEXT_ORIGINAL_OFFSET, offset);

		// Formatter OFF/ON
		document.setBoolean(XMLFormatterConstants.FORMATTER_OFF_ON_ENABLED,
				getBoolean(XMLFormatterConstants.FORMATTER_OFF_ON_ENABLED));
		document.setString(XMLFormatterConstants.FORMATTER_ON, getString(XMLFormatterConstants.FORMATTER_ON));
		document.setString(XMLFormatterConstants.FORMATTER_OFF, getString(XMLFormatterConstants.FORMATTER_OFF));

		return document;
	}

}
