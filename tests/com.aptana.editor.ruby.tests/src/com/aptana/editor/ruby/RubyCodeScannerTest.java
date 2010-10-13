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
package com.aptana.editor.ruby;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class RubyCodeScannerTest extends AbstractTokenScannerTestCase
{

	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new RubyCodeScanner()
		{
			protected IToken getToken(String tokenName)
			{
				return new Token(tokenName);
			};
		};
	}

	private void setUpScanner(String code)
	{
		setUpScanner(code, 0, code.length());
	}

	private void setUpScanner(String code, int offset, int length)
	{
		Document doc = new Document(code);
		scanner.setRange(doc, offset, length);
	}

	private void assertToken(String scope, int offset, int length)
	{
		// FIXME MErge with AbstractTokenScannerTestCase.assertToken
		IToken token = scanner.nextToken();
		assertEquals("Offsets don't match", offset, scanner.getTokenOffset());
		assertEquals("Lengths don't match", length, scanner.getTokenLength());
		assertEquals("Token scope doesn't match", scope, token.getData());
	}

	public void testNoParensNextIdentifierIsntParameter()
	{
		String code = "def denominator\nmethod_call\nend";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 11); // 'denominator'
		assertToken("default.ruby", 15, 1); // '\n'
		assertToken("default.ruby", 16, 11); // 'method_call'
		assertToken("default.ruby", 27, 1); // '\n'
		assertToken("keyword.control.ruby", 28, 3); // 'end'
	}

	public void testMethodDefinition()
	{
		String code = "def denominator() 0 end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 11); // 'denominator'
		assertToken("default.ruby", 15, 1); // '('
		assertToken("default.ruby", 16, 1); // ')'
		assertToken("default.ruby", 17, 1); // ' '
		assertToken("constant.numeric.ruby", 18, 1); // '0'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("keyword.control.ruby", 20, 3); // 'end'
	}

	public void testSpecialCompareMethodDefinition()
	{
		String code = "def <=>(other) 0 end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 3); // '<=>'
		assertToken("default.ruby", 7, 1); // '('
		assertToken("variable.parameter.ruby", 8, 5); // 'other'
		assertToken("default.ruby", 13, 1); // ')'
		assertToken("default.ruby", 14, 1); // ' '
		assertToken("constant.numeric.ruby", 15, 1); // '0'
		assertToken("default.ruby", 16, 1); // ' '
		assertToken("keyword.control.ruby", 17, 3); // 'end'
	}

	public void testPercentMethodDefinition()
	{
		String code = "def %(other) 0.0 || Rational.new end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '%'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("constant.numeric.ruby", 13, 3); // '0.0'
		assertToken("default.ruby", 16, 1); // ' '
		assertToken("keyword.operator.logical.ruby", 17, 2); // '||'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("support.class.ruby", 20, 8); // 'Rational'
		assertToken("default.ruby", 28, 1); // '.'
		assertToken("default.ruby", 29, 3); // 'new'
		assertToken("default.ruby", 32, 1); // ' '
		assertToken("keyword.control.ruby", 33, 3); // 'end'
	}

	public void testMultiplyMethodDefinition()
	{
		String code = "def *(other) 0.0 || Rational.new end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '*'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("constant.numeric.ruby", 13, 3); // '0.0'
		assertToken("default.ruby", 16, 1); // ' '
		assertToken("keyword.operator.logical.ruby", 17, 2); // '||'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("support.class.ruby", 20, 8); // 'Rational'
		assertToken("default.ruby", 28, 1); // '.'
		assertToken("default.ruby", 29, 3); // 'new'
		assertToken("default.ruby", 32, 1); // ' '
		assertToken("keyword.control.ruby", 33, 3); // 'end'
	}

	public void testPowerMethodDefinition()
	{
		String code = "def **(other) 0.0 || Rational.new end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '**'
		assertToken("default.ruby", 6, 1); // '('
		assertToken("variable.parameter.ruby", 7, 5); // 'other'
		assertToken("default.ruby", 12, 1); // ')'
		assertToken("default.ruby", 13, 1); // ' '
		assertToken("constant.numeric.ruby", 14, 3); // '0.0'
		assertToken("default.ruby", 17, 1); // ' '
		assertToken("keyword.operator.logical.ruby", 18, 2); // '||'
		assertToken("default.ruby", 20, 1); // ' '
		assertToken("support.class.ruby", 21, 8); // 'Rational'
		assertToken("default.ruby", 29, 1); // '.'
		assertToken("default.ruby", 30, 3); // 'new'
		assertToken("default.ruby", 33, 1); // ' '
		assertToken("keyword.control.ruby", 34, 3); // 'end'
	}

	public void testPlusMethodDefinition()
	{
		String code = "def +(other) 0.0 || Rational.new end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '+'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("constant.numeric.ruby", 13, 3); // '0.0'
		assertToken("default.ruby", 16, 1); // ' '
		assertToken("keyword.operator.logical.ruby", 17, 2); // '||'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("support.class.ruby", 20, 8); // 'Rational'
		assertToken("default.ruby", 28, 1); // '.'
		assertToken("default.ruby", 29, 3); // 'new'
		assertToken("default.ruby", 32, 1); // ' '
		assertToken("keyword.control.ruby", 33, 3); // 'end'
	}

	public void testMinusMethodDefinition()
	{
		String code = "def *(other) 0.0 || Rational.new end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '-'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("constant.numeric.ruby", 13, 3); // '0.0'
		assertToken("default.ruby", 16, 1); // ' '
		assertToken("keyword.operator.logical.ruby", 17, 2); // '||'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("support.class.ruby", 20, 8); // 'Rational'
		assertToken("default.ruby", 28, 1); // '.'
		assertToken("default.ruby", 29, 3); // 'new'
		assertToken("default.ruby", 32, 1); // ' '
		assertToken("keyword.control.ruby", 33, 3); // 'end'
	}

	public void testDivideMethodDefinition()
	{
		String code = "def /(other) 0.0 || Rational.new end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '/'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("constant.numeric.ruby", 13, 3); // '0.0'
		assertToken("default.ruby", 16, 1); // ' '
		assertToken("keyword.operator.logical.ruby", 17, 2); // '||'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("support.class.ruby", 20, 8); // 'Rational'
		assertToken("default.ruby", 28, 1); // '.'
		assertToken("default.ruby", 29, 3); // 'new'
		assertToken("default.ruby", 32, 1); // ' '
		assertToken("keyword.control.ruby", 33, 3); // 'end'
	}

	public void testEqualMethodDefinition()
	{
		String code = "def ==(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '=='
		assertToken("default.ruby", 6, 1); // '('
		assertToken("variable.parameter.ruby", 7, 5); // 'other'
		assertToken("default.ruby", 12, 1); // ')'
		assertToken("default.ruby", 13, 1); // ' '
		assertToken("variable.other.constant.ruby", 14, 7); // 'BOOLEAN'
		assertToken("default.ruby", 21, 1); // ' '
		assertToken("keyword.control.ruby", 22, 3); // 'end'
	}

	public void testTripleEqualMethodDefinition()
	{
		String code = "def ===(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 3); // '==='
		assertToken("default.ruby", 7, 1); // '('
		assertToken("variable.parameter.ruby", 8, 5); // 'other'
		assertToken("default.ruby", 13, 1); // ')'
		assertToken("default.ruby", 14, 1); // ' '
		assertToken("variable.other.constant.ruby", 15, 7); // 'BOOLEAN'
		assertToken("default.ruby", 22, 1); // ' '
		assertToken("keyword.control.ruby", 23, 3); // 'end'
	}

	public void testGreaterThanOrEqualMethodDefinition()
	{
		String code = "def >=(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '>='
		assertToken("default.ruby", 6, 1); // '('
		assertToken("variable.parameter.ruby", 7, 5); // 'other'
		assertToken("default.ruby", 12, 1); // ')'
		assertToken("default.ruby", 13, 1); // ' '
		assertToken("variable.other.constant.ruby", 14, 7); // 'BOOLEAN'
		assertToken("default.ruby", 21, 1); // ' '
		assertToken("keyword.control.ruby", 22, 3); // 'end'
	}

	public void testLessThanOrEqualMethodDefinition()
	{
		String code = "def <=(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '<='
		assertToken("default.ruby", 6, 1); // '('
		assertToken("variable.parameter.ruby", 7, 5); // 'other'
		assertToken("default.ruby", 12, 1); // ')'
		assertToken("default.ruby", 13, 1); // ' '
		assertToken("variable.other.constant.ruby", 14, 7); // 'BOOLEAN'
		assertToken("default.ruby", 21, 1); // ' '
		assertToken("keyword.control.ruby", 22, 3); // 'end'
	}

	public void testLessThanMethodDefinition()
	{
		String code = "def <(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '<'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("variable.other.constant.ruby", 13, 7); // 'BOOLEAN'
		assertToken("default.ruby", 20, 1); // ' '
		assertToken("keyword.control.ruby", 21, 3); // 'end'
	}

	public void testGreaterThanMethodDefinition()
	{
		String code = "def >(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '>'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("variable.other.constant.ruby", 13, 7); // 'BOOLEAN'
		assertToken("default.ruby", 20, 1); // ' '
		assertToken("keyword.control.ruby", 21, 3); // 'end'
	}

	public void testBitwiseOrMethodDefinition()
	{
		String code = "def |(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '|'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("variable.other.constant.ruby", 13, 7); // 'BOOLEAN'
		assertToken("default.ruby", 20, 1); // ' '
		assertToken("keyword.control.ruby", 21, 3); // 'end'
	}

	public void testBitwiseAndMethodDefinition()
	{
		String code = "def &(other) self || other end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '&'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("variable.language.ruby", 13, 4); // 'self'
		assertToken("default.ruby", 17, 1); // ' '
		assertToken("keyword.operator.logical.ruby", 18, 2); // '||'
		assertToken("default.ruby", 20, 1); // ' '
		assertToken("default.ruby", 21, 5); // 'other'
		assertToken("default.ruby", 26, 1); // ' '
		assertToken("keyword.control.ruby", 27, 3); // 'end'
	}

	public void testShiftMethodDefinition()
	{
		String code = "def <<(obj) self end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '<<'
		assertToken("default.ruby", 6, 1); // '('
		assertToken("variable.parameter.ruby", 7, 3); // 'obj'
		assertToken("default.ruby", 10, 1); // ')'
		assertToken("default.ruby", 11, 1); // ' '
		assertToken("variable.language.ruby", 12, 4); // 'self'
		assertToken("default.ruby", 16, 1); // ' '
		assertToken("keyword.control.ruby", 17, 3); // 'end'
	}

	public void testOverridePlusMethodDefinition()
	{
		String code = "def +@() self end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '+@'
		assertToken("default.ruby", 6, 1); // '('
		assertToken("default.ruby", 7, 1); // ')'
		assertToken("default.ruby", 8, 1); // ' '
		assertToken("variable.language.ruby", 9, 4); // 'self'
		assertToken("default.ruby", 13, 1); // ' '
		assertToken("keyword.control.ruby", 14, 3); // 'end'
	}

	public void testOverrideMinusMethodDefinition()
	{
		String code = "def -@() 0 end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '-@'
		assertToken("default.ruby", 6, 1); // '('
		assertToken("default.ruby", 7, 1); // ')'
		assertToken("default.ruby", 8, 1); // ' '
		assertToken("constant.numeric.ruby", 9, 1); // '0'
		assertToken("default.ruby", 10, 1); // ' '
		assertToken("keyword.control.ruby", 11, 3); // 'end'
	}

	public void testBitwiseComplementMethodDefinition()
	{
		String code = "def ~() 0 end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '~'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("default.ruby", 6, 1); // ')'
		assertToken("default.ruby", 7, 1); // ' '
		assertToken("constant.numeric.ruby", 8, 1); // '0'
		assertToken("default.ruby", 9, 1); // ' '
		assertToken("keyword.control.ruby", 10, 3); // 'end'
	}

	public void testHatMethodDefinition()
	{
		String code = "def ^(other) BOOLEAN end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 1); // '^'
		assertToken("default.ruby", 5, 1); // '('
		assertToken("variable.parameter.ruby", 6, 5); // 'other'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("variable.other.constant.ruby", 13, 7); // 'BOOLEAN'
		assertToken("default.ruby", 20, 1); // ' '
		assertToken("keyword.control.ruby", 21, 3); // 'end'
	}

	public void testArrayIndexMethodDefinition()
	{
		String code = "def [](*) at(0) end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '[]'
		assertToken("default.ruby", 6, 1); // '('
		assertToken("variable.parameter.ruby", 7, 1); // '*'
		assertToken("default.ruby", 8, 1); // ')'
		assertToken("default.ruby", 9, 1); // ' '
		assertToken("default.ruby", 10, 2); // 'at'
		assertToken("default.ruby", 12, 1); // '('
		assertToken("constant.numeric.ruby", 13, 1); // '0'
		assertToken("default.ruby", 14, 1); // ')'
		assertToken("default.ruby", 15, 1); // ' '
		assertToken("keyword.control.ruby", 16, 3); // 'end'
	}

	public void testArraySetMethodDefinition()
	{
		String code = "def []=(key, value) value end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 3); // '[]='
		assertToken("default.ruby", 7, 1); // '('
		assertToken("variable.parameter.ruby", 8, 3); // 'key'
		assertToken("default.ruby", 11, 1); // ','
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("variable.parameter.ruby", 13, 5); // 'value'
		assertToken("default.ruby", 18, 1); // ')'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("default.ruby", 20, 5); // 'value'
		assertToken("default.ruby", 25, 1); // ' '
		assertToken("keyword.control.ruby", 26, 3); // 'end'
	}

	public void testNextMethodDefinition()
	{
		String code = "def next() 0 end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 4); // 'next'
		assertToken("default.ruby", 8, 1); // '('
		assertToken("default.ruby", 9, 1); // ')'
		assertToken("default.ruby", 10, 1); // ' '
		assertToken("constant.numeric.ruby", 11, 1); // '0'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("keyword.control.ruby", 13, 3); // 'end'
	}

	public void testBeginMethodDefinition()
	{
		String code = "def begin(n) 0 end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 5); // 'begin'
		assertToken("default.ruby", 9, 1); // '('
		assertToken("variable.parameter.ruby", 10, 1); // 'n'
		assertToken("default.ruby", 11, 1); // ')'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("constant.numeric.ruby", 13, 1); // '0'
		assertToken("default.ruby", 14, 1); // ' '
		assertToken("keyword.control.ruby", 15, 3); // 'end'
	}

	public void testEndMethodDefinition()
	{
		String code = "def end(n) 0 end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 3); // 'end'
		assertToken("default.ruby", 7, 1); // '('
		assertToken("variable.parameter.ruby", 8, 1); // 'n'
		assertToken("default.ruby", 9, 1); // ')'
		assertToken("default.ruby", 10, 1); // ' '
		assertToken("constant.numeric.ruby", 11, 1); // '0'
		assertToken("default.ruby", 12, 1); // ' '
		assertToken("keyword.control.ruby", 13, 3); // 'end'
	}

	public void testMatchMethodDefinition()
	{
		String code = "def =~(other) FALSE end";
		setUpScanner(code);
		assertToken("keyword.control.def.ruby", 0, 3); // 'def'
		assertToken("default.ruby", 3, 1); // ' '
		assertToken("entity.name.function.ruby", 4, 2); // '=~'
		assertToken("default.ruby", 6, 1); // '('
		assertToken("variable.parameter.ruby", 7, 5); // 'other'
		assertToken("default.ruby", 12, 1); // ')'
		assertToken("default.ruby", 13, 1); // ' '
		assertToken("variable.other.constant.ruby", 14, 5); // 'FALSE'
		assertToken("default.ruby", 19, 1); // ' '
		assertToken("keyword.control.ruby", 20, 3); // 'end'
	}

	public void testTwoAliasLines()
	{
		String code = "alias :include? :===\nalias :member? :===";
		setUpScanner(code);
		assertToken("keyword.control.ruby", 0, 5); // 'alias'
		assertToken("default.ruby", 5, 1); // ' '
		assertToken("constant.other.symbol.ruby", 6, 1); // ':'
		assertToken("constant.other.symbol.ruby", 7, 8); // 'include?'
		assertToken("default.ruby", 15, 1); // ' '
		assertToken("constant.other.symbol.ruby", 16, 1); // ':'
		assertToken("constant.other.symbol.ruby", 17, 3); // '==='
		assertToken("default.ruby", 20, 1); // '\n'
		assertToken("keyword.control.ruby", 21, 5); // 'alias'
		assertToken("default.ruby", 26, 1); // ' '
		assertToken("constant.other.symbol.ruby", 27, 1); // ':'
		assertToken("constant.other.symbol.ruby", 28, 7); // 'member?'
		assertToken("default.ruby", 35, 1); // ' '
		assertToken("constant.other.symbol.ruby", 36, 1); // ':'
		assertToken("constant.other.symbol.ruby", 37, 3); // '==='
	}
}
