/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

import com.aptana.buildpath.core.tests.AbstractValidatorTestCase;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.ICSSConstants;
import com.aptana.parsing.ParseState;

public class CSSValidatorTest extends AbstractValidatorTestCase
{

	@Override
	protected IBuildParticipant createValidator()
	{
		return new CSSValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return CSSCorePlugin.PLUGIN_ID;
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
		return "css";
	}

	@Override
	protected String getContentType()
	{
		return ICSSConstants.CONTENT_TYPE_CSS;
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new ParseState(source), ICSSConstants.W3C_PROBLEM);
	}

	@Test
	public void testCSS3TransitionProperty() throws CoreException
	{
		String text = "div {\ntransition: width 2s;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSSPropertyPrecededByDash() throws CoreException
	{
		String text = "div {\n-background-color: #123;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testPropertyValueNone() throws CoreException
	{
		String text = "H1:before {\ncontent: none;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3ResizeProperty() throws CoreException
	{
		String text = "div {\nresize: both;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3BackgroundProperty() throws CoreException
	{
		String text = "div {\nbackground-clip: border-box;\nbackground-origin: content-box;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3SrcPropertyInFontFace() throws CoreException
	{
		String text = "@font-face {\nsrc: url(\"\");\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());

		// makes sure it's just a warning unrelated to the src
		IProblem item = items.get(0);
		assertEquals(IMarker.SEVERITY_WARNING, item.getSeverity().intValue());
	}

	@Test
	public void testCSS3AtRule() throws CoreException
	{
		String text = "@namespace \"\";";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3MediaQuery() throws CoreException
	{
		String text = "@media only screen and (max-width: 600px) {\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3RgbaColor() throws CoreException
	{
		String text = "div {\nbackground-color: rgba(255, 255, 255, 0.5);\ncolor: rgba(255, 255, 255, 0.5);\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3BoxSizingProperty() throws CoreException
	{
		String text = "div {\nbox-sizing: border-box;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3OutlineOffsetProperty() throws CoreException
	{
		String text = "div {\noutline-offset: 10px;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3TransformProperty() throws CoreException
	{
		String text = "div {\ntransform: scale(0.5) rotate(90deg) translate(10px, 10px) skew(45deg, 60deg);\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3UserSelectProperty() throws CoreException
	{
		String text = "div {\nuser-select: none;\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3LinearGradient() throws CoreException
	{
		String text = "div {\nbackground-image: -ms-linear-gradient(left, #2c072c, #3d243d, #2c072c);\nbackground-image: -o-linear-gradient(left, #2c072c, #3d243d, #2c072c);\nbackground-image: linear-gradient(left, #2c072c, #3d243d, #2c072c);\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3NotSyntax() throws CoreException
	{
		//@formatter:off
		String text = "" +
				"audio:not([controls]) {\n" +
				"    display: none;\n" +
				"    height: 0;\n" +
				"}\n" +
				" \n" +
				"svg:not(:root) {\n" +
				"    overflow: hidden;\n" +
				"}\n" +
				"";
		//@formatter:on

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3NotSyntax1() throws CoreException
	{
		String text = "div:not(.home){}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3NotSyntax2() throws CoreException
	{
		String text = "div *:not(p) em{}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3NotSyntax3() throws CoreException
	{
		String text = "input:not([type=\"file\"]){}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3NotSyntax4() throws CoreException
	{
		String text = "h2:not(.foo, .bar){}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testCSS3NotSyntax5() throws CoreException
	{
		String text = "li:not(.pingback) .comment-content p:first-child:first-line{}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}
}
