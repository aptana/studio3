package com.aptana.js.core.parsing;

import com.aptana.parsing.IParser;

public class JSBeaverParserPerformanceTests extends JSParserPerformanceTest
{
	@Override
	protected IParser createParser()
	{
		return new JSParser();
	}

}
