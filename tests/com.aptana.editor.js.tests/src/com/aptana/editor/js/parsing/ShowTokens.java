package com.aptana.editor.js.parsing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aptana.editor.js.parsing.lexer.JSTokenType;

import junit.framework.TestCase;
import beaver.Symbol;

public class ShowTokens extends TestCase
{
	private JSScanner _scanner;

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
		super.setUp();

		this._scanner = new JSScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._scanner = null;

		super.tearDown();
	}

	/**
	 * testSnippet
	 * 
	 * @throws Exception
	 */
	public void testSnippet() throws Exception
	{
		String source = this.getSource("unrecognized-regex.js");
		
		this._scanner.setSource(source);

		Symbol s = this._scanner.nextToken();

		while (s != null && s.getId() != JSTokenType.EOF.getIndex())
		{
			System.out.println(s.value);
			s = this._scanner.nextToken();
		}
	}
}
