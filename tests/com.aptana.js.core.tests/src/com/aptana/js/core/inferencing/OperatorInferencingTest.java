/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.internal.core.inferencing.JSSymbolTypeInferrer;

public class OperatorInferencingTest extends InferencingTestsBase
{
	/**
	 * assignmentTypeTests
	 * 
	 * @param source
	 * @param types
	 */
	public void assignmentTypeTests(String source, String... types)
	{
		JSScope globals = this.getGlobals(source);
		assertNotNull(globals);

		JSPropertyCollection object = globals.getSymbol("abc");
		assertNotNull(object);

		// NOTE: getting property elements of all symbols in the specified scope
		// as a side-effect caches each JSObject's type values.
		JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(globals, null, null, new JSIndexQueryHelper(getIndex()));
		symbolInferrer.getScopeProperties();

		List<String> symbolTypes = object.getTypes();
		assertNotNull(symbolTypes);
		assertNotNull(types);
		assertEquals(types.length, symbolTypes.size());

		for (String type : types)
		{
			assertTrue(symbolTypes.contains(type));
		}
	}

	/**
	 * testAddNumbersVar
	 */
	@Test public void testAddNumbersVar()
	{
		this.varTypeTests("var addNumbersVar = 4 + 5;", "addNumbersVar", "Number");
	}

	/**
	 * testAddStringsVar
	 */
	@Test public void testAddStringsVar()
	{
		this.varTypeTests("var addStringsVar = 'ab' + 'cd';", "addStringsVar", "String");
	}

	/**
	 * testAddMixedVar
	 */
	@Test public void testAddMixedVar()
	{
		this.varTypeTests("var addMixedVar = 'ab' + 10;", "addMixedVar", "String");
	}

	/**
	 * testSubtractVar
	 */
	@Test public void testSubtractVar()
	{
		this.varTypeTests("var subVar = a - b;", "subVar", "Number");
	}

	/* shift operators */

	/**
	 * testShiftLeftVar
	 */
	@Test public void testShiftLeftVar()
	{
		this.varTypeTests("var shiftLeftVar = a << b;", "shiftLeftVar", "Number");
	}

	/**
	 * testShiftRightVar
	 */
	@Test public void testShiftRightVar()
	{
		this.varTypeTests("var shiftRightVar = a >> b;", "shiftRightVar", "Number");
	}

	/**
	 * testArithmeticShiftRightVar
	 */
	@Test public void testArithmeticShiftRightVar()
	{
		this.varTypeTests("var arithmeticShiftRightVar = a >> b;", "arithmeticShiftRightVar", "Number");
	}

	/* bit operators */

	/**
	 * testBitAndVar
	 */
	@Test public void testBitAndVar()
	{
		this.varTypeTests("var bitAndVar = a & b;", "bitAndVar", "Number");
	}

	/**
	 * testBitXorVar
	 */
	@Test public void testBitXorVar()
	{
		this.varTypeTests("var bitXorVar = a ^ b;", "bitXorVar", "Number");
	}

	/**
	 * testBitOrVar
	 */
	@Test public void testBitOrVar()
	{
		this.varTypeTests("var bitOrVar = a | b;", "bitOrVar", "Number");
	}

	/* multiplicative operators */

	/**
	 * testMultiplyVar
	 */
	@Test public void testMultiplyVar()
	{
		this.varTypeTests("var multiplyVar = a * b;", "multiplyVar", "Number");
	}

	/**
	 * testDivideVar
	 */
	@Test public void testDivideVar()
	{
		this.varTypeTests("var divideVar = a * b;", "divideVar", "Number");
	}

	/**
	 * testModVar
	 */
	@Test public void testModVar()
	{
		this.varTypeTests("var modVar = a % b;", "modVar", "Number");
	}

	/* equality operators */

	/**
	 * testEqualVar
	 */
	@Test public void testEqualVar()
	{
		this.varTypeTests("var equalVar = a == b;", "equalVar", "Boolean");
	}

