/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.aptana.buildpath.core.tests.AbstractValidatorTestCase;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.JSParseState;

public class JSStyleValidatorTest extends AbstractValidatorTestCase
{

	@Override
	protected IBuildParticipant createValidator()
	{
		return new JSStyleValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return JSCorePlugin.PLUGIN_ID;
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
	protected String getContentType()
	{
		return IJSConstants.CONTENT_TYPE_JS;
	}

	@Override
	protected String getFileExtension()
	{
		return "js";
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new JSParseState(source), IJSConstants.JSSTYLE_PROBLEM_MARKER_TYPE);
	}
	
	@Test
	public void testAlreadyDefined() throws CoreException
	{
		// @formatter:off
		String text = "function bar() {\n" +
				"    var foo = 1;\n" +
				"    var foo = 2;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "'foo' is already defined.", 3, IMarker.SEVERITY_WARNING, 42);
	}
	
	

	@Test
	public void testAlreadyDefinedOK1() throws CoreException
	{
		// @formatter:off
		String text = "var Class = ( function() {\n" +
				"    var IS_DONTENUM_BUGGY = ( function() {\n" +
				"        return true;\n" +
				"    }());\n" +
				"}());";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "'' is already defined.");
	}

	@Test
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

	@Test
	public void testAScopeOK1() throws CoreException
	{
		// @formatter:off
		String text = "var Prototype = {\n" +
				"	Browser : (function() {\n" +
				"		var isOpera = Object.prototype.toString.call(window.opera) == '[object Opera]';\n" +
				"	})(),\n" +
				"	\n" +
				"	BrowserFeatures : {\n" +
				"		ElementExtensions : (function() {\n" +
				"			var constructor = window.Element || window.HTMLElement;\n" +
				"			return !!(constructor && constructor.prototype);\n" +
				"		})()\n" +
				"	}\n" +
				"}; ";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "'window' used out of scope.");
	}

	@Test
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
	
	@Test
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

	@Test
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

	@Test
	public void testBadAssignmentA1() throws CoreException
	{
		// @formatter:off
		String text = "'string' = 2;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 9);
	}

	@Test
	public void testBadAssignmentA2() throws CoreException
	{
		// @formatter:off
		String text = "function foo() { arguments['bar'] = 1; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 34);
	}

	@Test
	public void testBadAssignmentA3() throws CoreException
	{
		// @formatter:off
		String text = "function foo() { arguments[1] = 1; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 30);
	}

	@Test
	public void testBadAssignmentA4() throws CoreException
	{
		// @formatter:off
		String text = "function foo() { arguments.bar = 1; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 31);
	}

	@Test
	public void testBadAssignmentA5() throws CoreException
	{
		// @formatter:off
		String text = "/sdf/ = 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad assignment.", 1, IMarker.SEVERITY_WARNING, 6);
	}

	@Test
	public void testBadAssignmentAOK1() throws CoreException
	{
		// @formatter:off
		String text = "Prototype.BrowserFeatures.SpecificElementExtensions = false;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Bad assignment.");
	}

	@Test
	public void testBadNew() throws CoreException
	{
		// @formatter:off
		String text = "function Blah() {}\n" +
				"new Blah();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not use 'new' for side effects.", 2, IMarker.SEVERITY_WARNING, 29);
	}

	@Test
	public void testBadNewOK1() throws CoreException
	{
		// @formatter:off
		String text = "function Blah() {}\n" +
				"var b = new Blah();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Do not use 'new' for side effects.");
	}

	@Test
	public void testBadNewOK2() throws CoreException
	{
		// @formatter:off
		String text = "function Blah() {}\n" +
				"var b;\n" +
				"b = new Blah();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Do not use 'new' for side effects.");
	}

	@Test
	public void testBadNewOK3() throws CoreException
	{
		// @formatter:off
		String text = "throw new TypeError();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Do not use 'new' for side effects.");
	}

	@Test
	public void testBadNewOK4() throws CoreException
	{
		// @formatter:off
		String text = "function foo() { return new TypeError(); }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Do not use 'new' for side effects.");
	}

	@Test
	public void testBadNumber1() throws CoreException
	{
		// @formatter:off
		String text = "var number = 1.7976931348623157e+309;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad number '1.7976931348623157e+309'.", 1, IMarker.SEVERITY_WARNING, 36);
	}

	@Test
	public void testBadNumber2() throws CoreException
	{
		// @formatter:off
		String text = "var number = -1.7976931348623157e+309;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Bad number '1.7976931348623157e+309'.", 1, IMarker.SEVERITY_WARNING, 37);
	}

	@Test
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

	@Test
	public void testCombineVar() throws CoreException
	{
		// @formatter:off
		String text = "function foo() {\n" +
				"    var bar = 1;\n" +
				"    var x = 10;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Combine this with the previous 'var' statement.", 3, IMarker.SEVERITY_WARNING, 42);
	}

	@Test
	public void testCombineVarOK1() throws CoreException
	{
		// @formatter:off
		String text = "function eachSlice(number, iterator, context) {\n" +
				"	var index = -number, slices = [], array = this.toArray();\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Combine this with the previous 'var' statement.");
	}

	@Test
	public void testConditionalAssignmentAndLeftSide() throws CoreException
	{
		// @formatter:off
		String text = "var a = 1;\n" +
				"if ((a = 3) && a) {\n" +
				"    var b = 3;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 18);
	}

	@Test
	public void testConditionalAssignmentAndRightSide() throws CoreException
	{
		// @formatter:off
		String text = "var a = 1;\n" +
				"if (a && (a = 3)) {\n" +
				"    var b = 3;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 23);
	}

	@Test
	public void testConditionalAssignmentDoWhile() throws CoreException
	{
		// @formatter:off
		String text = "var a = 3;\n" +
				"do {\n" +
				"    a = 7;\n" +
				"} while (a = 1);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 4,
				IMarker.SEVERITY_WARNING, 38);
	}

	@Test
	public void testConditionalAssignmentFor() throws CoreException
	{
		// @formatter:off
		String text = "var a;\n" +
				"for (a = 3; a = 7; a += 3) {\n" +
				"    var b = 1;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 21);
	}

	@Test
	public void testConditionalAssignmentIf() throws CoreException
	{
		// @formatter:off
		String text = "var a = 3;\n" +
				"if (a = 1) {\n" +
				"    a = 7;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 17);
	}

	@Test
	public void testConditionalAssignmentOrLeftSide() throws CoreException
	{
		// @formatter:off
		String text = "var a = 1;\n" +
				"if ((a = 3) || a) {\n" +
				"    var b = 3;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 18);
	}

	@Test
	public void testConditionalAssignmentOrRightSide() throws CoreException
	{
		// @formatter:off
		String text = "var a = 1;\n" +
				"if (a || (a = 3)) {\n" +
				"    var b = 3;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 23);
	}

	@Test
	public void testConditionalAssignmentSwitch() throws CoreException
	{
		// @formatter:off
		String text = "var a;\n" +
				"switch (a = 3) {\n" +
				"case 3:\n" +
				"    var b = 1;\n" +
				"    break;\n" +
				"default:\n" +
				"    break;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 17);
	}

	@Test
	public void testConditionalAssignmentTernary() throws CoreException
	{
		// @formatter:off
		String text = "var a = 3;\n" +
				"var b = (a = 3) ? true : false;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 22);
	}

	@Test
	public void testConditionalAssignmentWhile() throws CoreException
	{
		// @formatter:off
		String text = "var a = 3;\n" +
				"while (a = 1) {\n" +
				"    a = 7;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2,
				IMarker.SEVERITY_WARNING, 20);
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

	@Test
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

	@Test
	public void testDangerousComment1() throws CoreException
	{
		setOption("safe", true);
		// @formatter:off
		String text = "/* </ */";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Dangerous comment.", 1, IMarker.SEVERITY_WARNING, 2);
	}

	@Test
	public void testDangerousComment2() throws CoreException
	{
		setOption("safe", true);
		// @formatter:off
		String text = "// <";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Dangerous comment.", 1, IMarker.SEVERITY_WARNING, 2);
	}

	@Test
	public void testDangerousComment3() throws CoreException
	{
		setOption("safe", true);
		// @formatter:off
		String text = "// scRipT";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Dangerous comment.", 1, IMarker.SEVERITY_WARNING, 2);
	}

	@Test
	public void testDangerousComment4() throws CoreException
	{
		setOption("safe", true);
		// @formatter:off
		String text = "// &Lt";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Dangerous comment.", 1, IMarker.SEVERITY_WARNING, 2);
	}

	@Test
	public void testDangerousComment5() throws CoreException
	{
		setOption("safe", true);
		// @formatter:off
		String text = "/* ]    ] */";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Dangerous comment.", 1, IMarker.SEVERITY_WARNING, 2);
	}

	@Test
	public void testDangerousComment6() throws CoreException
	{
		setOption("safe", true);
		// @formatter:off
		String text = "/* <   ! */";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Dangerous comment.", 1, IMarker.SEVERITY_WARNING, 2);
	}

	@Test
	public void testDangerousComment7() throws CoreException
	{
		setOption("safe", true);
		// @formatter:off
		String text = "/*@cc */";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Dangerous comment.", 1, IMarker.SEVERITY_WARNING, 2);
	}

	@Test
	public void testDangerousCommentOKIfSafeOptionNotExplicitlyTurnedOn() throws CoreException
	{
		// @formatter:off
		String text = "/*  Prototype Javascript framework, version 1.7 */";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Dangerous comment.");
	}

	@Test
	public void testDanglingA1() throws CoreException
	{
		// @formatter:off
		String text = "var _foo = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unexpected dangling '_' in '_foo'.", 1, IMarker.SEVERITY_WARNING, 4);
	}

	@Test
	public void testDanglingA2() throws CoreException
	{
		// @formatter:off
		String text = "var foo_ = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unexpected dangling '_' in 'foo_'.", 1, IMarker.SEVERITY_WARNING, 4);
	}

	@Test
	public void testDeleteOK() throws CoreException
	{
		// @formatter:off
		String text = "var o = { x: 1 };\n" +
				"delete o.x;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Only properties should be deleted.");
	}

	@Test
	public void testEmptyBlock1() throws CoreException
	{
		// @formatter:off
		String text = "if (true) {\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty block.", 2, IMarker.SEVERITY_WARNING, 12);
	}

	@Test
	public void testEmptyBlock2() throws CoreException
	{
		// @formatter:off
		String text = "if (true) {\n" +
				"    var i = 0;\n" +
				"} else {\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty block.", 4, IMarker.SEVERITY_WARNING, 36);
	}

	@Test
	public void testEmptyBlock3() throws CoreException
	{
		// @formatter:off
		String text = "for (i = 0; i < 10; i += 1) {\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty block.", 2, IMarker.SEVERITY_WARNING, 30);
	}

	@Test
	public void testEmptyBlock4() throws CoreException
	{
		// @formatter:off
		String text = "while (true) {\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty block.", 2, IMarker.SEVERITY_WARNING, 15);
	}

	@Test
	public void testEmptyBlock5() throws CoreException
	{
		// @formatter:off
		String text = "do {\n" +
				"} while (i > 0);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty block.", 2, IMarker.SEVERITY_WARNING, 7);
	}

	@Test
	public void testEmptyClass() throws CoreException
	{
		// @formatter:off
		String text = "var regexp = /[]/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Empty class.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	@Test
	public void testEvil1() throws CoreException
	{
		// @formatter:off
		String text = "eval('blah');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "eval is evil.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	@Test
	public void testEvil2() throws CoreException
	{
		// @formatter:off
		String text = "var foo = '';\n" +
					"foo['eval'] = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "eval is evil.", 2, IMarker.SEVERITY_WARNING, 18);
	}

	@Test
	public void testEvil3() throws CoreException
	{
		// @formatter:off
		String text = "var foo = '';\n" +
					"foo.execScript = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "eval is evil.", 2, IMarker.SEVERITY_WARNING, 29);
	}

	@Test
	public void testExpectedNumberA1() throws CoreException
	{
		// @formatter:off
		String text = "var r = /x{a}/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a number and instead saw 'a'.", 1, IMarker.SEVERITY_WARNING, 10);
	}

	@Test
	public void testExpectedNumberAOK1() throws CoreException
	{
		// @formatter:off
		String text = "Template.Pattern = /(^|.|\\r|\\n)(#\\{(.*?)\\})/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Expected a number and instead saw '(.*?)\\'.");
	}

	@Test
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

	@Test
	public void testForIfOk1() throws CoreException
	{
		// @formatter:off
		String text = "var array = [1, 2, 3];\n" +
				"var i;\n" +
				"for (i in array) {\n" +
				"    if (array[i] === Object) {\n" +
				"        var x = i % 2;\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items,
				"The body of a for in should be wrapped in an if statement to filter unwanted properties from the prototype.");
	}

	@Test
	public void testForIfOk2() throws CoreException
	{
		// @formatter:off
		String text = "var array = [1, 2, 3];\n" +
				"var i;\n" +
				"for (i in array) {\n" +
				"    if (typeof array[i] === Object) {\n" +
				"        var x = i % 2;\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items,
				"The body of a for in should be wrapped in an if statement to filter unwanted properties from the prototype.");
	}

	@Test
	public void testForIfOk3() throws CoreException
	{
		// @formatter:off
		String text = "var array = [1, 2, 3];\n" +
				"var i;\n" +
				"for (i in array) {\n" +
				"    if (array.hasOwnProperty(i)) {\n" +
				"        var x = i % 2;\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items,
				"The body of a for in should be wrapped in an if statement to filter unwanted properties from the prototype.");
	}

	@Test
	public void testForIfOk4() throws CoreException
	{
		// @formatter:off
		String text = "var array = [1, 2, 3];\n" +
				"var i;\n" +
				"for (i in array) {\n" +
				"    if (ADSAFE.has(array, i)) {\n" +
				"        var x = i % 2;\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items,
				"The body of a for in should be wrapped in an if statement to filter unwanted properties from the prototype.");
	}

	@Test
	public void testForIfOk5() throws CoreException
	{
		// @formatter:off
		String text = "var array = [1, 2, 3];\n" +
				"var i;\n" +
				"for (i in array) {\n" +
				"    if (Object.prototype.hasOwnProperty.call(array, i)) {\n" +
				"        var x = i % 2;\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items,
				"The body of a for in should be wrapped in an if statement to filter unwanted properties from the prototype.");
	}

	@Test
	public void testFunctionEval() throws CoreException
	{
		// @formatter:off
		String text = "var number = new Function('');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "The Function constructor is eval.", 1, IMarker.SEVERITY_WARNING, 25);
	}

	@Test
	public void testImpliedEvil1() throws CoreException
	{
		// @formatter:off
		String text = "setTimeout('blah', 2000);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Implied eval is evil. Pass a function instead of a string.", 1,
				IMarker.SEVERITY_WARNING, 0);
	}

	@Test
	public void testImpliedEvil2() throws CoreException
	{
		// @formatter:off
		String text = "setInterval('blah', 2000);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Implied eval is evil. Pass a function instead of a string.", 1,
				IMarker.SEVERITY_WARNING, 0);
	}

	@Test
	public void testInsecureA1() throws CoreException
	{
		// @formatter:off
		String text = "var r = /.regexp/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Insecure '.'.", 1, IMarker.SEVERITY_WARNING, 9);
	}

	@Test
	public void testInsecureA2() throws CoreException
	{
		// @formatter:off
		String text = "var r = /[^t]regexp/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Insecure '^'.", 1, IMarker.SEVERITY_WARNING, 10);
	}

	@Test
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

	@Test
	public void testLeadingDecimalA() throws CoreException
	{
		// @formatter:off
		String text = "var number = .8;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "A leading decimal point can be confused with a dot: '.8'.", 1,
				IMarker.SEVERITY_WARNING, 13);
	}

	@Test
	public void testMissingA1() throws CoreException
	{
		String text = "new Array";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 9);
	}

	@Test
	public void testMissingA1OK() throws CoreException
	{
		String text = "new Array();";

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Missing '()'.");
	}

	@Test
	public void testMissingA2() throws CoreException
	{
		String text = "switch (1) {\n}";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Missing 'case'.", 2, IMarker.SEVERITY_WARNING, 13);
	}

	@Test
	public void testMoveInvocation2() throws CoreException
	{
		// @formatter:off
		String text = "var Prototype = {\n" +
				"  Browser: (function(){\n" +
				"    var ua = navigator.userAgent;\n" +
				"  })()\n" +
				"};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		// This isn't a "bad_wrap", it's a "move_invocation"
		assertDoesntContain(items, "Do not wrap function literals in parens unless they are to be immediately invoked.");
		assertProblemExists(items, "Move the invocation into the parens that contain the function.", 4,
				IMarker.SEVERITY_WARNING, 80);
	}

	@Test
	public void testNotAConstructor1() throws CoreException
	{
		// @formatter:off
		String text = "var x = new Math();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not use Math as a constructor.", 1, IMarker.SEVERITY_WARNING, 12);
	}

	@Test
	public void testNotAConstructor2() throws CoreException
	{
		// @formatter:off
		String text = "var x = new Number();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not use Number as a constructor.", 1, IMarker.SEVERITY_WARNING, 12);
	}

	@Test
	public void testNotAConstructor3() throws CoreException
	{
		// @formatter:off
		String text = "var x = new String();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not use String as a constructor.", 1, IMarker.SEVERITY_WARNING, 12);
	}

	@Test
	public void testNotAConstructor4() throws CoreException
	{
		// @formatter:off
		String text = "var x = new JSON();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not use JSON as a constructor.", 1, IMarker.SEVERITY_WARNING, 12);
	}

	@Test
	public void testNotAConstructor5() throws CoreException
	{
		// @formatter:off
		String text = "var x = new Boolean();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Do not use Boolean as a constructor.", 1, IMarker.SEVERITY_WARNING, 12);
	}

	// public void testANotDefined() throws CoreException
	// {
