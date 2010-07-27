package com.aptana.editor.js.contentassist;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.aptana.editor.js.inferencing.JSNodeTypeInferrer;
import com.aptana.editor.js.inferencing.JSPropertyCollection;
import com.aptana.editor.js.inferencing.JSScope;
import com.aptana.editor.js.inferencing.JSSymbolCollector;
import com.aptana.editor.js.inferencing.JSSymbolTypeInferrer;
import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public class InferencingTests extends TestCase
{
	/**
	 * getParseRootNode
	 * 
	 * @param source
	 * @return
	 */
	protected IParseNode getParseRootNode(String source)
	{
		JSParser parser = new JSParser();
		ParseState parseState = new ParseState();

		parseState.setEditState(source, source, 0, 0);
		
		try
		{
			parser.parse(parseState);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		
		return parseState.getParseResult();
	}
	
	/**
	 * getGlobals
	 * 
	 * @param source
	 * @return
	 */
	protected JSScope getGlobals(String source)
	{
		IParseNode root = this.getParseRootNode(source);
		assertTrue(root instanceof JSParseRootNode);
		
		JSSymbolCollector s = new JSSymbolCollector();
		((JSParseRootNode) root).accept(s);
		
		return s.getScope();
	}
	
	/**
	 * getTypes
	 * 
	 * @param node
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, JSNode node)
	{
		JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, null, null);
		
		node.accept(walker);
		
		return walker.getTypes();
	}
	
	/**
	 * getTypes
	 * 
	 * @param nodes
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, List<JSNode> nodes)
	{
		List<String> result = new LinkedList<String>();
		
		for (IParseNode node : nodes)
		{
			JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, null, null);
			
			assertTrue(node instanceof JSNode);
			
			((JSNode) node).accept(walker);
			
			result.addAll(walker.getTypes());
		}
		
		return result;
	}
	
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
		JSSymbolTypeInferrer symbolInferrer = new JSSymbolTypeInferrer(globals, null, null);
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
	 * varTypeTests
	 * 
	 * @param source
	 * @param symbol
	 * @param types
	 */
	public void varTypeTests(String source, String symbol, String... types)
	{
		JSScope globals = this.getGlobals(source);
		
		assertTrue(globals.hasLocalSymbol(symbol));
		JSPropertyCollection object = globals.getSymbol(symbol);
		List<JSNode> values = object.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());
		
		List<String> symbolTypes = this.getTypes(globals, values);
		assertNotNull(types);
		assertEquals(types.length, symbolTypes.size());
		
		for (String type : types)
		{
			assertTrue(symbolTypes.contains(type));
		}
	}
	
	/* literals */
	
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
	
	/* arithmetic operators */
	
	/**
	 * testAddNumbersVar
	 */
	public void testAddNumbersVar()
	{
		this.varTypeTests("var addNumbersVar = 4 + 5;", "addNumbersVar", "Number");
	}
	
	/**
	 * testAddStringsVar
	 */
	public void testAddStringsVar()
	{
		this.varTypeTests("var addStringsVar = 'ab' + 'cd';", "addStringsVar", "String");
	}
	
	/**
	 * testAddMixedVar
	 */
	public void testAddMixedVar()
	{
		this.varTypeTests("var addMixedVar = 'ab' + 10;", "addMixedVar", "String");
	}
	
	/**
	 * testSubtractVar
	 */
	public void testSubtractVar()
	{
		this.varTypeTests("var subVar = a - b;", "subVar", "Number");
	}
	
	/* shift operators */
	
	/**
	 * testShiftLeftVar
	 */
	public void testShiftLeftVar()
	{
		this.varTypeTests("var shiftLeftVar = a << b;", "shiftLeftVar", "Number");
	}
	
	/**
	 * testShiftRightVar
	 */
	public void testShiftRightVar()
	{
		this.varTypeTests("var shiftRightVar = a >> b;", "shiftRightVar", "Number");
	}
	
	/**
	 * testArithmeticShiftRightVar
	 */
	public void testArithmeticShiftRightVar()
	{
		this.varTypeTests("var arithmeticShiftRightVar = a >> b;", "arithmeticShiftRightVar", "Number");
	}
	
	/* bit operators */
	
	/**
	 * testBitAndVar
	 */
	public void testBitAndVar()
	{
		this.varTypeTests("var bitAndVar = a & b;", "bitAndVar", "Number");
	}
	
	/**
	 * testBitXorVar
	 */
	public void testBitXorVar()
	{
		this.varTypeTests("var bitXorVar = a ^ b;", "bitXorVar", "Number");
	}
	
	/**
	 * testBitOrVar
	 */
	public void testBitOrVar()
	{
		this.varTypeTests("var bitOrVar = a | b;", "bitOrVar", "Number");
	}
	
	/* multiplicative operators */
	
	/**
	 * testMultiplyVar
	 */
	public void testMultiplyVar()
	{
		this.varTypeTests("var multiplyVar = a * b;", "multiplyVar", "Number");
	}
	
	/**
	 * testDivideVar
	 */
	public void testDivideVar()
	{
		this.varTypeTests("var divideVar = a * b;", "divideVar", "Number");
	}
	
	/**
	 * testModVar
	 */
	public void testModVar()
	{
		this.varTypeTests("var modVar = a % b;", "modVar", "Number");
	}
	
	/* equality operators */
	
	/**
	 * testEqualVar
	 */
	public void testEqualVar()
	{
		this.varTypeTests("var equalVar = a == b;", "equalVar", "Boolean");
	}
	
	/**
	 * testNotEqualVar
	 */
	public void testNotEqualVar()
	{
		this.varTypeTests("var notEqualVar = a != b;", "notEqualVar", "Boolean");
	}
	
	/**
	 * testInstanceEqualVar
	 */
	public void testInstanceEqualVar()
	{
		this.varTypeTests("var equalVar = a === b;", "equalVar", "Boolean");
	}
	
	/**
	 * testInstanceNotEqualVar
	 */
	public void testInstanceNotEqualVar()
	{
		this.varTypeTests("var notEqualVar = a !== b;", "notEqualVar", "Boolean");
	}
	
	/* relational operators */
	
	/**
	 * testLessThanVar
	 */
	public void testLessThanVar()
	{
		this.varTypeTests("var lessThanVar = a < b;", "lessThanVar", "Boolean");
	}
	
	/**
	 * testGreaterThanVar
	 */
	public void testGreaterThanVar()
	{
		this.varTypeTests("var greaterThanVar = a > b;", "greaterThanVar", "Boolean");
	}
	
	/**
	 * testLessThanEqualVar
	 */
	public void testLessThanEqualVar()
	{
		this.varTypeTests("var lessThanEqualVar = a <= b;", "lessThanEqualVar", "Boolean");
	}
	
	/**
	 * testGreaterThanEqualVar
	 */
	public void testGreaterThanEqualVar()
	{
		this.varTypeTests("var greaterThanEqualVar = a >= b;", "greaterThanEqualVar", "Boolean");
	}
	
	/**
	 * testInstanceOfVar
	 */
	public void testInstanceOfVar()
	{
		this.varTypeTests("var instanceOfVar = a instanceof b;", "instanceOfVar", "Boolean");
	}
	
	/**
	 * testInVar
	 */
	public void testInVar()
	{
		this.varTypeTests("var inVar = a in b;", "inVar", "Boolean");
	}
	
	/* logical operators */
	
	/**
	 * testLogicalAndVar
	 */
	public void testLogicalAndVar()
	{
		this.varTypeTests("var logicalAndVar = a && b;", "logicalAndVar", "Boolean");
	}
	
	/**
	 * testLogicalOrVar
	 */
	public void testLogicalOrVar()
	{
		this.varTypeTests("var logicalOrVar = a || b;", "logicalOrVar", "Boolean");
	}
	
	/* pre-unary operators */
	
	/**
	 * testDeleteVar
	 */
	public void testDeleteVar()
	{
		this.varTypeTests("var deleteVar = delete a.b;", "deleteVar", "Boolean");
	}
	
	/**
	 * testLogicalNotVar
	 */
	public void testLogicalNotVar()
	{
		this.varTypeTests("var logicalNotVar = !b;", "logicalNotVar", "Boolean");
	}
	
	/**
	 * testNegationVar
	 */
	public void testNegationVar()
	{
		this.varTypeTests("var negationVar = -a;", "negationVar", "Number");
	}
	
	/**
	 * testPreDecrementVar
	 */
	public void testPreDecrementVar()
	{
		this.varTypeTests("var preDecrementVar = --a;", "preDecrementVar", "Number");
	}
	
	/**
	 * testPreIncrementVar
	 */
	public void testPreIncrementVar()
	{
		this.varTypeTests("var preIncrementVar = ++a;", "preIncrementVar", "Number");
	}
	
	/**
	 * testBitNotVar
	 */
	public void testBitNotVar()
	{
		this.varTypeTests("var bitNotVar = ~a;", "bitNotVar", "Number");
	}
	
	/**
	 * testTypeofVar
	 */
	public void testTypeofVar()
	{
		this.varTypeTests("var typeofVar = typeof a;", "typeofVar", "String");
	}
	
	/**
	 * testVoidVar
	 */
	public void testVoidVar()
	{
		this.varTypeTests("var voidVar = void a;", "voidVar");
	}
	
	/* post-unary operators */
	
	/**
	 * testPostDecrementVar
	 */
	public void testPostDecrementVar()
	{
		this.varTypeTests("var postDecrementVar = a--;", "postDecrementVar", "Number");
	}
	
	/**
	 * testPostIncrementVar
	 */
	public void testPostIncrementVar()
	{
		this.varTypeTests("var postIncrementVar = a++;", "postIncrementVar", "Number");
	}
	
	/* trinary operators */
	
	/**
	 * testConditionalVar
	 */
	public void testConditionalVar()
	{
		this.varTypeTests("var conditionalVar = (a == true) ? 10 : 20;", "conditionalVar", "Number");
	}
	
	/**
	 * testMixedConditionalVar
	 */
	public void testMixedConditionalVar()
	{
		this.varTypeTests("var mixedConditionalVar = (a == true) ? 10 : '20';", "mixedConditionalVar", "Number", "String");
	}
	
	/* new operator */
	
	// TODO: add tests for 'new' Array, Boolean, Date, Function, Object, Number, String, RegExp
	
	/* assignment operators */
	
	/**
	 * testSimpleAssignment
	 */
	public void testSimpleAssignment()
	{
		this.assignmentTypeTests("abc = 10;", "Number");
	}
	
	/**
	 * testMultiAssignment
	 */
	public void testMultiAssignment()
	{
		this.assignmentTypeTests("abc = def = 10;", "Number");
	}
	
	/**
	 * testPlusAssignNumbers
	 */
	public void testPlusAssignNumbers()
	{
		this.assignmentTypeTests("var abc = 10; abc += 20", "Number");
	}
	
	/**
	 * testPlusAssignStrings
	 */
	public void testPlusAssignStrings()
	{
		this.assignmentTypeTests("var abc = '10'; abc += '20'", "String");
	}
	
	/**
	 * testPlusAssignMixed
	 */
	public void testPlusAssignMixed()
	{
		this.assignmentTypeTests("var abc = 10; abc += '20'", "Number", "String");
	}
	
	/**
	 * testPlusAssignMixed2
	 */
	public void testPlusAssignMixed2()
	{
		this.assignmentTypeTests("var abc = '10'; abc += 20", "String", "Number");
	}
	
	/**
	 * testArithmeticShiftRightAssign
	 */
	public void testArithmeticShiftRightAssign()
	{
		this.assignmentTypeTests("abc >>= b;", "Number");
	}
	
	/**
	 * testBitwiseAndAssign
	 */
	public void testBitwiseAndAssign()
	{
		this.assignmentTypeTests("abc &= b;", "Number");
	}
	
	/**
	 * testBitwiseOrAssign
	 */
	public void testBitwiseOrAssign()
	{
		this.assignmentTypeTests("abc |= b;", "Number");
	}
	
	/**
	 * testBitwiseXorAssign
	 */
	public void testBitwiseXorAssign()
	{
		this.assignmentTypeTests("abc ^= b;", "Number");
	}
	
	/**
	 * testBitwiseDivideAssign
	 */
	public void testBitwiseDivideAssign()
	{
		this.assignmentTypeTests("abc /= b;", "Number");
	}
	
	/**
	 * testModAssign
	 */
	public void testModAssign()
	{
		this.assignmentTypeTests("abc %= b;", "Number");
	}
	
	/**
	 * testMultiplyAssign
	 */
	public void testMultiplyAssign()
	{
		this.assignmentTypeTests("abc *= b;", "Number");
	}
	
	/**
	 * testShiftLeftAssign
	 */
	public void testShiftLeftAssign()
	{
		this.assignmentTypeTests("abc <<= b;", "Number");
	}
	
	/**
	 * testShiftRightAssign
	 */
	public void testShiftRightAssign()
	{
		this.assignmentTypeTests("abc >>= b;", "Number");
	}
	
	/**
	 * testSubtractAssign
	 */
	public void testSubtractAssign()
	{
		this.assignmentTypeTests("abc -= b;", "Number");
	}
}
