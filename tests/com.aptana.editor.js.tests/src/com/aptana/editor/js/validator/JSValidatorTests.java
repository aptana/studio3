/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.common.validator.IValidationItem;
import com.aptana.editor.js.IJSConstants;
import com.aptana.parsing.ParseState;

public class JSValidatorTests extends AbstractValidatorTestCase
{

	public void testJSParseErrors() throws CoreException
	{
		String text = "var foo = function() {\nhello()\n};";

		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);
		List<IValidationItem> items = getParseErrors(text, IJSConstants.CONTENT_TYPE_JS, new ParseState());
		assertEquals(1, items.size());
		IValidationItem item = items.get(0);

		assertEquals("Error was not found on expected line", 3, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \"}\"",
				item.getMessage());
	}

	public void testNoJSParseErrors() throws CoreException
	{
		String text = "var foo = function() {\nhello();\n};";

		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);
		List<IValidationItem> items = getParseErrors(text, IJSConstants.CONTENT_TYPE_JS, new ParseState());
		assertEquals(0, items.size());
	}

	public void testJSLintValidator() throws CoreException
	{
		String text = "var foo = function() {\nhello();\n};";

		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(IJSConstants.CONTENT_TYPE_JS + ":" + IPreferenceConstants.SELECTED_VALIDATORS,
				"JSLint JavaScript Validator");

		List<IValidationItem> items = getParseErrors(text, IJSConstants.CONTENT_TYPE_JS, new ParseState());
		assertEquals(1, items.size());

		IValidationItem item = items.get(0);
		assertEquals(2, item.getLineNumber());
		assertEquals("'hello' is not defined.", item.getMessage());
		assertEquals(IMarker.SEVERITY_WARNING, item.getSeverity());
		assertEquals(24, item.getOffset());
	}
}
