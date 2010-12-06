/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import java.io.IOException;

import junit.framework.TestCase;
import beaver.Symbol;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;
import com.aptana.editor.js.sdoc.parsing.SDocScanner;

public class SDocScannerTests extends TestCase
{
	private SDocScanner _scanner;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		this._scanner = new SDocScanner();
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
	 * lexemeTypeTests
	 * 
	 * @param source
	 * @param types
	 * @throws Exception 
	 * @throws IOException 
	 */
	protected void lexemeTypeTests(String source, SDocTokenType... types)
	{
		this._scanner.setSource(source);

		for (int i = 0; i < types.length; i++)
		{
			SDocTokenType type = types[i];
			try
			{
				Symbol symbol = this._scanner.nextToken();
				
				assertEquals(symbol.value + " at index " + i, type.getIndex(), symbol.getId());
			}
			catch (IOException e)
			{
				fail(e.getMessage());
			}
			catch (Exception e)
			{
				fail(e.getMessage());
			}
		}
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
	 * testNoTypes
	 */
	public void testNoTypes()
	{
		String source = "{}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testSimpleType
	 */
	public void testSimpleType()
	{
		String source = "{Number}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testGenericArrayType
	 */
	public void testGenericArrayType()
	{
		String source = "{Array<String>}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.ARRAY,
			SDocTokenType.LESS_THAN,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.GREATER_THAN,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testFuntionType
	 */
	public void testFuntionType()
	{
		String source = "{Function(String)->Boolean}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.FUNCTION,
			SDocTokenType.LPAREN,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RPAREN,
			SDocTokenType.ARROW,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testFuntionType2
	 */
	public void testFuntionType2()
	{
		String source = "{Function(String):Boolean}";
		
		lexemeTypeTests(
			source,
			SDocTokenType.LCURLY,
			SDocTokenType.FUNCTION,
			SDocTokenType.LPAREN,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RPAREN,
			SDocTokenType.COLON,
			SDocTokenType.IDENTIFIER,
			SDocTokenType.RCURLY
		);
	}
	
	/**
	 * testSampleBlock
	 */
	public void testSampleBlock()
	{
		String source =
			"/**\n" +
			" * This is a sample block\n" +
			" *\n" +
			" * @param {Number} count\n" +
			" *   The number of times to do something\n" +
			" */";
		
		lexemeTypeTests(
			source,
			SDocTokenType.START_DOCUMENTATION,	// /**
			SDocTokenType.TEXT,					// This
			SDocTokenType.TEXT,					// is
			SDocTokenType.TEXT,					// a
			SDocTokenType.TEXT,					// sample
			SDocTokenType.TEXT,					// block
			SDocTokenType.PARAM,				// @param
			SDocTokenType.LCURLY,				// {
			SDocTokenType.IDENTIFIER,			// Number
			SDocTokenType.RCURLY,				// }
			SDocTokenType.TEXT,					// count
			SDocTokenType.TEXT,					// The
			SDocTokenType.TEXT,					// number
			SDocTokenType.TEXT,					// of
			SDocTokenType.TEXT,					// times
			SDocTokenType.TEXT,					// to
			SDocTokenType.TEXT,					// do
			SDocTokenType.TEXT,					// something
			SDocTokenType.END_DOCUMENTATION		// */
		);
	}
}
