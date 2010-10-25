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

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.jrubyparser.parser.Tokens;

import com.aptana.editor.ruby.RubyTokenScanner;

public class RubyTokenScannerTest extends TestCase
{

	private RubyTokenScanner fScanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		fScanner = new RubyTokenScanner();
	}

	private void setUpScanner(String code)
	{
		setUpScanner(code, 0, code.length());
	}

	private void setUpScanner(String code, int offset, int length)
	{
		Document doc = new Document(code);
		fScanner.setRange(doc, offset, length);
	}

	private void assertToken(int tokenType, int offset, int length)
	{
		IToken token = fScanner.nextToken();
		assertEquals("Offsets don't match", offset, fScanner.getTokenOffset());
		assertEquals("Lengths don't match", length, fScanner.getTokenLength());
		assertEquals("Token type doesn't match", tokenType, token.getData());
	}

	public void testSimpleClassDefinition()
	{
		String code = "class Chris\nend\n";
		setUpScanner(code);
		assertToken(Tokens.kCLASS, 0, 5);
		assertToken(Tokens.tWHITESPACE, 5, 1);
		assertToken(Tokens.tCONSTANT, 6, 5);
		assertToken(Tokens.tWHITESPACE, 11, 1);
		assertToken(Tokens.kEND, 12, 3);
	}

	public void testSymbolAtEndOfLine()
	{
		String code = "  helper_method :logged_in?\n" + "  def method\n" + "    \n" + "  end";
		setUpScanner(code);
		assertToken(Tokens.tWHITESPACE, 0, 2);
		assertToken(Tokens.tIDENTIFIER, 2, 13); // 'helper_method'
		assertToken(Tokens.tWHITESPACE, 15, 1);
		assertToken(Tokens.tSYMBEG, 16, 1); // ':'
		assertToken(Tokens.tSYMBEG, 17, 10); // 'logged_in?'
		assertToken(Tokens.tWHITESPACE, 27, 1); // '\n'
		assertToken(Tokens.tWHITESPACE, 28, 2);
		assertToken(Tokens.kDEF, 30, 3); // 'def'
		assertToken(Tokens.tWHITESPACE, 33, 1);
		assertToken(Tokens.tIDENTIFIER, 34, 6); // 'method'
	}

	public void testSymbolInsideBrackets()
	{
		String code = "test[:begin]";
		setUpScanner(code);
		assertToken(Tokens.tIDENTIFIER, 0, 4); // 'test'
		assertToken(Tokens.tLBRACK, 4, 1); // '['
		assertToken(Tokens.tSYMBEG, 5, 1); // ' :'
		assertToken(Tokens.tSYMBEG, 6, 5); // 'begin'
		assertToken(Tokens.tRBRACK, 11, 1); // ']'
	}

	public void testSymbolInsideParentheses()
	{
		String code = "Object.const_defined?(:RedCloth)";
		setUpScanner(code);
		assertToken(Tokens.tCONSTANT, 0, 6); // 'Object'
		assertToken(Tokens.tDOT, 6, 1); // '.'
		assertToken(Tokens.tFID, 7, 14); // 'const_define?'
		assertToken(Tokens.tLPAREN2, 21, 1); // '('
		assertToken(Tokens.tSYMBEG, 22, 1); // ':'
		assertToken(Tokens.tSYMBEG, 23, 8); // 'RedCloth'
		assertToken(Tokens.tRPAREN, 31, 1); // ')'
	}

	public void testAliasWithTwoSymbols()
	{
		String code = "alias :tsort_each_child :each_key";
		setUpScanner(code);
		assertToken(Tokens.kALIAS, 0, 5); // 'alias'
		assertToken(Tokens.tWHITESPACE, 5, 1); // ' '
		assertToken(Tokens.tSYMBEG, 6, 1); // ':'
		assertToken(Tokens.tSYMBEG, 7, 16); // 'tsort_each_child'
		assertToken(Tokens.tWHITESPACE, 23, 1); // ' '
		assertToken(Tokens.tSYMBEG, 24, 1); // ':'
		assertToken(Tokens.tSYMBEG, 25, 8); // 'each_key'
	}

	public void testSymbolInsideBracketsTwo()
	{
		String code = "@repository=params[:repository]";
		setUpScanner(code);
		assertToken(Tokens.tIVAR, 0, 11); // '@repository'
		assertToken(RubyTokenScanner.ASSIGNMENT, 11, 1); // '='
		assertToken(Tokens.tIDENTIFIER, 12, 6); // 'params'
		assertToken(Tokens.tLBRACK, 18, 1); // '['
		assertToken(Tokens.tSYMBEG, 19, 1); // ':'
		assertToken(Tokens.tSYMBEG, 20, 10); // 'repository'
		assertToken(Tokens.tRBRACK, 30, 1); // ']'
	}

	public void testTertiaryConditional()
	{
		String code = "multiparameter_name = true ? value.method : value";
		setUpScanner(code);
		assertToken(Tokens.tIDENTIFIER, 0, 19); // 'multiparameter_name'
		assertToken(Tokens.tWHITESPACE, 19, 1); // ' '
		assertToken(RubyTokenScanner.ASSIGNMENT, 20, 1); // '='
		assertToken(Tokens.tWHITESPACE, 21, 1); // ' '
		assertToken(Tokens.kTRUE, 22, 4); // 'true'
		assertToken(Tokens.tWHITESPACE, 26, 1); // ' '
		assertToken(RubyTokenScanner.QUESTION, 27, 1); // '?'
		assertToken(Tokens.tWHITESPACE, 28, 1); // ' '
		assertToken(Tokens.tIDENTIFIER, 29, 5); // 'value'
		assertToken(Tokens.tDOT, 34, 1); // '.'
		assertToken(Tokens.tIDENTIFIER, 35, 6); // 'method'
		assertToken(Tokens.tWHITESPACE, 41, 1); // ' '
		assertToken(RubyTokenScanner.COLON, 42, 1); // ':'
		assertToken(Tokens.tWHITESPACE, 43, 1); // ' '
		assertToken(Tokens.tIDENTIFIER, 44, 5); // 'value'
	}

	public void testWhen()
	{
		String code = "case value\n" + "when FalseClass: 0\n" + "else value\n" + "end";
		setUpScanner(code);
		assertToken(Tokens.kCASE, 0, 4); // 'case'
		assertToken(Tokens.tWHITESPACE, 4, 1); // ' '
		assertToken(Tokens.tIDENTIFIER, 5, 5); // 'value'
		assertToken(Tokens.tWHITESPACE, 10, 1); // '\n'
		assertToken(Tokens.kWHEN, 11, 4); // 'when'
		assertToken(Tokens.tWHITESPACE, 15, 1); // ' '
		assertToken(Tokens.tCONSTANT, 16, 10); // 'FalseClass'
		assertToken(RubyTokenScanner.COLON, 26, 1); // ':'
		assertToken(Tokens.tWHITESPACE, 27, 1); // ' '
		assertToken(Tokens.tINTEGER, 28, 1); // '0'
	}

	public void testAppendSymbol()
	{
		String code = "puts(:<<)";
		setUpScanner(code);
		assertToken(Tokens.tIDENTIFIER, 0, 4); // 'puts'
		assertToken(Tokens.tLPAREN2, 4, 1); // '('
		assertToken(Tokens.tSYMBEG, 5, 1); // ':'
		assertToken(Tokens.tSYMBEG, 6, 2); // '<<'
		assertToken(Tokens.tRPAREN, 8, 1); // ')'
	}

	public void testDollarDollarSymbol()
	{
		String code = "puts(:$$)";
		setUpScanner(code);
		assertToken(Tokens.tIDENTIFIER, 0, 4); // 'puts'
		assertToken(Tokens.tLPAREN2, 4, 1); // '('
		assertToken(Tokens.tSYMBEG, 5, 1); // ':'
		assertToken(Tokens.tSYMBEG, 6, 2); // '$$'
		assertToken(Tokens.tRPAREN, 8, 1); // ')'
	}

	public void testTertiaryConditionalWithNoSpaces()
	{
		String code = "puts(a?b:c)";
		setUpScanner(code);
		assertToken(Tokens.tIDENTIFIER, 0, 4); // 'puts'
		assertToken(Tokens.tLPAREN2, 4, 1); // '('
		assertToken(Tokens.tFID, 5, 2); // 'a?'
		assertToken(Tokens.tIDENTIFIER, 7, 1); // 'b'
		assertToken(Tokens.tCOLON2, 8, 1); // ':'
		assertToken(Tokens.tIDENTIFIER, 9, 1); // 'c'
		assertToken(Tokens.tRPAREN, 10, 1); // ')'
	}

	public void testClassVariable()
	{
		String code = "@@var = 1";
		setUpScanner(code);
		assertToken(Tokens.tCVAR, 0, 5); // '@@var'
		assertToken(Tokens.tWHITESPACE, 5, 1); // ' '
		assertToken(RubyTokenScanner.ASSIGNMENT, 6, 1); // '='
		assertToken(Tokens.tWHITESPACE, 7, 1); // ' '
		assertToken(Tokens.tINTEGER, 8, 1); // '1'
	}
}
