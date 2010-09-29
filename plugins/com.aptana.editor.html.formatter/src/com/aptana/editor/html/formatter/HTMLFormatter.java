package com.aptana.editor.html.formatter;

import java.io.StringReader;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.html.parsing.IHTMLParserConstants;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.IFormatterContext;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IScriptFormatter;
import com.aptana.formatter.util.DumpContentException;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.ui.preferences.IPreferenceDelegate;

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
			HTMLFormatterConstants.LINES_BEFORE_NON_HTML_ELEMENTS, HTMLFormatterConstants.LINES_AFTER_NON_HTML_ELEMENTS };

	private String lineSeparator;

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected HTMLFormatter(String lineSeparator, Map<String, ? extends Object> preferences)
	{
		super(preferences);
		this.lineSeparator = lineSeparator;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#format(java.lang.String, int, int, int)
	 */
	public TextEdit format(String source, int offset, int length, int indentationLevel) throws FormatterException
	{
		String input = source.substring(offset, offset + length);
		HTMLParser parser = getParser();
		IParseState parseState = new HTMLParseState();
		parseState.setEditState(input, null, 0, 0);
		try
		{
			IParseNode parseResult = parser.parse(parseState);
			if (parseResult != null)
			{
				final String output = format(input, parseResult, indentationLevel);
				if (output != null)
				{
					if (!input.equals(output))
					{
						if (equalsIgnoreBlanks(new StringReader(input), new StringReader(output)))
						{
							return new ReplaceEdit(offset, length, output);
						}
						else
						{
							FormatterPlugin.log(new Status(IStatus.ERROR, HTMLFormatterPlugin.PLUGIN_ID, IStatus.OK,
									FormatterMessages.RubyFormatter_formatterError, new DumpContentException(input
											+ "\n=========================\n" + output))); //$NON-NLS-1$
						}
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
		return getInt(HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE);
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
		return getInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE);
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
	 */
	private String format(String input, IParseNode parseResult, int indentationLevel)
	{
		final HTMLFormatterNodeBuilder builder = new HTMLFormatterNodeBuilder();
		final FormatterDocument document = createFormatterDocument(input);
		IFormatterContainerNode root = builder.build(parseResult, document);
		new HTMLFormatterNodeRewriter(parseResult, document).rewrite(root);
		IFormatterContext context = new HTMLFormatterContext(indentationLevel);
		FormatterWriter writer = new FormatterWriter(document, lineSeparator, createIndentGenerator());
		writer.setWrapLength(getInt(HTMLFormatterConstants.WRAP_COMMENTS_LENGTH));
		writer.setLinesPreserve(getInt(HTMLFormatterConstants.PRESERVED_LINES));
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

	/**
	 * @return HTMLParser
	 */
	private HTMLParser getParser()
	{
		HTMLParser htmlParser = null;
		IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IHTMLParserConstants.LANGUAGE);
		if (pool != null)
		{
			IParser parser = pool.checkOut();
			if (parser instanceof HTMLParser)
			{
				htmlParser = (HTMLParser) parser;
			}
			pool.checkIn(parser);
		}
		if (htmlParser == null)
		{
			htmlParser = new HTMLParser();
		}
		return htmlParser;
	}

	private FormatterDocument createFormatterDocument(String input)
	{
		FormatterDocument document = new FormatterDocument(input);
		document.setInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE, getInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE));
		document.setBoolean(HTMLFormatterConstants.WRAP_COMMENTS, getBoolean(HTMLFormatterConstants.WRAP_COMMENTS));
		document.setSet(HTMLFormatterConstants.INDENT_EXCLUDED_TAGS, getSet(
				HTMLFormatterConstants.INDENT_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		document.setSet(HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, getSet(
				HTMLFormatterConstants.NEW_LINES_EXCLUDED_TAGS, IPreferenceDelegate.PREFERECE_DELIMITER));
		for (int i = 0; i < BLANK_LINES.length; i++)
		{
			document.setInt(BLANK_LINES[i], getInt(BLANK_LINES[i]));
		}
		return document;
	}

}
