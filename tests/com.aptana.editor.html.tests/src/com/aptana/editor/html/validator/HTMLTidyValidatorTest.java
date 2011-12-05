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
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;

public class HTMLTidyValidatorTest extends AbstractValidatorTestCase
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

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
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

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
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

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new HTMLParseState(), IHTMLConstants.TIDY_PROBLEM);
	}
}
