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

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.parsing.JSParseState;

public class JSValidatorTests extends AbstractValidatorTestCase
{

	@Override
	protected AbstractBuildParticipant createValidator()
	{
		return new JSLintValidator();
	}

	@Override
	protected String getFileExtension()
	{
		return "js";
	}

	public void testJSParseErrors() throws CoreException
	{
		String text = "var foo = function() {\nhello()\n};";

		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);
		// Turn off JSLint
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(IJSConstants.CONTENT_TYPE_JS + ":" + IPreferenceConstants.SELECTED_VALIDATORS, "");

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 3, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \"}\"",
				item.getMessage());
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new JSParseState(), IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}

	public void testNoJSParseErrors() throws CoreException
	{
		String text = "var foo = function() {\nhello();\n};";

		setEnableParseError(true, IJSConstants.CONTENT_TYPE_JS);
		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	public void testJSLintValidator() throws CoreException
	{
		String text = "var foo = function() {\nhello();\n};";

		// Turn on JSLint
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		prefs.put(IJSConstants.CONTENT_TYPE_JS + ":" + IPreferenceConstants.SELECTED_VALIDATORS,
				"JSLint JavaScript Validator");

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());

		IProblem item = items.get(0);
		assertEquals(2, item.getLineNumber());
		assertEquals("'hello' is not defined.", item.getMessage());
		assertEquals(IMarker.SEVERITY_WARNING, item.getSeverity());
		assertEquals(24, item.getOffset());
	}
}
