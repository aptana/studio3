package com.aptana.editor.erb.html.outline;

import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.erb.html.parsing.ERBScript;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
import com.aptana.editor.ruby.core.IRubyScript;
import com.aptana.editor.ruby.outline.RubyOutlineLabelProvider;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.IParseState;

public class RHTMLOutlineLabelProvider extends HTMLOutlineLabelProvider
{

	private static final Image ERB_ICON = Activator.getImage("icons/element.gif"); //$NON-NLS-1$

	private static final int TRIM_TO_LENGTH = 20;

	private IParseState fParseState;

	public RHTMLOutlineLabelProvider(IParseState parseState)
	{
		fParseState = parseState;
		addSubLanguage(IRubyParserConstants.LANGUAGE, new RubyOutlineLabelProvider());
	}

	@Override
	protected Image getDefaultImage(Object element)
	{
		if (element instanceof ERBScript)
		{
			return ERB_ICON;
		}
		return super.getDefaultImage(element);
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
		// locates the ruby source
		IRubyScript ruby = script.getScript();
		source = source.substring(ruby.getStartingOffset(), ruby.getEndingOffset());
		// gets the first line of the ruby source
		StringTokenizer st = new StringTokenizer(source, "\n\r\f"); //$NON-NLS-1$
		source = st.nextToken();
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
