/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.formatter;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.json.JSONPlugin;
import com.aptana.editor.json.preferences.IPreferenceConstants;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterUtils;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.IScriptFormatter;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.ScriptFormattingContextProperties;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * JSONFormatter
 */
public class JSONFormatter extends AbstractScriptFormatter implements IScriptFormatter
{
	/**
	 * JSONFormatter
	 * 
	 * @param lineSeparator
	 * @param preferences
	 * @param mainContentType
	 */
	protected JSONFormatter(String lineSeparator, Map<String, String> preferences, String mainContentType)
	{
		super(preferences, mainContentType, lineSeparator);
	}

	/**
	 * createFormatterDocument
	 * 
	 * @param input
	 * @param offset
	 * @return
	 */
	private FormatterDocument createFormatterDocument(String input, int offset)
	{
		FormatterDocument document = new FormatterDocument(input);

		document.setInt(IPreferenceConstants.FORMATTER_TAB_SIZE, getInt(IPreferenceConstants.FORMATTER_TAB_SIZE));
		document.setInt(ScriptFormattingContextProperties.CONTEXT_ORIGINAL_OFFSET, offset);

		return document;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.AbstractScriptFormatter#detectIndentationLevel(org.eclipse.jface.text.IDocument, int)
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
				JSONFormatterNodeBuilder builder = new JSONFormatterNodeBuilder();
				FormatterDocument formatterDocument = createFormatterDocument(source, offset);
				IFormatterContainerNode root = builder.build(parseResult, formatterDocument);

				new JSONFormatterNodeRewriter().rewrite(root);
				IFormatterContext context = new JSONFormatterContext(0);
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
			IParseRootNode parseResult = ParserPoolFactory.parse(getMainContentType(), input).getRootNode();
			if (parseResult != null)
			{
				String output = format(input, parseResult, indentationLevel, offset, isSelection);

				if (output != null)
				{
					if (!input.equals(output))
					{
						return new ReplaceEdit(offset, length, output);
					}
					else
					{
						return new MultiTextEdit(); // NOP
					}
				}
			}
		}
		catch (Exception e)
		{
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus,
					ERROR_DISPLAY_TIMEOUT, true);
			IdeLog.logError(JSONPlugin.getDefault(), e);
		}

		return null;
	}

	/**
	 * format
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

		// create document as a means for retrieving source
		FormatterDocument document = createFormatterDocument(input, offset);

		// create format node builder and generate root node
		JSONFormatterNodeBuilder builder = new JSONFormatterNodeBuilder();
		IFormatterContainerNode root = builder.build(parseResult, document);

		// include comments
		JSONFormatterNodeRewriter rewriter = new JSONFormatterNodeRewriter();
		rewriter.rewrite(root);

		// create a formatting context
		IFormatterContext context = new JSONFormatterContext(indentationLevel);

		// create writer to walk the formatter tree and to emit source
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());

		// walk the formatter tree
		root.accept(context, writer);

		// make sure to emit any remaining text at the end of the file
		writer.flush(context);
		String output = writer.getOutput();
		if (isSelection)
		{
			output = leftTrim(output, spacesCount);
		}
		return output;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(IPreferenceConstants.FORMATTER_INDENTATION_SIZE, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	public String getIndentType()
	{
		return getString(IPreferenceConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	public int getTabSize()
	{
		return getInt(IPreferenceConstants.FORMATTER_TAB_SIZE, getEditorSpecificTabWidth());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#getEditorSpecificTabWidth()
	 */
	public int getEditorSpecificTabWidth()
	{
		return EditorUtil.getSpaceIndentSize(JSONPlugin.getDefault().getBundle().getSymbolicName());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.IScriptFormatter#isEditorInsertSpacesForTabs()
	 */
	public boolean isEditorInsertSpacesForTabs()
	{
		return FormatterUtils.isInsertSpacesForTabs(JSONPlugin.getDefault().getPreferenceStore());
	}
}
