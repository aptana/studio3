package com.aptana.editor.coffee;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.junit.Test;

import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;

public class CoffeeCodeScannerTest extends AbstractTokenScannerTestCase
{
	@Override
	protected ITokenScanner createTokenScanner()
	{
		return new CoffeeCodeScanner();
	}

	@Test
	public void testNumberAssignment()
	{
		String src = "number = 42";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// square
		assertToken(getToken("variable.assignment.coffee"), 0, 6);
		assertToken(Token.WHITESPACE, 6, 1);
		// =
		assertToken(getToken("keyword.operator.coffee"), 7, 1);
		assertToken(Token.WHITESPACE, 8, 1);
		// 42
		assertToken(getToken("constant.numeric.coffee"), 9, 2);
	}

	@Test
	public void testBooleanAssignment()
	{
		String src = "opposite = true";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// opposite
		assertToken(getToken("variable.assignment.coffee"), 0, 8);
		assertToken(Token.WHITESPACE, 8, 1);
		// =
		assertToken(getToken("keyword.operator.coffee"), 9, 1);
		assertToken(Token.WHITESPACE, 10, 1);
		// true
		assertToken(getToken("constant.language.boolean.true.coffee"), 11, 4);
	}

	@Test
	public void testArrayAssignment()
	{
		String src = "list = [1, 2, 3, 4, 5]";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// list
		assertToken(getToken("variable.assignment.coffee"), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		// =
		assertToken(getToken("keyword.operator.coffee"), 5, 1);
		assertToken(Token.WHITESPACE, 6, 1);
		// [
		assertToken(getToken("meta.brace.square.coffee"), 7, 1);
		// 1
		assertToken(getToken("constant.numeric.coffee"), 8, 1);
		// ,
		assertToken(getToken("meta.delimiter.object.comma.coffee"), 9, 1);
		assertToken(Token.WHITESPACE, 10, 1);
		// 2
		assertToken(getToken("constant.numeric.coffee"), 11, 1);
		// ,
		assertToken(getToken("meta.delimiter.object.comma.coffee"), 12, 1);
		assertToken(Token.WHITESPACE, 13, 1);
		// 3
		assertToken(getToken("constant.numeric.coffee"), 14, 1);
		// ,
		assertToken(getToken("meta.delimiter.object.comma.coffee"), 15, 1);
		assertToken(Token.WHITESPACE, 16, 1);
		// 4
		assertToken(getToken("constant.numeric.coffee"), 17, 1);
		// ,
		assertToken(getToken("meta.delimiter.object.comma.coffee"), 18, 1);
		assertToken(Token.WHITESPACE, 19, 1);
		// 5
		assertToken(getToken("constant.numeric.coffee"), 20, 1);
		// ]
		assertToken(getToken("meta.brace.square.coffee"), 21, 1);
	}

	@Test
	public void testAssignmentWithParens()
	{
		String src = "cubes = (math.cube num for num in list)";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// cubes
		assertToken(getToken("variable.assignment.coffee"), 0, 5);
		assertToken(Token.WHITESPACE, 5, 1);
		// =
		assertToken(getToken("keyword.operator.coffee"), 6, 1);
		assertToken(Token.WHITESPACE, 7, 1);
		// (
		assertToken(getToken("meta.brace.round.coffee"), 8, 1);
		// math
		assertToken(getToken(""), 9, 4);
		// .
		assertToken(getToken("meta.delimiter.method.period.coffee"), 13, 1);
		// cube
		assertToken(getToken(""), 14, 4);
		assertToken(Token.WHITESPACE, 18, 1);
		// num
		assertToken(getToken(""), 19, 3);
		assertToken(Token.WHITESPACE, 22, 1);
		// for
		assertToken(getToken("keyword.control.coffee"), 23, 3);
		assertToken(Token.WHITESPACE, 26, 1);
		// num
		assertToken(getToken(""), 27, 3);
		assertToken(Token.WHITESPACE, 30, 1);
		// in
		assertToken(getToken("keyword.control.coffee"), 31, 2);
		assertToken(Token.WHITESPACE, 33, 1);
		// list
		assertToken(getToken(""), 34, 4);
		assertToken(getToken("meta.brace.round.coffee"), 38, 1);
	}

	@Test
	public void testFunctionAssignment()
	{
		String src = "square = (x) -> x * x";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// square
		assertToken(getToken("meta.function.coffee entity.name.function.coffee"), 0, 6);
		assertToken(Token.WHITESPACE, 6, 1);
		// =
		assertToken(getToken("keyword.operator.coffee"), 7, 1);
		assertToken(Token.WHITESPACE, 8, 1);
		// (x)
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 9, 1);
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 10, 1);
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 11, 1);
		assertToken(getToken("meta.inline.function.coffee"), 12, 1);
		// ->
		assertToken(getToken("meta.inline.function.coffee storage.type.function.coffee"), 13, 2);
		assertToken(Token.WHITESPACE, 15, 1);
		// x
		assertToken(getToken(""), 16, 1);
		assertToken(Token.WHITESPACE, 17, 1);
		// *
		assertToken(getToken("keyword.operator.coffee"), 18, 1);
		assertToken(Token.WHITESPACE, 19, 1);
		// x
		assertToken(getToken(""), 20, 1);
	}

	@Test
	public void testClassDeclaration()
	{
		String src = "class Animal\n" + //
				"  constructor: (@name) ->";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// class
		assertToken(getToken("meta.class.coffee storage.type.class.coffee"), 0, 5);
		assertToken(getToken("meta.class.coffee"), 5, 1);
		// Animal
		assertToken(getToken("meta.class.coffee entity.name.type.class.coffee"), 6, 6);
		assertToken(Token.WHITESPACE, 12, 3);
		// constructor
		assertToken(getToken("meta.function.coffee entity.name.function.coffee"), 15, 11);
		// :
		assertToken(getToken("keyword.operator.coffee"), 26, 1);
		assertToken(Token.WHITESPACE, 27, 1);
		// (
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 28, 1);
		// @name
		assertToken(getToken("meta.inline.function.coffee variable.other.readwrite.instance.coffee"), 29, 5);
		// )
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 34, 1);
		assertToken(getToken("meta.inline.function.coffee"), 35, 1);
		// ->
		assertToken(getToken("meta.inline.function.coffee storage.type.function.coffee"), 36, 2);
	}

	@Test
	public void testSubClassDeclaration()
	{
		String src = "class Horse extends Animal\n" + //
				"  move: ->\n" + //
				"    alert\n" + //
				"    super 45";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// class
		assertToken(getToken("meta.class.coffee storage.type.class.coffee"), 0, 5);
		assertToken(getToken("meta.class.coffee"), 5, 1);
		// Horse
		assertToken(getToken("meta.class.coffee entity.name.type.class.coffee"), 6, 5);
		assertToken(getToken("meta.class.coffee"), 11, 1);
		// extends
		assertToken(getToken("meta.class.coffee keyword.control.inheritance.coffee"), 12, 7);
		assertToken(getToken("meta.class.coffee"), 19, 1);
		// Animal
		assertToken(getToken("meta.class.coffee entity.other.inherited-class.coffee"), 20, 6);
		assertToken(Token.WHITESPACE, 26, 3);
		// move
		assertToken(getToken("meta.function.coffee entity.name.function.coffee"), 29, 4);
		// :
		assertToken(getToken("keyword.operator.coffee"), 33, 1);
		assertToken(Token.WHITESPACE, 34, 1);
		// ->
		assertToken(getToken("meta.inline.function.coffee storage.type.function.coffee"), 35, 2);
		assertToken(Token.WHITESPACE, 37, 5);
		// alert
		assertToken(getToken(""), 42, 5);
		assertToken(Token.WHITESPACE, 47, 5);
		// super
		assertToken(getToken("variable.language.coffee"), 52, 5);
		assertToken(Token.WHITESPACE, 57, 1);
		// 45
		assertToken(getToken("constant.numeric.coffee"), 58, 2);
	}

	@Test
	public void testAnonymousFunctionAsArgument()
	{
		String src = "bind 1, (event) =>\n" + //
				"  @customer.purchase @cart";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		// bind
		assertToken(getToken(""), 0, 4);
		assertToken(Token.WHITESPACE, 4, 1);
		// 1
		assertToken(getToken("constant.numeric.coffee"), 5, 1);
		// ,
		assertToken(getToken("meta.delimiter.object.comma.coffee"), 6, 1);
		assertToken(Token.WHITESPACE, 7, 1);
		// (
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 8, 1);
		// event
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 9, 5);
		// )
		assertToken(getToken("meta.inline.function.coffee variable.parameter.function.coffee"), 14, 1);
		assertToken(getToken("meta.inline.function.coffee"), 15, 1);
		// =>
		assertToken(getToken("meta.inline.function.coffee storage.type.function.coffee"), 16, 2);
		assertToken(Token.WHITESPACE, 18, 3);
		// @customer
		assertToken(getToken("variable.other.readwrite.instance.coffee"), 21, 9);
		// .
		assertToken(getToken("meta.delimiter.method.period.coffee"), 30, 1);
		// purchase
		assertToken(getToken(""), 31, 8);
		assertToken(Token.WHITESPACE, 39, 1);
		// @cart
		assertToken(getToken("variable.other.readwrite.instance.coffee"), 40, 5);
	}

	@Test
	public void testInstanceVariableWithUnderscoresAndNumbers()
	{
		String src = "@scale_3d_effect";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("variable.other.readwrite.instance.coffee"), 0, 16);
	}
}
