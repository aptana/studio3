/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.JSParseState;

public class JSLintValidatorTest extends AbstractValidatorTestCase
{

	@Override
	protected AbstractBuildParticipant createValidator()
	{
		return new JSLintValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return JSPlugin.PLUGIN_ID;
			}

			@Override
			public String getId()
			{
				return ID;
			}

			@Override
			// Don't filter anything out
			public List<String> getFilters()
			{
				return Collections.emptyList();
			}

			@Override
			// Don't skip errors/warnings just because they're on same line!
			protected boolean hasErrorOrWarningOnLine(List<IProblem> items, int line)
			{
				return false;
			}
		};
	}

	@Override
	protected String getFileExtension()
	{
		return "js";
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new JSParseState(), IJSConstants.JSLINT_PROBLEM_MARKER_TYPE);
	}

	public void testJSLintValidator() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint undef: false */\n" +
				"var foo = function() {\n" +
				"  hello();\n" +
				"};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "'hello' was used before it was defined.", 3, IMarker.SEVERITY_WARNING, 50);
	}

	public void testVarANot() throws CoreException
	{
		String text = "var chris = blah = 'something';";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Variable blah was not declared correctly.", 1, IMarker.SEVERITY_WARNING, 12);
	}

	public void testUsedBeforeA() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint undef: false */\n" +
				"function chris() {\n" +
				"    var bar = foo + 1;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "'foo' was used before it was defined.", 3, IMarker.SEVERITY_WARNING, 58);
	}

	public void testWriteIsWrong() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint evil: false */\n" +
				"document.write('something');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "document.write can be a form of eval.", 2, IMarker.SEVERITY_WARNING, 24);
	}

	public void testWriteIsWrong2() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint evil: false */\n" +
				"document.writeln('something');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "document.write can be a form of eval.", 2, IMarker.SEVERITY_WARNING, 24);
	}

	public void testWrapRegexp() throws CoreException
	{
		// @formatter:off
		String text = "function chris() { return /regexp/i; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Wrap the /regexp/ literal in parens to disambiguate the slash operator.", 1,
				IMarker.SEVERITY_WARNING, 26);
	}

	public void testWrapImmediate() throws CoreException
	{
		// @formatter:off
		String text = "var someVar = function () {\n" +
					"}();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(
				items,
				"Wrap an immediate function invocation in parentheses to assist the reader in understanding that the expression is the result of a function, and not the function itself.",
				2, IMarker.SEVERITY_WARNING, 30);
	}

	public void testFixedWrapImmediate() throws CoreException
	{
		// @formatter:off
		String text = "var someVar = (function () {\n" +
					"}());";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(
				items,
				"Wrap an immediate function invocation in parentheses to assist the reader in understanding that the expression is the result of a function, and not the function itself.");
	}

	public void testWeirdTernary1() throws CoreException
	{
		// @formatter:off
		String text = "var someVar = true ? 1 : 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird ternary.", 1, IMarker.SEVERITY_WARNING, 23);
	}

	public void testWeirdTernary2() throws CoreException
	{
		// @formatter:off
		String text = "var someVar = true ? 'string' : 'string';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird ternary.", 1, IMarker.SEVERITY_WARNING, 30);
	}

	public void testWeirdRelation1() throws CoreException
	{
		// @formatter:off
		String text = "var result = (1 >= 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 16);
	}

	public void testWeirdRelation2() throws CoreException
	{
		// @formatter:off
		String text = "var result = ('1' == 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 18);
	}

	public void testWeirdRelation3() throws CoreException
	{
		// @formatter:off
		String text = "var result = ('1' != 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 18);
	}

	public void testWeirdRelation4() throws CoreException
	{
		// @formatter:off
		String text = "var result = ('1' > 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 18);
	}

	public void testWeirdRelation5() throws CoreException
	{
		// @formatter:off
		String text = "var result = ('1' < 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 18);
	}

	public void testWeirdRelation6() throws CoreException
	{
		// @formatter:off
		String text = "var result = ('1' <= 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 18);
	}

	public void testWeirdRelation7() throws CoreException
	{
		// @formatter:off
		String text = "var result = ('1' === 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 18);
	}

	public void testWeirdRelation8() throws CoreException
	{
		// @formatter:off
		String text = "var result = ('1' !== 2);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird relation.", 1, IMarker.SEVERITY_WARNING, 18);
	}

	public void testWeirdProgram1() throws CoreException
	{
		// @formatter:off
		String text = "break;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	public void testWeirdProgram2() throws CoreException
	{
		// @formatter:off
		String text = "continue;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	public void testWeirdProgram3() throws CoreException
	{
		// @formatter:off
		String text = "return;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	public void testWeirdProgram4() throws CoreException
	{
		// @formatter:off
		String text = "throw 'e';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 6);
	}

	public void testWeirdNew() throws CoreException
	{
		// @formatter:off
		String text = "var someObj = new function () {};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird construction. Delete 'new'.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	public void testUseOr() throws CoreException
	{
		// @formatter:off
		String text = "var someObj = false ? false : true;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the || operator.", 1, IMarker.SEVERITY_WARNING, 20);
	}

	public void testWeirdCondition1() throws CoreException
	{
		// @formatter:off
		String text = "var someObj = false ? false : true;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird condition.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	public void testWeirdCondition2() throws CoreException
	{
		// @formatter:off
		String text = "var someObj = false || false;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird condition.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	public void testWeirdCondition3() throws CoreException
	{
		// @formatter:off
		String text = "var someObj = false && false;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird condition.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	public void testWeirdCondition4() throws CoreException
	{
		// @formatter:off
		String text = "if (2 - 3) {\n" +
					"    return 4;\n" +
					"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird condition.", 1, IMarker.SEVERITY_WARNING, 4);
	}

	public void testWeirdAssignment() throws CoreException
	{
		// @formatter:off
		String text = "var foo;\n" +
				"foo = foo;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird assignment.", 2, IMarker.SEVERITY_WARNING, 13);
	}

	public void testUseParam() throws CoreException
	{
		// @formatter:off
		String text = "function chris(a, b) {\n" +
				"   return arguments[1];\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use a named parameter.", 2, IMarker.SEVERITY_WARNING, 33);
	}

	public void testUseObject() throws CoreException
	{
		// @formatter:off
		String text = "var foo = new Object();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the object literal notation {}.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	public void testUseObject2() throws CoreException
	{
		// @formatter:off
		String text = "var foo = Object();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the object literal notation {}.", 1, IMarker.SEVERITY_WARNING, 16);
	}

	public void testUseBraces() throws CoreException
	{
		// @formatter:off
		String text = "var regexp = /  /;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Spaces are hard to count. Use {2}.", 1, IMarker.SEVERITY_WARNING, 15);
	}

	public void testUseArray() throws CoreException
	{
		// @formatter:off
		String text = "var array = new Array(1, 2, 3);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 21);
	}

	public void testUseArray2() throws CoreException
	{
		// @formatter:off
		String text = "var array = new Array();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 21);
	}

	public void testUseArray3() throws CoreException
	{
		// @formatter:off
		String text = "var array = new Array;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 16);
	}

	public void testUseArray4() throws CoreException
	{
		// @formatter:off
		String text = "'string'.split(1);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 9);
	}

	public void testURL() throws CoreException
	{
		// @formatter:off
		String text = "var string = 'javascript: blah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testURL2() throws CoreException
	{
		// @formatter:off
		String text = "var string = 'jscript: blah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testURL3() throws CoreException
	{
		// @formatter:off
		String text = "var string = 'ecmascript: blah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testURL4() throws CoreException
	{
		// @formatter:off
		String text = "var string = 'vbscript: blah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testURL5() throws CoreException
	{
		// @formatter:off
		String text = "var string = 'mocha: blah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testURL6() throws CoreException
	{
		// @formatter:off
		String text = "var string = 'livescript: blah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testURL7() throws CoreException
	{
		// @formatter:off
		String text = "var string = 'livescri' + 'pt: blah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testURL8() throws CoreException
	{
		// @formatter:off
		String text = "function bar() {\n" +
		"jscript:\n" +
		"    for (var i = 0; i < 10, i++)\n" +
		"    {\n" +
		"    }\n" +
		"};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "JavaScript URL.", 2, IMarker.SEVERITY_WARNING, 17);
	}

	// FIXME I can't seem to find a way to insert the required special characters into the string
	// public void testUnsafe() throws CoreException
	// {
//		// @formatter:off
//		String text = "var string = '^A';";
//		// @formatter:on
	//
	// List<IProblem> items = getParseErrors(text);
	// assertProblemExists(items, "Unsafe character.", 1, IMarker.SEVERITY_WARNING, 14);
	// }

	public void testUnrecognizedTagA() throws CoreException
	{
		// @formatter:off
		String text = "<html><madeup></madeup></html>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unrecognized tag '<madeup>'.", 1, IMarker.SEVERITY_WARNING, 7);
	}

	public void testUnrecognizedStyleAttributeA() throws CoreException
	{
		// @formatter:off
		String text = "<html>\n" +
				"<body>\n" +
				"<div style=\"fake: value;\"></div>\n" +
				"</body>\n" +
				"</html>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unrecognized style attribute 'fake'.", 3, IMarker.SEVERITY_WARNING, 26);
	}

	public void testTrailingDecimalA() throws CoreException
	{
		// @formatter:off
		String text = "var number = 1.;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "A trailing decimal point can be confused with a dot: '.1.'.", 1,
				IMarker.SEVERITY_WARNING, 15);
	}

	public void testReservedA1() throws CoreException
	{
		// @formatter:off
		String text = "var __iterator__ = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Reserved name '__iterator__'.", 1, IMarker.SEVERITY_WARNING, 4);
	}

	public void testReservedA2() throws CoreException
	{
		// @formatter:off
		String text = "var __proto__ = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Reserved name '__proto__'.", 1, IMarker.SEVERITY_WARNING, 4);
	}

	public void testRadix() throws CoreException
	{
		// @formatter:off
		String text = "var number = parseInt('');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Missing radix parameter.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testFunctionEval() throws CoreException
	{
		// @formatter:off
		String text = "var number = new Function('');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "The Function constructor is eval.", 1, IMarker.SEVERITY_WARNING, 25);
	}

	public void testEvil1() throws CoreException
	{
		// @formatter:off
		String text = "eval('blah');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "eval is evil.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	public void testEvil2() throws CoreException
	{
		// @formatter:off
		String text = "var foo = '';\n" +
					"foo['eval'] = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "eval is evil.", 2, IMarker.SEVERITY_WARNING, 18);
	}

	public void testEvil3() throws CoreException
	{
		// @formatter:off
		String text = "var foo = '';\n" +
					"foo.execScript = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "eval is evil.", 2, IMarker.SEVERITY_WARNING, 29);
	}

	public void testDanglingA1() throws CoreException
	{
		// @formatter:off
		String text = "var _foo = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unexpected dangling '_' in '_foo'.", 1, IMarker.SEVERITY_WARNING, 4);
	}

	public void testDanglingA2() throws CoreException
	{
		// @formatter:off
		String text = "var foo_ = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unexpected dangling '_' in 'foo_'.", 1, IMarker.SEVERITY_WARNING, 4);
	}

	public void testImpliedEvil1() throws CoreException
	{
		// @formatter:off
		String text = "setTimeout('blah', 2000);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Implied eval is evil. Pass a function instead of a string.", 1,
				IMarker.SEVERITY_WARNING, 0);
	}

	public void testImpliedEvil2() throws CoreException
	{
		// @formatter:off
		String text = "setInterval('blah', 2000);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Implied eval is evil. Pass a function instead of a string.", 1,
				IMarker.SEVERITY_WARNING, 0);
	}

	public void testIsNaN1() throws CoreException
	{
		// @formatter:off
		String text = "var result = NaN >= 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testIsNaN2() throws CoreException
	{
		// @formatter:off
		String text = "var result = NaN <= 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testIsNaN3() throws CoreException
	{
		// @formatter:off
		String text = "var result = NaN == 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testIsNaN4() throws CoreException
	{
		// @formatter:off
		String text = "var result = NaN === 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testIsNaN5() throws CoreException
	{
		// @formatter:off
		String text = "var result = NaN != 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testIsNaN6() throws CoreException
	{
		// @formatter:off
		String text = "var result = NaN !== 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testIsNaN7() throws CoreException
	{
		// @formatter:off
		String text = "var result = NaN < 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testIsNaN8() throws CoreException
	{
		// @formatter:off
		String text = "var result = 2 > NaN;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the isNaN function to compare with NaN.", 1, IMarker.SEVERITY_WARNING, 17);
	}

	public void testSubscript() throws CoreException
	{
		// @formatter:off
		String text = "var result = true;\n" +
				"result['something'] = 'yeah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "['something'] is better written in dot notation.", 2, IMarker.SEVERITY_WARNING, 26);
	}

	public void testSubscriptOK() throws CoreException
	{
		// @formatter:off
		String text = "var result = true;\n" +
				"result['case'] = 'yeah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "['case'] is better written in dot notation.");
	}

	public void testSyncA() throws CoreException
	{
		// @formatter:off
		String text = "var result = true;\n" +
				"result.methodSync('yeah');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unexpected sync method: 'methodSync'.", 2, IMarker.SEVERITY_WARNING, 26);
	}

	public void testLeadingDecimalA() throws CoreException
	{
		// @formatter:off
		String text = "var number = .8;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "A leading decimal point can be confused with a dot: '.8'.", 1,
				IMarker.SEVERITY_WARNING, 13);
	}

	public void testNestedNot() throws CoreException
	{
		// @formatter:off
		String text = "<html>\n" +
				"<head>\n" +
				"<style>\n" +
				":not(:not(table)) {\n" +
				"color: red;\n" +
				"}\n" +
				"</style>\n" +
				"</head>\n" +
				"</html>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Nested not.", 4, IMarker.SEVERITY_WARNING, 27);
	}

	public void testAnd() throws CoreException
	{
		// @formatter:off
		String text = "var one = 1;\n" +
				"var that = 'that';\n" +
				"var another = true;\n" +
				"\n" +
				"var result = one || that && another;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "The '&&' subexpression should be wrapped in parens.", 5, IMarker.SEVERITY_WARNING,
				78);
	}

	public void testAvoidA1() throws CoreException
	{
		// @formatter:off
		String text = "function foo() {\n" +
				"    var yeah = arguments.caller;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Avoid 'arguments.caller'.", 2, IMarker.SEVERITY_WARNING, 32);
	}

	public void testAvoidA2() throws CoreException
	{
		// @formatter:off
		String text = "function foo() {\n" +
				"    var yeah = arguments.callee;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Avoid 'arguments.callee'.", 2, IMarker.SEVERITY_WARNING, 32);
	}

	public void testConstructorNameA1() throws CoreException
	{
		// @formatter:off
		String text = "function bar() {}\n" +
				"var foo = new bar();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "A constructor name 'bar' should start with an uppercase letter.", 2,
				IMarker.SEVERITY_WARNING, 32);
	}

	public void testEmptyClass() throws CoreException
	{
		// @formatter:off
		String text = "var regexp = /[]/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty class.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	public void testEmptyCase() throws CoreException
	{
		// @formatter:off
		String text = "var something = 1;\n" +
				"var i = 0;\n" +
				"switch (something) {\n" +
				"case 1:\n" +
				"default:\n" +
				"    break;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty case.", 5, IMarker.SEVERITY_WARNING, 59);
	}

	public void testStrangeLoop1() throws CoreException
	{
		// @formatter:off
		String text = "var i;\n" +
				"for (i = 0; i < 10; i++) {\n" +
				"    var x = i * i;\n" +
				"    break;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Strange loop.", 4, IMarker.SEVERITY_WARNING, 62);
	}

	public void testStrangeLoop2() throws CoreException
	{
		// @formatter:off
		String text = "var i;\n" +
				"do {\n" +
				"    var x = i * i;\n" +
				"    continue;\n" +
				"} while (i < 20);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Strange loop.", 4, IMarker.SEVERITY_WARNING, 43);
	}

	public void testStrangeLoop3() throws CoreException
	{
		// @formatter:off
		String text = "var i = 0;\n" +
				"while (true) {\n" +
				"    var x = i * i;\n" +
				"    throw 'error!';\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Strange loop.", 4, IMarker.SEVERITY_WARNING, 63);
	}

	public void testNameFunction() throws CoreException
	{
		// @formatter:off
		String text = "function () {}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Missing name in function statement.", 1, IMarker.SEVERITY_WARNING, 9);
	}

	public void testAssignException() throws CoreException
	{
		// @formatter:off
		String text = "function message() {\n" +
				"    try {\n" +
				"        // comment\n" +
				"    } catch (err) {\n" +
				"        err = 'something';\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not assign to the exception parameter.", 5, IMarker.SEVERITY_WARNING, 78);
	}

	public void testBadNumber1() throws CoreException
	{
		// @formatter:off
		String text = "var number = 1.7976931348623157e+309;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad number '1.7976931348623157e+309'.", 1, IMarker.SEVERITY_WARNING, 36);
	}

	public void testBadNumber2() throws CoreException
	{
		// @formatter:off
		String text = "var number = -1.7976931348623157e+309;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad number '1.7976931348623157e+309'.", 1, IMarker.SEVERITY_WARNING, 37);
	}

	public void testBadNameA1() throws CoreException
	{
		// @formatter:off
		String text = "<html><head 123></head></html>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad name: '123'.", 1, IMarker.SEVERITY_WARNING, 12);
	}

	public void testBadNameA2() throws CoreException
	{
		// @formatter:off
		String text = "<html><head></1></html>>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad name: '1'.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	public void testBadAssignmentA1() throws CoreException
	{
		// @formatter:off
		String text = "'string' = 2;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 9);
	}

	public void testBadAssignmentA2() throws CoreException
	{
		// @formatter:off
		String text = "function foo() { arguments['bar'] = 1; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 34);
	}

	public void testBadAssignmentA3() throws CoreException
	{
		// @formatter:off
		String text = "function foo() { arguments[1] = 1; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 30);
	}

	public void testBadAssignmentA4() throws CoreException
	{
		// @formatter:off
		String text = "function foo() { arguments.bar = 1; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 31);
	}

	public void testBadAssignmentA5() throws CoreException
	{
		// @formatter:off
		String text = "/sdf/ = 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 6);
	}

	public void testDelete1() throws CoreException
	{
		// @formatter:off
		String text = "var x = 1;\n" +
				"delete x;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Only properties should be deleted.", 2, IMarker.SEVERITY_WARNING, 19);
	}

	public void testDelete2() throws CoreException
	{
		// @formatter:off
		String text = "function x() {}\n" +
				"delete x;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Only properties should be deleted.", 2, IMarker.SEVERITY_WARNING, 24);
	}

	public void testDeleteOK() throws CoreException
	{
		// @formatter:off
		String text = "var o = { x: 1 };\n" +
				"delete o.x;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Only properties should be deleted.");
	}

	public void testTabsDontMessUpOffset() throws CoreException
	{
		// @formatter:off
		String text = "function x() {\n" +
				"	eval('blah!');\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "eval is evil.", 2, IMarker.SEVERITY_WARNING, 16);
	}

	public void testSlashEqual() throws CoreException
	{
		// @formatter:off
		String text = "/= 2;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "A regular expression literal can be confused with '/='.", 1,
				IMarker.SEVERITY_WARNING, 3);
	}

	public void testExpectedAAtBC() throws CoreException
	{
		// @formatter:off
		String text = "function x() {\n" +
				"var a = 1;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected 'var' at column 5, not column 1.", 2, IMarker.SEVERITY_WARNING, 15);
	}

	public void testES5() throws CoreException
	{
		// @formatter:off
		String text = "var obj = (function () {\n" +
				"        var a;\n" +
				"        return {\n" +
				"            get a() {\n" +
				"                return a;\n" +
				"            },\n" +
				"            \n" +
				"            set a(value) {\n" +
				"                a = 'prepender: ' + value;\n" +
				"            }\n" +
				"        };\n" +
				"    }());";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "This is an ES5 feature.", 4, IMarker.SEVERITY_WARNING, 69);
	}

	public void testES5_2() throws CoreException
	{
		// @formatter:off
		String text = "var string = \"\\\n" +
				"\";";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "This is an ES5 feature.", 1, IMarker.SEVERITY_WARNING, 15);
	}

	public void testParameterSetA() throws CoreException
	{
		// @formatter:off
		String text = "var obj = (function () {\n" +
				"        var a;\n" +
				"        return {\n" +
				"            get a() {\n" +
				"                return a;\n" +
				"            },\n" +
				"            \n" +
				"            set a() {\n" +
				"                a = 'prepender: ';\n" +
				"            }\n" +
				"        };\n" +
				"    }());";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		// FIXME Should be an error
		assertProblemExists(items, "Expected parameter (value) in set value function.", 8, IMarker.SEVERITY_WARNING,
				145);
	}

	public void testParameterSetA2() throws CoreException
	{
		// @formatter:off
		String text = "var obj = (function () {\n" +
				"        var a;\n" +
				"        return {\n" +
				"            get a() {\n" +
				"                return a;\n" +
				"            },\n" +
				"            \n" +
				"            set a(value, other) {\n" +
				"                a = 'prepender: ';\n" +
				"            }\n" +
				"        };\n" +
				"    }());";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		// FIXME Should be an error
		assertProblemExists(items, "Expected parameter (value) in set value function.", 8, IMarker.SEVERITY_WARNING,
				145);
	}

	public void testParameterAGetB() throws CoreException
	{
		// @formatter:off
		String text = "var obj = (function () {\n" +
				"        var a;\n" +
				"        return {\n" +
				"            get a(param) {\n" +
				"                return a;\n" +
				"            },\n" +
				"            \n" +
				"            set a(value) {\n" +
				"                a = 'prepender: ';\n" +
				"            }\n" +
				"        };\n" +
				"    }());";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unexpected parameter 'param' in get a function.", 4, IMarker.SEVERITY_WARNING, 75);
	}

	public void testOctalA() throws CoreException
	{
		for (int i = 0; i <= 6; i++)
		{
			// @formatter:off
			String text = "var string = \"\\" + i + "\";";
			// @formatter:on

			List<IProblem> items = getParseErrors(text);
			assertProblemExists(items, "Don't use octal: '\\" + i + "'. Use '\\u....' instead.", 1,
					IMarker.SEVERITY_WARNING, 15);
		}
	}

	public void testParameterArgumentsA() throws CoreException
	{
		// @formatter:off
		String text = "function foo(param) {\n" +
				"    param = 3;\n" +
				"    arguments[0] = true;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not mutate parameter 'param' when using 'arguments'.", 1,
				IMarker.SEVERITY_WARNING, 13);
	}

	public void testUnnecessaryInitialize() throws CoreException
	{
		// @formatter:off
		String text = "var foo = undefined;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "It is not necessary to initialize 'foo' to 'undefined'.", 1,
				IMarker.SEVERITY_WARNING, 8);
	}

	public void testStatementBlock() throws CoreException
	{
		// @formatter:off
		String text = "var foo = 1;\n" +
				"{}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected to see a statement and instead saw a block.", 2, IMarker.SEVERITY_WARNING,
				14);
	}

	public void testInsecureA1() throws CoreException
	{
		// @formatter:off
		String text = "var r = /.regexp/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Insecure '.'.", 1, IMarker.SEVERITY_WARNING, 9);
	}

	public void testInsecureA2() throws CoreException
	{
		// @formatter:off
		String text = "var r = /[^t]regexp/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Insecure '^'.", 1, IMarker.SEVERITY_WARNING, 10);
	}

	public void testMoveInvocation() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint sloppy: true, es5: true */\n" +
				"var obj = (function () {\n" +
				"        var a;\n" +
				"        return {\n" +
				"            get a() {\n" +
				"                return a;\n" +
				"            },\n" +
				"            set a(value) {\n" +
				"                a = 'prepender: ';\n" +
				"            }\n" +
				"        };\n" +
				"    })();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Move the invocation into the parens that contain the function.", 12,
				IMarker.SEVERITY_WARNING, 249);
	}

	public void testReadOnly() throws CoreException
	{
		Set<String> predefineds = CollectionsUtil.newSet("Array", "Boolean", "Date", "decodeURI", "decodeURIComponent",
				"encodeURI", "encodeURIComponent", "Error", "eval", "EvalError", "Function", "isFinite", "isNaN",
				"JSON", "Math", "Number", "Object", "parseInt", "parseFloat", "RangeError", "ReferenceError", "RegExp",
				"String", "SyntaxError", "TypeError", "URIError");
		for (String predefined : predefineds)
		{
			// @formatter:off
			String text = predefined + " = true;";
			// @formatter:on

			List<IProblem> items = getParseErrors(text);
			// FIXME Should be an error
			assertProblemExists(items, "Read only.", 1, IMarker.SEVERITY_WARNING, "eval".equals(predefined) ? 7 : 0);
		}
	}

	public void testBadWrap() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint sloppy: true, es5: true */\n" +
				"var obj = (function () {\n" +
				"        var a;\n" +
				"        return {\n" +
				"            get a() {\n" +
				"                return a;\n" +
				"            },\n" +
				"            set a(value) {\n" +
				"                a = 'prepender: ';\n" +
				"            }\n" +
				"        };\n" +
				"    });";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items,
				"Do not wrap function literals in parens unless they are to be immediately invoked.", 2,
				IMarker.SEVERITY_WARNING, 46);
	}

	public void testBadInvocation1() throws CoreException
	{
		// @formatter:off
		String text = "'string'(7);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		// FIXME Should be error
		assertProblemExists(items, "Bad invocation.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	public void testBadInvocation2() throws CoreException
	{
		// @formatter:off
		String text = "/regexp/(7);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		// FIXME Should be error
		assertProblemExists(items, "Bad invocation.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	protected void assertProblemExists(List<IProblem> items, String msg, int line, int severity, int offset)
	{
		IProblem item = assertContains(items, msg);
		assertEquals("line", line, item.getLineNumber());
		assertEquals("severity", severity, item.getSeverity());
		assertEquals("offset", offset, item.getOffset());
	}
}
