package com.aptana.js.core.parsing;

import com.aptana.parsing.IParser;

public class GraalParserPerformanceTest extends JSParserPerformanceTest
{
	@Override
	protected IParser createParser()
	{
		return new GraalJSParser();
	}

}
