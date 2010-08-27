package com.aptana.editor.erb.html.parsing;

import com.aptana.editor.html.parsing.HTMLParserScanner;

public class RHTMLParserScanner extends HTMLParserScanner
{

	public RHTMLParserScanner()
	{
		super(new RHTMLScanner());
	}
}
