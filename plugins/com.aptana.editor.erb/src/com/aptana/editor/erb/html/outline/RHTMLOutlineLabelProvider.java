package com.aptana.editor.erb.html.outline;

import com.aptana.editor.html.outline.HTMLOutlineLabelProvider;
import com.aptana.editor.ruby.core.IRubyScript;
import com.aptana.editor.ruby.outline.RubyOutlineLabelProvider;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;

public class RHTMLOutlineLabelProvider extends HTMLOutlineLabelProvider
{

	public RHTMLOutlineLabelProvider()
	{
		addSubLanguage(IRubyParserConstants.LANGUAGE, new RubyOutlineLabelProvider());
	}

	@Override
	protected String getDefaultText(Object element)
	{
		if (element instanceof IRubyScript)
		{
			return "erb"; //$NON-NLS-1$
		}
		return super.getDefaultText(element);
	}
}
