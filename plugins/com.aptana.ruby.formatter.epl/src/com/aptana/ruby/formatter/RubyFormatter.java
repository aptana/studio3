/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.ruby.formatter;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jrubyparser.parser.ParserResult;

import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.editor.ruby.parsing.NullParserResult;
import com.aptana.editor.ruby.parsing.RubyParser;
import com.aptana.editor.ruby.parsing.RubySourceParser;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterIndentDetector;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.ruby.formatter.internal.DumpContentException;
import com.aptana.ruby.formatter.internal.Messages;
import com.aptana.ruby.formatter.internal.RubyFormatterContext;
import com.aptana.ruby.formatter.internal.RubyFormatterNodeBuilder;
import com.aptana.ruby.formatter.internal.RubyFormatterNodeRewriter;

public class RubyFormatter extends AbstractScriptFormatter
{

	protected static final String[] INDENTING = { RubyFormatterConstants.INDENT_CLASS,
			RubyFormatterConstants.INDENT_MODULE, RubyFormatterConstants.INDENT_METHOD,
			RubyFormatterConstants.INDENT_BLOCKS, RubyFormatterConstants.INDENT_IF, RubyFormatterConstants.INDENT_CASE,
			RubyFormatterConstants.INDENT_WHEN };

	protected static final String[] BLANK_LINES = { RubyFormatterConstants.LINES_FILE_AFTER_REQUIRE,
			RubyFormatterConstants.LINES_FILE_BETWEEN_MODULE, RubyFormatterConstants.LINES_FILE_BETWEEN_CLASS,
			RubyFormatterConstants.LINES_FILE_BETWEEN_METHOD, RubyFormatterConstants.LINES_BEFORE_FIRST,
			RubyFormatterConstants.LINES_BEFORE_MODULE, RubyFormatterConstants.LINES_BEFORE_CLASS,
			RubyFormatterConstants.LINES_BEFORE_METHOD };

	private final String lineDelimiter;

	public RubyFormatter(String lineDelimiter, Map<String, ? extends Object> preferences)
	{
		super(preferences);
		this.lineDelimiter = lineDelimiter;
	}

