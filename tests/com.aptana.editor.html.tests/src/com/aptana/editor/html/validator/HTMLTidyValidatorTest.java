/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.util.List;

import org.eclipse.core.resources.IMarker;
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

	public void testOKDoctype() throws CoreException
	{
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n"
				+ "	\"http://www.w3.org/TR/html4/strict.dtd\">";

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "SYSTEM, PUBLIC, W3C, DTD, EN must be upper case");
	}

	public void testUnknownAttribute() throws CoreException
	{
		// @formatter:off
		String text = "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\">\n" +
				"    <head>\n" +
				"        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" +
				"        <title>HTML</title>\n" +
				"        <meta name=\"author\" content=\"qatester\" />\n" +
				"        <!-- Date: 2012-05-25 -->\n" +
				"    </head>\n" +
				"    <body>\n" +
				"        <a href=\"http://google.com\" src=\"this shouldn't work\" div>Google</a>\n" +
				"    </body>\n" +
				"</html>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContains(items, "a proprietary attribute \"div\"");
	}

	public void testLowercaseDoctypeW3C() throws CoreException
	{
		String text = "<!DOCTYPE HTML PUBLIC \"-//w3c//DTD HTML 4.01//EN\"\n"
				+ "	\"http://www.w3.org/TR/html4/strict.dtd\">";

		List<IProblem> items = getParseErrors(text);
		assertTrue(items.size() > 0);
		assertContainsProblem(items, "SYSTEM, PUBLIC, W3C, DTD, EN must be upper case", IMarker.SEVERITY_WARNING, 1,
				26, 3);
	}

	public void testLowercaseDoctypeEN() throws CoreException
	{
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//en\"\n"
				+ "	\"http://www.w3.org/TR/html4/strict.dtd\">";

		List<IProblem> items = getParseErrors(text);
		assertTrue(items.size() > 0);
		assertContainsProblem(items, "SYSTEM, PUBLIC, W3C, DTD, EN must be upper case", IMarker.SEVERITY_WARNING, 1,
				46, 2);
	}

	public void testLowercaseDoctypeDTD() throws CoreException
	{
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//dtd HTML 4.01//EN\"\n"
				+ "	\"http://www.w3.org/TR/html4/strict.dtd\">";

		List<IProblem> items = getParseErrors(text);
		assertTrue(items.size() > 0);
		assertContainsProblem(items, "SYSTEM, PUBLIC, W3C, DTD, EN must be upper case", IMarker.SEVERITY_WARNING, 1,
				31, 3);
	}

	public void testLowercaseDoctypeSYSTEM() throws CoreException
	{
		String text = "<!DOCTYPE HTML system 'about:legacy-compat'>";

		List<IProblem> items = getParseErrors(text);
		assertTrue(items.size() > 0);
		assertContainsProblem(items, "SYSTEM, PUBLIC, W3C, DTD, EN must be upper case", IMarker.SEVERITY_WARNING, 1,
				15, 6);
	}

	public void testOKSystemDoctype() throws CoreException
	{
		String text = "<!DOCTYPE HTML SYSTEM \"about:legacy-compat\">";

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "SYSTEM, PUBLIC, W3C, DTD, EN must be upper case");
	}

	public void testMalformedDoctype() throws CoreException
	{
		String text = "<!DOCTYPE HTML blah>";

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "expected \"html PUBLIC\" or \"html SYSTEM\"", IMarker.SEVERITY_WARNING, 1, 0, 9);
	}

	public void testMissingDoctype() throws CoreException
	{
		String text = "<html><head><title></title></head></html>";

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "Missing DOCTYPE", IMarker.SEVERITY_WARNING, 1, 0, 0);
	}

	public void testDoctypeAfterElements() throws CoreException
	{
		String text = "<html>\n<!DOCTYPE html>\n</html>";

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "<!DOCTYPE> isn't allowed after elements", IMarker.SEVERITY_WARNING, 2, 7, 9);
	}

	public void testMissingTitleElement() throws CoreException
	{
		String text = "<html><head></head><body></body></html>";

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "should insert missing 'title' element", IMarker.SEVERITY_WARNING, 1, 0, 0);
	}

	public void testTrimEmptyH1() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"	<h1></h1>\n" +
			"</body>\n" +
			"</HTML>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "should trim empty <h1>", IMarker.SEVERITY_WARNING, 7, 69, 9);
	}

	// public void testNonEmptyElementThatDoesntSupportContent() throws CoreException
	// {
