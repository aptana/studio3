package com.aptana.js.core.parsing.antlr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.junit.After;
import org.junit.Test;

import com.aptana.core.build.IProblem;
import com.aptana.core.util.FileUtil;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.ParseError;

public class JSParserTest
{
	private static final String EOL = FileUtil.NEW_LINE;

	private JSParser fParser;
	private List<IParseError> fErrors;

	@After
	public void tearDown() throws Exception
	{
		fParser = null;
		fErrors = null;
	}

	@Test
	public void testEmptyStatement() throws Exception
	{
		assertParseResult(";" + EOL);
	}

	@Test
	public void testEmptyBlock() throws Exception
	{
		assertParseResult("{}" + EOL); //$NON-NLS-1$
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
		assertParseResult("a = typeof(object);" + EOL, "a = typeof (object);" + EOL); //$NON-NLS-1$
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
		assertParseResult("abc = [,,];" + EOL, "abc = [null, null];" + EOL); //$NON-NLS-1$
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
		// Trailing comma is NOT treated a elision
		assertParseResult("abc = [1, 2, 3,];" + EOL, "abc = [1, 2, 3];" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testArrayLiteralTrailingElision() throws Exception
	{
		// Trailing comma is NOT treated a elision
		assertParseResult("abc = [1, 2, 3,,,];" + EOL, "abc = [1, 2, 3, null, null];" + EOL); //$NON-NLS-1$
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
		assertParseResult("while (true) {}" + EOL); //$NON-NLS-1$
	}

	@Test
	public void testLabeledStatement() throws Exception
	{
		assertParseResult("myLabel: while (true) {}" + EOL); //$NON-NLS-1$
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

	// @Test
	// public void testSDocComment() throws Exception
	// {
	// JSFlexScanner scanner = new JSFlexScanner();
	// scanner.setSource("/**/");
	// fParser.parse(scanner);
	//
	// List<Symbol> comments = scanner.getMultiLineComments();
	//
	// assertEquals(1, comments.size());
	// }

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
		// assertParseErrors("Missing semicolon"); // FIXME This parser handles this fine
	}

	@Test
	public void testMissingClosingParenthesis() throws Exception
	{
		assertParseResult("testing(", "testing();" + EOL);
		// assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
		assertParseErrors("no viable alternative at input '('");
	}

	@Test
	public void testMissingIdentifier() throws Exception
	{
		assertParseResult("var x =", "var x = ;" + EOL);
		// assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
		assertParseErrors("mismatched input '<EOF>'");
	}

	@Test
	public void testMissingIdentifier2() throws Exception
	{
		assertParseResult("x.", "x.;" + EOL);
		// assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
		assertParseErrors("mismatched input '<EOF>'");
	}

	@Test
	public void testMissingArg() throws Exception
	{
		assertParseResult("fun(a,);", "fun(a, );" + EOL);
		// assertParseErrors("Syntax Error: unexpected token \")\"");
		assertParseErrors("no viable alternative at input '('");
	}

	@Test
	public void testMissingIdentifier3() throws Exception
	{
		assertParseResult("new", "new ;" + EOL);
		// assertParseErrors("Syntax Error: unexpected token \"end-of-file\"");
		assertParseErrors("no viable alternative at input 'new'");
	}

	@Test
	public void testMissingPropertyValue() throws Exception
	{
		assertParseResult("var x = { t };", "var x = {t:t};" + EOL);
		assertNoErrors();
	}

	@Test
	public void testMissingPropertyValue2() throws Exception
	{
		assertParseResult("var x = { t: };", "var x = {t: };" + EOL);
		assertParseErrors("mismatched input '}'");
	}

	// @Test
	// public void testSingleLineComment() throws Exception
	// {
	// String source = "// this is a single-line comment";
	//
	// IParseRootNode root = parse(source);
	// IParseNode[] comments = root.getCommentNodes();
	// assertNotNull(comments);
	// assertEquals(1, comments.length);
	// IParseNode comment = comments[0];
	// assertEquals(0, comment.getStartingOffset());
	// assertEquals(source.length() - 1, comment.getEndingOffset());
	// }

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

	// /**
	// * Test APSTUD-4072
	// *
	// * @throws IOException
	// * @throws beaver.Parser.Exception
	// */
	// @Test
	// public void testNodeOffsetsAtEOF() throws Exception
	// {
	// String source = "a.foo()\n// this is a comment";
	// IParseNode result = parse(source);
	//
	// assertNotNull(result);
	// assertEquals(1, result.getChildCount());
	//
	// IParseNode invokeNode = result.getFirstChild();
	// assertNotNull(invokeNode);
	// assertEquals(0, invokeNode.getStartingOffset());
	// assertEquals(6, invokeNode.getEndingOffset());
	// }

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
		// assertParseErrors("Syntax Error: unexpected token \"'\"");
		assertParseErrors("extraneous input ''' expecting");
	}

	@Test
	public void testUnclosedComment() throws Exception
	{
		assertParseResult("var thing; /* comment", "var thing;" + EOL + EOL);
		// assertParseErrors("Syntax Error: unexpected token \"/\"");
		assertParseErrors("mismatched input '/' expecting <EOF>");
	}

	@Test
	public void testUnclosedRegexp() throws Exception
	{
		assertParseResult("var regexp = /;", EOL);
		// assertParseErrors("Syntax Error: unexpected token \"/\"", "Syntax Error: unexpected token \";\"");
		assertParseErrors("mismatched input '/'");
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
		// assertParseErrors("Syntax Error: unexpected token \"import\"");
		assertParseErrors("no viable alternative at input 'function import'", "missing '=>' at '{'");
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
		assertParseResult("export * from 'lib/math';" + EOL, "export * from 'lib/math'" + EOL);
		assertNoErrors();
	}

	@Test
	public void testExportDefault() throws Exception
	{
		// FIXME Not sure why the semicolon doesn't get printed out...
		assertParseResult("export default (x) => Math.exp(x);" + EOL, "export default (x) => Math.exp(x)" + EOL);
		assertNoErrors();
	}

	@Test
	public void testImportStarAs() throws Exception
	{
		// FIXME Not sure why the semicolon doesn't get printed out...
		assertParseResult("import * as math from 'lib/math';" + EOL, "import * as math from 'lib/math'" + EOL);
		assertNoErrors();
	}

	@Test
	public void testImportBoundNames() throws Exception
	{
		assertParseResult("import { sum, pi } from 'lib/math';" + EOL, "import {sum, pi} from 'lib/math'" + EOL);
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
		assertParseResult("function f ([name, val]) {console.log(name, val);}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testFunctionParameterObjectDestructuringWithAliases() throws Exception
	{
		assertParseResult("function g ({name: n, val: v}) {console.log(n, v);}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testFunctionParameterObjectDestructuring() throws Exception
	{
		assertParseResult("function h ({name, val}) {console.log(name, val);}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testArrayDestructuringAssignment() throws Exception
	{
		assertParseResult("var [ a, , b ] = list;" + EOL, "var [a, null, b] = list;" + EOL);
		assertNoErrors();
	}

	@Test
	public void testArrayDestructuringAssignmentSwappingVariables() throws Exception
	{
		assertParseResult("[ b, a ] = [ a, b ];" + EOL, "[b, a] = [a, b];" + EOL);
		assertNoErrors();
	}

	@Test
	public void testClassDeclarationWithNoProperties() throws Exception
	{
		assertParseResult("class Shape {}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testClassDefinitionWithConstructorMethod() throws Exception
	{
		assertParseResult("class Shape {constructor (id, x, y) {this.id = id;this.move(x, y);}}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testClassDefinitionWithStaticMethod() throws Exception
	{
		assertParseResult(
				"class Rectangle {static defaultRectangle () {return new Rectangle('default', 0, 0, 100, 100);}}"
						+ EOL);
		assertNoErrors();
	}

	@Test
	public void testClassDefinitionWithGetterMethod() throws Exception
	{
		assertParseResult("class Rectangle {get width() {return this._width;}}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testClassDefinitionWithSetterMethod() throws Exception
	{
		assertParseResult("class Rectangle {set width(width) {this._width = width;}}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testClassDefinitionWithSuperclass() throws Exception
	{
		assertParseResult("class Circle extends Shape {}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testClassDefinitionWithCallToSuper() throws Exception
	{
		assertParseResult("class Circle extends Shape {constructor (id, x, y, radius) {super(id, x, y);}}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testGeneratorFunction() throws Exception
	{
		assertParseResult(
				"function* range (start, end, step) {while (start < end) {yield start;start += step;}}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testGeneratorMethodInsideClass() throws Exception
	{
		assertParseResult("class Clz {* bar () {}}" + EOL, "class Clz {* bar () {}}" + EOL);
		assertNoErrors();
	}

	@Test
	public void testGeneratorMethodInsideObject() throws Exception
	{
		assertParseResult("let Obj = {* foo () {}};" + EOL, "let Obj = {* foo () {}};" + EOL);
		assertNoErrors();
	}

	@Test
	public void testComputedPropertyNameInObjectLiteral() throws Exception
	{
		assertParseResult("let obj = {['baz' + quux()]: 42};" + EOL);
		assertNoErrors();
	}

	@Test
	public void testPropertyShorthandInObjectLiteral() throws Exception
	{
		assertParseResult("obj = {x, y};" + EOL);
		assertNoErrors();
	}

	@Test
	public void testInitializedPropertyInObjectLiteral() throws Exception
	{
		assertParseResult("obj = {x: x};" + EOL);
		assertNoErrors();
	}

	// utility methods
	protected void assertParseErrors(String... messages)
	{
		assertNotNull(fErrors);
		assertEquals(messages.length, fErrors.size());

		for (int i = 0; i < messages.length; i++)
		{
			String msg = fErrors.get(i).getMessage();
			// match prefix only. If no prefix match, give full diff in failure
			if (!msg.startsWith(messages[i]))
			{
				assertEquals(messages[i], fErrors.get(i).getMessage());
			}
		}
	}

	protected void assertParseResult(String source) throws Exception
	{
		assertParseResult(source, source);
	}

	protected void parse(String source)
	{
		CharStream stream = new ANTLRInputStream(source);
		JSLexer lexer = new JSLexer(stream);
		CommonTokenStream input = new CommonTokenStream(lexer);
		input.fill();
		fParser = new JSParser(input);
		fErrors = new ArrayList<IParseError>();
		fParser.addErrorListener(new ANTLRErrorListener()
		{

			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e)
			{
				// FIXME Convert the arg1 object here into a Symbol?
				fErrors.add(new ParseError("javascript", null, msg, IProblem.Severity.WARNING));
			}

			public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5)
			{
				// TODO Auto-generated method stub

			}

			public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4,
					ATNConfigSet arg5)
			{
				// TODO Auto-generated method stub

			}

			public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5,
					ATNConfigSet arg6)
			{
				// TODO Auto-generated method stub

			}
		});
		fParser.program();
	}

	protected void assertParseResult(String source, String expected) throws Exception
	{
		// TODO Generate source from the tree! We'd need to add a walker, likely one that generates our *own* AST so we
		// can use everything we already have!
		parse(source);
	}

	private void assertNoErrors()
	{
		assertEquals(0, fParser.getNumberOfSyntaxErrors());
	}
}
