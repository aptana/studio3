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
public class BlockItemsTest extends OutlineItemTestCase
{

	@Test
	public void testVarArrayInFunction()
	{
		testItem("function xyz() { var abc = []; }", "/outline/function/array-literal", "abc");
	}

	@Test
	public void testVarBooleanInFunction()
	{
		testItem("function xyz() { var abc = true; }", "/outline/function/boolean", "abc");
	}

	@Test
	public void testVarFunctionInFunction()
	{
		testItem("function xyz() { var abc = function() {}; }", "/outline/function/function", "abc()");
	}

	@Test
	public void testVarNullInFunction()
	{
		testItem("function xyz() { var abc = null; }", "/outline/function/null", "abc");
	}

	@Test
	public void testVarNumberInFunction()
	{
		testItem("function xyz() { var abc = 10; }", "/outline/function/number", "abc");
	}

	@Test
	public void testVarObjectInFunction()
	{
		testItem("function xyz() { var abc = {}; }", "/outline/function/object-literal", "abc");
	}

	@Test
	public void testVarRegexInFunction()
	{
		testItem("function xyz() { var abc = /abc/; }", "/outline/function/regex", "abc");
	}

	@Test
	public void testVarStringInFunction()
	{
		testItem("function xyz() { var abc = \"abc\"; }", "/outline/function/string", "abc");
	}

	@Test
	public void testFunctionInFunction()
	{
		testItem("function xyz() { function abc() {} }", "/outline/function/function", "abc()");
	}

	@Test
	public void testGlobalAssignFunction()
	{
		testItem("abc = function() {};", "/outline/function", "abc()");
	}

	@Test
	public void testAssignFunctionInFunction()
	{
		testItem("function xyz() { abc = function() {}; }", "/outline/function/function", "abc()");
	}

	@Test
	public void testAssignObjectInFunction()
	{
		testItem("function xyz() { abc = {}; }", "/outline/function/object-literal", "abc");
	}

	@Test
	public void testAssignInvocationInFunction()
	{
		testItem("function xyz() { abc = Object(); }", "/outline/function/property", "abc");
	}

	@Test
	public void testAssignDottedInvocationInFunction()
	{
		testItem("function xyz() { abc = a.b.c.d(); }", "/outline/function/property", "abc");
	}

	@Test
	public void testReturnObjectInFunction()
	{
		String source = "function xyz() { return { abc: true }; }";

		testItem(source, "/outline/function", "xyz()", 1);
		testItem(source, "/outline/function/boolean", "abc");
	}

	@Test
	public void testSelfInvokingFunctionLiteral()
	{
		testItem("(function() { var x = 10; })()", "/outline/number", "x");
	}

	@Test
	public void testVarArrayInIf()
	{
		testItem("if (true) { var abc = []; }", "/outline/array-literal", "abc");
	}

	@Test
	public void testVarBooleanInIf()
	{
		testItem("if (true) { var abc = true; }", "/outline/boolean", "abc");
	}

	@Test
	public void testVarFunctionInIf()
	{
		testItem("if (true) { var abc = function() {}; }", "/outline/function", "abc()");
	}

	@Test
	public void testVarNullInIf()
	{
		testItem("if (true) { var abc = null; }", "/outline/null", "abc");
	}

	@Test
	public void testVarNumberInIf()
	{
		testItem("if (true) { var abc = 10; }", "/outline/number", "abc");
	}

	@Test
	public void testVarObjectInIf()
	{
		testItem("if (true) { var abc = {}; }", "/outline/object-literal", "abc");
	}

	@Test
	public void testVarRegexInIf()
	{
		testItem("if (true) { var abc = /abc/; }", "/outline/regex", "abc");
	}

	@Test
	public void testVarStringInIf()
	{
		testItem("if (true) { var abc = \"abc\"; }", "/outline/string", "abc");
	}

	@Test
	public void testFunctionInIf()
	{
		testItem("if (true) { function abc() {} }", "/outline/function", "abc()");
	}

	@Test
	public void testAssignFunctionInIf()
	{
		testItem("if (true) { abc = function() {}; }", "/outline/function", "abc()");
	}

	@Test
	public void testAssignObjectInIf()
	{
		testItem("if (true) { abc = {}; }", "/outline/object-literal", "abc");
	}

	@Test
	public void testAssignInvocationInIf()
	{
		testItem("if (true) { abc = Object(); }", "/outline/property", "abc");
	}

	@Test
	public void testAssignDottedInvocationInIf()
	{
		testItem("if (true) { abc = a.b.c.d(); }", "/outline/property", "abc");
	}

	@Test
	public void testVarArrayInIfElse()
	{
		testItem("if (true) { } else { var abc = []; }", "/outline/array-literal", "abc");
	}

	@Test
	public void testVarBooleanInIfElse()
	{
		testItem("if (true) { } else { var abc = true; }", "/outline/boolean", "abc");
	}

	@Test
	public void testVarFunctionInIfElse()
	{
		testItem("if (true) { } else { var abc = function() {}; }", "/outline/function", "abc()");
	}

	@Test
	public void testVarNullInIfElse()
	{
		testItem("if (true) { } else { var abc = null; }", "/outline/null", "abc");
	}

	@Test
	public void testVarNumberInIfElse()
	{
		testItem("if (true) { } else { var abc = 10; }", "/outline/number", "abc");
	}

