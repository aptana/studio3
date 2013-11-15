/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.util.List;

import junit.framework.TestCase;
import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ListCrossProduct;
import com.aptana.core.util.StringUtil;

public class JSFlexScannerTest extends TestCase
{
	private JSFlexScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws java.lang.Exception
	{
		super.setUp();

		_scanner = new JSFlexScanner();
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws java.lang.Exception
	{
		_scanner = null;

		super.tearDown();
	}

	/**
	 * assertTokenType
	 * 
	 * @param source
	 * @param type
	 * @throws Exception
	 */
	protected void assertTokenType(String source, JSTokenType type)
	{
		_scanner.setSource(source);

		try
		{
			Symbol token = _scanner.nextToken();
			int id = token.getId();
			int length = token.getEnd() - token.getStart() + 1;

			assertEquals("unexpected token type", type.getIndex(), id);
			assertEquals("token length does not match source length: " + source, source.length(), length);
		}
		catch (Throwable e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * assertTokenTypes
	 * 
	 * @param source
	 * @param types
	 * @throws Exception
	 */
	protected void assertTokenTypes(String source, JSTokenType... types)
	{
		_scanner.setSource(source);

		for (int i = 0; i < types.length; i++)
		{
			try
			{
				JSTokenType type = types[i];
				Symbol token = _scanner.nextToken();
				int id = token.getId();

				assertEquals("in '" + source + "' at token index " + i, type.getIndex(), id);
			}
			catch (Throwable e)
			{
				fail(e.getMessage());
			}
		}
	}

	protected void assertListCrossProducts(List<List<String>> lists, JSTokenType tokenType)
	{
		ListCrossProduct<String> crossProduct = new ListCrossProduct<String>();

		for (List<String> list : lists)
		{
			crossProduct.addList(list);
		}

		for (List<String> list : crossProduct)
		{
			String text = StringUtil.concat(list);

			assertTokenType(text, tokenType);
		}
	}

	protected void scanOnce(String source)
	{
		_scanner.setSource(source);

		try
		{
			_scanner.nextToken();
		}
		catch (Throwable e)
		{
			fail(e.getMessage());
		}
	}

	// begin tests

	public void testVSDocComment()
	{
		scanOnce("/// this is a vsdoc comment"); // , JSTokenType.VSDOC);

		assertEquals(1, _scanner.getVSDocComments().size());
	}

	public void testSDocComment()
	{
		scanOnce("/**\n  * this is an sdoc comment\n */"); // , JSTokenType.SDOC);

		assertEquals(1, _scanner.getSDocComments().size());
	}

	public void testSingleLineComment()
	{
		scanOnce("// this is a singe line comment"); // , JSTokenType.SINGLELINE_COMMENT);

		assertEquals(1, _scanner.getSingleLineComments().size());
	}

	public void testMultiLineComment()
	{
		scanOnce("/*\n  * this is a multi-line comment\n */"); // , JSTokenType.MULTILINE_COMMENT);

		assertEquals(1, _scanner.getMultiLineComments().size());
	}

	public void testDoubleQuotedString()
	{
		assertTokenTypes("\"this is a string\"", JSTokenType.STRING);
	}

	public void testSingleQuotedString()
	{
		assertTokenTypes("'this is a string'", JSTokenType.STRING);
	}

	public void testBreak()
	{
		assertTokenTypes("break", JSTokenType.BREAK);
	}

	public void testCase()
	{
		assertTokenTypes("case", JSTokenType.CASE);
	}

	public void testCatch()
	{
		assertTokenTypes("catch", JSTokenType.CATCH);
	}

	public void testContinue()
	{
		assertTokenTypes("continue", JSTokenType.CONTINUE);
	}

	public void testDefault()
	{
		assertTokenTypes("default", JSTokenType.DEFAULT);
	}

	public void testDelete()
	{
		assertTokenTypes("delete", JSTokenType.DELETE);
	}

	public void testDo()
	{
		assertTokenTypes("do", JSTokenType.DO);
	}

	public void testElse()
	{
		assertTokenTypes("else", JSTokenType.ELSE);
	}

	public void testFalse()
	{
		assertTokenTypes("false", JSTokenType.FALSE);
	}

	public void testFinally()
	{
		assertTokenTypes("finally", JSTokenType.FINALLY);
	}

	public void testFor()
	{
		assertTokenTypes("for", JSTokenType.FOR);
	}

	public void testFunction()
	{
		assertTokenTypes("function", JSTokenType.FUNCTION);
	}

	public void testIf()
	{
		assertTokenTypes("if", JSTokenType.IF);
	}

	public void testInstanceOf()
	{
		assertTokenTypes("instanceof", JSTokenType.INSTANCEOF);
	}

	public void testIn()
	{
		assertTokenTypes("in", JSTokenType.IN);
	}

	public void testNew()
	{
		assertTokenTypes("new", JSTokenType.NEW);
	}

	public void testNull()
	{
		assertTokenTypes("null", JSTokenType.NULL);
	}

	public void testReturn()
	{
		assertTokenTypes("return", JSTokenType.RETURN);
	}

	public void testSwitch()
	{
		assertTokenTypes("switch", JSTokenType.SWITCH);
	}

	public void testThis()
	{
		assertTokenTypes("this", JSTokenType.THIS);
	}

	public void testThrow()
	{
		assertTokenTypes("throw", JSTokenType.THROW);
	}

	public void testTrue()
	{
		assertTokenTypes("true", JSTokenType.TRUE);
	}

	public void testTry()
	{
		assertTokenTypes("try", JSTokenType.TRY);
	}

	public void testTypeOf()
	{
		assertTokenTypes("typeof", JSTokenType.TYPEOF);
	}

	public void testVar()
	{
		assertTokenTypes("var", JSTokenType.VAR);
	}

	public void testVoid()
	{
		assertTokenTypes("void", JSTokenType.VOID);
	}

	public void testWhile()
	{
		assertTokenTypes("while", JSTokenType.WHILE);
	}

	public void testWith()
	{
		assertTokenTypes("with", JSTokenType.WITH);
	}

	// identifiers

	public void testIdentifier()
	{
		// @formatter:off
		@SuppressWarnings("unchecked")
		List<List<String>> lists = CollectionsUtil.newList(
			CollectionsUtil.newList("_", "$", "a", "A"),
			CollectionsUtil.newList("", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
		);
		// @formatter:on

		this.assertListCrossProducts(lists, JSTokenType.IDENTIFIER);
	}

	// operators

	public void testArithmeticShiftRightAssign()
	{
		// identifiers
		assertTokenTypes(">>>=", JSTokenType.GREATER_GREATER_GREATER_EQUAL);
	}

	public void testArithmeticShiftRight()
	{
		assertTokenTypes(">>>", JSTokenType.GREATER_GREATER_GREATER);
	}

	public void testShiftLeftAssign()
	{
		assertTokenTypes("<<=", JSTokenType.LESS_LESS_EQUAL);
	}

	public void testShiftLeft()
	{
		assertTokenTypes("<<", JSTokenType.LESS_LESS);
	}

	public void testLessEqual()
	{
		assertTokenTypes("<=", JSTokenType.LESS_EQUAL);
	}

	public void testLess()
	{
		assertTokenTypes("<", JSTokenType.LESS);
	}

	public void testShiftRightAssign()
	{
		assertTokenTypes(">>=", JSTokenType.GREATER_GREATER_EQUAL);
	}

	public void testShiftRight()
	{
		assertTokenTypes(">>", JSTokenType.GREATER_GREATER);
	}

	public void testGreaterEqual()
	{
		assertTokenTypes(">=", JSTokenType.GREATER_EQUAL);
	}

	public void testGreater()
	{
		assertTokenTypes(">", JSTokenType.GREATER);
	}

	public void testInstanceEquality()
	{
		assertTokenTypes("===", JSTokenType.EQUAL_EQUAL_EQUAL);
	}

	public void testEquality()
	{
		assertTokenTypes("==", JSTokenType.EQUAL_EQUAL);
	}

	public void testAssign()
	{
		assertTokenTypes("=", JSTokenType.EQUAL);
	}

	public void testNotInstanceEquality()
	{
		assertTokenTypes("!==", JSTokenType.EXCLAMATION_EQUAL_EQUAL);
	}

	public void testNotEquality()
	{
		assertTokenTypes("!=", JSTokenType.EXCLAMATION_EQUAL);
	}

	public void testLogicalNot()
	{
		assertTokenTypes("!", JSTokenType.EXCLAMATION);
	}

	public void testLogicalAnd()
	{
		assertTokenTypes("&&", JSTokenType.AMPERSAND_AMPERSAND);
	}

	public void testBitwiseAndAssign()
	{
		assertTokenTypes("&=", JSTokenType.AMPERSAND_EQUAL);
	}

	public void testBitwiseAnd()
	{
		assertTokenTypes("&", JSTokenType.AMPERSAND);
	}

	public void testLogicalOr()
	{
		assertTokenTypes("||", JSTokenType.PIPE_PIPE);
	}

	public void testBitwiseOrAssign()
	{
		assertTokenTypes("|=", JSTokenType.PIPE_EQUAL);
	}

	public void testBitwiseOr()
	{
		assertTokenTypes("|", JSTokenType.PIPE);
	}

	public void testMultiplyAssign()
	{
		assertTokenTypes("*=", JSTokenType.STAR_EQUAL);
	}

	public void testMultiply()
	{
		assertTokenTypes("*", JSTokenType.STAR);
	}

	public void testDivideAssign()
	{
		// NOTE: Division cannot be first token in stream because of special-case regex handling
		assertTokenTypes("a/=", JSTokenType.IDENTIFIER, JSTokenType.FORWARD_SLASH_EQUAL);
	}

	public void testDivide()
	{
		// NOTE: Division cannot be first token in stream because of special-case regex handling
		assertTokenTypes("a/", JSTokenType.IDENTIFIER, JSTokenType.FORWARD_SLASH);
	}

	public void testModAssign()
	{
		assertTokenTypes("%=", JSTokenType.PERCENT_EQUAL);
	}

	public void testMod()
	{
		assertTokenTypes("%", JSTokenType.PERCENT);
	}

	public void testDecrement()
	{
		assertTokenTypes("--", JSTokenType.MINUS_MINUS);
	}

	public void testSubtractAssign()
	{
		assertTokenTypes("-=", JSTokenType.MINUS_EQUAL);
	}

	public void testSubtract()
	{
		assertTokenTypes("-", JSTokenType.MINUS);
	}

	public void testIncrement()
	{
		assertTokenTypes("++", JSTokenType.PLUS_PLUS);
	}

	public void testAddAssign()
	{
		assertTokenTypes("+=", JSTokenType.PLUS_EQUAL);
	}

	public void testAdd()
	{
		assertTokenTypes("+", JSTokenType.PLUS);
	}

	public void testExclusiveOrAssign()
	{
		assertTokenTypes("^=", JSTokenType.CARET_EQUAL);
	}

	public void testExclusiveOr()
	{
		assertTokenTypes("^", JSTokenType.CARET);
	}

	public void testQuestion()
	{
		assertTokenTypes("?", JSTokenType.QUESTION);
	}

	public void testBitwiseNot()
	{
		assertTokenTypes("~", JSTokenType.TILDE);
	}

	public void testSemicolon()
	{
		assertTokenTypes(";", JSTokenType.SEMICOLON);
	}

	public void testLeftParenthesis()
	{
		assertTokenTypes("(", JSTokenType.LPAREN);
	}

	public void testRightParenthesis()
	{
		assertTokenTypes(")", JSTokenType.RPAREN);
	}

	public void testLeftBracket()
	{
		assertTokenTypes("[", JSTokenType.LBRACKET);
	}

	public void testRightBracket()
	{
		assertTokenTypes("]", JSTokenType.RBRACKET);
	}

	public void testLeftCurly()
	{
		assertTokenTypes("{", JSTokenType.LCURLY);
	}

	public void testRightCurly()
	{
		assertTokenTypes("}", JSTokenType.RCURLY);
	}

	public void testComma()
	{
		assertTokenTypes(",", JSTokenType.COMMA);
	}

	public void testColon()
	{
		assertTokenTypes(":", JSTokenType.COLON);
	}

	public void testPeriod()
	{
		assertTokenTypes(".", JSTokenType.DOT);
	}

	public void testRegex()
	{
		assertTokenTypes("/^abc/", JSTokenType.REGEX);
		assertTokenTypes("/^abc/img", JSTokenType.REGEX);
	}

	/**
	 * APSTUD-4647
	 */
	public void testRegex2()
	{
		// @formatter:off
		assertTokenTypes(
			"var r = /[/]/;",
			JSTokenType.VAR,
			JSTokenType.IDENTIFIER,
			JSTokenType.EQUAL,
			JSTokenType.REGEX,
			JSTokenType.SEMICOLON
		);
		// @formatter:on
	}

	public void testRegex3()
	{
		// @formatter:off
		assertTokenTypes(
			"var r = /opacity=([^)]*)/)[1] ) /",
			JSTokenType.VAR,
			JSTokenType.IDENTIFIER,
			JSTokenType.EQUAL,
			JSTokenType.REGEX,
			JSTokenType.RPAREN,
			JSTokenType.LBRACKET,
			JSTokenType.NUMBER,
			JSTokenType.RBRACKET,
			JSTokenType.RPAREN,
			JSTokenType.FORWARD_SLASH
		);
		// @formatter:on
	}

	public void testRegex4()
	{
		// @formatter:off
		assertTokenTypes(
			"var r = /h\\d/i;",
			JSTokenType.VAR,
			JSTokenType.IDENTIFIER,
			JSTokenType.EQUAL,
			JSTokenType.REGEX,
			JSTokenType.SEMICOLON
		);
		// @formatter:on
	}

	public void testNotRegex()
	{
		assertTokenTypes("/* squelch */", JSTokenType.EOF);
	}

	public void testInteger()
	{
		assertTokenTypes("10", JSTokenType.NUMBER);
	}

	public void testHex()
	{
		// @formatter:off
		@SuppressWarnings("unchecked")
		List<List<String>> lists = CollectionsUtil.newList(
			//{ "+", "-", "" },	// TODO: apparently the scanner can't differentiate between 5 + 10 and 5 + +10?
			CollectionsUtil.newList("0"),
			CollectionsUtil.newList("x", "X"),
			CollectionsUtil.newList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F"),
			CollectionsUtil.newList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F")
		);
		// @formatter:on

		this.assertListCrossProducts(lists, JSTokenType.NUMBER);
	}

	public void testFloat()
	{
		assertTokenTypes("1.", JSTokenType.NUMBER);
		assertTokenTypes(".9", JSTokenType.NUMBER);
		assertTokenTypes("1.9", JSTokenType.NUMBER);
	}

	public void testScientificNotation()
	{
		// @formatter:off
		@SuppressWarnings("unchecked")
		List<List<String>> lists = CollectionsUtil.newList(
			//{ "+", "-", "" },	// TODO: apparently the scanner can't differentiate between 5 + 10 and 5 + +10?
			CollectionsUtil.newList("1", "1.", ".9", "1.9"),
			CollectionsUtil.newList("e", "E"),
			CollectionsUtil.newList("+", "-", ""),
			CollectionsUtil.newList("10")
		);
		// @formatter:on

		this.assertListCrossProducts(lists, JSTokenType.NUMBER);
	}
}