	/**
	 * testNotEqualVar
	 */
	@Test public void testNotEqualVar()
	{
		this.varTypeTests("var notEqualVar = a != b;", "notEqualVar", "Boolean");
	}

	/**
	 * testInstanceEqualVar
	 */
	@Test public void testInstanceEqualVar()
	{
		this.varTypeTests("var equalVar = a === b;", "equalVar", "Boolean");
	}

	/**
	 * testInstanceNotEqualVar
	 */
	@Test public void testInstanceNotEqualVar()
	{
		this.varTypeTests("var notEqualVar = a !== b;", "notEqualVar", "Boolean");
	}

	/* relational operators */

	/**
	 * testLessThanVar
	 */
	@Test public void testLessThanVar()
	{
		this.varTypeTests("var lessThanVar = a < b;", "lessThanVar", "Boolean");
	}

	/**
	 * testGreaterThanVar
	 */
	@Test public void testGreaterThanVar()
	{
		this.varTypeTests("var greaterThanVar = a > b;", "greaterThanVar", "Boolean");
	}

	/**
	 * testLessThanEqualVar
	 */
	@Test public void testLessThanEqualVar()
	{
		this.varTypeTests("var lessThanEqualVar = a <= b;", "lessThanEqualVar", "Boolean");
	}

	/**
	 * testGreaterThanEqualVar
	 */
	@Test public void testGreaterThanEqualVar()
	{
		this.varTypeTests("var greaterThanEqualVar = a >= b;", "greaterThanEqualVar", "Boolean");
	}

	/**
	 * testInstanceOfVar
	 */
	@Test public void testInstanceOfVar()
	{
		this.varTypeTests("var instanceOfVar = a instanceof b;", "instanceOfVar", "Boolean");
	}

	/**
	 * testInVar
	 */
	@Test public void testInVar()
	{
		this.varTypeTests("var inVar = a in b;", "inVar", "Boolean");
	}

	/* logical operators */

	/**
	 * testLogicalAndVar
	 */
	@Test public void testLogicalAndVar()
	{
		this.varTypeTests("var logicalAndVar = \"\" && 10;", "logicalAndVar", "String", "Number");
	}

	/**
	 * testLogicalOrVar
	 */
	@Test public void testLogicalOrVar()
	{
		this.varTypeTests("var logicalOrVar = \"\" || 10;", "logicalOrVar", "String", "Number");
	}

	/* pre-unary operators */

	/**
	 * testDeleteVar
	 */
	@Test public void testDeleteVar()
	{
		this.varTypeTests("var deleteVar = delete a.b;", "deleteVar", "Boolean");
	}

	/**
	 * testLogicalNotVar
	 */
	@Test public void testLogicalNotVar()
	{
		this.varTypeTests("var logicalNotVar = !b;", "logicalNotVar", "Boolean");
	}

	/**
	 * testNegationVar
	 */
	@Test public void testNegationVar()
	{
		this.varTypeTests("var negationVar = -a;", "negationVar", "Number");
	}

	/**
	 * testPreDecrementVar
	 */
	@Test public void testPreDecrementVar()
	{
		this.varTypeTests("var preDecrementVar = --a;", "preDecrementVar", "Number");
	}

	/**
	 * testPreIncrementVar
	 */
	@Test public void testPreIncrementVar()
	{
		this.varTypeTests("var preIncrementVar = ++a;", "preIncrementVar", "Number");
	}

	/**
	 * testBitNotVar
	 */
	@Test public void testBitNotVar()
	{
		this.varTypeTests("var bitNotVar = ~a;", "bitNotVar", "Number");
	}

	/**
	 * testTypeofVar
	 */
	@Test public void testTypeofVar()
	{
		this.varTypeTests("var typeofVar = typeof a;", "typeofVar", "String");
	}

	/**
	 * testVoidVar
	 */
	@Test public void testVoidVar()
	{
		this.varTypeTests("var voidVar = void a;", "voidVar");
	}

