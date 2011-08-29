/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.validator;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.parsing.ParseState;

public class CSSValidatorTests extends AbstractValidatorTestCase
{

	public void testCSSParseErrors() throws CoreException
	{
		String text = "div#paginator {\nfloat: left\nwidth: 65px\n}";

		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);
		List<IValidationItem> items = getParseErrors(text, ICSSConstants.CONTENT_TYPE_CSS, new ParseState());
		assertEquals(1, items.size());

		IValidationItem item = items.get(0);

		assertEquals("Error was not found on expected line", 3, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \":\"",
				item.getMessage());
	}

	public void testNoCSSParseErrors() throws CoreException
	{
		String text = "div#paginator {\nfloat: left;\nwidth: 65px\n}";

		setEnableParseError(true, ICSSConstants.CONTENT_TYPE_CSS);
		List<IValidationItem> items = getParseErrors(text, ICSSConstants.CONTENT_TYPE_CSS, new ParseState());
		assertEquals(0, items.size());
	}

	public void testCSS3TransitionPropertyError() throws CoreException
	{
		String text = "div {\ntransition: width 2s;\n}";

		List<IValidationItem> items = getParseErrors(text, ICSSConstants.CONTENT_TYPE_CSS, new ParseState());
		assertEquals(0, items.size());
	}
}
