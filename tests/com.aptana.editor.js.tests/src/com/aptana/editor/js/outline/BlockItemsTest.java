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
package com.aptana.editor.js.outline;

/**
 * @author Kevin Lindsey
 */
public class BlockItemsTest extends BaseOutlineItemTest
{

	public void testVarArrayInFunction()
	{
		testItem("function xyz() { var abc = []; }", "/outline/function/array-literal", "abc");
	}

	public void testVarBooleanInFunction()
	{
		testItem("function xyz() { var abc = true; }", "/outline/function/boolean", "abc");
	}

	public void testVarFunctionInFunction()
	{
		testItem("function xyz() { var abc = function() {}; }", "/outline/function/function", "abc()");
	}

	public void testVarNullInFunction()
	{
		testItem("function xyz() { var abc = null; }", "/outline/function/null", "abc");
	}

	public void testVarNumberInFunction()
	{
		testItem("function xyz() { var abc = 10; }", "/outline/function/number", "abc");
	}

	public void testVarObjectInFunction()
	{
		testItem("function xyz() { var abc = {}; }", "/outline/function/object-literal", "abc");
	}

	public void testVarRegexInFunction()
	{
		testItem("function xyz() { var abc = /abc/; }", "/outline/function/regex", "abc");
	}

	public void testVarStringInFunction()
	{
		testItem("function xyz() { var abc = \"abc\"; }", "/outline/function/string", "abc");
	}

	public void testFunctionInFunction()
	{
		testItem("function xyz() { function abc() {} }", "/outline/function/function", "abc()");
	}

	public void testGlobalAssignFunction()
	{
		testItem("abc = function() {};", "/outline/function", "abc()");
	}

	public void testAssignFunctionInFunction()
	{
		testItem("function xyz() { abc = function() {}; }", "/outline/function/function", "abc()");
	}

	public void testAssignObjectInFunction()
	{
		testItem("function xyz() { abc = {}; }", "/outline/function/object-literal", "abc");
	}

	public void testAssignInvocationInFunction()
	{
		testItem("function xyz() { abc = Object(); }", "/outline/function/property", "abc");
	}

	public void testAssignDottedInvocationInFunction()
	{
		testItem("function xyz() { abc = a.b.c.d(); }", "/outline/function/property", "abc");
	}

	public void testReturnObjectInFunction()
	{
		String source = "function xyz() { return { abc: true }; }";

		testItem(source, "/outline/function", "xyz()", 1);
		testItem(source, "/outline/function/boolean", "abc");
	}

	public void testSelfInvokingFunctionLiteral()
	{
		testItem("(function() { var x = 10; })()", "/outline/number", "x");
	}

	public void testVarArrayInIf()
	{
		testItem("if (true) { var abc = []; }", "/outline/array-literal", "abc");
	}

	public void testVarBooleanInIf()
	{
		testItem("if (true) { var abc = true; }", "/outline/boolean", "abc");
	}

	public void testVarFunctionInIf()
	{
		testItem("if (true) { var abc = function() {}; }", "/outline/function", "abc()");
	}

	public void testVarNullInIf()
	{
		testItem("if (true) { var abc = null; }", "/outline/null", "abc");
	}

	public void testVarNumberInIf()
	{
		testItem("if (true) { var abc = 10; }", "/outline/number", "abc");
	}

	public void testVarObjectInIf()
	{
		testItem("if (true) { var abc = {}; }", "/outline/object-literal", "abc");
	}

	public void testVarRegexInIf()
	{
		testItem("if (true) { var abc = /abc/; }", "/outline/regex", "abc");
	}

	public void testVarStringInIf()
	{
		testItem("if (true) { var abc = \"abc\"; }", "/outline/string", "abc");
	}

	public void testFunctionInIf()
	{
		testItem("if (true) { function abc() {} }", "/outline/function", "abc()");
	}

	public void testAssignFunctionInIf()
	{
		testItem("if (true) { abc = function() {}; }", "/outline/function", "abc()");
	}

	public void testAssignObjectInIf()
	{
		testItem("if (true) { abc = {}; }", "/outline/object-literal", "abc");
	}

	public void testAssignInvocationInIf()
	{
		testItem("if (true) { abc = Object(); }", "/outline/property", "abc");
	}

	public void testAssignDottedInvocationInIf()
	{
		testItem("if (true) { abc = a.b.c.d(); }", "/outline/property", "abc");
	}

	public void testVarArrayInIfElse()
	{
		testItem("if (true) { } else { var abc = []; }", "/outline/array-literal", "abc");
	}

	public void testVarBooleanInIfElse()
	{
		testItem("if (true) { } else { var abc = true; }", "/outline/boolean", "abc");
	}

	public void testVarFunctionInIfElse()
	{
		testItem("if (true) { } else { var abc = function() {}; }", "/outline/function", "abc()");
	}

	public void testVarNullInIfElse()
	{
		testItem("if (true) { } else { var abc = null; }", "/outline/null", "abc");
	}

	public void testVarNumberInIfElse()
	{
		testItem("if (true) { } else { var abc = 10; }", "/outline/number", "abc");
	}

