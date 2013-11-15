/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import beaver.Symbol;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.ASTUtil;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class JSParserTest extends TestCase
{
	private static final String EOL = FileUtil.NEW_LINE;

	private JSParser fParser;

	private ParseResult fParseResult;

	@Override
	protected void setUp() throws Exception
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

	public void testEmptyStatement() throws Exception
	{
		assertParseResult(";" + EOL);
	}

	public void testEmptyBlock() throws Exception
	{
		assertParseResult("{}" + EOL); //$NON-NLS-1$
	}

	public void testAssign() throws Exception
	{
		assertParseResult("a = 10;" + EOL); //$NON-NLS-1$
	}

	public void testAddAndAssign() throws Exception
	{
		assertParseResult("a += 10;" + EOL); //$NON-NLS-1$
	}

	public void testArithmeticShiftRightAndAssign() throws Exception
	{
		assertParseResult("a >>>= 10;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseAndAndAssign() throws Exception
	{
		assertParseResult("a &= 10;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseOrAndAssign() throws Exception
	{
		assertParseResult("a |= 10;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseXorAndAssign() throws Exception
	{
		assertParseResult("a ^= 10;" + EOL); //$NON-NLS-1$
	}

	public void testDivideAndAssign() throws Exception
	{
		assertParseResult("a /= 10;" + EOL); //$NON-NLS-1$
	}

	public void testModAndAssign() throws Exception
	{
		assertParseResult("a %= 10;" + EOL); //$NON-NLS-1$
	}

	public void testMultiplyAndAssign() throws Exception
	{
		assertParseResult("a *= 10;" + EOL); //$NON-NLS-1$
	}

	public void testShiftLeftAndAssign() throws Exception
	{
		assertParseResult("a <<= 10;" + EOL); //$NON-NLS-1$
	}

	public void testShiftRightAndAssign() throws Exception
	{
		assertParseResult("a >>= 10;" + EOL); //$NON-NLS-1$
	}

	public void testSubtractAndAssign() throws Exception
	{
		assertParseResult("a -= 10;" + EOL); //$NON-NLS-1$
	}

	public void testAdd() throws Exception
	{
		assertParseResult("5 + 5;" + EOL); //$NON-NLS-1$
	}

	public void testEqual() throws Exception
	{
		assertParseResult("abc = def;" + EOL); //$NON-NLS-1$
	}

	public void testGreaterThan() throws Exception
	{
		assertParseResult("abc > def;" + EOL); //$NON-NLS-1$
	}

	public void testGreaterThanOrEqual() throws Exception
	{
		assertParseResult("abc >= def;" + EOL); //$NON-NLS-1$
	}

	public void testIdentity() throws Exception
	{
		assertParseResult("abc === def;" + EOL); //$NON-NLS-1$
	}

	public void testIn() throws Exception
	{
		assertParseResult("\"abc\" in def;" + EOL); //$NON-NLS-1$
	}

	public void testInstanceOf() throws Exception
	{
		assertParseResult("abc instanceof def;" + EOL); //$NON-NLS-1$
	}

	public void testLessThan() throws Exception
	{
		assertParseResult("abc < def;" + EOL); //$NON-NLS-1$
	}

	public void testLessThanOrEqual() throws Exception
	{
		assertParseResult("abc <= def;" + EOL); //$NON-NLS-1$
	}

	public void testLogicalAnd() throws Exception
	{
		assertParseResult("abc && def;" + EOL); //$NON-NLS-1$
	}

	public void testLogicalOr() throws Exception
	{
		assertParseResult("abc || def;" + EOL); //$NON-NLS-1$
	}

	public void testNotEqual() throws Exception
	{
		assertParseResult("abc != def;" + EOL); //$NON-NLS-1$
	}

	public void testNotIdentity() throws Exception
	{
		assertParseResult("abc !== def;" + EOL); //$NON-NLS-1$
	}

	public void testArithmeticShiftRight() throws Exception
	{
		assertParseResult("abc >>> 5;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseAnd() throws Exception
	{
		assertParseResult("abc & 5;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseOr() throws Exception
	{
		assertParseResult("abc | 5;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseXor() throws Exception
	{
		assertParseResult("abc ^ 5;" + EOL); //$NON-NLS-1$
	}

	public void testDivide() throws Exception
	{
		assertParseResult("abc / 5;" + EOL); //$NON-NLS-1$
	}

	public void testMod() throws Exception
	{
		assertParseResult("abc % 5;" + EOL); //$NON-NLS-1$
	}

	public void testMultiply() throws Exception
	{
		assertParseResult("abc * 5;" + EOL); //$NON-NLS-1$
	}

	public void testShiftLeft() throws Exception
	{
		assertParseResult("abc << 5;" + EOL); //$NON-NLS-1$
	}

	public void testShiftRight() throws Exception
	{
		assertParseResult("abc >> 5;" + EOL); //$NON-NLS-1$
	}

	public void testSubtract() throws Exception
	{
		assertParseResult("abc - 5;" + EOL); //$NON-NLS-1$
	}

	public void testGetElement() throws Exception
	{
		assertParseResult("abc[10];" + EOL); //$NON-NLS-1$
	}

	public void testGetProperty() throws Exception
	{
		assertParseResult("abc.def;" + EOL); //$NON-NLS-1$
	}

	public void testLogicalNot() throws Exception
	{
		assertParseResult("a = !false;" + EOL); //$NON-NLS-1$
	}

	public void testBitwiseNot() throws Exception
	{
		assertParseResult("a = ~10;" + EOL); //$NON-NLS-1$
	}

	public void testNegate() throws Exception
	{
		assertParseResult("a = -10;" + EOL); //$NON-NLS-1$
	}

	public void testPositive() throws Exception
	{
		assertParseResult("a = +10;" + EOL); //$NON-NLS-1$
	}

	public void testPreDecrement() throws Exception
	{
		assertParseResult("--a;" + EOL); //$NON-NLS-1$
	}

	public void testPreIncrement() throws Exception
	{
		assertParseResult("++a;" + EOL); //$NON-NLS-1$
	}

	public void testPostDecrement() throws Exception
	{
		assertParseResult("a--;" + EOL); //$NON-NLS-1$
	}

	public void testPostIncrement() throws Exception
	{
		assertParseResult("a++;" + EOL); //$NON-NLS-1$
	}

	public void testDelete() throws Exception
	{
		assertParseResult("delete obj.prop;" + EOL); //$NON-NLS-1$
	}

	public void testTypeof1() throws Exception
	{
		assertParseResult("a = typeof(object);" + EOL); //$NON-NLS-1$
	}

	public void testTypeof2() throws Exception
	{
		assertParseResult("a = typeof object;" + EOL); //$NON-NLS-1$
	}

	public void testVoid() throws Exception
	{
		assertParseResult("void (true);" + EOL); //$NON-NLS-1$
	}

	public void testNull() throws Exception
	{
		assertParseResult("null;" + EOL); //$NON-NLS-1$
	}

	public void testTrue() throws Exception
	{
		assertParseResult("true;" + EOL); //$NON-NLS-1$
	}

	public void testFalse() throws Exception
	{
		assertParseResult("false;" + EOL); //$NON-NLS-1$
	}

	public void testNumber() throws Exception
	{
		assertParseResult("10.3;" + EOL); //$NON-NLS-1$
	}

	public void testString() throws Exception
	{
		assertParseResult("\"this is a string\";" + EOL); //$NON-NLS-1$
	}

	public void testRegex() throws Exception
	{
		assertParseResult("/abc/;" + EOL); //$NON-NLS-1$
	}

	public void testRegexWithEscapedSlash() throws Exception
	{
		assertParseResult("/a\\/bc/;" + EOL); //$NON-NLS-1$
	}

	public void testRegexWithTrailingModifiers() throws Exception
	{
		assertParseResult("/abc/ig;" + EOL); //$NON-NLS-1$
	}

	public void testComplexRegex() throws Exception
	{
		assertParseResult("/^[1-3]{0,4}\\/.*$/;" + EOL); //$NON-NLS-1$
	}

	public void testComplexRegexWithModifiers() throws Exception
	{
		assertParseResult("/^[1-3]{0,4}\\/.*$/gim;" + EOL); //$NON-NLS-1$
	}

	public void testThis() throws Exception
	{
		assertParseResult("this;" + EOL); //$NON-NLS-1$
	}

	public void testIdentifier() throws Exception
	{
		assertParseResult("abc;" + EOL); //$NON-NLS-1$
	}

	public void testInvoke() throws Exception
	{
		assertParseResult("x = abc();" + EOL); //$NON-NLS-1$
	}

	public void testInvokeDereferenceArray() throws Exception
	{
		assertParseResult("x = abc()[0];" + EOL); //$NON-NLS-1$
	}

	public void testInvokePropertyReference() throws Exception
	{
		assertParseResult("x = abc().abc;" + EOL); //$NON-NLS-1$
	}

	public void testInvokeWithArgs() throws Exception
	{
		assertParseResult("x = abc(10, 20, 30);" + EOL); //$NON-NLS-1$
	}

	public void testInvokeInvoke() throws Exception
	{
		assertParseResult("x = abc()();" + EOL); //$NON-NLS-1$
	}

	public void testVar() throws Exception
	{
		assertParseResult("var abc = 10;" + EOL); //$NON-NLS-1$
	}

	public void testIfInIfElse() throws Exception
	{
		assertParseResult("if (true) if (false) a; else b; else c;" + EOL); //$NON-NLS-1$
	}

	public void testVarInIfElse() throws Exception
	{
		assertParseResult("if (true) var abc = 10; else true;" + EOL); //$NON-NLS-1$
	}

	public void testWithInIfElse() throws Exception
	{
		assertParseResult("if (true) with (abc) a++; else true;" + EOL); //$NON-NLS-1$
	}

	public void testLabelInIfElse() throws Exception
	{
		assertParseResult("if (true) label: a++; else true;" + EOL); //$NON-NLS-1$
	}

	public void testThisInIfElse() throws Exception
	{
		assertParseResult("if (true) this.abc = 10; else true;" + EOL); //$NON-NLS-1$
	}

	public void testInvokeInIfElse() throws Exception
	{
		assertParseResult("if (true) abc(); else true;" + EOL); //$NON-NLS-1$
	}

	public void testInvokeDereferenceArrayInIfElse() throws Exception
	{
		assertParseResult("if (true) abc()[0]; else true;" + EOL); //$NON-NLS-1$
	}

	public void testInvokePropertyReferenceInIfElse() throws Exception
	{
		assertParseResult("if (true) abc().abc; else true;" + EOL); //$NON-NLS-1$
	}

	public void testInvokeInvokeInIfElse() throws Exception
	{
		assertParseResult("if (true) abc()(); else true;" + EOL); //$NON-NLS-1$
	}

	public void testEmptyInIfElse() throws Exception
	{
		// TODO: should be a semicolon between (true) and else
		assertParseResult("if (true)  else true;" + EOL); //$NON-NLS-1$
	}

	public void testDoWhileInIfElse() throws Exception
	{
		assertParseResult("if (true) do a++; while (a < 10); else true;" + EOL); //$NON-NLS-1$
	}

	public void testWhileInIfElse() throws Exception
	{
		assertParseResult("if (true) while (a < 10) a++; else true;" + EOL); //$NON-NLS-1$
	}

	public void testForBodyOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (;;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForAdvanceOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (;; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForConditionOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (; a < 10;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForNoInitializeInIfElse() throws Exception
	{
		assertParseResult("if (true) for (; a < 10; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForInitializeOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0;;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForVarInitializeOnlyInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0;;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForNoConditionInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0;; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForVarNoConditionInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0;; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForNoAdvanceInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0; a < 10;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForVarNoAdvanceInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0; a < 10;) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a = 0; a < 10; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForVarInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a = 0; a < 10; a++) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForInInIfElse() throws Exception
	{
		assertParseResult("if (true) for (a in obj) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testForVarInInIfElse() throws Exception
	{
		assertParseResult("if (true) for (var a in obj) {a;} else true;" + EOL); //$NON-NLS-1$
	}

	public void testTryCatch() throws Exception
	{
		assertParseResult("try {} catch (e) {}" + EOL); //$NON-NLS-1$
	}

	public void testTryFinally() throws Exception
	{
		assertParseResult("try {} finally {}" + EOL); //$NON-NLS-1$
	}

	public void testTryCatchFinally() throws Exception
	{
		assertParseResult("try {} catch (e) {} finally {}" + EOL); //$NON-NLS-1$
	}

	public void testConditional() throws Exception
	{
		assertParseResult("(abc) ? true : false;" + EOL); //$NON-NLS-1$
	}

	public void testEmptyFunction() throws Exception
	{
		assertParseResult("function abc () {}" + EOL); //$NON-NLS-1$
	}

	public void testEmptyFunctionWithOneParameter() throws Exception
	{
		assertParseResult("function abc (a) {}" + EOL); //$NON-NLS-1$
	}

	public void testEmptyFunctionWithParameters() throws Exception
	{
		assertParseResult("function abc (a, b, c) {}" + EOL); //$NON-NLS-1$
	}

	public void testEmptyArray() throws Exception
	{
		assertParseResult("abc = [];" + EOL); //$NON-NLS-1$
	}

	public void testElisionArray() throws Exception
	{
		assertParseResult("abc = [,,];" + EOL, "abc = [null, null, null];" + EOL); //$NON-NLS-1$
	}

	public void testLeadingElisionArray() throws Exception
	{
		assertParseResult("abc = [,,10];" + EOL, "abc = [null, null, 10];" + EOL); //$NON-NLS-1$
	}

	public void testElisionMidArray() throws Exception
	{
		assertParseResult("abc = [10,,10];" + EOL, "abc = [10, null, 10];" + EOL); //$NON-NLS-1$
	}

	public void testArrayLiteral() throws Exception
	{
		assertParseResult("abc = [1, 2, 3];" + EOL); //$NON-NLS-1$
	}

	public void testArrayLiteralTrailingComma() throws Exception
	{
		assertParseResult("abc = [1, 2, 3,];" + EOL, "abc = [1, 2, 3, null];" + EOL); //$NON-NLS-1$
	}

	public void testArrayLiteralTrailingElision() throws Exception
	{
		assertParseResult("abc = [1, 2, 3,,,];" + EOL, "abc = [1, 2, 3, null, null, null];" + EOL); //$NON-NLS-1$
	}

	public void testGroup() throws Exception
	{
		assertParseResult("a = (3 + 4) * 5;" + EOL); //$NON-NLS-1$
	}

	public void testComma() throws Exception
	{
		assertParseResult("abc = 10, 20;" + EOL); //$NON-NLS-1$
	}

	public void testConstruct() throws Exception
	{
		assertParseResult("a = new Object(\"test\");" + EOL); //$NON-NLS-1$
	}

	public void testConstructConstruct() throws Exception
	{
		assertParseResult("a = new new Thing();" + EOL); //$NON-NLS-1$
	}

	public void testConstructWithoutArgs() throws Exception
	{
		assertParseResult("a = new Object();" + EOL); //$NON-NLS-1$
	}

	public void testObjectLiteral() throws Exception
	{
		assertParseResult("abc = {name: \"Name\", index: 2, id: 10};" + EOL); //$NON-NLS-1$
	}

	public void testObjectLiteralWithTrailingComma() throws Exception
	{
		assertParseResult(
				"abc = {name: \"Name\", index: 2, id: 10,};" + EOL, "abc = {name: \"Name\", index: 2, id: 10};" + EOL); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testObjectLiteralWithNumberPropertyName() throws Exception
	{
		assertParseResult("abc = {20: \"Name\"};" + EOL); //$NON-NLS-1$
	}

	public void testThrow() throws Exception
	{
		assertParseResult("throw new Error(\"error\");" + EOL); //$NON-NLS-1$
	}

	public void testWhile() throws Exception
	{
		assertParseResult("while (true) {}" + EOL); //$NON-NLS-1$
	}

	public void testLabeledStatement() throws Exception
	{
		assertParseResult("myLabel: while (true) {}" + EOL); //$NON-NLS-1$
	}

	public void testContinue() throws Exception
	{
		assertParseResult("while (true) {continue;}" + EOL); //$NON-NLS-1$
	}

	public void testBreak() throws Exception
	{
		assertParseResult("while (true) {break;}" + EOL); //$NON-NLS-1$
	}

	public void testDefault() throws Exception
	{
		assertParseResult("switch (abc) {default: break;}" + EOL); //$NON-NLS-1$
	}

	public void testCase() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;}" + EOL); //$NON-NLS-1$
	}

	public void testEmptySwitch() throws Exception
	{
		assertParseResult("switch (abc) {}" + EOL); //$NON-NLS-1$
	}

	public void testSwitchDefaultLast() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;default: break;}" + EOL); //$NON-NLS-1$
	}

	public void testSwitchDefaultFirst() throws Exception
	{
		assertParseResult("switch (abc) {default: break;case 10: break;}" + EOL); //$NON-NLS-1$
	}

	public void testSwitchDefaultInMiddle() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;default: break;case 20: break;}" + EOL); //$NON-NLS-1$
	}

	public void testSwitchMultipleCasesNoDefault() throws Exception
	{
		assertParseResult("switch (abc) {case 10: break;case 20: break;}" + EOL); //$NON-NLS-1$
	}

	public void testEmptyCase() throws Exception
	{
		assertParseResult("switch (abc) {case 10: }" + EOL); //$NON-NLS-1$
	}

	public void testEmptyDefault() throws Exception
	{
		assertParseResult("switch (abc) {default: }" + EOL); //$NON-NLS-1$
	}

	public void testReturn() throws Exception
	{
		assertParseResult("function abc () {return;}" + EOL); //$NON-NLS-1$
	}

	public void testReturnWithExpression() throws Exception
	{
		assertParseResult("function abc () {return false;}" + EOL); //$NON-NLS-1$
	}

	public void testIf() throws Exception
	{
		assertParseResult("if (a < b) {a = 10;} else {a = 20;}" + EOL); //$NON-NLS-1$
	}

	public void testIfComma() throws Exception
	{
		assertParseResult("if (a < b, b < c) {a = 10;} else {a = 20;}" + EOL); //$NON-NLS-1$
	}

	public void testIfWithoutElse() throws Exception
	{
		assertParseResult("if (a < b) {a = 10;}" + EOL); //$NON-NLS-1$
	}

	public void testDo() throws Exception
	{
		assertParseResult("do {a++;} while (a < 10);" + EOL); //$NON-NLS-1$
	}

	public void testForBodyOnly() throws Exception
	{
		assertParseResult("for (;;) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForAdvanceOnly() throws Exception
	{
		assertParseResult("for (;; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForConditionOnly() throws Exception
	{
		assertParseResult("for (; a < 10;) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForNoInitialize() throws Exception
	{
		assertParseResult("for (; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForInitializeOnly() throws Exception
	{
		assertParseResult("for (a = 0;;) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForInitializeComma() throws Exception
	{
		assertParseResult("for (a = 0, b = 1;;) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForNoCondition() throws Exception
	{
		assertParseResult("for (a = 0;; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForNoAdvance() throws Exception
	{
		assertParseResult("for (a = 0; a < 10;) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testFor() throws Exception
	{
		assertParseResult("for (a = 0; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarInitializeOnly() throws Exception
	{
		assertParseResult("for (var a = 0;;) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarNoCondition() throws Exception
	{
		assertParseResult("for (var a = 0;; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarNoAdvance() throws Exception
	{
		assertParseResult("for (var a = 0; a < 10;) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVar() throws Exception
	{
		assertParseResult("for (var a = 0; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVars() throws Exception
	{
		assertParseResult("for (var a = 0, b = 0; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarRelational() throws Exception
	{
		assertParseResult("for (var a = 0, b = a < 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarEquality() throws Exception
	{
		assertParseResult("for (var a = 0, b = a == 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarBitwiseAnd() throws Exception
	{
		assertParseResult("for (var a = 0, b = a & 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarBitwiseXor() throws Exception
	{
		assertParseResult("for (var a = 0, b = a ^ 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarBitwiseOr() throws Exception
	{
		assertParseResult("for (var a = 0, b = a | 10; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarLogicalAnd() throws Exception
	{
		assertParseResult("for (var a = 0, b = a && c; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarLogicalOr() throws Exception
	{
		assertParseResult("for (var a = 0, b = a || c; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForVarConditional() throws Exception
	{
		assertParseResult("for (var a = 0, b = a ? c : d; a < 10; a++) {a;}" + EOL); //$NON-NLS-1$
	}

	public void testForIn() throws Exception
	{
		assertParseResult("for (a in obj) {a;}" + EOL);
	}

	public void testForVarIn() throws Exception
	{
		assertParseResult("for (var a in obj) {a;}" + EOL);
	}

	// bug and regression tests here

	public void testSDocComment() throws Exception
	{
		JSFlexScanner scanner = new JSFlexScanner();
		scanner.setSource("/**/");
		fParser.parse(scanner);

		List<Symbol> comments = scanner.getMultiLineComments();

		assertEquals(1, comments.size());
	}

	public void testPlusNegativeNumber() throws Exception
	{
		assertParseResult("var x = 5 + -3" + EOL);
		assertParseResult("var x = 5+ -3" + EOL, "var x = 5 + -3" + EOL);
		assertParseResult("var x = 5 +-3" + EOL, "var x = 5 + -3" + EOL);
		assertParseResult("var x = 5+-3" + EOL, "var x = 5 + -3" + EOL);
	}

	public void testPlusPositiveNumber() throws Exception
	{
		assertParseResult("var x = 5 + +3" + EOL);
		assertParseResult("var x = 5+ +3" + EOL, "var x = 5 + +3" + EOL);

		// NOTE: The following commented tests are currently failing
		// parseTest("var x = 5 ++3" + EOL, "var x = 5 + +3" + EOL);
		// parseTest("var x = 5++3" + EOL, "var x = 5 + +3" + EOL);
	}

	public void testMinusNegativeNumber() throws Exception
	{
		assertParseResult("var x = 5 - -3" + EOL);
		assertParseResult("var x = 5- -3" + EOL, "var x = 5 - -3" + EOL);

		// NOTE: The following commented tests are currently failing
		// parseTest("var x = 5 --3" + EOL, "var x = 5 - -3" + EOL);
		// parseTest("var x = 5--3" + EOL, "var x = 5 - -3" + EOL);
	}

	public void testMinusPositiveNumber() throws Exception
	{
		assertParseResult("var x = 5 - +3" + EOL);
		assertParseResult("var x = 5- +3" + EOL, "var x = 5 - +3" + EOL);
		assertParseResult("var x = 5 -+3" + EOL, "var x = 5 - +3" + EOL);
		assertParseResult("var x = 5-+3" + EOL, "var x = 5 - +3" + EOL);
	}

	// begin recovery strategy tests

	public void testMissingSemicolon() throws Exception
	{
		assertParseResult("abc", "abc;" + EOL);
		assertParseErrors("Missing semicolon");
	}

	public void testMissingClosingParenthesis() throws Exception
	{
		assertParseResult("testing(", "testing();" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	public void testMissingIdentifier() throws Exception
	{
		assertParseResult("var x =", "var x = " + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	public void testMissingIdentifier2() throws Exception
	{
		assertParseResult("x.", "x.;" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	public void testMissingArg() throws Exception
	{
		assertParseResult("fun(a,);", "fun(a, );" + EOL);
		assertParseErrors("Syntax Error: unexpected token \")\"");
	}

	public void testMissingIdentifier3() throws Exception
	{
		assertParseResult("new", "new ;" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
	}

	public void testMissingPropertyValue() throws Exception
	{
		assertParseResult("var x = { t };", "var x = {t: };" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"}\"");
	}

	public void testMissingPropertyValue2() throws Exception
	{
		assertParseResult("var x = { t: };", "var x = {t: };" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"}\"");
	}

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
	public void testSwitchWithoutExpression() throws Exception
	{
		assertParseResult("switch () {}" + EOL);
	}

	/**
	 * test TISTUD-1269 (part 2)
	 * 
	 * @throws Exception
	 */
	public void testCaseWithPartialIdentifier() throws Exception
	{
		assertParseResult("switch (id) {case Ti.}", "switch (id) {case Ti.: }" + EOL);
	}

	public void testUnclosedString() throws Exception
	{
		assertParseResult("var string = 'something", "var string = " + EOL);
		assertParseErrors("Syntax Error: unexpected token \"'\"");
	}

	public void testUnclosedComment() throws Exception
	{
		assertParseResult("var thing; /* comment", "var thing;" + EOL + EOL);
		assertParseErrors("Syntax Error: unexpected token \"/\"");
	}

	public void testUnclosedRegexp() throws Exception
	{
		assertParseResult("var regexp = /;", EOL);
		assertParseErrors("Syntax Error: unexpected token \"/\"", "Syntax Error: unexpected token \";\"");
	}

	public void testReservedWordAsPropertyName() throws Exception
	{
		assertParseResult("this.default = 1;" + EOL);
		assertTrue(fParseResult.getErrors().isEmpty());
	}

	public void testReservedWordAsPropertyName2() throws Exception
	{
		assertParseResult("a[\"public\"] = 1;" + EOL);
		assertTrue(fParseResult.getErrors().isEmpty());
	}

	public void testReservedWordAsPropertyName3() throws Exception
	{
		assertParseResult("a = {default: \"test\"};" + EOL);
		assertTrue(fParseResult.getErrors().isEmpty());
	}

	public void testReservedWordAsFunctionName() throws Exception
	{
		parse("function import() {};" + EOL);
		assertParseErrors("Syntax Error: unexpected token \"import\"");
	}

	/**
	 * This method is not being used for formal testing, but it's useful to determine how effective
	 * {@link ParseNode#trimToSize()} is.
	 * 
	 * @throws Exception
	 */
	public void trimToSize() throws Exception
	{
		ParseState parseState = new ParseState(getSource("performance/ext/ext-all-debug-w-comments.js"));
		ASTUtil.showBeforeAndAfterTrim(parse(parseState));
	}

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
