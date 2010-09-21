package com.aptana.editor.html.formatter;

import java.util.Map;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.html.parsing.IHTMLParserConstants;
import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.epl.FormatterPlugin;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.IScriptFormatter;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

/**
 * HTML code formatter.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatter extends AbstractScriptFormatter implements IScriptFormatter
{

	/**
	 * Constructor.
	 * 
	 * @param preferences
	 */
	protected HTMLFormatter(Map<String, ? extends Object> preferences)
	{
		super(preferences);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#format(java.lang.String, int, int, int)
	 */
	@Override
	public TextEdit format(String source, int offset, int length, int indentationLevel) throws FormatterException
	{
		String input = source.substring(offset, offset + length);
		HTMLParser parser = getParser();
		IParseState parseState = new HTMLParseState();
		parseState.setEditState(input, null, 0, 0);
		try
		{
			IParseNode parseResult = parser.parse(parseState);
			System.out.println(parseResult);
		}
		catch (Exception e)
		{
			FormatterPlugin.logError(e);
		}
		return new MultiTextEdit(); // NOP
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentSize()
	 */
	@Override
	public int getIndentSize()
	{
		return getInt(HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getIndentType()
	 */
	@Override
	public String getIndentType()
	{
		return getString(HTMLFormatterConstants.FORMATTER_TAB_CHAR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.IScriptFormatter#getTabSize()
	 */
	@Override
	public int getTabSize()
	{
		return getInt(HTMLFormatterConstants.FORMATTER_TAB_SIZE);
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

}
