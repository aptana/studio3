/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

/**
 * @author Kevin Lindsey
 */
public class SimpleItemsTest extends BaseOutlineItemTest
{

	public void testGlobalVarArray()
	{
		testItem("var x = [1, 2, 3];", "/outline/array-literal", "x");
	}

	public void testGlobalVarBoolean()
	{
		testItem("var x = true;", "/outline/boolean", "x");
	}

	public void testGlobalVarFunction()
	{
		testItem("var x = function() {};", "/outline/function", "x()");
	}

	public void testGlobalVarNull()
	{
		testItem("var x = null;", "/outline/null", "x");
	}

	public void testGlobalVarNumber()
	{
		testItem("var x = 10;", "/outline/number", "x");
	}

	public void testGlobalVarObject()
	{
		testItem("var x = {};", "/outline/object-literal", "x");
	}

	public void testGlobalVarRegex()
	{
		testItem("var x = /abc/;", "/outline/regex", "x");
	}

	public void testGlobalVarString()
	{
		testItem("var x = \"10\";", "/outline/string", "x");
	}

	public void testGlobalFunctionDeclaration()
	{
		testItem("function abc() {}", "/outline/function", "abc()");
	}

	public void testGlobalAssignObject()
	{
		testItem("abc = {};", "/outline/object-literal", "abc");
	}

	public void testGlobalAssignInvocation()
	{
		testItem("abc = Object();", "/outline/property", "abc");
	}

	public void testGlobalAssignDottedInvocation()
	{
		testItem("abc = a.b.c.d();", "/outline/property", "abc");
	}

	public void testReturnObjectInFunction()
	{
		String source = "xyz = function() { return { abc: true }; }";

		testItem(source, "/outline/function", "xyz()", 1);
		testItem(source, "/outline/function/boolean", "abc");
	}
}