//		// @formatter:off
//		String text = "<!DOCTYPE html>\n" +
//			"<HTML>\n" +
//			"<HEAD>\n" +
//			"<meta>Content</meta>\n" +
//			"<TITLE>Example</TITLE>\n" +
//			"</HEAD>\n" +
//			"<body>\n" +
//			"</body>\n" +
//			"</HTML>";
//		// @formatter:on
	//
	// List<IProblem> items = getParseErrors(text);
	// assertContains(items, "meta element not empty or not closed");
	// }

	// TODO Test for unrecognized attribute!

	public void testDeprecatedElement() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"	<applet></applet>\n" +
			"</body>\n" +
			"</HTML>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "Deprecated element applet: Deprecated in HTML 4.01. Use \"Object\" instead.",
				IMarker.SEVERITY_WARNING, 7, 69, 17);
	}

	public void testDuplicateIdValues() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"	<div id='a'><div id='b'><p id='a'></p></div></div>\n" +
			"</body>\n" +
			"</HTML>";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "<p> 'id' attribute value 'a' not unique", IMarker.SEVERITY_WARNING, 7, 93, 10);
	}

	public void testOKMultipleFramesetElements() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n" +
			"   \"http://www.w3.org/TR/html4/frameset.dtd\">\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>A simple frameset document</TITLE>\n" +
			"</HEAD>\n" +
			"<FRAMESET cols=\"20%, 80%\">\n" +
			"  <FRAMESET rows=\"100, 200\">\n" +
			"      <FRAME src=\"contents_of_frame1.html\">\n" +
			"      <FRAME src=\"contents_of_frame2.gif\">\n" +
			"  </FRAMESET>\n" +
			"  <FRAME src=\"contents_of_frame3.html\">\n" +
			"  <NOFRAMES>\n" +
			"      <P>This frameset document contains:\n" +
			"      <UL>\n" +
			"         <LI><A href=\"contents_of_frame1.html\">Some neat contents</A>\n" +
			"         <LI><IMG src=\"contents_of_frame2.gif\" alt=\"A neat image\">\n" +
			"         <LI><A href=\"contents_of_frame3.html\">Some other neat contents</A>\n" +
			"      </UL>\n" +
			"  </NOFRAMES>\n" +
			"</FRAMESET>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "Repeated FRAMESET element");
	}

	public void testDuplicateFramesetElements() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n" +
			"   \"http://www.w3.org/TR/html4/frameset.dtd\">\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>A simple frameset document</TITLE>\n" +
			"</HEAD>\n" +
			"<FRAMESET cols=\"20%, 80%\">\n" +
			"  <FRAME src=\"contents_of_frame3.html\">\n" +
			"  <NOFRAMES>\n" +
			"      <P>This frameset document contains:\n" +
			"      <UL>\n" +
			"         <LI><A href=\"contents_of_frame1.html\">Some neat contents</A>\n" +
			"         <LI><IMG src=\"contents_of_frame2.gif\" alt=\"A neat image\">\n" +
			"         <LI><A href=\"contents_of_frame3.html\">Some other neat contents</A>\n" +
			"      </UL>\n" +
			"  </NOFRAMES>\n" +
			"</FRAMESET>\n" +
			"<FRAMESET rows=\"100, 200\">\n" +
			"    <FRAME src=\"contents_of_frame1.html\">\n" +
			"    <FRAME src=\"contents_of_frame2.gif\">\n" +
			"</FRAMESET>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "repeated FRAMESET element", IMarker.SEVERITY_WARNING, 18, 553, 26);
	}

	public void testInsertImplicitNOFRAMES() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n" +
			"   \"http://www.w3.org/TR/html4/frameset.dtd\">\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>A poorly-designed frameset document</TITLE>\n" +
			"</HEAD>\n" +
			"<FRAMESET cols=\"20%, 80%\">\n" +
			"   <FRAME src=\"table_of_contents.html\">\n" +
			"   <FRAME src=\"ostrich.gif\">\n" +
			"</FRAMESET>\n" +
			"<BODY>\n" +
			"	<h1>\n" +
			"		Hi there!\n" +
			"	</h1>\n" +
			"</BODY>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "should insert implicit <noframes>", IMarker.SEVERITY_WARNING, 11, 286, 6);
	}

	public void testElementOutsideNoFramesContent() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n" +
				"   \"http://www.w3.org/TR/html4/frameset.dtd\">\n" +
				"<HTML>\n" +
				"<HEAD>\n" +
				"<TITLE>A poorly-designed frameset document</TITLE>\n" +
				"</HEAD>\n" +
				"<FRAMESET cols=\"20%, 80%\">\n" +
				"   <FRAME src=\"table_of_contents.html\">\n" +
				"   <FRAME src=\"ostrich.gif\" longdesc=\"ostrich-desc.html\">\n" +
				"   <NOFRAMES></NOFRAMES>\n" +
				"</FRAMESET>\n" +
				"<h1>Hello world</h1>\n" +
				"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "<h1> not inside 'noframes' element", IMarker.SEVERITY_WARNING, 12, 340, 4);
	}

	public void testMissingNoFrames() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n" +
				"   \"http://www.w3.org/TR/html4/frameset.dtd\">\n" +
				"<HTML>\n" +
				"<HEAD>\n" +
				"<TITLE>A poorly-designed frameset document</TITLE>\n" +
				"</HEAD>\n" +
				"<FRAMESET cols=\"20%, 80%\">\n" +
				"   <FRAME src=\"table_of_contents.html\">\n" +
				"   <FRAME src=\"ostrich.gif\">\n" +
				"</FRAMESET>\n" +
				"<h1>Hello world</h1>\n" +
				"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "missing </noframes>", IMarker.SEVERITY_WARNING, 11, 286, 4);
	}

	public void testUnescapedAmpersand() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"	<p>Franks & Beans</p>\n" +
			"</body>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "unescaped & which should be written as &amp;", IMarker.SEVERITY_WARNING, 7, 79, 1);
	}

	protected void assertContainsProblem(List<IProblem> items, String msg, int severity, int line, int offset,
			int length)
	{
		IProblem problem = assertContains(items, msg);
		assertEquals("severity", severity, problem.getSeverity());
		assertEquals("offset", offset, problem.getOffset());
		assertEquals("length", length, problem.getLength());
		assertEquals("lineNumber", line, problem.getLineNumber());
	}

	public void testEntityNotEndingInSemicolon() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"	<p>Franks &amp Beans</p>\n" +
			"</body>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "entity \"&amp\" doesn't end in ';'", IMarker.SEVERITY_WARNING, 7, 79, 4);
	}

	public void testUnescapedOrUnknownEntity() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"	<p>Franks &am Beans</p>\n" +
			"</body>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "unescaped & or unknown entity \"&am\"", IMarker.SEVERITY_WARNING, 7, 79, 3);
	}

	public void testAttributeValueOutsidePredefinedValues() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"<article hidden=\"yup\"><p>Yeah</p></article>\n" + 
			"</body>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertContainsProblem(items, "article attribute \"hidden\" has invalid value \"yup\"",
				IMarker.SEVERITY_WARNING, 7, 68, 22);
	}

	public void testAttributeValueInPredefinedValues() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"<article hidden=\"true\"><p>Yeah</p></article>\n" + 
			"</body>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "article attribute \"hidden\" has invalid value \"true\"");
	}

	public void testAttributeValueWithAsteriskDefinedInMetadata() throws CoreException
	{
		// @formatter:off
		String text = "<!DOCTYPE html>\n" +
			"<HTML>\n" +
			"<HEAD>\n" +
			"<TITLE>Example</TITLE>\n" +
			"<meta name=\"description\" content=\"not specified\" />\n" +
			"</HEAD>\n" +
			"<body>\n" +
			"</body>\n" +
			"</HTML>\n";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertDoesntContain(items, "meta attribute \"content\" has invalid value \"not specified\"");
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new HTMLParseState(source), IHTMLConstants.TIDY_PROBLEM);
	}
}
