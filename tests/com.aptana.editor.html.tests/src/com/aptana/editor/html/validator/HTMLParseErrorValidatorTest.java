/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.aptana.buildpath.core.tests.AbstractValidatorTestCase;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.js.core.IJSConstants;

public class HTMLParseErrorValidatorTest extends AbstractValidatorTestCase
{
	@Override
	protected IBuildParticipant createValidator()
	{
		return new HTMLParserValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return HTMLPlugin.PLUGIN_ID;
			}
		};
	}

	@Override
	protected String getFileExtension()
	{
		return "html";
	}

	@Override
	protected String getContentType()
	{
		return IHTMLConstants.CONTENT_TYPE_HTML;
	}

	@Test
	public void testHTMLSelfClosingTagOnNonVoidElement() throws CoreException
	{
		String text = "<html>\n<title>test</title>\n<body>\n<video />\n</body>\n</html>\n";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 4, item.getLineNumber());
		assertEquals("Error message did not match expected error message",
				"Self-closing syntax (/>) used on a non-void HTML element", item.getMessage());
	}

	@Test
	public void testHTMLMissingEndTag() throws CoreException
	{
		String text = "<html>\n<title>test\n<body>\n</body>\n</html>";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		assertContains(items, "Missing end tag </title>");
	}

	@Test
	public void testHTMLMissingEndOpenTag() throws CoreException
	{
		String text = "<html>\n<body>\n<a\n</body>\n</html>";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		assertContains(items, "\"<\" missing '>' for end of tag");
	}

	@Test
	public void testHTMLNoErrors() throws CoreException
	{
		String text = "<html>\n<title>test</title>\n<body>\n</body>\n</html>";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testHTMLEmbeddedCSSParseError() throws CoreException
	{
		String text = "<html>\n<style>\ndiv#paginator {\nfloat: left\nwidth: 65px\n}\n</style>\n<title>test</title>\n<body></body>\n</html>";

		List<IProblem> htmlProblems = getParseErrors(text);
		assertEquals(0, htmlProblems.size());

		List<IProblem> cssProblems = getParseErrors(text, ICSSConstants.CSS_PROBLEM);
		assertEquals(1, cssProblems.size());
		IProblem item = cssProblems.get(0);

		assertEquals("Error was not found on expected line", 5, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \":\"",
				item.getMessage());
	}

	@Test
	public void testNoHTMLEmbeddedCSSParseError() throws CoreException
	{
		String text = "<html>\n<style>\ndiv#paginator {\nfloat: left;\nwidth: 65px\n}\n</style>\n<title>test</title>\n<body></body>\n</html>";

		List<IProblem> items = getParseErrors(text);
		assertEquals("A validation error was found in valid html with embedded css", 0, items.size());

		List<IProblem> cssProblems = getParseErrors(text, ICSSConstants.CSS_PROBLEM);
		assertEquals(0, cssProblems.size());
	}

	@Test
	public void testNoHTMLEmbeddedJSParseError() throws CoreException
	{
		String text = "<html>\n<script>\nvar foo = function() {\nhello();\n};\n</script>\n<title>test</title>\n<body></body>\n</html>";

		List<IProblem> htmlProblems = getParseErrors(text);
		assertEquals(0, htmlProblems.size());

		List<IProblem> jsProblems = getParseErrors(text, IJSConstants.JS_PROBLEM_MARKER_TYPE);
		assertEquals("A validation error was found in valid html with embedded js", 0, jsProblems.size());
	}

	@Test
	public void testHTMLEmbeddedJSParseError() throws CoreException
	{
		String text = "<html>\n<script>\nvar foo = function() {\nhello(\n};\n</script>\n<title>test</title>\n<body></body>\n</html>";

		List<IProblem> htmlProblems = getParseErrors(text);
		assertEquals(0, htmlProblems.size());

		List<IProblem> jsProblems = getParseErrors(text, IJSConstants.JS_PROBLEM_MARKER_TYPE);
		assertEquals(1, jsProblems.size());
		IProblem item = jsProblems.get(0);

		assertEquals("Error was not found on expected line", 2, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "SyntaxError:4:0 Expected an operand but found }\n" + 
				"};\n" + 
				"^",
				item.getMessage());
	}

	@Test
	public void testInvalidClosingTag() throws CoreException
	{
		// @formatter:off
		String text = "<html>\n" +
				"    <head>\n" +
				"        <title>\n" +
				"        <title>\n" +
				"    <head>\n" +
				"    <body>  \n" +  
				"<p>\n" +
				"<p>\n" +
				"    <body>\n" +
				"</html>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		// body tag is optional, so no warnings about end tags
		// p tag end is optional, so no warning about that
		assertContainsProblem(items, "Missing end tag </title>", IMarker.SEVERITY_WARNING, 3, 26, 7);
		assertContainsProblem(items, "Missing end tag </head>", IMarker.SEVERITY_WARNING, 2, 11, 6);
		assertContainsProblem(items, "<title> is probably intended as </title>", IMarker.SEVERITY_WARNING, 4, 42, 7);
		assertContainsProblem(items, "<head> is probably intended as </head>", IMarker.SEVERITY_WARNING, 5, 54, 6);
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, IHTMLConstants.HTML_PROBLEM);
	}

	protected List<IProblem> getParseErrors(String source, String markerType) throws CoreException
	{
		return getParseErrors(source, new HTMLParseState(source), markerType);
	}
}
