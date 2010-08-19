package com.aptana.editor.erb.html.outline;

import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.outline.CommonOutlineItem;
import com.aptana.editor.erb.Activator;
import com.aptana.editor.erb.html.parsing.ERBScript;
import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
import com.aptana.editor.ruby.core.IRubyScript;
import com.aptana.editor.ruby.outline.RubyOutlineLabelProvider;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;

public class RHTMLOutlineLabelProvider extends HTMLOutlineLabelProvider
{

	private static final Image ERB_ICON = Activator.getImage("icons/embedded_code_fragment.png"); //$NON-NLS-1$

	private static final int TRIM_TO_LENGTH = 20;

	private IParseState fParseState;

	public RHTMLOutlineLabelProvider(IParseState parseState)
	{
		fParseState = parseState;
		addSubLanguage(IRubyParserConstants.LANGUAGE, new RubyOutlineLabelProvider());
	}

	@Override
	public Image getImage(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			if (node instanceof ERBScript)
			{
				return ERB_ICON;
			}
		}
		return super.getImage(element);
	}

	@Override
	public String getText(Object element)
	{
		if (element instanceof CommonOutlineItem)
		{
			IParseNode node = ((CommonOutlineItem) element).getReferenceNode();
			if (node instanceof ERBScript)
			{
				return getDisplayText((ERBScript) node);
			}
		}
		return super.getText(element);
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
