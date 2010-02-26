package com.aptana.editor.erb.html.outline;

import com.aptana.editor.erb.html.parsing.ERBScript;
import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
import com.aptana.editor.ruby.outline.RubyOutlineLabelProvider;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.IParseState;

public class RHTMLOutlineLabelProvider extends HTMLOutlineLabelProvider
{

	private static final int TRIM_TO_LENGTH = 20;

	private IParseState fParseState;

	public RHTMLOutlineLabelProvider(IParseState parseState)
	{
		fParseState = parseState;
		addSubLanguage(IRubyParserConstants.LANGUAGE, new RubyOutlineLabelProvider());
	}

	@Override
	protected String getDefaultText(Object element)
	{
		if (element instanceof ERBScript)
		{
			return getDisplayText((ERBScript) element);
		}
		return super.getDefaultText(element);
	}

	private String getDisplayText(ERBScript script)
	{
		StringBuilder text = new StringBuilder();
		text.append(script.getStartTag()).append(" "); //$NON-NLS-1$

		String source = new String(fParseState.getSource());
		source = source.substring(script.getStartingOffset(), script.getEndingOffset());
		if (source.length() <= TRIM_TO_LENGTH)
		{
			text.append(source);
		}
		else
		{
			text.append(source.substring(0, TRIM_TO_LENGTH - 1)).append("..."); //$NON-NLS-1$
		}
		text.append(" ").append(script.getEndTag()); //$NON-NLS-1$
		return text.toString();
	}
}
