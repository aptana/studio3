package com.aptana.js.core.parsing.antlr;

import org.junit.Test;

import com.aptana.js.core.parsing.JSParserTest;
import com.aptana.js.core.tests.ITestFiles;
import com.aptana.parsing.IParser;

public class JSANTLRParserTest extends JSParserTest
{

	// FIXME This still fails 3 unit tests:
	// testSemicolonInsertion1: inserting semicolons into invalid code
	// testSwitchWithoutExpression: Not handling bad switch syntax as well as beaver with recovery strategies
	// testFunctionWithoutBody: Not handling bad function declaration syntax as well as beaver with recovery strategies

	@Override
	protected IParser createParser()
	{
		return new JSWrappingParser();
	}

	@Override
	protected boolean isANTLR()
	{
		return true;
	}

	@Override
	protected boolean isBeaver()
	{
		return false;
	}

	protected String unexpectedToken(String token)
	{
		return "extraneous input '" + token + "' expecting";
	}

	@Override
	protected String mismatchedToken(String token)
	{
		return "mismatched input '" + token + "'";
	}
	
	@Test
	public void testJaxerComposerCommands() throws Exception
	{
		parse(getSource(ITestFiles.JAXER_FILES[4]));
		assertNoErrors();
	}
	
	@Test
	public void testJaxerLotsOfFunctions() throws Exception
	{
		parse(getSource("performance/jaxer/regress-155081-2.js"));
		assertNoErrors();
	}
}
