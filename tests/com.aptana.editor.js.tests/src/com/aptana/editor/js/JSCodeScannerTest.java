package com.aptana.editor.js;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class JSCodeScannerTest extends TestCase
{

	private JSCodeScanner scanner;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		scanner = new JSCodeScanner()
		{
			protected IToken createToken(String string)
			{
				return getToken(string);
			};
		};
	}

	@Override
	protected void tearDown() throws Exception
	{
		scanner = null;
		super.tearDown();
	}

	public void testBasicTokenizing()
	{
		String src = "var one = 1;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.js"), 0, 3);
		assertToken(Token.WHITESPACE, 3, 1);
		assertToken(getToken("source.js"), 4, 3);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("keyword.operator.js"), 8, 1);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken("constant.numeric.js"), 10, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 11, 1);
	}

	public void testOperatorTokens()
	{
		String src = ">>>= >>> <<= >>= === !== >> << != <= >= == -- ++ && || *= /= %= += -= &= |= ^= ? ! % & * - + ~ = < > ^ | / ";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("keyword.operator.js"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);

		for (int i = 5; i < 25; i += 4)
		{
			assertToken(src.substring(i, i + 4), getToken("keyword.operator.js"), i, 3);
			assertToken(Token.WHITESPACE, i + 3, 1);
		}
		for (int i = 25; i < 79; i += 3)
		{
			assertToken(src.substring(i, i + 3), getToken("keyword.operator.js"), i, 2);
			assertToken(Token.WHITESPACE, i + 2, 1);
		}
		for (int i = 79; i < src.length(); i += 2)
		{
			assertToken(src.substring(i, i + 2), getToken("keyword.operator.js"), i, 1);
			assertToken(Token.WHITESPACE, i + 1, 1);
		}
	}

	// TODO Add tests for the function words that I turned into word rules from regexp

	public void testNumbers()
	{
		String src = "0xff 0X123 1 9.234 1E8";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("constant.numeric.js"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken("constant.numeric.js"), 5, 5);
		assertToken(Token.WHITESPACE, 10, 1);
		assertToken(getToken("constant.numeric.js"), 11, 1);
		assertToken(Token.WHITESPACE, 12, 1);
		assertToken(getToken("constant.numeric.js"), 13, 5);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken("constant.numeric.js"), 19, 3);
	}

	public void testConstantWords()
	{
		String src = "true false null Infinity NaN undefined super this debugger";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("constant.language.boolean.true.js"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		assertToken(getToken("constant.language.boolean.false.js"), 5, 5);
		assertToken(Token.WHITESPACE, 10, 1);
		assertToken(getToken("constant.language.null.js"), 11, 4);
		assertToken(Token.WHITESPACE, 15, 1);
		assertToken(getToken("constant.language.js"), 16, 8);
		assertToken(Token.WHITESPACE, 24, 1);
		assertToken(getToken("constant.language.js"), 25, 3);
		assertToken(Token.WHITESPACE, 28, 1);
		assertToken(getToken("constant.language.js"), 29, 9);
		assertToken(Token.WHITESPACE, 38, 1);
		assertToken(getToken("variable.language.js"), 39, 5);
		assertToken(Token.WHITESPACE, 44, 1);
		assertToken(getToken("variable.language.js"), 45, 4);
		assertToken(Token.WHITESPACE, 49, 1);
		assertToken(getToken("keyword.other.js"), 50, 8);
	}

	public void testMetaChars()
	{
		String src = "(){}[],;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("meta.brace.round.js"), 0, 1);
		assertToken(getToken("meta.brace.round.js"), 1, 1);
		assertToken(getToken("meta.brace.curly.js"), 2, 1);
		assertToken(getToken("meta.brace.curly.js"), 3, 1);
		assertToken(getToken("meta.brace.square.js"), 4, 1);
		assertToken(getToken("meta.brace.square.js"), 5, 1);
		assertToken(getToken("meta.delimiter.object.comma.js"), 6, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 7, 1);
	}

	public void testPrototypeSnippet()
	{
		String src = "var Class = {\n" + "  create: function() {\n"
				+ "    var parent = null, properties = $A(arguments);\n"
				+ "    if (Object.isFunction(properties[0]))\n" + "      parent = properties.shift();\n" + "    \n"
				+ "    function klass() {\n" + "      this.initialize.apply(this, arguments);\n" + "    }\n" + "    \n"
				+ "    Object.extend(klass, Class.Methods);\n" + "    klass.superclass = parent;\n"
				+ "    klass.subclasses = [];\n" + "    \n" + "    if (parent) {\n"
				+ "      var subclass = function() { };\n" + "      subclass.prototype = parent.prototype;\n"
				+ "      klass.prototype = new subclass;\n" + "      parent.subclasses.push(klass);\n" + "    }\n"
				+ "    \n" + "    for (var i = 0; i < properties.length; i++)\n"
				+ "      klass.addMethods(properties[i]);\n" + "    \n" + "    if (!klass.prototype.initialize)\n"
				+ "      klass.prototype.initialize = Prototype.emptyFunction;\n" + "    \n"
				+ "    klass.prototype.constructor = klass;\n" + "    \n" + "    return klass;\n" + "  }\n" + "};";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		// line 1
		assertToken(getToken("storage.type.js"), 0, 3);
		assertToken(Token.WHITESPACE, 3, 1);
		assertToken(getToken("source.js"), 4, 5);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken("keyword.operator.js"), 10, 1);
		assertToken(Token.WHITESPACE, 11, 1);
		assertToken(getToken("meta.brace.curly.js"), 12, 1);
		assertToken(Token.WHITESPACE, 13, 3);

		// line 2
		// Textmate rule should apply, but we can't support it yet:
		// \b([a-zA-Z_?.$][\w?.$]*)\s*:\s*\b(function)?\s*(\()(.*?)(\))';
		assertToken(getToken("source.js"), 16, 6); // create FIXME Should be entity.name.function.js
		assertToken(getToken(null), 22, 1); // ':'
		assertToken(Token.WHITESPACE, 23, 1); // ' '
		assertToken(getToken("storage.type.js"), 24, 8); // function
		assertToken(getToken("meta.brace.round.js"), 32, 1); // '(' FIXME Should be
		// punctuation.definition.parameters.begin.js
		assertToken(getToken("meta.brace.round.js"), 33, 1);// ')' FIXME Should be
		// punctuation.definition.parameters.end.js
		assertToken(Token.WHITESPACE, 34, 1); // ' '
		assertToken(getToken("meta.brace.curly.js"), 35, 1); // {
		assertToken(Token.WHITESPACE, 36, 5);

		// line 3
		assertToken(getToken("storage.type.js"), 41, 3); // var
		assertToken(Token.WHITESPACE, 44, 1);
		assertToken(getToken("source.js"), 45, 6); // parent
		assertToken(Token.WHITESPACE, 51, 1);
		assertToken(getToken("keyword.operator.js"), 52, 1); // =
		assertToken(Token.WHITESPACE, 53, 1);
		assertToken(getToken("constant.language.null.js"), 54, 4); // null
		assertToken(getToken("meta.delimiter.object.comma.js"), 58, 1); // ,
		assertToken(Token.WHITESPACE, 59, 1);
		assertToken(getToken("source.js"), 60, 10); // properties
		assertToken(Token.WHITESPACE, 70, 1);
		assertToken(getToken("keyword.operator.js"), 71, 1); // =
		assertToken(Token.WHITESPACE, 72, 1);
		assertToken(getToken("source.js"), 73, 2); // $A
		assertToken(getToken("meta.brace.round.js"), 75, 1); // (
		assertToken(getToken("source.js"), 76, 9); // arguments
		assertToken(getToken("meta.brace.round.js"), 85, 1); // )
		assertToken(getToken("punctuation.terminator.statement.js"), 86, 1);
		assertToken(Token.WHITESPACE, 87, 5);

		// line 4
		// if (Object.isFunction(properties[0]))\n
		assertToken(getToken("keyword.control.js"), 92, 2);
		assertToken(Token.WHITESPACE, 94, 1);
		assertToken(getToken("meta.brace.round.js"), 95, 1);
		assertToken(getToken("support.class.js"), 96, 6);
		assertToken(getToken(null), 102, 1);
		assertToken(getToken("source.js"), 103, 10);
		assertToken(getToken("meta.brace.round.js"), 113, 1);
		assertToken(getToken("source.js"), 114, 10);
		assertToken(getToken("meta.brace.square.js"), 124, 1);
		assertToken(getToken("constant.numeric.js"), 125, 1);
		assertToken(getToken("meta.brace.square.js"), 126, 1);
		assertToken(getToken("meta.brace.round.js"), 127, 1);
		assertToken(getToken("meta.brace.round.js"), 128, 1);
		assertToken(Token.WHITESPACE, 129, 7);

		// TODO Test all the rest of the lines! (Or at least the "interesting" parts with new token types
	}

	public void testUnderscoreInIdentifierWithKeyword()
	{
		String src = "add_child";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("source.js"), 0, 9);
	}

	private IToken getToken(String string)
	{
		return new Token(string);
	}

	private void assertToken(IToken token, int offset, int length)
	{
		assertToken(null, token, offset, length);
	}

	private void assertToken(String msg, IToken token, int offset, int length)
	{
		try
		{
			assertEquals(token.getData(), scanner.nextToken().getData());
			assertEquals(offset, scanner.getTokenOffset());
			assertEquals(length, scanner.getTokenLength());
		}
		catch (AssertionFailedError e)
		{
			System.out.println(msg);
			throw e;
		}

	}
}
