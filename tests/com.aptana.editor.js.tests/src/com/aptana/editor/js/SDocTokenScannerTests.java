package com.aptana.editor.js;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.js.parsing.SDocTokenScanner;
import com.aptana.editor.js.parsing.lexer.SDocTokenType;

public class SDocTokenScannerTests extends TestCase
{
	private SDocTokenScanner _scanner;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		
		this._scanner = new SDocTokenScanner();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._scanner = null;
		
		super.tearDown();
	}

	/**
	 * lexemeTypeTests
	 * 
	 * @param source
	 * @param types
	 */
	protected void lexemeTypeTests(String source, SDocTokenType... types)
	{
		IDocument document = new Document(source);
		
		this._scanner.setRange(document, 0, source.length());
		
		for (SDocTokenType type : types)
		{
			IToken token = this._scanner.nextToken();
			Object data = token.getData();
			
			assertTrue(data instanceof SDocTokenType);
			assertEquals(type, data);
		}
	}
	
	/**
	 * testLeftParen
	 */
	public void testLeftParen()
	{
		String source = "(";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LPAREN
		);
	}
	
	/**
	 * testRightParen
	 */
	public void testRightParen()
	{
		String source = ")";
		
		lexemeTypeTests(
			source,
			SDocTokenType.RPAREN
		);
	}
	
	/**
	 * testLeftCurly
	 */
	public void testLeftCurly()
	{
		String source = "{";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY
		);
	}
	
	/**
	 * testRightCurly
	 */
	public void testRightCurly()
	{
		String source = "}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testLeftBracket
	 */
	public void testLeftBracket()
	{
		String source = "[";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LBRACKET
		);
	}
	
	/**
	 * testRightBracket
	 */
	public void testRightBracket()
	{
		String source = "]";
		
		lexemeTypeTests(
			source,
			SDocTokenType.RBRACKET
		);
	}
	
	/**
	 * testLessThan
	 */
	public void testLessThan()
	{
		String source = "<";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LESS_THAN
		);
	}
	
	/**
	 * testGreaterThan
	 */
	public void testGreaterThan()
	{
		String source = ">";
		
		lexemeTypeTests(
			source,
			SDocTokenType.GREATER_THAN
		);
	}
	
	/**
	 * testColon
	 */
	public void testColon()
	{
		String source = ":";
		
		lexemeTypeTests(
			source,
			SDocTokenType.COLON
		);
	}
	
	/**
	 * testPound
	 */
	public void testPound()
	{
		String source = "#";
		
		lexemeTypeTests(
			source,
			SDocTokenType.POUND
		);
	}
	
	/**
	 * testComma
	 */
	public void testComma()
	{
		String source = ",";
		
		lexemeTypeTests(
			source,
			SDocTokenType.COMMA
		);
	}
	
	/**
	 * testPipe
	 */
	public void testPipe()
	{
		String source = "|";
		
		lexemeTypeTests(
			source,
			SDocTokenType.PIPE
		);
	}
	
	/**
	 * testStartDocumentation
	 */
	public void testStartDocumentation()
	{
		String source = "/**";
		
		lexemeTypeTests(
			source,
			SDocTokenType.START_DOCUMENTATION
		);
	}
	
	/**
	 * testEndDocumentation
	 */
	public void testEndDocumentation()
	{
		String source = "*/";
		
		lexemeTypeTests(
			source,
			SDocTokenType.END_DOCUMENTATION
		);
	}
	
	/**
	 * testArrow
	 */
	public void testArrow()
	{
		String source = "->";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ARROW
		);
	}
	
	/**
	 * testEllipsis
	 */
	public void testEllipsis()
	{
		String source = "...";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ELLIPSIS
		);
	}
	
	/**
	 * testAdvanced
	 */
	public void testAdvanced()
	{
		String source = "@advanced";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ADVANCED
		);
	}
	
	/**
	 * testAlias
	 */
	public void testAlias()
	{
		String source = "@alias";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ALIAS
		);
	}
	
	/**
	 * testAuthor
	 */
	public void testAuthor()
	{
		String source = "@author";
		
		lexemeTypeTests(
			source,
			SDocTokenType.AUTHOR
		);
	}
	
	/**
	 * testClassDescription
	 */
	public void testClassDescription()
	{
		String source = "@classDescription";
		
		lexemeTypeTests(
			source,
			SDocTokenType.CLASS_DESCRIPTION
		);
	}
	
	/**
	 * testConstructor
	 */
	public void testConstructor()
	{
		String source = "@constructor";
		
		lexemeTypeTests(
			source,
			SDocTokenType.CONSTRUCTOR
		);
	}
	
	/**
	 * testExample
	 */
	public void testExample()
	{
		String source = "@example";
		
		lexemeTypeTests(
			source,
			SDocTokenType.EXAMPLE
		);
	}
	
	/**
	 * testException
	 */
	public void testException()
	{
		String source = "@exception";
		
		lexemeTypeTests(
			source,
			SDocTokenType.EXCEPTION
		);
	}
	
	/**
	 * testExtends
	 */
	public void testExtends()
	{
		String source = "@extends";
		
		lexemeTypeTests(
			source,
			SDocTokenType.EXTENDS
		);
	}
	
	/**
	 * testInternal
	 */
	public void testInternal()
	{
		String source = "@internal";
		
		lexemeTypeTests(
			source,
			SDocTokenType.INTERNAL
		);
	}
	
	/**
	 * testMethod
	 */
	public void testMethod()
	{
		String source = "@method";
		
		lexemeTypeTests(
			source,
			SDocTokenType.METHOD
		);
	}
	
	/**
	 * testNamespace
	 */
	public void testNamespace()
	{
		String source = "@namespace";
		
		lexemeTypeTests(
			source,
			SDocTokenType.NAMESPACE
		);
	}
	
	/**
	 * testOverview
	 */
	public void testOverview()
	{
		String source = "@overview";
		
		lexemeTypeTests(
			source,
			SDocTokenType.OVERVIEW
		);
	}
	
	/**
	 * testParam
	 */
	public void testParam()
	{
		String source = "@param";
		
		lexemeTypeTests(
			source,
			SDocTokenType.PARAM
		);
	}
	
	/**
	 * testPrivate
	 */
	public void testPrivate()
	{
		String source = "@private";
		
		lexemeTypeTests(
			source,
			SDocTokenType.PRIVATE
		);
	}
	
	/**
	 * testProperty
	 */
	public void testProperty()
	{
		String source = "@property";
		
		lexemeTypeTests(
			source,
			SDocTokenType.PROPERTY
		);
	}
	
	/**
	 * testReturn
	 */
	public void testReturn()
	{
		String source = "@return";
		
		lexemeTypeTests(
			source,
			SDocTokenType.RETURN
		);
	}
	
	/**
	 * testSee
	 */
	public void testSee()
	{
		String source = "@see";
		
		lexemeTypeTests(
			source,
			SDocTokenType.SEE
		);
	}
	
	/**
	 * testUserTag
	 */
	public void testUserTag()
	{
		String source = "@myCustomTag";
		
		lexemeTypeTests(
			source,
			SDocTokenType.UNKNOWN
		);
	}
	
	/**
	 * testFunction
	 */
	public void testFunction()
	{
		String source = "function";
		
		lexemeTypeTests(
			source,
			SDocTokenType.FUNCTION
		);
	}
	
	/**
	 * testArray
	 */
	public void testArray()
	{
		String source = "array";
		
		lexemeTypeTests(
			source,
			SDocTokenType.ARRAY
		);
	}
	
	/**
	 * testSimpleIdentifiers
	 */
	public void testSimpleIdentifiers()
	{
		String source = "Number $number _number";
		
		lexemeTypeTests(
			source,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.WHITESPACE,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.WHITESPACE,
			SDocTokenType.IDENTIFIER
		);
	}
	
	/**
	 * testNearIdentifiers
	 */
	public void testNearIdentifiers()
	{
		String source = "functions arrays";
		
		lexemeTypeTests(
			source,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.WHITESPACE,
			SDocTokenType.IDENTIFIER
		);
	}
}
