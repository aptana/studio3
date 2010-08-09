package com.aptana.editor.css;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import com.aptana.editor.css.parsing.CSSParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class CSSParserPerformanceTest extends TestCase
{

	private CSSParser fParser;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new CSSParser();
	}

	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
	}

	public void testTime() throws Exception
	{
		InputStream stream = getClass().getResourceAsStream("wp-admin.css");
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
