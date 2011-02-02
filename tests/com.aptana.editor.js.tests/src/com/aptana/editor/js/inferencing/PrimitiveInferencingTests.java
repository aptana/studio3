/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

public class PrimitiveInferencingTests extends InferencingTestsBase
{
	/**
	 * testTrueVar
	 */
	public void testTrueVar()
	{
		this.varTypeTests("var trueVar = true;", "trueVar", "Boolean");
	}

	/**
	 * testFalseVar
	 */
	public void testFalseVar()
	{
		this.varTypeTests("var falseVar = false;", "falseVar", "Boolean");
	}

	/**
	 * testIntVar
	 */
	public void testIntVar()
	{
		this.varTypeTests("var intVar = 10;", "intVar", "Number");
	}

	/**
	 * testHexVar
	 */
	public void testHexVar()
	{
		this.varTypeTests("var hexVar = 0x10;", "hexVar", "Number");
	}

	/**
	 * testFloatVar
	 */
	public void testFloatVar()
	{
		this.varTypeTests("var floatVar = 10.01;", "floatVar", "Number");
	}

	/**
	 * testArrayLiteralVar
	 */
	public void testArrayLiteralVar()
	{
		this.varTypeTests("var arrayLiteralVar = [];", "arrayLiteralVar", "Array");
	}

	/**
	 * testObjectLiteralVar
	 */
	public void testObjectLiteralVar()
	{
		this.varTypeTests("var objectLiteralVar = {};", "objectLiteralVar", "Object");
	}

	/**
	 * testRegExpLiteralVar
	 */
	public void testRegExpLiteralVar()
	{
		this.varTypeTests("var regExpLiteralVar = /abc/i;", "regExpLiteralVar", "RegExp");
	}

	/**
	 * testFunctionLiteralVar
	 */
	public void testFunctionLiteralVar()
	{
		this.varTypeTests("var functionLiteralVar = function() {};", "functionLiteralVar", "Function");
	}

	/**
	 * testSingleQuotedStringVar
	 */
	public void testSingleQuotedStringVar()
	{
		this.varTypeTests("var singleQuotedStringVar = 'abc';", "singleQuotedStringVar", "String");
	}

	/**
	 * testDoubleQuotedStringVar
	 */
	public void testDoubleQuotedStringVar()
	{
		this.varTypeTests("var doubleQuotedStringVar = \"abc\";", "doubleQuotedStringVar", "String");
	}
}
