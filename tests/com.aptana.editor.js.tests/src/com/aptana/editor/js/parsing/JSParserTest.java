/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.parsing;

import junit.framework.TestCase;

import com.aptana.editor.js.parsing.JSParser;
import com.aptana.editor.js.parsing.JSScanner;
import com.aptana.parsing.ast.IParseNode;

public class JSParserTest extends TestCase
{

	private static final String EOL = "\n";

	private JSParser fParser;
	private JSScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		fParser = new JSParser();
		fScanner = new JSScanner();
	}

	public void testEmptyStatement() throws Exception
	{
		parseTest(";" + EOL);
	}

	public void testEmptyBlock() throws Exception
	{
		parseTest("{}" + EOL); //$NON-NLS-1$
	}

	public void testAssign() throws Exception
	{
		parseTest("a = 10;" + EOL); //$NON-NLS-1$
	}

	public void testAddAndAssign() throws Exception
	{
		parseTest("a += 10;" + EOL); //$NON-NLS-1$
	}

	public void testArithmeticShiftRightAndAssign() throws Exception
	{
		parseTest("a >>>= 10;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseAndAndAssign() throws Exception
	{
		parseTest("a &= 10;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseOrAndAssign() throws Exception
	{
		parseTest("a |= 10;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseXorAndAssign() throws Exception
	{
		parseTest("a ^= 10;" + EOL); //$NON-NLS-1$
	}

	public void testDivideAndAssign() throws Exception
	{
		parseTest("a /= 10;" + EOL); //$NON-NLS-1$
	}

	public void testModAndAssign() throws Exception
	{
		parseTest("a %= 10;" + EOL); //$NON-NLS-1$
	}

	public void testMultiplyAndAssign() throws Exception
	{
		parseTest("a *= 10;" + EOL); //$NON-NLS-1$
	}

	public void testShiftLeftAndAssign() throws Exception
	{
		parseTest("a <<= 10;" + EOL); //$NON-NLS-1$
	}

	public void testShiftRightAndAssign() throws Exception
	{
		parseTest("a >>= 10;" + EOL); //$NON-NLS-1$
	}

	public void testSubtractAndAssign() throws Exception
	{
		parseTest("a -= 10;" + EOL); //$NON-NLS-1$
	}

	public void testAdd() throws Exception
	{
		parseTest("5 + 5;" + EOL); //$NON-NLS-1$
	}

	public void testEqual() throws Exception
	{
		parseTest("abc = def;" + EOL); //$NON-NLS-1$
	}

	public void testGreaterThan() throws Exception
	{
		parseTest("abc > def;" + EOL); //$NON-NLS-1$
	}

	public void testGreaterThanOrEqual() throws Exception
	{
		parseTest("abc >= def;" + EOL); //$NON-NLS-1$
	}

	public void testIdentity() throws Exception
	{
		parseTest("abc === def;" + EOL); //$NON-NLS-1$
	}

	public void testIn() throws Exception
	{
		parseTest("\"abc\" in def;" + EOL); //$NON-NLS-1$
	}

	public void testInstanceOf() throws Exception
	{
		parseTest("abc instanceof def;" + EOL); //$NON-NLS-1$
	}

	public void testLessThan() throws Exception
	{
		parseTest("abc < def;" + EOL); //$NON-NLS-1$
	}

	public void testLessThanOrEqual() throws Exception
	{
		parseTest("abc <= def;" + EOL); //$NON-NLS-1$
	}

	public void testLogicalAnd() throws Exception
	{
		parseTest("abc && def;" + EOL); //$NON-NLS-1$
	}

	public void testLogicalOr() throws Exception
	{
		parseTest("abc || def;" + EOL); //$NON-NLS-1$
	}

	public void testNotEqual() throws Exception
	{
		parseTest("abc != def;" + EOL); //$NON-NLS-1$
	}

	public void testNotIdentity() throws Exception
	{
		parseTest("abc !== def;" + EOL); //$NON-NLS-1$
	}

	public void testArithmeticShiftRight() throws Exception
	{
		parseTest("abc >>> 5;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseAnd() throws Exception
	{
		parseTest("abc & 5;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseOr() throws Exception
	{
		parseTest("abc | 5;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseXor() throws Exception
	{
		parseTest("abc ^ 5;" + EOL); //$NON-NLS-1$
	}

	public void testDivide() throws Exception
	{
		parseTest("abc / 5;" + EOL); //$NON-NLS-1$
	}

	public void testMod() throws Exception
	{
		parseTest("abc % 5;" + EOL); //$NON-NLS-1$
	}

	public void testMultiply() throws Exception
	{
		parseTest("abc * 5;" + EOL); //$NON-NLS-1$
	}

	public void testShiftLeft() throws Exception
	{
		parseTest("abc << 5;" + EOL); //$NON-NLS-1$
	}

	public void testShiftRight() throws Exception
	{
		parseTest("abc >> 5;" + EOL); //$NON-NLS-1$
	}

	public void testSubtract() throws Exception
	{
		parseTest("abc - 5;" + EOL); //$NON-NLS-1$
	}

	public void testGetElement() throws Exception
	{
		parseTest("abc[10];" + EOL); //$NON-NLS-1$
	}

	public void testGetProperty() throws Exception
	{
		parseTest("abc.def;" + EOL); //$NON-NLS-1$
	}

	public void testLogicalNot() throws Exception
	{
		parseTest("a = !false;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseNot() throws Exception
	{
		parseTest("a = ~10;" + EOL); //$NON-NLS-1$
	}

	public void testNegate() throws Exception
	{
		parseTest("a = -10;" + EOL); //$NON-NLS-1$
	}

	public void testPositive() throws Exception
	{
		parseTest("a = +10;" + EOL); //$NON-NLS-1$
	}

	public void testPreDecrement() throws Exception
	{
		parseTest("--a;" + EOL); //$NON-NLS-1$
	}

	public void testPreIncrement() throws Exception
	{
		parseTest("++a;" + EOL); //$NON-NLS-1$
	}

	public void testPostDecrement() throws Exception
	{
		parseTest("a--;" + EOL); //$NON-NLS-1$
	}

	public void testPostIncrement() throws Exception
	{
		parseTest("a++;" + EOL); //$NON-NLS-1$
	}

	public void testDelete() throws Exception
	{
		parseTest("delete obj.prop;" + EOL); //$NON-NLS-1$
	}

	public void testTypeof1() throws Exception
	{
		parseTest("a = typeof(object);" + EOL); //$NON-NLS-1$
	}

	public void testTypeof2() throws Exception
	{
		parseTest("a = typeof object;" + EOL); //$NON-NLS-1$
	}

	public void testVoid() throws Exception
	{
		parseTest("void (abc());" + EOL); //$NON-NLS-1$
	}

	public void testNull() throws Exception
	{
		parseTest("null;" + EOL); //$NON-NLS-1$
	}

	public void testTrue() throws Exception
	{
		parseTest("true;" + EOL); //$NON-NLS-1$
	}

	public void testFalse() throws Exception
	{
		parseTest("false;" + EOL); //$NON-NLS-1$
	}

	public void testNumber() throws Exception
	{
		parseTest("10.3;" + EOL); //$NON-NLS-1$
	}

	public void testString() throws Exception
	{
		parseTest("\"this is a string\";" + EOL); //$NON-NLS-1$
	}

	public void testRegex() throws Exception
	{
		parseTest("/abc/;" + EOL); //$NON-NLS-1$
	}
	
	public void testRegexWithEscapedSlash() throws Exception
	{
		parseTest("/a\\/bc/;" + EOL); //$NON-NLS-1$
	}
	
	public void testRegexWithTrailingModifiers() throws Exception
	{
		parseTest("/abc/ig;" + EOL); //$NON-NLS-1$
	}
	
	public void testComplexRegex() throws Exception
	{
		parseTest("/^[1-3]{0,4}\\/.*$/;" + EOL); //$NON-NLS-1$
	}
	
	public void testComplexRegexWithModifiers() throws Exception
	{
		parseTest("/^[1-3]{0,4}\\/.*$/gim;" + EOL); //$NON-NLS-1$
	}

	public void testThis() throws Exception
	{
		parseTest("this;" + EOL); //$NON-NLS-1$
	}

	public void testIdentifier() throws Exception
	{
		parseTest("abc;" + EOL); //$NON-NLS-1$
	}

	public void testInvoke() throws Exception
	{
		parseTest("abc();" + EOL); //$NON-NLS-1$
	}

	public void testArguments() throws Exception
	{
		parseTest("abc(a, b, c);" + EOL); //$NON-NLS-1$
	}

	public void testVar() throws Exception
	{
		parseTest("var abc = 10;" + EOL); //$NON-NLS-1$
	}

	public void testTryCatch() throws Exception
	{
		parseTest("try {} catch (e) {}" + EOL); //$NON-NLS-1$
	}

	public void testTryFinally() throws Exception
	{
		parseTest("try {} finally {}" + EOL); //$NON-NLS-1$
	}

	public void testTryCatchFinally() throws Exception
	{
		parseTest("try {} catch (e) {} finally {}" + EOL); //$NON-NLS-1$
	}

	public void testConditional() throws Exception
	{
		parseTest("(abc) ? true : false;" + EOL); //$NON-NLS-1$
	}

	public void testEmptyFunction() throws Exception
	{
		parseTest("function abc () {}" + EOL); //$NON-NLS-1$
	}

	public void testEmptyFunctionWithOneParameter() throws Exception
	{
		parseTest("function abc (a) {}" + EOL); //$NON-NLS-1$
	}

	public void testEmptyFunctionWithParameters() throws Exception
	{
		parseTest("function abc (a, b, c) {}" + EOL); //$NON-NLS-1$
	}

	public void testEmptyArray() throws Exception
	{
		parseTest("abc = [];" + EOL); //$NON-NLS-1$
	}

	public void testElisionArray() throws Exception
	{
		parseTest("abc = [,,];" + EOL, "abc = [null, null, null];" + EOL); //$NON-NLS-1$
	}

	public void testArrayLiteral() throws Exception
	{
		parseTest("abc = [1, 2, 3];" + EOL); //$NON-NLS-1$
	}

	public void testArrayLiteralTrailingComma() throws Exception
	{
		parseTest("abc = [1, 2, 3,];" + EOL, "abc = [1, 2, 3, null];" + EOL); //$NON-NLS-1$
	}

	public void testArrayLiteralTrailingElision() throws Exception
	{
		parseTest("abc = [1, 2, 3,,,];" + EOL, "abc = [1, 2, 3, null, null, null];" + EOL); //$NON-NLS-1$
	}

	public void testGroup() throws Exception
	{
		parseTest("a = (3 + 4) * 5;" + EOL); //$NON-NLS-1$
	}

	public void testComma() throws Exception
	{
		parseTest("abc = 10, def = 20;" + EOL); //$NON-NLS-1$
	}

	public void testConstruct() throws Exception
	{
		parseTest("a = new Object(\"test\");" + EOL); //$NON-NLS-1$
	}

	public void testConstructWithoutArgs() throws Exception
	{
		parseTest("a = new Object();" + EOL); //$NON-NLS-1$
	}

	public void testObjectLiteral() throws Exception
	{
		parseTest("abc = {name: \"Name\", index: 2, id: 10};" + EOL); //$NON-NLS-1$
	}

	public void testThrow() throws Exception
	{
		parseTest("throw new Error(\"error\");" + EOL); //$NON-NLS-1$
	}

	public void testWhile() throws Exception
	{
		parseTest("while (true) {}" + EOL); //$NON-NLS-1$
	}

	public void testLabeledStatement() throws Exception
	{
		parseTest("myLabel: while (true) {}" + EOL); //$NON-NLS-1$
	}

	public void testContinue() throws Exception
	{
		parseTest("while (true) {continue;}" + EOL); //$NON-NLS-1$
	}

	public void testBreak() throws Exception
	{
		parseTest("while (true) {break;}" + EOL); //$NON-NLS-1$
	}

	public void testDefault() throws Exception
	{
		parseTest("switch (abc) {default: break;}" + EOL); //$NON-NLS-1$
	}

	public void testCase() throws Exception
	{
		parseTest("switch (abc) {case 10: break;}" + EOL); //$NON-NLS-1$
	}

	public void testSwitch() throws Exception
	{
		parseTest("switch (abc) {case 10: break;default: break;}" + EOL); //$NON-NLS-1$
	}

	public void testReturn() throws Exception
	{
		parseTest("function abc () {return false;}" + EOL); //$NON-NLS-1$
	}

	public void testIf() throws Exception
	{
		parseTest("if (a < b) {a = 10;} else {a = 20;}" + EOL); //$NON-NLS-1$
	}

	public void testIfWithoutElse() throws Exception
	{
		parseTest("if (a < b) {a = 10;}" + EOL); //$NON-NLS-1$
	}

	public void testDo() throws Exception
	{
		parseTest("do {a++;} while (a < 10);" + EOL); //$NON-NLS-1$
	}

	public void testForBodyOnly() throws Exception
	{
		parseTest("for (;;) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForAdvanceOnly() throws Exception
	{
		parseTest("for (;; a++) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForConditionOnly() throws Exception
	{
		parseTest("for (; a < 10;) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForNoInitialize() throws Exception
	{
		parseTest("for (; a < 10; a++) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForInitializeOnly() throws Exception
	{
		parseTest("for (a = 0;;) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForNoCondition() throws Exception
	{
		parseTest("for (a = 0;; a++) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForNoAdvance() throws Exception
	{
		parseTest("for (a = 0; a < 10;) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testFor() throws Exception
	{
		parseTest("for (a = 0; a < 10; a++) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForVarInitializeOnly() throws Exception
	{
		parseTest("for (var a = 0;;) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForVarNoCondition() throws Exception
	{
		parseTest("for (var a = 0;; a++) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForVarNoAdvance() throws Exception
	{
		parseTest("for (var a = 0; a < 10;) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForVar() throws Exception
	{
		parseTest("for (var a = 0; a < 10; a++) {show(a);}" + EOL); //$NON-NLS-1$
	}

	public void testForIn() throws Exception
	{
		parseTest("for (a in obj) {show(a);}" + EOL);
	}

	public void testForVarIn() throws Exception
	{
		parseTest("for (var a in obj) {show(a);}" + EOL);
	}

	protected void parseTest(String source) throws Exception
	{
		parseTest(source, source);
	}

	protected void parseTest(String source, String expected) throws Exception
	{
		fScanner.setSource(source);

		IParseNode result = (IParseNode) fParser.parse(fScanner);
		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append(EOL);
		}
		assertEquals(expected, text.toString());
	}
}
