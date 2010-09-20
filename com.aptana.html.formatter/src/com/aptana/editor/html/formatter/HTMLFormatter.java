package com.aptana.editor.html.formatter;

import java.util.Map;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import com.aptana.formatter.AbstractScriptFormatter;
import com.aptana.formatter.ui.FormatterException;
import com.aptana.formatter.ui.IScriptFormatter;

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
		// TODO - Format the HTML.
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

}
