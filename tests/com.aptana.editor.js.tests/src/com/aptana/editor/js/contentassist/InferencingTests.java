package com.aptana.editor.js.contentassist;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.ast.JSNode;
import com.aptana.editor.js.parsing.ast.JSParseRootNode;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.Scope;
import com.aptana.parsing.ast.IParseNode;

public class InferencingTests extends TestCase
{
	/**
	 * getGlobals
	 * 
	 * @param source
	 * @return
	 * @throws Exception 
	 */
	protected Scope<JSNode> getGlobals(String source)
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
		
		IParseNode root = parseState.getParseResult();
		assertTrue(root instanceof JSParseRootNode);
		
		JSSymbolCollector s = new JSSymbolCollector();
		((JSParseRootNode) root).accept(s);
		
		return s.getScope();
	}
	
	/**
	 * getTypes
	 * 
	 * @param nodes
	 * @return
	 */
	protected List<String> getTypes(Scope<JSNode> globals, List<JSNode> nodes)
	{
		List<String> result = new LinkedList<String>();
		
		for (IParseNode node : nodes)
		{
			JSTypeWalker walker = new JSTypeWalker(globals, null);
			
			assertTrue(node instanceof JSNode);
			
			((JSNode) node).accept(walker);
			
			result.addAll(walker.getTypes());
		}
		
		return result;
	}
	
	/**
	 * typeTests
	 * 
	 * @param source
	 * @param symbol
	 * @param types
	 */
	public void typeTests(String source, String symbol, String... types)
	{
		Scope<JSNode> globals = this.getGlobals(source);
		
		assertTrue(globals.hasLocalSymbol(symbol));
		List<JSNode> values = globals.getSymbol(symbol);
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
		this.typeTests("var trueVar = true;", "trueVar", "Boolean");
	}
	
	/**
	 * testFalseVar
	 */
	public void testFalseVar()
	{
		this.typeTests("var falseVar = false;", "falseVar", "Boolean");
	}
	
	/**
	 * testIntVar
	 */
	public void testIntVar()
	{
		this.typeTests("var intVar = 10;", "intVar", "Number");
	}
	
	/**
	 * testHexVar
	 */
	public void testHexVar()
	{
		this.typeTests("var hexVar = 0x10;", "hexVar", "Number");
	}
	
	/**
	 * testFloatVar
	 */
	public void testFloatVar()
	{
		this.typeTests("var floatVar = 10.01;", "floatVar", "Number");
	}
	
	/**
	 * testArrayLiteralVar
	 */
	public void testArrayLiteralVar()
	{
		this.typeTests("var arrayLiteralVar = [];", "arrayLiteralVar", "Array");
	}
	
	/**
	 * testObjectLiteralVar
	 */
	public void testObjectLiteralVar()
	{
		this.typeTests("var objectLiteralVar = {};", "objectLiteralVar", "Object");
	}
	
	/**
	 * testRegExpLiteralVar
	 */
	public void testRegExpLiteralVar()
	{
		this.typeTests("var regExpLiteralVar = /abc/i;", "regExpLiteralVar", "RegExp");
	}
	
	/**
	 * testFunctionLiteralVar
	 */
	public void testFunctionLiteralVar()
	{
		this.typeTests("var functionLiteralVar = function() {};", "functionLiteralVar", "Function");
	}
	
	/**
	 * testSingleQuotedStringVar
	 */
	public void testSingleQuotedStringVar()
	{
		this.typeTests("var singleQuotedStringVar = 'abc';", "singleQuotedStringVar", "String");
	}
	
	/**
	 * testDoubleQuotedStringVar
	 */
	public void testDoubleQuotedStringVar()
	{
		this.typeTests("var doubleQuotedStringVar = \"abc\";", "doubleQuotedStringVar", "String");
	}
	
	/* arithmetic operators */
	
	/**
	 * testAddNumbersVar
	 */
	public void testAddNumbersVar()
	{
		this.typeTests("var addNumbersVar = 4 + 5;", "addNumbersVar", "Number");
	}
	
	/**
	 * testAddStringsVar
	 */
	public void testAddStringsVar()
	{
		this.typeTests("var addStringsVar = 'ab' + 'cd';", "addStringsVar", "String");
	}
	
	/**
	 * testAddMixedVar
	 */
	public void testAddMixedVar()
	{
		this.typeTests("var addMixedVar = 'ab' + 10;", "addMixedVar", "String");
	}
	
	/**
	 * testSubtractVar
	 */
	public void testSubtractVar()
	{
		this.typeTests("var subVar = a - b;", "subVar", "Number");
	}
	
	/* shift operators */
	
	/**
	 * testShiftLeftVar
	 */
	public void testShiftLeftVar()
	{
		this.typeTests("var shiftLeftVar = a << b;", "shiftLeftVar", "Number");
	}
	
	/**
	 * testShiftRightVar
	 */
	public void testShiftRightVar()
	{
		this.typeTests("var shiftRightVar = a >> b;", "shiftRightVar", "Number");
	}
	
	/**
	 * testArithmeticShiftRightVar
	 */
	public void testArithmeticShiftRightVar()
	{
		this.typeTests("var arithmeticShiftRightVar = a >> b;", "arithmeticShiftRightVar", "Number");
	}
	
	/* bit operators */
	
	/**
	 * testBitAndVar
	 */
	public void testBitAndVar()
	{
		this.typeTests("var bitAndVar = a & b;", "bitAndVar", "Number");
	}
	
	/**
	 * testBitXorVar
	 */
	public void testBitXorVar()
	{
		this.typeTests("var bitXorVar = a ^ b;", "bitXorVar", "Number");
	}
	
	/**
	 * testBitOrVar
	 */
	public void testBitOrVar()
	{
		this.typeTests("var bitOrVar = a | b;", "bitOrVar", "Number");
	}
	
	/* multiplicative operators */
	
	/**
	 * testMultiplyVar
	 */
	public void testMultiplyVar()
	{
		this.typeTests("var multiplyVar = a * b;", "multiplyVar", "Number");
	}
	
	/**
	 * testDivideVar
	 */
	public void testDivideVar()
	{
		this.typeTests("var divideVar = a * b;", "divideVar", "Number");
	}
	
	/**
	 * testModVar
	 */
	public void testModVar()
	{
		this.typeTests("var modVar = a % b;", "modVar", "Number");
	}
	
	/* equality operators */
	
	/**
	 * testEqualVar
	 */
	public void testEqualVar()
	{
		this.typeTests("var equalVar = a == b;", "equalVar", "Boolean");
	}
	
	/**
	 * testNotEqualVar
	 */
	public void testNotEqualVar()
	{
		this.typeTests("var notEqualVar = a != b;", "notEqualVar", "Boolean");
	}
	
	/**
	 * testInstanceEqualVar
	 */
	public void testInstanceEqualVar()
	{
		this.typeTests("var equalVar = a === b;", "equalVar", "Boolean");
	}
	
	/**
	 * testInstanceNotEqualVar
	 */
	public void testInstanceNotEqualVar()
	{
		this.typeTests("var notEqualVar = a !== b;", "notEqualVar", "Boolean");
	}
	
	/* relational operators */
	
	/**
	 * testLessThanVar
	 */
	public void testLessThanVar()
	{
		this.typeTests("var lessThanVar = a < b;", "lessThanVar", "Boolean");
	}
	
	/**
	 * testGreaterThanVar
	 */
	public void testGreaterThanVar()
	{
		this.typeTests("var greaterThanVar = a > b;", "greaterThanVar", "Boolean");
	}
	
	/**
	 * testLessThanEqualVar
	 */
	public void testLessThanEqualVar()
	{
		this.typeTests("var lessThanEqualVar = a <= b;", "lessThanEqualVar", "Boolean");
	}
	
	/**
	 * testGreaterThanEqualVar
	 */
	public void testGreaterThanEqualVar()
	{
		this.typeTests("var greaterThanEqualVar = a >= b;", "greaterThanEqualVar", "Boolean");
	}
	
	/**
	 * testInstanceOfVar
	 */
	public void testInstanceOfVar()
	{
		this.typeTests("var instanceOfVar = a instanceof b;", "instanceOfVar", "Boolean");
	}
	
	/**
	 * testInVar
	 */
	public void testInVar()
	{
		this.typeTests("var inVar = a in b;", "inVar", "Boolean");
	}
	
	/* logical operators */
	
	/**
	 * testLogicalAndVar
	 */
	public void testLogicalAndVar()
	{
		this.typeTests("var logicalAndVar = a && b;", "logicalAndVar", "Boolean");
	}
	
	/**
	 * testLogicalOrVar
	 */
	public void testLogicalOrVar()
	{
		this.typeTests("var logicalOrVar = a || b;", "logicalOrVar", "Boolean");
	}
	
	/* pre-unary operators */
	
	/**
	 * testDeleteVar
	 */
	public void testDeleteVar()
	{
		this.typeTests("var deleteVar = delete a.b;", "deleteVar", "Boolean");
	}
	
	/**
	 * testLogicalNotVar
	 */
	public void testLogicalNotVar()
	{
		this.typeTests("var logicalNotVar = !b;", "logicalNotVar", "Boolean");
	}
	
	/**
	 * testNegationVar
	 */
	public void testNegationVar()
	{
		this.typeTests("var negationVar = -a;", "negationVar", "Number");
	}
	
	/**
	 * testPreDecrementVar
	 */
	public void testPreDecrementVar()
	{
		this.typeTests("var preDecrementVar = --a;", "preDecrementVar", "Number");
	}
	
	/**
	 * testPreIncrementVar
	 */
	public void testPreIncrementVar()
	{
		this.typeTests("var preIncrementVar = ++a;", "preIncrementVar", "Number");
	}
	
	/**
	 * testBitNotVar
	 */
	public void testBitNotVar()
	{
		this.typeTests("var bitNotVar = ~a;", "bitNotVar", "Number");
	}
	
	/**
	 * testTypeofVar
	 */
	public void testTypeofVar()
	{
		this.typeTests("var typeofVar = typeof a;", "typeofVar", "String");
	}
	
	/**
	 * testVoidVar
	 */
	public void testVoidVar()
	{
		this.typeTests("var voidVar = void a;", "voidVar");
	}
	
	/* post-unary operators */
	
	/**
	 * testPostDecrementVar
	 */
	public void testPostDecrementVar()
	{
		this.typeTests("var postDecrementVar = a--;", "postDecrementVar", "Number");
	}
	
	/**
	 * testPostIncrementVar
	 */
	public void testPostIncrementVar()
	{
		this.typeTests("var postIncrementVar = a++;", "postIncrementVar", "Number");
	}
	
	/* trinary operators */
	
	/**
	 * testConditionalVar
	 */
	public void testConditionalVar()
	{
		this.typeTests("var conditionalVar = (a == true) ? 10 : 20;", "conditionalVar", "Number");
	}
	
	/**
	 * testMixedConditionalVar
	 */
	public void testMixedConditionalVar()
	{
		this.typeTests("var mixedConditionalVar = (a == true) ? 10 : '20';", "mixedConditionalVar", "Number", "String");
	}
	
	/* new operator */
	
	// TODO: add tests for 'new' Array, Boolean, Date, Function, Object, Number, String, RegExp
}
