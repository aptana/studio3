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
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;

public class HTMLTidyValidatorTest extends AbstractValidatorTestCase
{
	@Override
	protected AbstractBuildParticipant createValidator()
	{
		return new HTMLTidyValidator()
		{

			@Override
			protected String getPreferenceNode()
			{
				return HTMLPlugin.PLUGIN_ID;
			}

			@Override
			public String getId()
			{
				return ID;
			}
		};
	}

	@Override
	protected String getFileExtension()
	{
		return "html";
	}

	public void testHTMLMissingEndTag() throws CoreException
	{
		String text = "<html>\n<title>test\n<body>\n</body>\n</html>";

		List<IProblem> items = getParseErrors(text);
		assertContains(items, "missing </title> before <body>");
	}

	public void testHTMLEOFNewLineDiv() throws CoreException
	{
		String text = "<div id=\"\"\n";

		List<IProblem> items = getParseErrors(text);
		assertContains(items, "end of file while parsing attributes");
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
		assertDoesntContain(items, "<script> lacks \"type\" attribute");
	}

	public void testHTML5HeaderTag() throws CoreException
	{
		String text = "<header><h1></h1></header>";

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "<header> is not recognized!");
	}

	public void testHTML5NavTag() throws CoreException
	{
		String text = "<nav></nav>";

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "<nav> is not recognized!");
	}

	public void testHTML5VideoTag() throws CoreException
	{
		String text = "<video width=\"320\" height=\"240\" controls=\"controls\">\n  <source src=\"movie.mp4\" type=\"video/mp4\" />\n  <source src=\"movie.ogg\" type=\"video/ogg\" />\n  Your browser does not support the video tag.\n</video>";

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "<video> is not recognized!");
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new HTMLParseState(), IHTMLConstants.TIDY_PROBLEM);
	}
}
