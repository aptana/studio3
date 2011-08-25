/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.Document;

import com.aptana.editor.common.parsing.FileService;
import com.aptana.editor.common.tests.util.TestProject;
import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.common.validator.IValidationManager;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.js.IJSConstants;
import com.aptana.parsing.IParseState;

public class HTMLValidatorTests extends AbstractValidatorTestCase
{

	public void testHTMLSelfClosingTagOnNonVoidElement() throws CoreException
	{
		String text = "<html>\n<title>test</title>\n<body>\n<video />\n</body>\n</html>\n";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		List<IValidationItem> items = getParseErrors(text, IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());
		assertEquals(1, items.size());
		IValidationItem item = items.get(0);

		assertEquals("Error was not found on expected line", 4, item.getLineNumber());
		assertEquals("Error message did not match expected error message",
				"Self-closing syntax (/>) used on a non-void HTML element", item.getMessage());
	}

	public void testHTMLMissingEndTag() throws CoreException
	{
		String text = "<html>\n<title>test\n<body>\n</body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		List<IValidationItem> items = getParseErrors(text, IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());
		assertTrue(items.size() > 0);
		assertEquals("Missing end tag </title>", items.get(0).getMessage());
	}

	public void testHTMLNoErrors() throws CoreException
	{
		String text = "<html>\n<title>test</title>\n<body>\n</body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		List<IValidationItem> items = getParseErrors(text, IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());
		assertEquals(0, items.size());
	}

	public void testHTMLEmbeddedCSSParseError() throws CoreException
	{
		String text = "<html>\n<style>\ndiv#paginator {\nfloat: left\nwidth: 65px\n}\n</style>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);

		List<IValidationItem> items = getParseErrors(text, IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());

		assertEquals(1, items.size());
		IValidationItem item = items.get(0);

		assertEquals("Error was not found on expected line", 5, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \":\"",
				item.getMessage());
	}

	public void testNoHTMLEmbeddedCSSParseError() throws CoreException
	{
		String text = "<html>\n<style>\ndiv#paginator {\nfloat: left;\nwidth: 65px\n}\n</style>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);

		List<IValidationItem> items = getParseErrors(text, IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());

		assertEquals("A validation error was found in valid html with embedded css", 0, items.size());
	}

	public void testNoHTMLEmbeddedJSParseError() throws CoreException
	{
		String text = "<html>\n<script>\nvar foo = function() {\nhello();\n};\n</script>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);

		List<IValidationItem> items = getParseErrors(text, IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());

		assertEquals("A validation error was found in valid html with embedded js", 0, items.size());
	}

	public void testHTMLEmbeddedJSParseError() throws CoreException
	{
		String text = "<html>\n<script>\nvar foo = function() {\nhello()\n};\n</script>\n<title>test</title>\n<body></body>\n</html>";

		setEnableParseError(true, IHTMLConstants.CONTENT_TYPE_HTML);
		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);

		List<IValidationItem> items = getParseErrors(text, IHTMLConstants.CONTENT_TYPE_HTML, new HTMLParseState());
		assertEquals(1, items.size());
		IValidationItem item = items.get(0);

		assertEquals("Error was not found on expected line", 4, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \"}\"",
				item.getMessage());
	}

	@Override
	protected List<IValidationItem> getParseErrors(String source, String language, IParseState ps) throws CoreException
	{
		TestProject project = new TestProject("Test", new String[] { "com.aptana.projects.webnature" });
		IResource file = project.createFile("parseErrorTest", source);

		FileService fileService = new FileService(language, ps);
		IValidationManager validationManager = fileService.getValidationManager();
		validationManager.addNestedLanguage(ICSSConstants.CONTENT_TYPE_CSS);
		validationManager.addNestedLanguage(IJSConstants.CONTENT_TYPE_JS);

		fileService.setDocument(new Document(source));
		fileService.setResource(file);
		fileService.parse(new NullProgressMonitor());
		fileService.validate();

		List<IValidationItem> items = validationManager.getValidationItems();

		project.delete();
		return items;
	}
}
