package com.aptana.editor.js.parsing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseState;

public class JSParserPerformanceTest extends TestCase
{
	private JSParser fParser;

	/**
	 * getSource
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	private String getSource(String resourceName) throws IOException
	{
		InputStream stream = getClass().getResourceAsStream(resourceName);
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		int read = -1;

		while ((read = stream.read()) != -1)
		{
			out.write(read);
		}

		stream.close();

		String src = new String(out.toByteArray());
		return src;
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		fParser = new JSParser();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		fParser = null;
	}

	/**
	 * testDojo
	 * 
	 * @throws Exception
	 */
	public void testDojo() throws Exception
	{
		time("dojo.js.uncompressed.js");
	}
	
	/**
	 * testExt
	 * 
	 * @throws Exception
	 */
	public void testExt() throws Exception
	{
		time("ext-core.js");
	}

	/**
	 * testTiMobile
	 * 
	 * @throws Exception
	 */
	public void testTiMobile() throws Exception
	{
		time("timobile.js");
	}

	/**
	 * testTinyMce
	 * 
	 * @throws Exception
	 */
	public void testTinyMce() throws Exception
	{
		time("tiny_mce.js");
	}

	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	protected void time(String resourceName) throws Exception
	{
		this.time(resourceName, 5);
	}
	
	/**
	 * time
	 * 
	 * @param resourceName
	 * @throws Exception
	 */
	protected void time(String resourceName, int numRuns) throws Exception
	{
		// grab source
		String src = getSource(resourceName);

		// apply to parse state
		IParseState parseState = new ParseState();
		parseState.setEditState(src, src, 0, 0);

		// start timing
		long start = System.currentTimeMillis();

		for (int i = 0; i < numRuns; i++)
		{
			try
			{
				fParser.parse(parseState);
			}
			catch (Exception e)
			{
				fail(e.getMessage());
			}
		}

		// get time difference
		long diff = System.currentTimeMillis() - start;

		// show results
		System.out.println(resourceName + " average time: " + (diff / numRuns) + "ms");
	}
}
