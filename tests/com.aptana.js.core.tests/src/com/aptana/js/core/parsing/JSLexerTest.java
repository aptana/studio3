package com.aptana.js.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ListCrossProduct;
import com.aptana.core.util.StringUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.tests.ITestFiles;

import beaver.Scanner.Exception;
import beaver.Symbol;

public abstract class JSLexerTest
{
	protected static interface IJSScanner
	{

		public void setSource(String source);

		public Symbol nextToken() throws java.lang.Exception;

		public List<Symbol> getVSDocComments();

		public List<Symbol> getSDocComments();

		public List<Symbol> getSingleLineComments();

		public List<Symbol> getMultiLineComments();

	}

	protected IJSScanner _scanner;

	// FIXME Looks like both JFlex and ANTLR lexers are fucking up templates (specifically if we see `` inside multiline
	// comments)

	@Before
	public void setUp() throws java.lang.Exception
	{
		_scanner = createScanner();
	}

	protected abstract IJSScanner createScanner();

	@After
	public void tearDown() throws java.lang.Exception
	{
		_scanner = null;
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

	@Test
	public void testVSDocComment()
	{
		scanOnce("/// this is a vsdoc comment"); // , JSTokenType.VSDOC);

		assertEquals(1, _scanner.getVSDocComments().size());
	}

	@Test
	public void testSDocComment()
	{
		scanOnce("/**\n  * this is an sdoc comment\n */"); // , JSTokenType.SDOC);

		assertEquals(1, _scanner.getSDocComments().size());
	}

	@Test
	public void testSingleLineComment()
	{
		scanOnce("// this is a singe line comment"); // , JSTokenType.SINGLELINE_COMMENT);

		assertEquals(1, _scanner.getSingleLineComments().size());
	}

	@Test
	public void testMultiLineComment()
	{
		scanOnce("/*\n  * this is a multi-line comment\n */"); // , JSTokenType.MULTILINE_COMMENT);

		assertEquals(1, _scanner.getMultiLineComments().size());
	}

	@Test
	public void testDoubleQuotedString()
	{
		assertTokenTypes("\"this is a string\"", JSTokenType.STRING);
	}

	@Test
	public void testSingleQuotedString()
	{
		assertTokenTypes("'this is a string'", JSTokenType.STRING);
	}

	@Test
	public void testBreak()
	{
		assertTokenTypes("break", JSTokenType.BREAK);
	}

	@Test
	public void testNoSubstitutionTemplateLiteral()
	{
		assertTokenTypes("`string`", JSTokenType.NO_SUB_TEMPLATE);
	}

	@Test
	public void testBackticksInsideMultilineComments()
	{
		assertTokenTypes("/*`string`*/", JSTokenType.MULTILINE_COMMENT, JSTokenType.EOF);
	}

	@Test
	public void testBackticksAcrossMultipleMultilineComments()
	{
		assertTokenTypes("/*`string`*/\r\n/* This is `some text` whatever */", JSTokenType.MULTILINE_COMMENT,
				JSTokenType.MULTILINE_COMMENT, JSTokenType.EOF);
	}

	@Test
	public void testBacktickInsideMultilineComments()
	{
		assertTokenTypes("/*`string`\r\n*\r\n*//* This is\r\n `some text` \r\nwhatever */",
				JSTokenType.MULTILINE_COMMENT, JSTokenType.MULTILINE_COMMENT, JSTokenType.EOF);
	}

	@Test
	public void testCase()
	{
		assertTokenTypes("case", JSTokenType.CASE);
	}

	@Test
	public void testCatch()
	{
		assertTokenTypes("catch", JSTokenType.CATCH);
	}

	@Test
	public void testContinue()
	{
		assertTokenTypes("continue", JSTokenType.CONTINUE);
	}

	@Test
	public void testDefault()
	{
		assertTokenTypes("default", JSTokenType.DEFAULT);
	}

	@Test
	public void testDelete()
	{
		assertTokenTypes("delete", JSTokenType.DELETE);
	}

	@Test
	public void testDo()
	{
		assertTokenTypes("do", JSTokenType.DO);
	}

	@Test
	public void testElse()
	{
		assertTokenTypes("else", JSTokenType.ELSE);
	}

	@Test
	public void testFalse()
	{
		assertTokenTypes("false", JSTokenType.FALSE);
	}

	@Test
	public void testFinally()
	{
		assertTokenTypes("finally", JSTokenType.FINALLY);
	}

	@Test
	public void testFor()
	{
		assertTokenTypes("for", JSTokenType.FOR);
	}

	@Test
	public void testFunction()
	{
		assertTokenTypes("function", JSTokenType.FUNCTION);
	}

	@Test
	public void testIf()
	{
		assertTokenTypes("if", JSTokenType.IF);
	}

	@Test
	public void testInstanceOf()
	{
		assertTokenTypes("instanceof", JSTokenType.INSTANCEOF);
	}

	@Test
	public void testIn()
	{
		assertTokenTypes("in", JSTokenType.IN);
	}

	@Test
	public void testNew()
	{
		assertTokenTypes("new", JSTokenType.NEW);
	}

	@Test
	public void testNull()
	{
		assertTokenTypes("null", JSTokenType.NULL);
	}

	@Test
	public void testReturn()
	{
		assertTokenTypes("return", JSTokenType.RETURN);
	}

	@Test
	public void testSwitch()
	{
		assertTokenTypes("switch", JSTokenType.SWITCH);
	}

	@Test
	public void testThis()
	{
		assertTokenTypes("this", JSTokenType.THIS);
	}

	@Test
	public void testThrow()
	{
		assertTokenTypes("throw", JSTokenType.THROW);
	}

	@Test
	public void testTrue()
	{
		assertTokenTypes("true", JSTokenType.TRUE);
	}

	@Test
	public void testTry()
	{
		assertTokenTypes("try", JSTokenType.TRY);
	}

	@Test
	public void testTypeOf()
	{
		assertTokenTypes("typeof", JSTokenType.TYPEOF);
	}

	@Test
	public void testVar()
	{
		assertTokenTypes("var", JSTokenType.VAR);
	}

	@Test
	public void testVoid()
	{
		assertTokenTypes("void", JSTokenType.VOID);
	}

	@Test
	public void testWhile()
	{
		assertTokenTypes("while", JSTokenType.WHILE);
	}

	@Test
	public void testWith()
	{
		assertTokenTypes("with", JSTokenType.WITH);
	}

	// identifiers

	@Test
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

	@Test
	public void testArithmeticShiftRightAssign()
	{
		// identifiers
		assertTokenTypes(">>>=", JSTokenType.GREATER_GREATER_GREATER_EQUAL);
	}

	@Test
	public void testArithmeticShiftRight()
	{
		assertTokenTypes(">>>", JSTokenType.GREATER_GREATER_GREATER);
	}

	@Test
	public void testShiftLeftAssign()
	{
		assertTokenTypes("<<=", JSTokenType.LESS_LESS_EQUAL);
	}

	@Test
	public void testShiftLeft()
	{
		assertTokenTypes("<<", JSTokenType.LESS_LESS);
	}

	@Test
	public void testLessEqual()
	{
		assertTokenTypes("<=", JSTokenType.LESS_EQUAL);
	}

	@Test
	public void testLess()
	{
		assertTokenTypes("<", JSTokenType.LESS);
	}

	@Test
	public void testShiftRightAssign()
	{
		assertTokenTypes(">>=", JSTokenType.GREATER_GREATER_EQUAL);
	}

	@Test
	public void testShiftRight()
	{
		assertTokenTypes(">>", JSTokenType.GREATER_GREATER);
	}

	@Test
	public void testGreaterEqual()
	{
		assertTokenTypes(">=", JSTokenType.GREATER_EQUAL);
	}

	@Test
	public void testGreater()
	{
		assertTokenTypes(">", JSTokenType.GREATER);
	}

	@Test
	public void testInstanceEquality()
	{
		assertTokenTypes("===", JSTokenType.EQUAL_EQUAL_EQUAL);
	}

	@Test
	public void testEquality()
	{
		assertTokenTypes("==", JSTokenType.EQUAL_EQUAL);
	}

	@Test
	public void testAssign()
	{
		assertTokenTypes("=", JSTokenType.EQUAL);
	}

	@Test
	public void testNotInstanceEquality()
	{
		assertTokenTypes("!==", JSTokenType.EXCLAMATION_EQUAL_EQUAL);
	}

	@Test
	public void testNotEquality()
	{
		assertTokenTypes("!=", JSTokenType.EXCLAMATION_EQUAL);
	}

	@Test
	public void testLogicalNot()
	{
		assertTokenTypes("!", JSTokenType.EXCLAMATION);
	}

	@Test
	public void testLogicalAnd()
	{
		assertTokenTypes("&&", JSTokenType.AMPERSAND_AMPERSAND);
	}

	@Test
	public void testBitwiseAndAssign()
	{
		assertTokenTypes("&=", JSTokenType.AMPERSAND_EQUAL);
	}

	@Test
	public void testBitwiseAnd()
	{
		assertTokenTypes("&", JSTokenType.AMPERSAND);
	}

	@Test
	public void testLogicalOr()
	{
		assertTokenTypes("||", JSTokenType.PIPE_PIPE);
	}

	@Test
	public void testBitwiseOrAssign()
	{
		assertTokenTypes("|=", JSTokenType.PIPE_EQUAL);
	}

	@Test
	public void testBitwiseOr()
	{
		assertTokenTypes("|", JSTokenType.PIPE);
	}

	@Test
	public void testMultiplyAssign()
	{
		assertTokenTypes("*=", JSTokenType.STAR_EQUAL);
	}

	@Test
	public void testMultiply()
	{
		assertTokenTypes("*", JSTokenType.STAR);
	}

	@Test
	public void testDivideAssign()
	{
		// NOTE: Division cannot be first token in stream because of special-case regex handling
		assertTokenTypes("a/=", JSTokenType.IDENTIFIER, JSTokenType.FORWARD_SLASH_EQUAL);
	}

	@Test
	public void testDivide()
	{
		// NOTE: Division cannot be first token in stream because of special-case regex handling
		assertTokenTypes("a/", JSTokenType.IDENTIFIER, JSTokenType.FORWARD_SLASH);
	}

	@Test
	public void testModAssign()
	{
		assertTokenTypes("%=", JSTokenType.PERCENT_EQUAL);
	}

	@Test
	public void testMod()
	{
		assertTokenTypes("%", JSTokenType.PERCENT);
	}

	@Test
	public void testDecrement()
	{
		assertTokenTypes("--", JSTokenType.MINUS_MINUS);
	}

	@Test
	public void testSubtractAssign()
	{
		assertTokenTypes("-=", JSTokenType.MINUS_EQUAL);
	}

	@Test
	public void testSubtract()
	{
		assertTokenTypes("-", JSTokenType.MINUS);
	}

	@Test
	public void testIncrement()
	{
		assertTokenTypes("++", JSTokenType.PLUS_PLUS);
	}

	@Test
	public void testAddAssign()
	{
		assertTokenTypes("+=", JSTokenType.PLUS_EQUAL);
	}

	@Test
	public void testAdd()
	{
		assertTokenTypes("+", JSTokenType.PLUS);
	}

	@Test
	public void testExclusiveOrAssign()
	{
		assertTokenTypes("^=", JSTokenType.CARET_EQUAL);
	}

	@Test
	public void testExclusiveOr()
	{
		assertTokenTypes("^", JSTokenType.CARET);
	}

	@Test
	public void testQuestion()
	{
		assertTokenTypes("?", JSTokenType.QUESTION);
	}

	@Test
	public void testBitwiseNot()
	{
		assertTokenTypes("~", JSTokenType.TILDE);
	}

	@Test
	public void testSemicolon()
	{
		assertTokenTypes(";", JSTokenType.SEMICOLON);
	}

	@Test
	public void testLeftParenthesis()
	{
		assertTokenTypes("(", JSTokenType.LPAREN);
	}

	@Test
	public void testRightParenthesis()
	{
		assertTokenTypes(")", JSTokenType.RPAREN);
	}

	@Test
	public void testLeftBracket()
	{
		assertTokenTypes("[", JSTokenType.LBRACKET);
	}

	@Test
	public void testRightBracket()
	{
		assertTokenTypes("]", JSTokenType.RBRACKET);
	}

	@Test
	public void testLeftCurly()
	{
		assertTokenTypes("{", JSTokenType.LCURLY);
	}

	@Test
	public void testRightCurly()
	{
		assertTokenTypes("}", JSTokenType.RCURLY);
	}

	@Test
	public void testComma()
	{
		assertTokenTypes(",", JSTokenType.COMMA);
	}

	@Test
	public void testColon()
	{
		assertTokenTypes(":", JSTokenType.COLON);
	}

	@Test
	public void testPeriod()
	{
		assertTokenTypes(".", JSTokenType.DOT);
	}

	@Test
	public void testGet()
	{
		assertTokenTypes("get", JSTokenType.GET);
	}

	@Test
	public void testSet()
	{
		assertTokenTypes("set", JSTokenType.SET);
	}

	@Test
	public void testRegex()
	{
		assertTokenTypes("/^abc/", JSTokenType.REGEX);
		assertTokenTypes("/^abc/img", JSTokenType.REGEX);
	}

	/**
	 * APSTUD-4647
	 */
	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testNotRegex()
	{
		assertTokenTypes("/* squelch */", JSTokenType.MULTILINE_COMMENT);
	}

	@Test
	public void testInteger()
	{
		assertTokenTypes("10", JSTokenType.NUMBER);
	}

	@Test
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

	@Test
	public void testFloat()
	{
		assertTokenTypes("1.", JSTokenType.NUMBER);
		assertTokenTypes(".9", JSTokenType.NUMBER);
		assertTokenTypes("1.9", JSTokenType.NUMBER);
	}

	@Test
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

	@Test
	public void testConst()
	{
		assertTokenTypes("const", JSTokenType.CONST);
	}

	@Test
	public void testLet()
	{
		assertTokenTypes("let", JSTokenType.LET);
	}

	@Test
	public void testAwait()
	{
		assertTokenTypes("await", JSTokenType.AWAIT);
	}

	@Test
	public void testClass()
	{
		assertTokenTypes("class", JSTokenType.CLASS);
	}

	@Test
	public void testExtends()
	{
		assertTokenTypes("extends", JSTokenType.EXTENDS);
	}

	@Test
	public void testSuper()
	{
		assertTokenTypes("super", JSTokenType.SUPER);
	}

	@Test
	public void testExport()
	{
		assertTokenTypes("export", JSTokenType.EXPORT);
	}

	@Test
	public void testImport()
	{
		assertTokenTypes("import", JSTokenType.IMPORT);
	}

	@Test
	public void testYield()
	{
		assertTokenTypes("yield", JSTokenType.YIELD);
	}

	@Test
	public void testTiMobile() throws IOException
	{
		// This is catching the close brace of an object literal and a way later backtick inside a multiline comment as a "TemplateTail" incorrectly
		assertTokenTypes(getSource(ITestFiles.TIMOBILE_FILES[0]), JSTokenType.MULTILINE_COMMENT, JSTokenType.FUNCTION,
				JSTokenType.IDENTIFIER, JSTokenType.LPAREN, JSTokenType.RPAREN, JSTokenType.LCURLY,
				JSTokenType.MULTILINE_COMMENT, JSTokenType.THIS, JSTokenType.DOT, JSTokenType.IDENTIFIER, JSTokenType.EQUAL, JSTokenType.LCURLY, JSTokenType.RCURLY);
	}

	protected String getSource(String resourceName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID), new Path(resourceName),
				false);
		return IOUtil.read(stream);
	}
}
