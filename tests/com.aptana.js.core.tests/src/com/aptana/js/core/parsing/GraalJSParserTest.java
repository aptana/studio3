package com.aptana.js.core.parsing;

import com.aptana.js.core.parsing.graal.GraalJSParser;
import com.aptana.parsing.IParser;

public class GraalJSParserTest extends JSParserTest
{

	@Override
	protected IParser createParser()
	{
		return new GraalJSParser();
	}

	@Override
	protected String mismatchedToken(String token)
	{
		// TODO Auto-generated method stub
		return token;
	}

	@Override
	protected String unexpectedToken(String token)
	{
		// TODO token-generated method stub
		return null;
	}

	@Override
	protected boolean isANTLR()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isBeaver()
	{
		// TODO Auto-generated method stub
		return false;
	}

}
