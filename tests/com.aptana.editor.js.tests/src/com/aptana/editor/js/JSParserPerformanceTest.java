package com.aptana.editor.js;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class JSParserPerformanceTest extends TestCase
{

	private JSParser fParser;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new JSParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
	}

	public void testTime() throws Exception
	{
		InputStream stream = getClass().getResourceAsStream("tiny_mce.js");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int read = -1;
		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}
		stream.close();
		String src = new String(out.toByteArray());

		IParseState parseState = new ParseState();
		int numRuns = 5;
		long start = System.currentTimeMillis();
		for (int i = 0; i < numRuns; i++)
		{
			parseState.setEditState(src, src, 0, 0);
			try
			{
				fParser.parse(parseState);
			}
			catch (Exception e)
			{
			}
		}
		long diff = System.currentTimeMillis() - start;
		System.out.println("Average time: " + (diff / numRuns) + "ms");
	}
}
