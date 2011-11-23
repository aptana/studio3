/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.js.IJSConstants;

public class HTMLValidatorTests extends AbstractValidatorTestCase
{
	@Override
	protected AbstractBuildParticipant createValidator()
	{
		return new HTMLTidyValidator();
	}

	@Override
	protected String getFileExtension()
	{
		return "html";
	}

	public void testHTMLSelfClosingTagOnNonVoidElement() throws CoreException
	{
		String text = "<html>\n<title>test</title>\n<body>\n<video />\n</body>\n</html>\n";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 4, item.getLineNumber());
		assertEquals("Error message did not match expected error message",
				"Self-closing syntax (/>) used on a non-void HTML element", item.getMessage());
	}

	public void testHTMLMissingEndTag() throws CoreException
	{
		String text = "<html>\n<title>test\n<body>\n</body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		List<IProblem> items = getParseErrors(text);
		assertEquals(2, items.size());
		assertContains(items, "Missing end tag </title>");
		assertContains(items, "missing </title> before <body>");
	}

	protected void assertContains(List<IProblem> items, String message)
	{
		for (IProblem item : items)
		{
			if (message.equals(item.getMessage()))
			{
				return;
			}
		}
		fail("Was unable to find an IValidationItem with message: " + message);
	}

	public void testHTMLNoErrors() throws CoreException
	{
		String text = "<html>\n<title>test</title>\n<body>\n</body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testHTMLEmbeddedCSSParseError() throws CoreException
	{
		String text = "<html>\n<style>\ndiv#paginator {\nfloat: left\nwidth: 65px\n}\n</style>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);

		List<IProblem> items = getParseErrors(text);

		assertEquals(1, items.size());
		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 5, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \":\"",
				item.getMessage());
	}

	public void testNoHTMLEmbeddedCSSParseError() throws CoreException
	{
		String text = "<html>\n<style>\ndiv#paginator {\nfloat: left;\nwidth: 65px\n}\n</style>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);

		List<IProblem> items = getParseErrors(text);

		assertEquals("A validation error was found in valid html with embedded css", 0, items.size());
	}

	public void testNoHTMLEmbeddedJSParseError() throws CoreException
	{
		String text = "<html>\n<script>\nvar foo = function() {\nhello();\n};\n</script>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);

		List<IProblem> items = getParseErrors(text);

		assertEquals("A validation error was found in valid html with embedded js", 0, items.size());
	}

	public void testHTMLEmbeddedJSParseError() throws CoreException
	{
		String text = "<html>\n<script>\nvar foo = function() {\nhello()\n};\n</script>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 5, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \"}\"",
				item.getMessage());
	}

	public void testNoTypeAttributeRequired() throws CoreException
	{
		String text = "<script src=\"\"></script>";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
	}

	public void testHTML5HeaderTag() throws CoreException
	{
		String text = "<header><h1></h1></header>";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
	}

	public void testHTML5NavTag() throws CoreException
	{
		String text = "<nav></nav>";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
	}

	@Override
	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return super.getParseErrors(source, new HTMLParseState(), IHTMLConstants.HTML_PROBLEM);
	}
}
