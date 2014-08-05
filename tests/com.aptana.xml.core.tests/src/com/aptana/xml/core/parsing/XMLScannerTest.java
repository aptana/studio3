package com.aptana.xml.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import beaver.Symbol;

public class XMLScannerTest
{

	private XMLScanner scanner;

	@Before
	public void setUp() throws Exception
	{
		scanner = new XMLScanner();
	}

	@After
	public void tearDown() throws Exception
	{
		scanner = null;
	}

	@Test
	public void testAttributeWithValue() throws IOException, beaver.Scanner.Exception
	{
		scanner.setSource("<tag attr=\"value\" />");
		assertToken(Terminals.LESS, 0, 0, scanner.nextToken());
		assertToken(Terminals.TEXT, 1, 3, "tag", scanner.nextToken());
		assertToken(Terminals.TEXT, 5, 8, "attr", scanner.nextToken());
		assertToken(Terminals.EQUAL, 9, 9, scanner.nextToken());
		assertToken(Terminals.STRING, 10, 16, "\"value\"", scanner.nextToken());
		assertToken(Terminals.SLASH_GREATER, 18, 19, scanner.nextToken());
	}

	@Test
	public void testUnquotedAttributeValue() throws IOException, beaver.Scanner.Exception
	{
		scanner.setSource("<tag attr=value />");
		assertToken(Terminals.LESS, 0, 0, scanner.nextToken());
		assertToken(Terminals.TEXT, 1, 3, "tag", scanner.nextToken());
		assertToken(Terminals.TEXT, 5, 8, "attr", scanner.nextToken());
		assertToken(Terminals.EQUAL, 9, 9, scanner.nextToken());
		assertToken(Terminals.TEXT, 10, 14, "value", scanner.nextToken());
		assertToken(Terminals.SLASH_GREATER, 16, 17, scanner.nextToken());
	}

	@Test
	public void testUnquotedAttributeValueBeginningWithDigit() throws IOException, beaver.Scanner.Exception
	{
		scanner.setSource("<tag attr=123 />");
		assertToken(Terminals.LESS, 0, 0, scanner.nextToken());
		assertToken(Terminals.TEXT, 1, 3, "tag", scanner.nextToken());
		assertToken(Terminals.TEXT, 5, 8, "attr", scanner.nextToken());
		assertToken(Terminals.EQUAL, 9, 9, scanner.nextToken());
		assertToken(Terminals.TEXT, 10, 12, "123", scanner.nextToken());
		assertToken(Terminals.SLASH_GREATER, 14, 15, scanner.nextToken());
	}

	@Test
	public void testAttributeWithNoValue() throws IOException, beaver.Scanner.Exception
	{
		scanner.setSource("<tag attr />");
		assertToken(Terminals.LESS, 0, 0, scanner.nextToken());
		assertToken(Terminals.TEXT, 1, 3, "tag", scanner.nextToken());
		assertToken(Terminals.TEXT, 5, 8, "attr", scanner.nextToken());
		assertToken(Terminals.SLASH_GREATER, 10, 11, scanner.nextToken());
	}

	protected void assertToken(short id, int start, int end, Symbol s)
	{
		assertToken(id, start, end, null, s);
	}

	protected void assertToken(short id, int start, int end, String value, Symbol s)
	{
		assertNotNull(s);
		assertEquals(value, s.value);
		assertEquals(id, s.getId());
		assertEquals(start, s.getStart());
		assertEquals(end, s.getEnd());
	}

}
