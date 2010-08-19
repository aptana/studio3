package com.aptana.editor.js.sdoc.parsing;

import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;
import com.aptana.editor.js.sdoc.parsing.SDocTokenScanner;

public class SDocTokenScannerTests extends SDocScannerTestBase
{
	/* (non-Javadoc)
	 * @see com.aptana.editor.js.SDocScannerTestBase#createScanner()
	 */
	@Override
	protected RuleBasedScanner createScanner()
	{
		return new SDocTokenScanner();
	}

	/**
	 * testNoTypes
	 */
	public void testNoTypes()
	{
		String source = "{}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.TYPES
		);
	}
	
	/**
	 * testTypes
	 */
	public void testTypes()
	{
		String source = "{Number}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.TYPES
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
	 * testCR
	 */
	public void testCR()
	{
		String source = "\r";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testLF
	 */
	public void testLF()
	{
		String source = "\n";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testCRLF
	 */
	public void testCRLF()
	{
		String source = "\r\n";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testSpace
	 */
	public void testSpace()
	{
		String source = " ";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
	
	/**
	 * testTab
	 */
	public void testTab()
	{
		String source = "\t";
		
		lexemeTypeTests(
			source,
			SDocTokenType.WHITESPACE
		);
	}
}
