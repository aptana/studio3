/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Before;
import org.junit.Test;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;

import beaver.Symbol;

public class JSParserTest
{
	private static final String EOL = FileUtil.NEW_LINE;

	private JSParser fParser;
	private ParseResult fParseResult;

	@Before
	public void setUp() throws Exception
	{
		fParser = new JSParser();
	}

	/**
	 * getSource
	 * 
	 * @param resourceName
	 * @return
	 * @throws IOException
	 */
	private String getSource(String resourceName) throws IOException
	{
		InputStream stream = FileLocator.openStream(Platform.getBundle(JSCorePlugin.PLUGIN_ID), new Path(resourceName),
				false);
		return IOUtil.read(stream);
	}

	@Test
	public void testEmptyStatement() throws Exception
	{
		assertParseResult(";" + EOL);
	}

	@Test
	public void testEmptyBlock() throws Exception
	{
		// semicolon recovery
		assertParseResult("{}" + EOL, "{};" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testAssign() throws Exception
	{
		assertParseResult("a = 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testAddAndAssign() throws Exception
	{
		assertParseResult("a += 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testArithmeticShiftRightAndAssign() throws Exception
	{
		assertParseResult("a >>>= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBitwiseAndAndAssign() throws Exception
	{
		assertParseResult("a &= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBitwiseOrAndAssign() throws Exception
	{
		assertParseResult("a |= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBitwiseXorAndAssign() throws Exception
	{
		assertParseResult("a ^= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testDivideAndAssign() throws Exception
	{
		assertParseResult("a /= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testModAndAssign() throws Exception
	{
		assertParseResult("a %= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testMultiplyAndAssign() throws Exception
	{
		assertParseResult("a *= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testShiftLeftAndAssign() throws Exception
	{
		assertParseResult("a <<= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testShiftRightAndAssign() throws Exception
	{
		assertParseResult("a >>= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testSubtractAndAssign() throws Exception
	{
		assertParseResult("a -= 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testAdd() throws Exception
	{
		assertParseResult("5 + 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEqual() throws Exception
	{
		assertParseResult("abc = def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testGreaterThan() throws Exception
	{
		assertParseResult("abc > def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testGreaterThanOrEqual() throws Exception
	{
		assertParseResult("abc >= def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testIdentity() throws Exception
	{
		assertParseResult("abc === def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testIn() throws Exception
	{
		assertParseResult("\"abc\" in def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInstanceOf() throws Exception
	{
		assertParseResult("abc instanceof def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLessThan() throws Exception
	{
		assertParseResult("abc < def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLessThanOrEqual() throws Exception
	{
		assertParseResult("abc <= def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLogicalAnd() throws Exception
	{
		assertParseResult("abc && def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLogicalOr() throws Exception
	{
		assertParseResult("abc || def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testNotEqual() throws Exception
	{
		assertParseResult("abc != def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testNotIdentity() throws Exception
	{
		assertParseResult("abc !== def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testArithmeticShiftRight() throws Exception
	{
		assertParseResult("abc >>> 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBitwiseAnd() throws Exception
	{
		assertParseResult("abc & 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBitwiseOr() throws Exception
	{
		assertParseResult("abc | 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBitwiseXor() throws Exception
	{
		assertParseResult("abc ^ 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testDivide() throws Exception
	{
		assertParseResult("abc / 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testMod() throws Exception
	{
		assertParseResult("abc % 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testMultiply() throws Exception
	{
		assertParseResult("abc * 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testShiftLeft() throws Exception
	{
		assertParseResult("abc << 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testShiftRight() throws Exception
	{
		assertParseResult("abc >> 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testSubtract() throws Exception
	{
		assertParseResult("abc - 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testGetElement() throws Exception
	{
		assertParseResult("abc[10];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testGetProperty() throws Exception
	{
		assertParseResult("abc.def;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLogicalNot() throws Exception
	{
		assertParseResult("a = !false;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBitwiseNot() throws Exception
	{
		assertParseResult("a = ~10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testNegate() throws Exception
	{
		assertParseResult("a = -10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testPositive() throws Exception
	{
		assertParseResult("a = +10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testPreDecrement() throws Exception
	{
		assertParseResult("--a;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testPreIncrement() throws Exception
	{
		assertParseResult("++a;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testPostDecrement() throws Exception
	{
		assertParseResult("a--;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testPostIncrement() throws Exception
	{
		assertParseResult("a++;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testDelete() throws Exception
	{
		assertParseResult("delete obj.prop;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testTypeof1() throws Exception
	{
		assertParseResult("a = typeof(object);" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testTypeof2() throws Exception
	{
		assertParseResult("a = typeof object;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testVoid() throws Exception
	{
		assertParseResult("void (true);" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testNull() throws Exception
	{
		assertParseResult("null;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testTrue() throws Exception
	{
		assertParseResult("true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testFalse() throws Exception
	{
		assertParseResult("false;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testNumber() throws Exception
	{
		assertParseResult("10.3;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testString() throws Exception
	{
		assertParseResult("\"this is a string\";" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testRegex() throws Exception
	{
		assertParseResult("/abc/;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testRegexWithEscapedSlash() throws Exception
	{
		assertParseResult("/a\\/bc/;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testRegexWithTrailingModifiers() throws Exception
	{
		assertParseResult("/abc/ig;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testComplexRegex() throws Exception
	{
		assertParseResult("/^[1-3]{0,4}\\/.*$/;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testComplexRegexWithModifiers() throws Exception
	{
		assertParseResult("/^[1-3]{0,4}\\/.*$/gim;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testThis() throws Exception
	{
		assertParseResult("this;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testIdentifier() throws Exception
	{
		assertParseResult("abc;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvoke() throws Exception
	{
		assertParseResult("x = abc();" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokeDereferenceArray() throws Exception
	{
		assertParseResult("x = abc()[0];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokePropertyReference() throws Exception
	{
		assertParseResult("x = abc().abc;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokeWithArgs() throws Exception
	{
		assertParseResult("x = abc(10, 20, 30);" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokeInvoke() throws Exception
	{
		assertParseResult("x = abc()();" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testVar() throws Exception
	{
		assertParseResult("var abc = 10;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testIfInIfElse() throws Exception
	{
		assertParseResult("if (true) if (false) a; else b; else c;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testVarInIfElse() throws Exception
	{
		assertParseResult("if (true) var abc = 10; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testWithInIfElse() throws Exception
	{
		assertParseResult("if (true) with (abc) a++; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLabelInIfElse() throws Exception
	{
		assertParseResult("if (true) label: a++; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testThisInIfElse() throws Exception
	{
		assertParseResult("if (true) this.abc = 10; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokeInIfElse() throws Exception
	{
		assertParseResult("if (true) abc(); else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokeDereferenceArrayInIfElse() throws Exception
	{
		assertParseResult("if (true) abc()[0]; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokePropertyReferenceInIfElse() throws Exception
	{
		assertParseResult("if (true) abc().abc; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testInvokeInvokeInIfElse() throws Exception
	{
		assertParseResult("if (true) abc()(); else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptyInIfElse() throws Exception
	{
		assertParseResult("if (true)  else true;" + EOL, "if (true) ; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testDoWhileInIfElse() throws Exception
	{
		assertParseResult("if (true) do a++; while (a < 10); else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testWhileInIfElse() throws Exception
	{
		assertParseResult("if (true) while (a < 10) a++; else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForBodyOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (;;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForAdvanceOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (;; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForConditionOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (; a < 10;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForNoInitializeInIfElse() throws Exception
	{
		assertParseResult("if (true) for (; a < 10; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForInitializeOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0;;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarInitializeOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0;;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForNoConditionInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0;; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarNoConditionInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0;; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForNoAdvanceInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0; a < 10;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarNoAdvanceInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0; a < 10;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0; a < 10; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0; a < 10; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForInInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a in obj) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarInInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a in obj) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testTryCatch() throws Exception
	{
		assertParseResult("try {} catch (e) {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testTryFinally() throws Exception
	{
		assertParseResult("try {} finally {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testTryCatchFinally() throws Exception
	{
		assertParseResult("try {} catch (e) {} finally {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testConditional() throws Exception
	{
		assertParseResult("(abc) ? true : false;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptyFunction() throws Exception
	{
		assertParseResult("function abc () {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptyFunctionWithOneParameter() throws Exception
	{
		assertParseResult("function abc (a) {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptyFunctionWithParameters() throws Exception
	{
		assertParseResult("function abc (a, b, c) {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptyArray() throws Exception
	{
		assertParseResult("abc = [];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testElisionArray() throws Exception
	{
		assertParseResult("abc = [,,];" + EOL, "abc = [null, null, null];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLeadingElisionArray() throws Exception
	{
		assertParseResult("abc = [,,10];" + EOL, "abc = [null, null, 10];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testElisionMidArray() throws Exception
	{
		assertParseResult("abc = [10,,10];" + EOL, "abc = [10, null, 10];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testArrayLiteral() throws Exception
	{
		assertParseResult("abc = [1, 2, 3];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testArrayLiteralTrailingComma() throws Exception
	{
		assertParseResult("abc = [1, 2, 3,];" + EOL, "abc = [1, 2, 3, null];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testArrayLiteralTrailingElision() throws Exception
	{
		assertParseResult("abc = [1, 2, 3,,,];" + EOL, "abc = [1, 2, 3, null, null, null];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testGroup() throws Exception
	{
		assertParseResult("a = (3 + 4) * 5;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testComma() throws Exception
	{
		assertParseResult("abc = 10, 20;" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testConstruct() throws Exception
	{
		assertParseResult("a = new Object(\"test\");" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testConstructConstruct() throws Exception
	{
		assertParseResult("a = new new Thing();" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testConstructWithoutArgs() throws Exception
	{
		assertParseResult("a = new Object();" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testObjectLiteral() throws Exception
	{
		assertParseResult("abc = {name: \"Name\", index: 2, id: 10};" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testObjectLiteralWithTrailingComma() throws Exception
	{
		assertParseResult("abc = {name: \"Name\", index: 2, id: 10,};" + EOL, //$NON-NLS-1$
				"abc = {name: \"Name\", index: 2, id: 10};" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testObjectLiteralWithNumberPropertyName() throws Exception
	{
		assertParseResult("abc = {20: \"Name\"};" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testThrow() throws Exception
	{
		assertParseResult("throw new Error(\"error\");" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testWhile() throws Exception
	{
		assertParseResult("while (true) {};" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLabeledStatement() throws Exception
	{
		assertParseResult("myLabel: while (true) {};" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testContinue() throws Exception
	{
		assertParseResult("while (true) {continue;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testBreak() throws Exception
	{
		assertParseResult("while (true) {break;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testDefault() throws Exception
	{
		assertParseResult("switch (abc) {default: break;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testCase() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptySwitch() throws Exception
	{
		assertParseResult("switch (abc) {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testSwitchDefaultLast() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;default: break;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testSwitchDefaultFirst() throws Exception
	{
		assertParseResult("switch (abc) {default: break;case 10: break;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testSwitchDefaultInMiddle() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;default: break;case 20: break;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testSwitchMultipleCasesNoDefault() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;case 20: break;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptyCase() throws Exception
	{
		assertParseResult("switch (abc) {case 10: }" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testEmptyDefault() throws Exception
	{
		assertParseResult("switch (abc) {default: }" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testReturn() throws Exception
	{
		assertParseResult("function abc () {return;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testReturnWithExpression() throws Exception
	{
		assertParseResult("function abc () {return false;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testIf() throws Exception
	{
		assertParseResult("if (a < b) {a = 10;} else {a = 20;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testIfComma() throws Exception
	{
		assertParseResult("if (a < b, b < c) {a = 10;} else {a = 20;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testIfWithoutElse() throws Exception
	{
		assertParseResult("if (a < b) {a = 10;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testDo() throws Exception
	{
		assertParseResult("do {a++;} while (a < 10);" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForBodyOnly() throws Exception
	{
		assertParseResult("for (;;) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForAdvanceOnly() throws Exception
	{
		assertParseResult("for (;; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForConditionOnly() throws Exception
	{
		assertParseResult("for (; a < 10;) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForNoInitialize() throws Exception
	{
		assertParseResult("for (; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForInitializeOnly() throws Exception
	{
		assertParseResult("for (a = 0;;) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForInitializeComma() throws Exception
	{
		assertParseResult("for (a = 0, b = 1;;) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForNoCondition() throws Exception
	{
		assertParseResult("for (a = 0;; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForNoAdvance() throws Exception
	{
		assertParseResult("for (a = 0; a < 10;) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testFor() throws Exception
	{
		assertParseResult("for (a = 0; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarInitializeOnly() throws Exception
	{
		assertParseResult("for (var a = 0;;) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarNoCondition() throws Exception
	{
		assertParseResult("for (var a = 0;; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarNoAdvance() throws Exception
	{
		assertParseResult("for (var a = 0; a < 10;) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVar() throws Exception
	{
		assertParseResult("for (var a = 0; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVars() throws Exception
	{
		assertParseResult("for (var a = 0, b = 0; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarRelational() throws Exception
	{
		assertParseResult("for (var a = 0, b = a < 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarEquality() throws Exception
	{
		assertParseResult("for (var a = 0, b = a == 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarBitwiseAnd() throws Exception
	{
		assertParseResult("for (var a = 0, b = a & 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarBitwiseXor() throws Exception
	{
		assertParseResult("for (var a = 0, b = a ^ 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarBitwiseOr() throws Exception
	{
		assertParseResult("for (var a = 0, b = a | 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarLogicalAnd() throws Exception
	{
		assertParseResult("for (var a = 0, b = a && c; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarLogicalOr() throws Exception
	{
		assertParseResult("for (var a = 0, b = a || c; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForVarConditional() throws Exception
	{
		assertParseResult("for (var a = 0, b = a ? c : d; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testForIn() throws Exception
	{
		assertParseResult("for (a in obj) {a;}" + EOL);
	}

	@Test
	public void testForVarIn() throws Exception
	{
		assertParseResult("for (var a in obj) {a;}" + EOL);
	}

	// bug and regression tests here

	@Test
	public void testSDocComment() throws Exception
	{
		JSFlexScanner scanner = new JSFlexScanner();
		scanner.setSource("/**/");
		fParser.parse(scanner);

		List<Symbol> comments = scanner.getMultiLineComments();

		assertEquals(1, comments.size());
	}

	@Test
	public void testPlusNegativeNumber() throws Exception
	{
		assertParseResult("var x = 5 + -3;" + EOL);
		assertParseResult("var x = 5+ -3" + EOL, "var x = 5 + -3;" + EOL);
		assertParseResult("var x = 5 +-3" + EOL, "var x = 5 + -3;" + EOL);
		assertParseResult("var x = 5+-3" + EOL, "var x = 5 + -3;" + EOL);
	}

	@Test
	public void testPlusPositiveNumber() throws Exception
	{
		assertParseResult("var x = 5 + +3;" + EOL);
		// Due to semicolon insertion recovery, the expected result will be terminated with a semicolon
		assertParseResult("var x = 5+ +3" + EOL, "var x = 5 + +3;" + EOL);

		// NOTE: The following commented tests are currently failing
		// parseTest("var x = 5 ++3" + EOL, "var x = 5 + +3" + EOL);
		// parseTest("var x = 5++3" + EOL, "var x = 5 + +3" + EOL);
	}

	@Test
	public void testMinusNegativeNumber() throws Exception
	{
		assertParseResult("var x = 5 - -3;" + EOL);
		assertParseResult("var x = 5- -3" + EOL, "var x = 5 - -3;" + EOL);

		// NOTE: The following commented tests are currently failing
		// parseTest("var x = 5 --3" + EOL, "var x = 5 - -3" + EOL);
		// parseTest("var x = 5--3" + EOL, "var x = 5 - -3" + EOL);
	}

	@Test
	public void testMinusPositiveNumber() throws Exception
	{
		assertParseResult("var x = 5 - +3;" + EOL);
		assertParseResult("var x = 5- +3" + EOL, "var x = 5 - +3;" + EOL);
		assertParseResult("var x = 5 -+3" + EOL, "var x = 5 - +3;" + EOL);
		assertParseResult("var x = 5-+3" + EOL, "var x = 5 - +3;" + EOL);
	}

	// begin recovery strategy tests

	@Test
	public void testMissingSemicolon() throws Exception
	{
		assertParseResult("abc", "abc;" + EOL);
		assertParseErrors("Missing semicolon");
	}

	@Test
	public void testMissingClosingParenthesis() throws Exception
	{
		assertParseResult("testing(", "testing();" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	@Test
	public void testMissingIdentifier() throws Exception
	{
		assertParseResult("var x =", "var x = ;" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	@Test
	public void testMissingIdentifier2() throws Exception
	{
		assertParseResult("x.", "x.;" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	@Test
	public void testMissingArg() throws Exception
	{
		assertParseResult("fun(a,);", "fun(a, );" + EOL);
		assertParseErrors("Syntax Error: unexpected token \")\"");
	}

	@Test
	public void testMissingIdentifier3() throws Exception
	{
		assertParseResult("new", "new ;" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	@Test
	public void testMissingPropertyValue() throws Exception
	{
		assertParseResult("var x = { t };", "var x = {t: };" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"}\"");
	}

	@Test
	public void testMissingPropertyValue2() throws Exception
	{
		assertParseResult("var x = { t: };", "var x = {t: };" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"}\"");
	}

	@Test
	public void testSingleLineComment() throws Exception
	{
		String source = "// this is a single-line comment";

		IParseRootNode root = parse(source);
		IParseNode[] comments = root.getCommentNodes();
		assertNotNull(comments);
		assertEquals(1, comments.length);
		IParseNode comment = comments[0];
		assertEquals(0, comment.getStartingOffset());
		assertEquals(source.length() - 1, comment.getEndingOffset());
	}

	/**
	 * Test fix for APSTUD-3214
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOperatorsWithoutSpace() throws Exception
	{
		assertParseResult("function foo() {i++>1;}" + EOL, "function foo () {i++ > 1;}" + EOL);
		assertParseResult("function foo() { if(i--==y) alert('test'); }" + EOL,
				"function foo () {if (i-- == y) alert('test');}" + EOL);
	}

	/**
	 * Test fix for TISTUD-627
	 * 
	 * @throws Exception
	 */
	@Test
	public void testFunctionWithoutBody() throws Exception
	{
		assertParseResult("function abc(s1, s2, s3)", "function abc (s1, s2, s3) {}" + EOL);
	}

	/**
	 * Test APSTUD-4072
	 * 
	 * @throws IOException
	 * @throws beaver.Parser.Exception
	 */
	@Test
	public void testNodeOffsetsAtEOF() throws Exception
	{
		String source = "a.foo()\n// this is a comment";
		IParseNode result = parse(source);

		assertNotNull(result);
		assertEquals(1, result.getChildCount());

		IParseNode invokeNode = result.getFirstChild();
		assertNotNull(invokeNode);
		assertEquals(0, invokeNode.getStartingOffset());
		assertEquals(6, invokeNode.getEndingOffset());
	}

	/**
	 * test TISTUD-1269 (part 1)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSwitchWithoutExpression() throws Exception
	{
		assertParseResult("switch () {}" + EOL);
	}

	/**
	 * test TISTUD-1269 (part 2)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCaseWithPartialIdentifier() throws Exception
	{
		assertParseResult("switch (id) {case Ti.}", "switch (id) {case Ti.: }" + EOL);
	}

	@Test
	public void testUnclosedString() throws Exception
	{
		assertParseResult("var string = 'something", "var string = ;" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"'\"");
	}

	@Test
	public void testUnclosedComment() throws Exception
	{
		assertParseResult("var thing; /* comment", "var thing;" + EOL + EOL);
		assertParseErrors("Syntax Error: unexpected token \"/\"");
	}

	@Test
	public void testUnclosedRegexp() throws Exception
	{
		assertParseResult("var regexp = /;", EOL);
		assertParseErrors("Syntax Error: unexpected token \"/\"", "Syntax Error: unexpected token \";\"");
	}

	@Test
	public void testReservedWordAsPropertyName() throws Exception
	{
		assertParseResult("this.default = 1;" + EOL);
		assertNoErrors();
	}

	@Test
	public void testReservedWordAsPropertyName2() throws Exception
	{
		assertParseResult("a[\"public\"] = 1;" + EOL);
		assertNoErrors();
	}

	@Test
	public void testReservedWordAsPropertyName3() throws Exception
	{
		assertParseResult("a = {default: \"test\"};" + EOL);
		assertNoErrors();
	}

	@Test
	public void testReservedWordAsFunctionName() throws Exception
	{
		parse("function import() {};" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"import\"");
	}

	@Test
	public void testGetterProperty() throws Exception
	{
		parse("Field.prototype = { get value() { return this._value; } };" + EOL);
		assertNoErrors();
	}

	@Test
	public void testSetterProperty() throws Exception
	{
		parse("Field.prototype = { set value(val) { this._value = val; } };" + EOL);
		assertNoErrors();
	}

	@Test
	public void testConstDeclaration() throws Exception
	{
		assertParseResult("const PI = 3.141593;" + EOL);
		assertNoErrors();
	}

	@Test
	public void testLetDeclaration() throws Exception
	{
		assertParseResult("let callbacks = [];" + EOL);
		assertNoErrors();
	}

	@Test
	public void testSpreadOperatorInArrayLiteral() throws Exception
	{
		assertParseResult("var other = [1, 2, ...params];" + EOL);
		assertNoErrors();
	}

	@Test
	public void testSpreadOperatorInFunctionParameters() throws Exception
	{
		assertParseResult("function f (x, y, ...a) {return (x + y) * a.length;}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testSpreadOperatorInFunctionCall() throws Exception
	{
		assertParseResult("f(1, 2, ...params) === 9;" + EOL);
		assertNoErrors();
	}

	@Test
	public void testDefaultParameters() throws Exception
	{
		assertParseResult("function f (x, y = 7, z = 42) {return x + y + z;}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testExportFunction() throws Exception
	{
		assertParseResult("export function sum (x, y) {return x + y;}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testExportVar() throws Exception
	{
		assertParseResult("export var pi = 3.141593;" + EOL);
		assertNoErrors();
	}
	
	@Test
	public void testExportStar() throws Exception
	{
		parse("export * from 'lib/math';" + EOL);
		assertNoErrors();
	}
	
	@Test
	public void testExportDefault() throws Exception
	{
		assertParseResult("export default (x) => Math.exp(x);" + EOL);
		assertNoErrors();
	}
	
	@Test
	public void testImportStarAs() throws Exception
	{
		parse("import * as math from 'lib/math';" + EOL);
		assertNoErrors();
	}

	@Test
	public void testImportBoundNames() throws Exception
	{
		parse("import { sum, pi } from 'lib/math';" + EOL);
		assertNoErrors();
	}
	
	@Test
	public void testForOf() throws Exception
	{
		assertParseResult("for (let n of fibonacci) {console.log(n);}" + EOL);
		assertNoErrors();
	}
	
	@Test
	public void testFunctionParameterArrayDestructuring() throws Exception
	{
		assertParseResult("function f ([ name, val ]) {console.log(name, val);}" + EOL);
		assertNoErrors();
	}
	
	@Test
	public void testFunctionParameterObjectDestructuringWithAliases() throws Exception
	{
		assertParseResult("function g ({ name: n, val: v }) {console.log(n, v);}" + EOL);
		assertNoErrors();
	}
	
	@Test
	public void testFunctionParameterObjectDestructuring() throws Exception
	{
		assertParseResult("function h ({ name, val }) {console.log(name, val);}" + EOL);
		assertNoErrors();
	}

	private void assertNoErrors()
	{
		if (fParseResult.getErrors().isEmpty())
		{
			assertTrue(true);
			return;
		}
		StringBuilder builder = new StringBuilder();
		for (IParseError error : fParseResult.getErrors())
		{
			builder.append(error.toString());
			builder.append("\n");
		}
		builder.deleteCharAt(builder.length() - 1); // remove extra newline at end
		fail(builder.toString());
	}

	/**
	 * This method is not being used for formal testing, but it's useful to determine how effective
	 * {@link ParseNode#trimToSize()} is.
	 * 
	 * @throws Exception
	 */
//	public void trimToSize() throws Exception
//	{
//		ParseState parseState = new ParseState(getSource("performance/ext/ext-all-debug-w-comments.js"));
//		ASTUtil.showBeforeAndAfterTrim(parse(parseState));
//	}

	private IParseRootNode parse(String source) throws Exception
	{
		return parse(new ParseState(source));
	}

	private IParseRootNode parse(ParseState parseState) throws Exception
	{
		fParseResult = fParser.parse(parseState);
		return fParseResult.getRootNode();
	}

	// utility methods
	protected void assertParseErrors(String... messages)
	{
		List<IParseError> errors = fParseResult.getErrors();
		assertNotNull(errors);
		assertEquals(messages.length, errors.size());

		for (int i = 0; i < messages.length; i++)
		{
			assertEquals(messages[i], errors.get(i).getMessage());
		}
	}

	protected void assertParseResult(String source) throws Exception
	{
		assertParseResult(source, source);
	}

	protected void assertParseResult(String source, String expected) throws Exception
	{
		IParseNode result = parse(source);
		StringBuilder text = new StringBuilder();
		IParseNode[] children = result.getChildren();
		for (IParseNode child : children)
		{
			text.append(child).append(EOL);
		}
		assertEquals(expected, text.toString());
	}
}
