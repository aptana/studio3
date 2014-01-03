/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import org.junit.Test;

/**
 * @author Kevin Lindsey
 */
public class SimpleItemsTest extends OutlineItemTestCase
{

	@Test
	public void testGlobalVarArray()
	{
		testItem("var x = [1, 2, 3];", "/outline/array-literal", "x");
	}

	@Test
	public void testGlobalVarBoolean()
	{
		testItem("var x = true;", "/outline/boolean", "x");
	}

	@Test
	public void testGlobalVarFunction()
	{
		testItem("var x = function() {};", "/outline/function", "x()");
	}

	@Test
	public void testGlobalVarNull()
	{
		testItem("var x = null;", "/outline/null", "x");
	}

	@Test
	public void testGlobalVarNumber()
	{
		testItem("var x = 10;", "/outline/number", "x");
	}

	@Test
	public void testGlobalVarObject()
	{
		testItem("var x = {};", "/outline/object-literal", "x");
	}

	@Test
	public void testGlobalVarRegex()
	{
		testItem("var x = /abc/;", "/outline/regex", "x");
	}

	@Test
	public void testGlobalVarString()
	{
		testItem("var x = \"10\";", "/outline/string", "x");
	}

	@Test
	public void testGlobalFunctionDeclaration()
	{
		testItem("function abc() {}", "/outline/function", "abc()");
	}

	@Test
	public void testGlobalAssignObject()
	{
		testItem("abc = {};", "/outline/object-literal", "abc");
	}

	@Test
	public void testGlobalAssignInvocation()
	{
		testItem("abc = Object();", "/outline/property", "abc");
	}

	@Test
	public void testGlobalAssignDottedInvocation()
	{
		testItem("abc = a.b.c.d();", "/outline/property", "abc");
	}

	@Test
	public void testReturnObjectInFunction()
	{
		String source = "xyz = function() { return { abc: true }; }";

		testItem(source, "/outline/function", "xyz()", 1);
		testItem(source, "/outline/function/boolean", "abc");
	}

	@Test
	public void testFunctionLiteralInArguments()
	{
		testItem("hello(function() {});", "/outline/function", "hello(@0:<function>)");
	}
}
