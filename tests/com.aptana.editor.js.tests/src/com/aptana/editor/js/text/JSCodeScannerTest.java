/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.core.IFilter;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.tests.AbstractTokenScannerTestCase;
import com.aptana.editor.js.text.JSCodeScanner;
import com.aptana.js.core.JSLanguageConstants;

/**
 * Note: inheriting all tests from the superclass (i.e.: should work the same way that the previous code scanner did
 * work). Overridden tests can be used to know the differences among scanners (note that some test-cases were tweaked in
 * the superclass itself).
 */
public class JSCodeScannerTest extends AbstractTokenScannerTestCase
{
	protected ITokenScanner createTokenScanner()
	{
		return new JSCodeScanner();
	}

	protected void enumerateLists(String[][] lists, String tokenType)
	{
		// accumulator used to determine the number of enumerations we have
		int count = 1;

		// current offset within each sub-list
		int[] offsets = new int[lists.length];

		// initialize offsets and get total enumeration count
		for (int i = 0; i < lists.length; i++)
		{
			offsets[i] = 0;

			count *= lists[i].length;
		}

		// walk through all enumerations
		for (int enumeration = 0; enumeration < count; enumeration++)
		{
			StringBuilder buffer = new StringBuilder();

			// concatenate the current item from each sub-list into a single string
			for (int i = 0; i < lists.length; i++)
			{
				buffer.append(lists[i][offsets[i]]);
			}

			// create document, scan, and check token type
			String src = buffer.toString();
			IDocument document = new Document(src);
			scanner.setRange(document, 0, src.length());
			assertToken(getToken(tokenType), 0, src.length());

			// advance each offset, taking carries into account
			for (int j = lists.length - 1; j >= 0; j--)
			{
				int current = offsets[j] + 1;

				if (current > lists[j].length - 1)
				{
					// reset offset and continue processing to account for carry
					offsets[j] = 0;
				}
				else
				{
					// value is in range, save it and stop processing
					offsets[j] = current;
					break;
				}
			}
		}
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

		scanner.setRange(document, 4, document.getLength() - 4);
		assertToken(getToken("source.js"), 4, 3);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("keyword.operator.js"), 8, 1);
		assertToken(Token.WHITESPACE, 9, 1);
		assertToken(getToken("constant.numeric.js"), 10, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 11, 1);

	}

	public void testStartWithWhitespace() throws Exception
	{
		String src = " / ";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertToken(Token.WHITESPACE, 0, 1);
		assertToken(src.substring(0, 2), getToken("keyword.operator.js"), 1, 1);
		assertToken(Token.WHITESPACE, 2, 1);
		assertToken(Token.EOF, 3, 0);
	}

	public void testNumbers()
	{
		String src = "0xff 0X123 1 9.234 1E8 .1 0.";
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
		assertToken(Token.WHITESPACE, 22, 1);
		assertToken(getToken("constant.numeric.js"), 23, 2);
		assertToken(Token.WHITESPACE, 25, 1);
		assertToken(getToken("constant.numeric.js"), 26, 2);
	}

	public void testHexNumbers()
	{
		// @formatter:off
		String[][] lists = {
			//{ "+", "-", "" },	// TODO: apparently the scanner can't differentiate between 5 + 10 and 5 + +10?
			{ "0" },
			{ "x", "X" },
			{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F" },
			{ "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "A", "B", "C", "D", "E", "F" }
		};
		// @formatter:on

		this.enumerateLists(lists, "constant.numeric.js");
	}

	public void testScientificNotation()
	{
		// @formatter:off
		String[][] lists = {
			//{ "+", "-", "" },	// TODO: apparently the scanner can't differentiate between 5 + 10 and 5 + +10?
			{ "1", ".9", "1.9" },
			{ "e", "E" },
			{ "+", "-", "" },
			{ "10" }
		};
		// @formatter:on

		this.enumerateLists(lists, "constant.numeric.js");
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
		//@formatter:off
		String src = 
		  "var Class = {\n" 
		+ "  create: function() {\n"
		+ "    var parent = null, properties = $A(arguments);\n"
		+ "    if (Object.isFunction(properties[0]))\n" 
		+ "      parent = properties.shift();\n" 
		+ "    \n"
		+ "    function klass() {\n" 
		+ "      this.initialize.apply(this, arguments);\n" 
		+ "    }\n" 
		+ "    \n"
		+ "    Object.extend(klass, Class.Methods);\n" 
		+ "    klass.superclass = parent;\n"
		+ "    klass.subclasses = [];\n" 
		+ "    \n" 
		+ "    if (parent) {\n"
		+ "      var subclass = function() { };\n" 
		+ "      subclass.prototype = parent.prototype;\n"
		+ "      klass.prototype = new subclass;\n" 
		+ "      parent.subclasses.push(klass);\n" 
		+ "    }\n"
		+ "    \n" 
		+ "    for (var i = 0; i < properties.length; i++)\n"
		+ "      klass.addMethods(properties[i]);\n" 
		+ "    \n" 
		+ "    if (!klass.prototype.initialize)\n"
		+ "      klass.prototype.initialize = Prototype.emptyFunction;\n" 
		+ "    \n"
		+ "    klass.prototype.constructor = klass;\n" 
		+ "    \n" 
		+ "    return klass;\n" 
		+ "  }\n" + "};";
		//@formatter:on
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
		assertToken(getToken("storage.type.function.js"), 24, 8); // function
		assertToken(getToken("punctuation.definition.parameters.begin.js"), 32, 1);
		assertToken(getToken("punctuation.definition.parameters.end.js"), 33, 1);
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
		assertToken(getToken("meta.delimiter.method.period.js"), 102, 1);
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

	public void testFunctionName()
	{
		String src = "function chris() {}";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.function.js"), 0, 8);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("entity.name.function.js"), 9, 5);
		assertToken(getToken("punctuation.definition.parameters.begin.js"), 14, 1);
		assertToken(getToken("punctuation.definition.parameters.end.js"), 15, 1);
		assertToken(Token.WHITESPACE, 16, 1);
		assertToken(getToken("meta.brace.curly.js"), 17, 1);
		assertToken(getToken("meta.brace.curly.js"), 18, 1);
	}

	public void testAnonymousFunctionName()
	{
		String src = "var eatCakeAnon = function(){};";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.js"), 0, 3); // var
		assertToken(Token.WHITESPACE, 3, 1);
		assertToken(getToken("entity.name.function.js"), 4, 11); // eatCakeAnon
		assertToken(Token.WHITESPACE, 15, 1);
		assertToken(getToken("keyword.operator.js"), 16, 1); // =
		assertToken(Token.WHITESPACE, 17, 1);
		assertToken(getToken("storage.type.function.js"), 18, 8);
		assertToken(getToken("punctuation.definition.parameters.begin.js"), 26, 1);
		assertToken(getToken("punctuation.definition.parameters.end.js"), 27, 1);
		assertToken(getToken("meta.brace.curly.js"), 28, 1);
		assertToken(getToken("meta.brace.curly.js"), 29, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 30, 1);
	}

	public void testFunctionWithArguments()
	{
		String src = "function Pet(name, species, hello){}";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.function.js"), 0, 8);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("entity.name.function.js"), 9, 3);
		assertToken(getToken("punctuation.definition.parameters.begin.js"), 12, 1);
		assertToken(getToken("variable.parameter.function.js"), 13, 4);
		assertToken(getToken("meta.delimiter.object.comma.js"), 17, 1);
		assertToken(Token.WHITESPACE, 18, 1);
		assertToken(getToken("variable.parameter.function.js"), 19, 7);
		assertToken(getToken("meta.delimiter.object.comma.js"), 26, 1);
		assertToken(Token.WHITESPACE, 27, 1);
		assertToken(getToken("variable.parameter.function.js"), 28, 5);
		assertToken(getToken("punctuation.definition.parameters.end.js"), 33, 1);
		assertToken(getToken("meta.brace.curly.js"), 34, 1);
		assertToken(getToken("meta.brace.curly.js"), 35, 1);
	}

	public void testBrokenStuff()
	{
		String src = "function sayHello() { alert(this.hello); }";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.function.js"), 0, 8);
		assertToken(Token.WHITESPACE, 8, 1);
		assertToken(getToken("entity.name.function.js"), 9, 8);
		assertToken(getToken("punctuation.definition.parameters.begin.js"), 17, 1);
		assertToken(getToken("punctuation.definition.parameters.end.js"), 18, 1);
		assertToken(Token.WHITESPACE, 19, 1);
		assertToken(getToken("meta.brace.curly.js"), 20, 1);
		assertToken(Token.WHITESPACE, 21, 1);
		assertToken(getToken("source.js"), 22, 5);
		assertToken(getToken("meta.brace.round.js"), 27, 1);
		assertToken(getToken("variable.language.js"), 28, 4);
		assertToken(getToken("meta.delimiter.method.period.js"), 32, 1);
		assertToken(getToken("source.js"), 33, 5);
		assertToken(getToken("meta.brace.round.js"), 38, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 39, 1);
		assertToken(Token.WHITESPACE, 40, 1);
		assertToken(getToken("meta.brace.curly.js"), 41, 1);
		assertEquals(Token.EOF, scanner.nextToken());
	}

	//@formatter:off
	/**
	 * Although the rules at the JSCodeScanner tries to match against "support functions" such as dom manipulation, none
	 * of them actually match, so, this test is just to make sure this is the expected behavior for now (may need to be
	 * revisited).
	 * 
	 * Note: just aAdding exception just for the 'log' function that was properly gotten as a firebug function.
	 */
	//@formatter:on
	public void testSupportFunctions() throws Exception
	{
		Object[] tokens = new Object[] { "meta.delimiter.method.period.js", "source.js", "meta.brace.round.js",
				"meta.brace.round.js", "null", "source.js", "meta.delimiter.method.period.js", "source.js", "null",
				"source.js", "null" };

		Object[] tokensLog = new Object[] { "support.function.js.firebug", "meta.brace.round.js",
				"meta.brace.round.js", "null", "source.js", "support.function.js.firebug", "null", "source.js", "null", };

		Object[] tokensLog2 = new Object[] { "meta.delimiter.method.period.js", "support.function.js.firebug",
				"meta.brace.round.js", "meta.brace.round.js", "null", "source.js", "meta.delimiter.method.period.js",
				"support.function.js.firebug", "null", "source.js", "null", };

		String arrays[][] = new String[][] { JSLanguageConstants.SUPPORT_FUNCTIONS,
				JSLanguageConstants.EVENT_HANDLER_FUNCTIONS, JSLanguageConstants.DOM_FUNCTIONS,
				JSLanguageConstants.FIREBUG_FUNCTIONS, JSLanguageConstants.SUPPORT_CONSTANTS, };

		for (String[] strings : arrays)
		{
			for (String string : strings)
			{
				// Handle odd cases in the constants.
				if (string.contains(".") || string.equals("undefined"))
				{
					continue;
				}
				String src = MessageFormat.format(".{0}() a.{0} {0}", string);
				IDocument document = new Document(src);
				scanner.setRange(document, 0, src.length());
				if (string.equals("log"))
				{
					try
					{
						assertTokensMsg("Tested: " + src, tokensLog);
					}
					catch (AssertionError e)
					{
						// The flex-based scanner will properly detect the dots in this case, so, we also check this
						// as an acceptable solution.
						scanner.setRange(document, 0, src.length());
						assertTokensMsg("Tested: " + src, tokensLog2);
					}

				}
				else
				{
					assertTokensMsg("Tested: " + src, tokens);
				}
			}
		}

	}

	public void testKeywordOperators() throws Exception
	{
		String src = StringUtil.join(" ", JSLanguageConstants.KEYWORD_OPERATORS);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null");

	}

	public void testOperators() throws Exception
	{

		String src = StringUtil.join(" ", JSLanguageConstants.OPERATORS);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null");
	}

	public void testSingleCharacterOperators() throws Exception
	{

		String src = StringUtil.join(" ", JSLanguageConstants.SINGLE_CHARACTER_OPERATORS);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null", "keyword.operator.js", "null",
				"keyword.operator.js", "null", "keyword.operator.js", "null");
	}

	public void testKeyWordControl() throws Exception
	{

		String src = StringUtil.join(" ", JSLanguageConstants.KEYWORD_CONTROL);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("keyword.control.js", "null", "keyword.control.js", "null", "keyword.control.js", "null",
				"keyword.control.js", "null", "keyword.control.js", "null", "keyword.control.js", "null",
				"keyword.control.js", "null", "keyword.control.js", "null", "keyword.control.js", "null",
				"keyword.control.js", "null", "keyword.control.js", "null", "keyword.control.js", "null",
				"keyword.control.js", "null", "keyword.control.js", "null", "keyword.control.js", "null");
	}

	public void testKeyWordControlFuture() throws Exception
	{

		String src = StringUtil.join(" ", JSLanguageConstants.KEYWORD_CONTROL_FUTURE);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("keyword.control.js", "null", "keyword.control.js", "null", "keyword.control.js", "null");
	}

	public void testStorageTypes() throws Exception
	{

		String[] storageTypes = JSLanguageConstants.STORAGE_TYPES;
		Collection<String> lst = CollectionsUtil.filter(Arrays.asList(storageTypes), new IFilter<String>()
		{

			public boolean include(String item)
			{
				if (item.equals("function")) // Removing function as it has other implications (and is tested
												// elsewhere).
				{
					return false;
				}
				return true;
			}
		});
		storageTypes = lst.toArray(new String[0]);
		String src = StringUtil.join(" ", storageTypes);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokensMsg("Tested: " + src, "storage.type.js", "null", "storage.type.js", "null", "storage.type.js",
				"null", "storage.type.js", "null", "storage.type.js", "null", "storage.type.js", "null",
				"storage.type.js", "null", "storage.type.js", "null", "storage.type.js", "null", "storage.type.js",
				"null", "storage.type.js", "null", "storage.type.js", "null", "storage.type.js", "null");
	}

	public void testStorageModifiers() throws Exception
	{

		String[] storageTypes = JSLanguageConstants.STORAGE_MODIFIERS;
		String src = StringUtil.join(" ", storageTypes);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		Object[] expected = new Object[] { "undefined.js", "null", "storage.modifier.js", "null",
				"storage.modifier.js", "null", "storage.modifier.js", "null", "storage.modifier.js", "null",
				"storage.modifier.js", "null", "storage.modifier.js", "null", "storage.modifier.js", "null",
				"storage.modifier.js", "null", "storage.modifier.js", "null", "storage.modifier.js", "null",
				"storage.modifier.js", "null", "storage.modifier.js", "null", "storage.modifier.js", "null" };
		try
		{
			assertTokensMsg("Tested: " + src, expected);
		}
		catch (AssertionError e)
		{
			expected[0] = "storage.type.js"; // The JS-JFlex grammar maps const directly as Terminal.VAR, so, add
												// exception for it.
			scanner.setRange(document, 0, src.length());
			assertTokensMsg("Tested: " + src, expected);
		}
	}

	public void testSupportClasses() throws Exception
	{
		String src = StringUtil.join(" ", JSLanguageConstants.SUPPORT_CLASSES);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("support.class.js", "null", "support.class.js", "null", "support.class.js", "null",
				"support.class.js", "null", "support.class.js", "null", "support.class.js", "null", "support.class.js",
				"null", "support.class.js", "null", "support.class.js", "null", "support.class.js", "null",
				"support.class.js", "null", "support.class.js", "null", "support.class.js", "null", "support.class.js",
				"null", "support.class.js", "null", "support.class.js", "null", "support.class.js", "null",
				"support.class.js", "null", "support.class.js", "null", "support.class.js", "null", "support.class.js",
				"null", "support.class.js", "null", "support.class.js", "null", "support.class.js", "null",
				"support.class.js", "null", "support.class.js", "null", "support.class.js", "null", "support.class.js",
				"null", "support.class.js", "null", "support.class.js", "null", "support.class.js", "null",
				"support.class.js", "null", "support.class.js", "null", "support.class.js", "null", "support.class.js",
				"null", "support.class.js", "null", "support.class.js", "null", "support.class.js", "null",
				"support.class.js", "null", "support.class.js", "null", "support.class.js", "null", "support.class.js",
				"null", "support.class.js", "null", "support.class.js", "null", "support.class.js", "null",
				"support.class.js", "null", "support.class.js", "null", "support.class.js", "null", "support.class.js",
				"null");
	}

	public void testSupportDomConstants() throws Exception
	{
		String src = StringUtil.join(" ", JSLanguageConstants.SUPPORT_DOM_CONSTANTS);
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("support.constant.dom.js", "null", "support.constant.dom.js", "null", "support.constant.dom.js",
				"null", "support.constant.dom.js", "null", "support.constant.dom.js", "null",
				"support.constant.dom.js", "null", "support.constant.dom.js", "null", "support.constant.dom.js",
				"null", "support.constant.dom.js", "null", "support.constant.dom.js", "null",
				"support.constant.dom.js", "null", "support.constant.dom.js", "null", "support.constant.dom.js",
				"null", "support.constant.dom.js", "null", "support.constant.dom.js", "null",
				"support.constant.dom.js", "null", "support.constant.dom.js", "null", "support.constant.dom.js",
				"null", "support.constant.dom.js", "null", "support.constant.dom.js", "null",
				"support.constant.dom.js", "null", "support.constant.dom.js", "null");
	}

	public void testOperatorTokens()
	{
		// Note: the original testOperatorTokens did have a '/' in the end which was removed because
		// it matched a regexp with /= %= += -= &= |= ^= ? ! % & * - + ~ = < > ^ | /
		String src = ">>>= >>> <<= >>= === !== >> << != <= >= == -- ++ && || *= /= %= += -= &= |= ^= ? ! % & * - + ~ = < > ^ | ";
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

		src = "/ ";
		document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertToken(src.substring(0, 1), getToken("keyword.operator.js"), 0, 1);
		assertToken(Token.WHITESPACE, 1, 1);

	}

	public void testNumberRegression()
	{
		String src = "var i = 1+\n//\n2;";
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());

		assertToken(getToken("storage.type.js"), 0, 3);
		assertToken(Token.WHITESPACE, 3, 1);
		assertToken(getToken("source.js"), 4, 1);
		assertToken(Token.WHITESPACE, 5, 1);
		assertToken(getToken("keyword.operator.js"), 6, 1);
		assertToken(Token.WHITESPACE, 7, 1);
		assertToken(getToken("constant.numeric.js"), 8, 1);
		assertToken(getToken("keyword.operator.js"), 9, 1);
		assertToken(Token.WHITESPACE, 10, 4);
		// assertToken(getToken("keyword.operator.js"), 11, 1); // technically not correct, but this scanner doesn't
		// // encounter comments normally
		// assertToken(getToken("keyword.operator.js"), 12, 1); // technically not correct, but this scanner doesn't
		// // encounter comments normally
		// assertToken(Token.WHITESPACE, 13, 1);
		assertToken(getToken("constant.numeric.js"), 14, 1);
		assertToken(getToken("punctuation.terminator.statement.js"), 15, 1);
	}

	/**
	 * This is a new test for corner-cases in the jflex scanner that the old scanner did not support.
	 */
	public void testFunctionHandlingOnJFlex() throws Exception
	{
		String src = "a = a = function"; // a should be a function name (i.e.: deal with look-ahead issues).
		IDocument document = new Document(src);
		scanner.setRange(document, 0, src.length());
		assertTokens("entity.name.function.js", "null", "keyword.operator.js", "null", "entity.name.function.js",
				"null", "keyword.operator.js", "null", "storage.type.function.js", "null");
	}
}
