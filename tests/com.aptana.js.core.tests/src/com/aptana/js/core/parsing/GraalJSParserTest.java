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
	protected String mismatchedToken(int line, int offset, String token)
	{
		return "filename.js:" + line + ":" + offset + " Expected an operand but found " + token;
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
