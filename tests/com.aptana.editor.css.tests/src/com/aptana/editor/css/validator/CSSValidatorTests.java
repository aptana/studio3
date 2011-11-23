/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.validator;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.css.ICSSConstants;

public class CSSValidatorTests extends AbstractValidatorTestCase
{

	@Override
	protected AbstractBuildParticipant createValidator()
	{
		return new CSSValidator();
	}

	@Override
	protected String getFileExtension()
	{
		return "css";
	}

	public void testCSSParseErrors() throws CoreException
	{
		String text = "div#paginator {\nfloat: left\nwidth: 65px\n}";

		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);
		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());

		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 3, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \":\"",
				item.getMessage());
	}

	public void testNoCSSParseErrors() throws CoreException
	{
		String text = "div#paginator {\nfloat: left;\nwidth: 65px\n}";

		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);
		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3TransitionProperty() throws CoreException
	{
		String text = "div {\ntransition: width 2s;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSSPropertyPrecededByDash() throws CoreException
	{
		String text = "div {\n-background-color: #123;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testPropertyValueNone() throws CoreException
	{
		String text = "H1:before {\ncontent: none;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3ResizeProperty() throws CoreException
	{
		String text = "div {\nresize: both;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3BackgroundProperty() throws CoreException
	{
		String text = "div {\nbackground-clip: border-box;\nbackground-origin: content-box;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3SrcPropertyInFontFace() throws CoreException
	{
		String text = "@font-face {\nsrc: url(\"\");\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());

		// makes sure it's just a warning unrelated to the src
		IProblem item = items.get(0);
		assertEquals(IMarker.SEVERITY_WARNING, item.getSeverity());
	}

	public void testCSS3AtRule() throws CoreException
	{
		String text = "@namespace \"\";";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3MediaQuery() throws CoreException
	{
		String text = "@media only screen and (max-width: 600px) {\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3RgbaColor() throws CoreException
	{
		String text = "div {\nbackground-color: rgba(255, 255, 255, 0.5);\ncolor: rgba(255, 255, 255, 0.5);\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3BoxSizingProperty() throws CoreException
	{
		String text = "div {\nbox-sizing: border-box;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3OutlineOffsetProperty() throws CoreException
	{
		String text = "div {\noutline-offset: 10px;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testCSS3TransformProperty() throws CoreException
	{
		String text = "div {\ntransform: scale(0.5) rotate(90deg) translate(10px, 10px) skew(45deg, 60deg);\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}
}
