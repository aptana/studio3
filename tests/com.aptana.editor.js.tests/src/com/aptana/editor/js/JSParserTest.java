/**
 * This file Copyright (c) 2005-2009 Aptana, Inc. This program is
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
package com.aptana.editor.js;

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

	public void testComma() throws Exception
	{
		parseTest("abc = 10, def = 20;" + EOL); //$NON-NLS-1$
	}

	public void testGroup() throws Exception
	{
		parseTest("a = (3 + 4) * 5;" + EOL); //$NON-NLS-1$
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
