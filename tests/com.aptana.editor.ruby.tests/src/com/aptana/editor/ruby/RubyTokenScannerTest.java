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
		assertToken(Tokens.tCONSTANT, 5, 6);
		assertToken(RubyTokenScanner.NEWLINE, 11, 1);
		assertToken(Tokens.kEND, 12, 3);
	}

	public void testSymbolAtEndOfLine()
	{
		String code = "  helper_method :logged_in?\n" + "  def method\n" + "    \n" + "  end";
		setUpScanner(code);
		assertToken(Tokens.tIDENTIFIER, 0, 15); // ' helper_method'
		assertToken(Tokens.tSYMBEG, 15, 2); // ' :'
		assertToken(Tokens.tSYMBEG, 17, 10); // 'logged_in?'
		assertToken(RubyTokenScanner.NEWLINE, 27, 1); // '\n'
		assertToken(Tokens.kDEF, 28, 5); // ' def'
		assertToken(Tokens.tIDENTIFIER, 33, 7); // ' method'
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
		assertToken(Tokens.tSYMBEG, 5, 2); // ' :'
		assertToken(Tokens.tSYMBEG, 7, 16); // 'tsort_each_child'
		assertToken(Tokens.tSYMBEG, 23, 2); // ' :'
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
		assertToken(RubyTokenScanner.ASSIGNMENT, 19, 2); // ' ='
		assertToken(Tokens.kTRUE, 21, 5); // ' true'
		assertToken(RubyTokenScanner.QUESTION, 26, 2); // ' ?'
		assertToken(Tokens.tIDENTIFIER, 28, 6); // ' value'
		assertToken(Tokens.tDOT, 34, 1); // '.'
		assertToken(Tokens.tIDENTIFIER, 35, 6); // 'method'
		assertToken(RubyTokenScanner.COLON, 41, 2); // ' :'
		assertToken(Tokens.tIDENTIFIER, 43, 6); // ' value'
	}

	public void testWhen()
	{
		String code = "case value\n" + "when FalseClass: 0\n" + "else value\n" + "end";
		setUpScanner(code);
		assertToken(Tokens.kCASE, 0, 4); // 'case'
		assertToken(Tokens.tIDENTIFIER, 4, 6); // ' value'
		assertToken(RubyTokenScanner.NEWLINE, 10, 1); // '\n'
		assertToken(Tokens.kWHEN, 11, 4); // 'when'
		assertToken(Tokens.tCONSTANT, 15, 11); // ' FalseClass'
		assertToken(RubyTokenScanner.COLON, 26, 1); // ':'
		assertToken(Tokens.tINTEGER, 27, 2); // ' 0'
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
		assertToken(RubyTokenScanner.ASSIGNMENT, 5, 2); // ' ='
		assertToken(Tokens.tINTEGER, 7, 2); // ' 1'
	}
}