	@Test
	public void testVarObjectInIfElse()
	{
		testItem("if (true) { } else { var abc = {}; }", "/outline/object-literal", "abc");
	}

	@Test
	public void testVarRegexInIfElse()
	{
		testItem("if (true) { } else { var abc = /abc/; }", "/outline/regex", "abc");
	}

	@Test
	public void testVarStringInIfElse()
	{
		testItem("if (true) { } else { var abc = \"abc\"; }", "/outline/string", "abc");
	}

	@Test
	public void testFunctionInIfElse()
	{
		testItem("if (true) { } else { function abc() {} }", "/outline/function", "abc()");
	}

	@Test
	public void testAssignFunctionInIfElse()
	{
		testItem("if (true) { } else { abc = function() {}; }", "/outline/function", "abc()");
	}

	@Test
	public void testAssignObjectInIfElse()
	{
		testItem("if (true) { } else { abc = {}; }", "/outline/object-literal", "abc");
	}

	@Test
	public void testAssignInvocationInIfElse()
	{
		testItem("if (true) { } else { abc = Object(); }", "/outline/property", "abc");
	}

	@Test
	public void testAssignDottedInvocationInIfElse()
	{
		testItem("if (true) { } else { abc = a.b.c.d(); }", "/outline/property", "abc");
	}

	@Test
	public void testVarArrayInTry()
	{
		testItem("try { var abc = []; } catch(e) {}", "/outline/array-literal", "abc");
	}

	@Test
	public void testVarBooleanInTry()
	{
		testItem("try { var abc = true; } catch(e) {}", "/outline/boolean", "abc");
	}

	@Test
	public void testVarFunctionInTry()
	{
		testItem("try { var abc = function() {}; } catch(e) {}", "/outline/function", "abc()");
	}

	@Test
	public void testVarNullInTry()
	{
		testItem("try { var abc = null; } catch(e) {}", "/outline/null", "abc");
	}

	@Test
	public void testVarNumberInTry()
	{
		testItem("try { var abc = 10; } catch(e) {}", "/outline/number", "abc");
	}

	@Test
	public void testVarObjectInTry()
	{
		testItem("try { var abc = {}; } catch(e) {}", "/outline/object-literal", "abc");
	}

	@Test
	public void testVarRegexInTry()
	{
		testItem("try { var abc = /abc/; } catch(e) {}", "/outline/regex", "abc");
	}

	@Test
	public void testVarStringInTry()
	{
		testItem("try { var abc = \"abc\"; } catch(e) {}", "/outline/string", "abc");
	}

	@Test
	public void testFunctionInTry()
	{
		testItem("try { function abc() {} } catch(e) {}", "/outline/function", "abc()");
	}

	@Test
	public void testAssignFunctionInTry()
	{
		testItem("try { abc = function() {}; } catch(e) {}", "/outline/function", "abc()");
	}

	@Test
	public void testAssignObjectInTry()
	{
		testItem("try { abc = {}; } catch(e) {}", "/outline/object-literal", "abc");
	}

	@Test
	public void testAssignInvocationInTry()
	{
		testItem("try { abc = Object(); } catch(e) {}", "/outline/property", "abc");
	}

	@Test
	public void testAssignDottedInvocationInTry()
	{
		testItem("try { abc = a.b.c.d(); } catch(e) {}", "/outline/property", "abc");
	}

	@Test
	public void testVarArrayInTryCatch()
	{
		testItem("try { } catch(e) { var abc = []; }", "/outline/array-literal", "abc");
	}

	@Test
	public void testVarBooleanInTryCatch()
	{
		testItem("try { } catch(e) { var abc = true; }", "/outline/boolean", "abc");
	}

	@Test
	public void testVarFunctionInTryCatch()
	{
		testItem("try { } catch(e) { var abc = function() {}; }", "/outline/function", "abc()");
	}

	@Test
	public void testVarNullInTryCatch()
	{
		testItem("try { } catch(e) { var abc = null; }", "/outline/null", "abc");
	}

	@Test
	public void testVarNumberInTryCatch()
	{
		testItem("try { } catch(e) { var abc = 10; }", "/outline/number", "abc");
	}

	@Test
	public void testVarObjectInTryCatch()
	{
		testItem("try { } catch(e) { var abc = {}; }", "/outline/object-literal", "abc");
	}

	@Test
	public void testVarRegexInTryCatch()
	{
		testItem("try { } catch(e) { var abc = /abc/; }", "/outline/regex", "abc");
	}

	@Test
	public void testVarStringInTryCatch()
	{
		testItem("try { } catch(e) { var abc = \"abc\"; }", "/outline/string", "abc");
	}

	@Test
	public void testFunctionInTryCatch()
	{
		testItem("try { } catch(e) { function abc() {} }", "/outline/function", "abc()");
	}

	@Test
	public void testAssignFunctionInTryCatch()
	{
		testItem("try { } catch(e) { abc = function() {}; }", "/outline/function", "abc()");
	}

	@Test
	public void testAssignObjectInTryCatch()
	{
		testItem("try { } catch(e) { abc = {}; }", "/outline/object-literal", "abc");
	}

	@Test
	public void testAssignInvocationInTryCatch()
	{
		testItem("try { } catch(e) { abc = Object(); }", "/outline/property", "abc");
	}

	@Test
	public void testAssignDottedInvocationInTryCatch()
	{
		testItem("try { } catch(e) { abc = a.b.c.d(); }", "/outline/property", "abc");
	}
}
