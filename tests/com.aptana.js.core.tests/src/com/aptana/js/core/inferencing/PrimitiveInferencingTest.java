/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import org.junit.Test;

public class PrimitiveInferencingTest extends InferencingTestsBase
{
	/**
	 * testTrueVar
	 */
	@Test
	public void testTrueVar()
	{
		this.varTypeTests("var trueVar = true;", "trueVar", "Boolean");
	}

	/**
	 * testFalseVar
	 */
	@Test
	public void testFalseVar()
	{
		this.varTypeTests("var falseVar = false;", "falseVar", "Boolean");
	}

	/**
	 * testIntVar
	 */
	@Test
	public void testIntVar()
	{
		this.varTypeTests("var intVar = 10;", "intVar", "Number");
	}

	/**
	 * testHexVar
	 */
	@Test
	public void testHexVar()
	{
		this.varTypeTests("var hexVar = 0x10;", "hexVar", "Number");
	}

	/**
	 * testFloatVar
	 */
	@Test
	public void testFloatVar()
	{
		this.varTypeTests("var floatVar = 10.01;", "floatVar", "Number");
	}

	/**
	 * testArrayLiteralVar
	 */
	@Test
	public void testArrayLiteralVar()
	{
		this.varTypeTests("var arrayLiteralVar = [];", "arrayLiteralVar", "Array");
	}

	/**
	 * testObjectLiteralVar
	 */
	@Test
	public void testObjectLiteralVar()
	{
		this.varTypeTests("var objectLiteralVar = {};", "objectLiteralVar", "Object");
	}

	/**
	 * testRegExpLiteralVar
	 */
	@Test
	public void testRegExpLiteralVar()
	{
		this.varTypeTests("var regExpLiteralVar = /abc/i;", "regExpLiteralVar", "RegExp");
	}

	/**
	 * testFunctionLiteralVar
	 */
	@Test
	public void testFunctionLiteralVar()
	{
		this.varTypeTests("var functionLiteralVar = function() {};", "functionLiteralVar", "Function");
	}

	/**
	 * testSingleQuotedStringVar
	 */
	@Test
	public void testSingleQuotedStringVar()
	{
		this.varTypeTests("var singleQuotedStringVar = 'abc';", "singleQuotedStringVar", "String");
	}

	/**
	 * testDoubleQuotedStringVar
	 */
	@Test
	public void testDoubleQuotedStringVar()
	{
		this.varTypeTests("var doubleQuotedStringVar = \"abc\";", "doubleQuotedStringVar", "String");
	}
}
