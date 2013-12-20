package com.aptana.parsing.lexer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LexemeTest
{

	@Test
	public void testLexemeBasics() throws Exception
	{
		int offset = 0;
		int endOffset = 10;
		String text = "01234567890";
		Lexeme<TestTokenType> lex = new Lexeme<TestTokenType>(TestTokenType.EOF, offset, endOffset, text);

		assertEquals(TestTokenType.EOF, lex.getType());
		assertTrue(lex.contains(0));
		assertTrue(lex.contains(10));
		assertFalse(lex.contains(-1));
		assertFalse(lex.contains(11));
		assertEquals("start offset", offset, lex.getStartingOffset());
		assertEquals("end offset", endOffset, lex.getEndingOffset());
		assertEquals("length", 11, lex.getLength());
		assertEquals("text", text, lex.getText());
		assertFalse("isEmpty()", lex.isEmpty());
		assertEquals("toString()", "EOF [0-10,01234567890]", lex.toString());
	}

	@Test
	public void testEmptyLexeme() throws Exception
	{
		int offset = 0;
		int endOffset = -1;
		Lexeme<TestTokenType> lex = new Lexeme<TestTokenType>(TestTokenType.EOF, offset, endOffset, null);

		assertEquals(TestTokenType.EOF, lex.getType());
		// FIXME Should length be -1 if text is null, but offsets are OK?
		assertEquals("length", -1, lex.getLength());
		assertNull("text", lex.getText());
		assertTrue("isEmpty()", lex.isEmpty());
		assertEquals("toString()", "EOF [0--1,null]", lex.toString());
	}

	private enum TestTokenType
	{
		EOF;
	}
}
