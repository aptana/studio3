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
package com.aptana.editor.json.formatter;

import java.util.Map;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.editor.json.preferences.IPreferenceConstants;
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
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.ui.util.StatusLineMessageTimerManager;

/**
 * JSONFormatter
 */
public class JSONFormatter extends AbstractScriptFormatter implements IScriptFormatter
{
	private String lineSeparator;

	/**
	 * JSONFormatter
	 * 
	 * @param lineSeparator
	 * @param preferences
	 * @param mainContentType
	 */
	protected JSONFormatter(String lineSeparator, Map<String, ? extends Object> preferences, String mainContentType)
	{
		super(preferences, mainContentType);

		this.lineSeparator = lineSeparator;
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
	public int detectIndentationLevel(IDocument document, int offset)
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
			IParseRootNode parseResult = ParserPoolFactory.parse(this.getMainContentType(), source);

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
	 * @see com.aptana.formatter.ui.IScriptFormatter#format(java.lang.String, int, int, int)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel) throws FormatterException
	{
		String input = new String(source.substring(offset, offset + length));
		IParseRootNode parseResult = ParserPoolFactory.parse(this.getMainContentType(), input);

		try
		{
			if (parseResult != null)
			{
				String output = format(input, parseResult, indentationLevel, offset);

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
			StatusLineMessageTimerManager.setErrorMessage(FormatterMessages.Formatter_formatterErrorStatus, ERROR_DISPLAY_TIMEOUT, true);
			FormatterPlugin.logError(e);
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
	private String format(String input, IParseRootNode parseResult, int indentationLevel, int offset) throws Exception
	{
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

		return writer.getOutput();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	public int getIndentSize()
	{
		return getInt(IPreferenceConstants.FORMATTER_INDENTATION_SIZE);
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
		return getInt(IPreferenceConstants.FORMATTER_TAB_SIZE);
	}
}
