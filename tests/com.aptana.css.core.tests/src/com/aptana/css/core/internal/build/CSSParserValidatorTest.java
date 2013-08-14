/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.aptana.buildpath.core.tests.AbstractValidatorTestCase;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.ICSSConstants;
import com.aptana.parsing.ParseState;

public class CSSParserValidatorTest extends AbstractValidatorTestCase
{

	@Override
	protected IBuildParticipant createValidator()
	{
		return new CSSParserValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return CSSCorePlugin.PLUGIN_ID;
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
		return getParseErrors(source, new ParseState(source), ICSSConstants.CSS_PROBLEM);
	}

	public void testCSSParseErrors() throws CoreException
	{
		String text = "div#paginator {\nfloat: left\nwidth: 65px\n}";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());

		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 3, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \":\"",
				item.getMessage());
	}

	public void testCSSParseErrorsAtNot() throws CoreException
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
}
