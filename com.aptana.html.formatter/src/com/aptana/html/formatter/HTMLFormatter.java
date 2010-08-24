package com.aptana.html.formatter;

import java.util.Map;

import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
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

}