	/* post-unary operators */

	/**
	 * testPostDecrementVar
	 */
	@Test public void testPostDecrementVar()
	{
		this.varTypeTests("var postDecrementVar = a--;", "postDecrementVar", "Number");
	}

	/**
	 * testPostIncrementVar
	 */
	@Test public void testPostIncrementVar()
	{
		this.varTypeTests("var postIncrementVar = a++;", "postIncrementVar", "Number");
	}

	/* trinary operators */

	/**
	 * testConditionalVar
	 */
	@Test public void testConditionalVar()
	{
		this.varTypeTests("var conditionalVar = (a == true) ? 10 : 20;", "conditionalVar", "Number");
	}

	/**
	 * testMixedConditionalVar
	 */
	@Test public void testMixedConditionalVar()
	{
		this.varTypeTests("var mixedConditionalVar = (a == true) ? 10 : '20';", "mixedConditionalVar", "Number", "String");
	}

	/* assignment operators */

	/**
	 * testSimpleAssignment
	 */
	@Test public void testSimpleAssignment()
	{
		this.assignmentTypeTests("abc = 10;", "Number");
	}

	/**
	 * testMultiAssignment
	 */
	@Test public void testMultiAssignment()
	{
		this.assignmentTypeTests("abc = def = 10;", "Number");
	}

	/**
	 * testPlusAssignNumbers
	 */
	@Test public void testPlusAssignNumbers()
	{
		this.assignmentTypeTests("var abc = 10; abc += 20", "Number");
	}

	/**
	 * testPlusAssignStrings
	 */
	@Test public void testPlusAssignStrings()
	{
		this.assignmentTypeTests("var abc = '10'; abc += '20'", "String");
	}

	/**
	 * testPlusAssignMixed
	 */
	@Test public void testPlusAssignMixed()
	{
		this.assignmentTypeTests("var abc = 10; abc += '20'", "Number", "String");
	}

	/**
	 * testPlusAssignMixed2
	 */
	@Test public void testPlusAssignMixed2()
	{
		this.assignmentTypeTests("var abc = '10'; abc += 20", "String", "Number");
	}

	/**
	 * testArithmeticShiftRightAssign
	 */
	@Test public void testArithmeticShiftRightAssign()
	{
		this.assignmentTypeTests("abc >>= b;", "Number");
	}

	/**
	 * testBitwiseAndAssign
	 */
	@Test public void testBitwiseAndAssign()
	{
		this.assignmentTypeTests("abc &= b;", "Number");
	}

	/**
	 * testBitwiseOrAssign
	 */
	@Test public void testBitwiseOrAssign()
	{
		this.assignmentTypeTests("abc |= b;", "Number");
	}

	/**
	 * testBitwiseXorAssign
	 */
	@Test public void testBitwiseXorAssign()
	{
		this.assignmentTypeTests("abc ^= b;", "Number");
	}

	/**
	 * testBitwiseDivideAssign
	 */
	@Test public void testBitwiseDivideAssign()
	{
		this.assignmentTypeTests("abc /= b;", "Number");
	}

	/**
	 * testModAssign
	 */
	@Test public void testModAssign()
	{
		this.assignmentTypeTests("abc %= b;", "Number");
	}

	/**
	 * testMultiplyAssign
	 */
	@Test public void testMultiplyAssign()
	{
		this.assignmentTypeTests("abc *= b;", "Number");
	}

	/**
	 * testShiftLeftAssign
	 */
	@Test public void testShiftLeftAssign()
	{
		this.assignmentTypeTests("abc <<= b;", "Number");
	}

	/**
	 * testShiftRightAssign
	 */
	@Test public void testShiftRightAssign()
	{
		this.assignmentTypeTests("abc >>= b;", "Number");
	}

	/**
	 * testSubtractAssign
	 */
	@Test public void testSubtractAssign()
	{
		this.assignmentTypeTests("abc -= b;", "Number");
	}
}