	public int detectIndentationLevel(IDocument document, int offset)
	{
		final String source = document.get();
		final ParserResult result;
		try
		{
			RubySourceParser sourceParser = getSourceParser();
			result = sourceParser.parse(source);
			if (!(result instanceof NullParserResult))
			{
				final RubyFormatterNodeBuilder builder = new RubyFormatterNodeBuilder();
				final FormatterDocument fDocument = createDocument(source);
				IFormatterContainerNode root = builder.build(result, fDocument);
				new RubyFormatterNodeRewriter(result, fDocument).rewrite(root);
				final IFormatterContext context = new RubyFormatterContext(0);
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
			FormatterPlugin.logError(t);
		}
		// TODO keep current indent
		return 0;
	}


	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	@Override
	public int getIndentSize()
	{
		return getInt(RubyFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	@Override
	public String getIndentType()
	{
		return getString(RubyFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	@Override
	public int getTabSize()
	{
		return getInt(RubyFormatterConstants.FORMATTER_TAB_SIZE);
	}

	
	public TextEdit format(String source, int offset, int length, int indent) throws FormatterException
	{
		String input = source.substring(offset, offset + length);
		if (isSlave())
		{
			// We are formatting an ERB content
			if (input.startsWith("<%=")) { //$NON-NLS-1$
				input = input.substring(3);
				offset += 3;
				length -= 3;
			}
			else if (input.startsWith("<%")) { //$NON-NLS-1$
				input = input.substring(2);
				offset += 2;
				length -= 2;
			}
			if (input.endsWith("%>")) { //$NON-NLS-1$
				input = input.substring(0, input.length() - 2);
				length -= 2;
			}
			// We also skip any new-line characters for the ERB case. Otherwise, the formatting will jump the code up.
			int toTrim = 0;
			for (int i = 0; i < input.length(); i++)
			{
				char c = input.charAt(i);
				if (c == '\n' || c == '\r')
				{
					toTrim++;
				}
				else
				{
					break;
				}
			}
			if (toTrim > 0)
			{
				input = input.substring(toTrim);
				offset += toTrim;
				length -= toTrim;
			}
		}
		RubySourceParser sourceParser = getSourceParser();
		ParserResult result = sourceParser.parse(input);
		if (!(result instanceof NullParserResult))
		{
			final String output = format(input, result, indent);
			if (output != null)
			{
				if (!input.equals(output))
				{
					if (!isValidation() || equalsIgnoreBlanks(new StringReader(input), new StringReader(output)))
					{
						return new ReplaceEdit(offset, length, output);
					}
					else
					{
						FormatterPlugin.log(new Status(IStatus.ERROR, RubyFormatterPlugin.PLUGIN_ID, IStatus.OK,
								Messages.RubyFormatter_contentCorrupted, new DumpContentException(input
										+ "\n<!-------!>\n" + output))); //$NON-NLS-1$
					}
				}
				else
				{
					return new MultiTextEdit(); // NOP
				}
			}
		}
		return null;
	}

	/**
	 * @return RubySourceParser
	 */
	private RubySourceParser getSourceParser()
	{
		RubySourceParser sourceParser = null;
		IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IRubyParserConstants.LANGUAGE);
		if (pool != null)
		{
			IParser parser = pool.checkOut();
			if (parser instanceof RubyParser)
			{
				sourceParser = ((RubyParser) parser).getSourceParser();
			}
			pool.checkIn(parser);
		}
		if (sourceParser == null)
		{
			sourceParser = new RubyParser().getSourceParser();
		}
		return sourceParser;
	}

	protected boolean isValidation()
	{
		return !getBoolean(RubyFormatterConstants.WRAP_COMMENTS);
	}

	/**
	 * @param input
	 * @param result
	 * @return
	 */
	private String format(String input, ParserResult result, int indent)
	{
		final RubyFormatterNodeBuilder builder = new RubyFormatterNodeBuilder();
		final FormatterDocument document = createDocument(input);
		IFormatterContainerNode root = builder.build(result, document);
		new RubyFormatterNodeRewriter(result, document).rewrite(root);
		IFormatterContext context = new RubyFormatterContext(indent);
		FormatterWriter writer = new FormatterWriter(document, lineDelimiter, createIndentGenerator());
		writer.setWrapLength(getInt(RubyFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(RubyFormatterConstants.LINES_PRESERVE));
		try
		{
			root.accept(context, writer);
			writer.flush(context);
			return writer.getOutput();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private FormatterDocument createDocument(String input)
	{
		FormatterDocument document = new FormatterDocument(input);
		for (int i = 0; i < INDENTING.length; ++i)
		{
			document.setBoolean(INDENTING[i], getBoolean(INDENTING[i]));
		}
		for (int i = 0; i < BLANK_LINES.length; ++i)
		{
			document.setInt(BLANK_LINES[i], getInt(BLANK_LINES[i]));
		}
		document.setInt(RubyFormatterConstants.FORMATTER_TAB_SIZE, getInt(RubyFormatterConstants.FORMATTER_TAB_SIZE));
		document.setBoolean(RubyFormatterConstants.WRAP_COMMENTS, getBoolean(RubyFormatterConstants.WRAP_COMMENTS));
		return document;
	}

	private boolean equalsIgnoreBlanks(Reader inputReader, Reader outputReader)
	{
		LineNumberReader input = new LineNumberReader(inputReader);
		LineNumberReader output = new LineNumberReader(outputReader);
		for (;;)
		{
			final String inputLine = readLine(input);
			final String outputLine = readLine(output);
			if (inputLine == null)
			{
				if (outputLine == null)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else if (outputLine == null)
			{
				return false;
			}
			else if (!inputLine.equals(outputLine))
			{
				return false;
			}
		}
	}

	private String readLine(LineNumberReader reader)
	{
		String line;
		do
		{
			try
			{
				line = reader.readLine();
			}
			catch (IOException e)
			{
				// should not happen
				return null;
			}
			if (line == null)
			{
				return line;
			}
			line = line.trim();
		}
		while (line.length() == 0);
		return line;
	}
}