//		// @formatter:off
//		String text = "function bar() {\n" +
//				"    var foo = 1;\n" +
//				"    var foo = 2;\n" +
//				"}";
//		// @formatter:on
	//
	// List<IProblem> items = getParseErrors(text);
	// assertProblemExists(items, "'foo' is already defined.", 3, IMarker.SEVERITY_WARNING, 42);
	// }

	@Test
	public void testNotALabelOK1() throws CoreException
	{
		// @formatter:off
		String text = "function foo() {\n" +
				"    var i;\n" +
				"something:\n" +
				"    for (i = 0; i < 10; i += 1) {\n" +
				"        if (i === 2) {\n" +
				"            break something;\n" +
				"        }\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "'something' is not a label.");
	}

	@Test
	public void testNotALabelOK2() throws CoreException
	{
		// @formatter:off
		String text = "function foo() {\n" +
				"    var i;\n" +
				"something:\n" +
				"    for (i = 0; i < 10; i += 1) {\n" +
				"        if (i === 2) {\n" +
				"            continue something;\n" +
				"        }\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "'something' is not a label.");
	}

	@Test
	public void testNotGreater() throws CoreException
	{
		// @formatter:off
		String text = "var r = /x{3,1}/;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "'3' should not be greater than '1'.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	@Test
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

	@Test
	public void testRadix() throws CoreException
	{
		// @formatter:off
		String text = "var number = parseInt('');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Missing radix parameter.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	@Test
	public void testReadOnlyEval() throws CoreException
	{
		Set<String> predefineds = CollectionsUtil.newSet("eval");
		for (String predefined : predefineds)
		{
			// @formatter:off
			String text = predefined + " = true;";
			// @formatter:on

			List<IProblem> items = getParseErrors(text);
			assertProblemExists(items, "Read only.", 1, IMarker.SEVERITY_ERROR, 7);
		}
	}

	@Test
	public void testReservedA1() throws CoreException
	{
		// @formatter:off
		String text = "var __iterator__ = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Reserved name '__iterator__'.", 1, IMarker.SEVERITY_ERROR, 4);
	}

	@Test
	public void testReservedA2() throws CoreException
	{
		// @formatter:off
		String text = "var __proto__ = '';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Reserved name '__proto__'.", 1, IMarker.SEVERITY_ERROR, 4);
	}

	@Test
	public void testStatementBlock1() throws CoreException
	{
		// @formatter:off
		String text = "var foo = 1;\n" +
				"{}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected to see a statement and instead saw a block.", 2, IMarker.SEVERITY_WARNING,
				14);
	}

	@Test
	public void testStatementBlock2() throws CoreException
	{
		// @formatter:off
		String text = "function foo() {\n" +
				"{}\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected to see a statement and instead saw a block.", 2, IMarker.SEVERITY_WARNING,
				18);
		assertCountOfProblems(items, 1, "Expected to see a statement and instead saw a block.");
	}

	@Test
	public void testStatementBlockOK1() throws CoreException
	{
		// @formatter:off
		String text = "var event;\n" +
					  "while (event = events.shift()) {\n" +
					  "	\n" +
					  "}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Expected to see a statement and instead saw a block.");
	}

	@Test
	public void testStatementBlockOK2() throws CoreException
	{
		// @formatter:off
		String text = "var event;\n" +
					  "while (event = events.shift()) {\n" +
					  "	{}\n" +
					  "}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Expected a conditional expression and instead saw an assignment.", 2, IMarker.SEVERITY_WARNING,
				24);
		assertCountOfProblems(items, 1, "Expected a conditional expression and instead saw an assignment.");
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testSubscript() throws CoreException
	{
		// @formatter:off
		String text = "var result = true;\n" +
				"result['something'] = 'yeah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "['something'] is better written in dot notation.", 2, IMarker.SEVERITY_WARNING, 26);
	}

	@Test
	public void testSubscriptOK() throws CoreException
	{
		// @formatter:off
		String text = "var result = true;\n" +
				"result['case'] = 'yeah';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "['case'] is better written in dot notation.");
	}

	@Test
	public void testSyncA() throws CoreException
	{
		// @formatter:off
		String text = "var result = true;\n" +
				"result.methodSync('yeah');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unexpected sync method: 'methodSync'.", 2, IMarker.SEVERITY_WARNING, 26);
	}

	@Test
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

	@Test
	public void testTrailingDecimalA() throws CoreException
	{
		// @formatter:off
		String text = "var number = 1.;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "A trailing decimal point can be confused with a dot: '1.'.", 1,
				IMarker.SEVERITY_WARNING, 15);
	}

	@Test
	public void testUnexpectedAOK1() throws CoreException
	{
		// @formatter:off
		String text = "var Prototype = {\n" +
				"  Browser: (function(){\n" +
				"    var ua = navigator.userAgent;\n" +
				"  })()\n" +
				"};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Unexpected 'ua'.");
	}

	@Test
	public void testUnexpectedAOK2() throws CoreException
	{
		// @formatter:off
		String text = "function create() {\n" +
				"    var parent = null, properties = $A(arguments);\n" +
				"};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Unexpected 'parent'.");
	}

	@Test
	public void testUnnecessaryInitialize() throws CoreException
	{
		// @formatter:off
		String text = "var foo = undefined;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "It is not necessary to initialize 'foo' to 'undefined'.", 1,
				IMarker.SEVERITY_WARNING, 8);
	}

	@Test
	public void testUnreachableAB1() throws CoreException
	{
		// @formatter:off
		String text = "break;\n" +
				"var a = 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unreachable 'var' after 'break'.", 2, IMarker.SEVERITY_WARNING, 7);
	}

	@Test
	public void testUnreachableAB2() throws CoreException
	{
		// @formatter:off
		String text = "continue;\n" +
				"var a = 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unreachable 'var' after 'continue'.", 2, IMarker.SEVERITY_WARNING, 10);
	}

	@Test
	public void testUnreachableAB3() throws CoreException
	{
		// @formatter:off
		String text = "return 1;\n" +
				"var a = 1;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Unreachable 'var' after 'return'.", 2, IMarker.SEVERITY_WARNING, 10);
	}

	@Test
	public void testUseArray() throws CoreException
	{
		// @formatter:off
		String text = "var array = new Array(1, 2, 3);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 21);
	}

	@Test
	public void testUseArray2() throws CoreException
	{
		// @formatter:off
		String text = "var array = new Array();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 21);
	}

	@Test
	public void testUseArray3() throws CoreException
	{
		// @formatter:off
		String text = "var array = new Array;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 16);
	}

	@Test
	public void testUseArray4() throws CoreException
	{
		// @formatter:off
		String text = "'string'.split(1);";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the array literal notation [].", 1, IMarker.SEVERITY_WARNING, 9);
	}

	@Test
	public void testUseBraces() throws CoreException
	{
		// @formatter:off
		String text = "var regexp = /  /;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Spaces are hard to count. Use {2}.", 1, IMarker.SEVERITY_WARNING, 15);
	}

	@Test
	public void testUsedBeforeA() throws CoreException
	{
		setOption("undef", false);
		// @formatter:off
		String text = "/*jslint undef: false */\n" +
				"function chris() {\n" +
				"    var bar = foo + 1;\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "'foo' was used before it was defined.", 3, IMarker.SEVERITY_WARNING, 58);
	}

	@Test
	public void testUsedBeforeA2() throws CoreException
	{
		setOption("undef", false);
		// @formatter:off
		String text = "var Events = Backbone.Events = {};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "'Backbone' was used before it was defined.", 1, IMarker.SEVERITY_WARNING, 13);
	}

	@Test
	public void testUsedBeforeAOK1() throws CoreException
	{
		// @formatter:off
		String text = "var Prototype = { Version: '1.7' }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "'Version' was used before it was defined.");
	}

	@Test
	public void testUseObject() throws CoreException
	{
		// @formatter:off
		String text = "var foo = new Object();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the object literal notation {}.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	@Test
	public void testUseObject2() throws CoreException
	{
		// @formatter:off
		String text = "var foo = Object();";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Use the object literal notation {}.", 1, IMarker.SEVERITY_WARNING, 16);
	}

	// public void testNotAScope1() throws CoreException
	// {
//		// @formatter:off
//		String text = "var i;\n" +
//				"for (i = 0; i < 10; i += 1) {\n" +
//				"    continue foo;\n" +
//				"}";
//		// @formatter:on
	//
	// List<IProblem> items = getParseErrors(text);
	// assertProblemExists(items, "'foo' is not a label.", 3, IMarker.SEVERITY_WARNING, 11);
	// }

	

	// FIXME This is a bug in JSlint that this doesn't work.
	// public void testNotAFunction2() throws CoreException
	// {
//		// @formatter:off
//		String text = "JSON(10);";
//		// @formatter:on
	//
	// List<IProblem> items = getParseErrors(text);
	// assertProblemExists(items, "'JSON' is not a function.", 1, IMarker.SEVERITY_WARNING, 0);
	// }

	@Test
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

	@Test
	public void testUseStrictOK() throws CoreException
	{
		// @formatter:off
		String text = "function foo() {\n" +
				"    \"use strict\";\n" +
				"    var x;\n" +
				"    try {\n" +
				"        x = 1;\n" +
				"    } catch (e) {\n" +
				"        x = 10;\n" +
				"    }\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Missing 'use strict' statement.");
	}

	@Test
	public void testVarANot() throws CoreException
	{
		String text = "var chris = blah = 'something';";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Variable blah was not declared correctly.", 1, IMarker.SEVERITY_ERROR, 12);
	}

	@Test
	public void testVarANot_OK() throws CoreException
	{
		String text = "var Events = Backbone.Events = {};";

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Variable  was not declared correctly.");
	}

	@Test
	public void testWeirdNew() throws CoreException
	{
		// @formatter:off
		String text = "var someObj = new function () {};";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird construction. Delete 'new'.", 1, IMarker.SEVERITY_WARNING, 14);
	}

	@Test
	public void testWeirdProgram1() throws CoreException
	{
		// @formatter:off
		String text = "break;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	@Test
	public void testWeirdProgram2() throws CoreException
	{
		// @formatter:off
		String text = "continue;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	@Test
	public void testWeirdProgram3() throws CoreException
	{
		// @formatter:off
		String text = "return;";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 0);
	}

	@Test
	public void testWeirdProgram4() throws CoreException
	{
		// @formatter:off
		String text = "throw 'e';";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Weird program.", 1, IMarker.SEVERITY_WARNING, 6);
	}

	@Test
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

	@Test
	public void testWrapImmediateOK() throws CoreException
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

	@Test
	public void testWrapRegexp1() throws CoreException
	{
		// @formatter:off
		String text = "function chris() { return /regexp/i; }";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Wrap the /regexp/ literal in parens to disambiguate the slash operator.", 1,
				IMarker.SEVERITY_WARNING, 26);
	}

	@Test
	public void testWrapRegexp2() throws CoreException
	{
		// @formatter:off
		String text = "function blank() {\n" +
				"	return /^\\s*$/.test(this);\n" +
				"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "Wrap the /regexp/ literal in parens to disambiguate the slash operator.", 2,
				IMarker.SEVERITY_WARNING, 27);
	}

	@Test
	public void testWriteIsWrong() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint evil: false */\n" +
				"document.write('something');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "document.write can be a form of eval.", 2, IMarker.SEVERITY_WARNING, 24);
	}

	@Test
	public void testWriteIsWrong2() throws CoreException
	{
		// @formatter:off
		String text = "/*jslint evil: false */\n" +
				"document.writeln('something');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(items, "document.write can be a form of eval.", 2, IMarker.SEVERITY_WARNING, 24);
	}

	@Test
	public void testTISTUD2925() throws CoreException
	{
		// @formatter:off
		String text = "var child = spawn(cmd.shift(), cmd);\n" +
					  "re = new RegExp('(\\u001b\\\\[\\\\d+m)?\\\\[?(' + logger.getLevels().join('|') + ')\\\\]?\\s*(\\u001b\\\\[\\\\d+m)?(.*)', 'i');";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Read only.");
	}

	protected void setOption(String optionName, boolean value)
	{
		((JSStyleValidator) fValidator).setOption(optionName, value);
	}

	// Tests we are intentionally skipping -----------------------

	@SuppressWarnings("nls")
	@Test
	public void testHexNumber() throws CoreException
	{
		String text = "var i = 0x9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999;";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(
				items,
				"Bad number '0x9999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999'.",
				1, IMarker.SEVERITY_WARNING, 1550);
	}

	@SuppressWarnings("nls")
	@Test
	public void testOctalNumber() throws CoreException
	{
		String text = "var i = 07777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777;";

		List<IProblem> items = getParseErrors(text);
		assertProblemExists(
				items,
				"Bad number '07777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777'.",
				1, IMarker.SEVERITY_WARNING, 1549);
	}

}