	public void testVarObjectInIfElse()
	{
		testItem("if (true) { } else { var abc = {}; }", "/outline/object-literal", "abc");
	}

	public void testVarRegexInIfElse()
	{
		testItem("if (true) { } else { var abc = /abc/; }", "/outline/regex", "abc");
	}

	public void testVarStringInIfElse()
	{
		testItem("if (true) { } else { var abc = \"abc\"; }", "/outline/string", "abc");
	}

	public void testFunctionInIfElse()
	{
		testItem("if (true) { } else { function abc() {} }", "/outline/function", "abc()");
	}

	public void testAssignFunctionInIfElse()
	{
		testItem("if (true) { } else { abc = function() {}; }", "/outline/function", "abc()");
	}

	public void testAssignObjectInIfElse()
	{
		testItem("if (true) { } else { abc = {}; }", "/outline/object-literal", "abc");
	}

	public void testAssignInvocationInIfElse()
	{
		testItem("if (true) { } else { abc = Object(); }", "/outline/property", "abc");
	}

	public void testAssignDottedInvocationInIfElse()
	{
		testItem("if (true) { } else { abc = a.b.c.d(); }", "/outline/property", "abc");
	}

	public void testVarArrayInTry()
	{
		testItem("try { var abc = []; } catch(e) {}", "/outline/array-literal", "abc");
	}

	public void testVarBooleanInTry()
	{
		testItem("try { var abc = true; } catch(e) {}", "/outline/boolean", "abc");
	}

	public void testVarFunctionInTry()
	{
		testItem("try { var abc = function() {}; } catch(e) {}", "/outline/function", "abc()");
	}

	public void testVarNullInTry()
	{
		testItem("try { var abc = null; } catch(e) {}", "/outline/null", "abc");
	}

	public void testVarNumberInTry()
	{
		testItem("try { var abc = 10; } catch(e) {}", "/outline/number", "abc");
	}

	public void testVarObjectInTry()
	{
		testItem("try { var abc = {}; } catch(e) {}", "/outline/object-literal", "abc");
	}

	public void testVarRegexInTry()
	{
		testItem("try { var abc = /abc/; } catch(e) {}", "/outline/regex", "abc");
	}

	public void testVarStringInTry()
	{
		testItem("try { var abc = \"abc\"; } catch(e) {}", "/outline/string", "abc");
	}

	public void testFunctionInTry()
	{
		testItem("try { function abc() {} } catch(e) {}", "/outline/function", "abc()");
	}

	public void testAssignFunctionInTry()
	{
		testItem("try { abc = function() {}; } catch(e) {}", "/outline/function", "abc()");
	}

	public void testAssignObjectInTry()
	{
		testItem("try { abc = {}; } catch(e) {}", "/outline/object-literal", "abc");
	}

	public void testAssignInvocationInTry()
	{
		testItem("try { abc = Object(); } catch(e) {}", "/outline/property", "abc");
	}

	public void testAssignDottedInvocationInTry()
	{
		testItem("try { abc = a.b.c.d(); } catch(e) {}", "/outline/property", "abc");
	}

	public void testVarArrayInTryCatch()
	{
		testItem("try { } catch(e) { var abc = []; }", "/outline/array-literal", "abc");
	}

	public void testVarBooleanInTryCatch()
	{
		testItem("try { } catch(e) { var abc = true; }", "/outline/boolean", "abc");
	}

	public void testVarFunctionInTryCatch()
	{
		testItem("try { } catch(e) { var abc = function() {}; }", "/outline/function", "abc()");
	}

	public void testVarNullInTryCatch()
	{
		testItem("try { } catch(e) { var abc = null; }", "/outline/null", "abc");
	}

	public void testVarNumberInTryCatch()
	{
		testItem("try { } catch(e) { var abc = 10; }", "/outline/number", "abc");
	}

	public void testVarObjectInTryCatch()
	{
		testItem("try { } catch(e) { var abc = {}; }", "/outline/object-literal", "abc");
	}

	public void testVarRegexInTryCatch()
	{
		testItem("try { } catch(e) { var abc = /abc/; }", "/outline/regex", "abc");
	}

	public void testVarStringInTryCatch()
	{
		testItem("try { } catch(e) { var abc = \"abc\"; }", "/outline/string", "abc");
	}

	public void testFunctionInTryCatch()
	{
		testItem("try { } catch(e) { function abc() {} }", "/outline/function", "abc()");
	}

	public void testAssignFunctionInTryCatch()
	{
		testItem("try { } catch(e) { abc = function() {}; }", "/outline/function", "abc()");
	}

	public void testAssignObjectInTryCatch()
	{
		testItem("try { } catch(e) { abc = {}; }", "/outline/object-literal", "abc");
	}

	public void testAssignInvocationInTryCatch()
	{
		testItem("try { } catch(e) { abc = Object(); }", "/outline/property", "abc");
	}

	public void testAssignDottedInvocationInTryCatch()
	{
		testItem("try { } catch(e) { abc = a.b.c.d(); }", "/outline/property", "abc");
	}
}
