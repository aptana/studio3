package com.aptana.editor.erb.html.outline;

import com.aptana.editor.html.outline.HTMLOutlineContentProvider;
import com.aptana.editor.ruby.outline.RubyOutlineContentProvider;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;

public class RHTMLOutlineContentProvider extends HTMLOutlineContentProvider
{
	public RHTMLOutlineContentProvider()
	{
		addSubLanguage(IRubyParserConstants.LANGUAGE, new RubyOutlineContentProvider());
	}
}
