/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.junit.Test;

import com.aptana.buildpath.core.tests.AbstractValidatorTestCase;
import com.aptana.core.build.IBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.JSParseState;
import com.aptana.js.core.preferences.IPreferenceConstants;

@SuppressWarnings("nls")
public class JSParserValidatorTest extends AbstractValidatorTestCase
{

	@Override
	protected IBuildParticipant createValidator()
	{
		return new JSParserValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return JSCorePlugin.PLUGIN_ID;
			}
		};
	}

	@Override
	protected String getFileExtension()
	{
		return "js";
	}

	@Override
	protected String getContentType()
	{
		return IJSConstants.CONTENT_TYPE_JS;
	}

	@Test
	public void testJSParseErrors() throws CoreException
	{
		String text = "var foo = function() {\nhello(\n};";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		IProblem item = items.get(0);

		assertEquals("Error was not found on expected line", 3, item.getLineNumber());
		assertEquals("Error message did not match expected error message", "Syntax Error: unexpected token \"}\"",
				item.getMessage());
	}

	@Test
	public void testNoJSParseErrors() throws CoreException
	{
		String text = "var foo = function() {\nhello();\n};";

		List<IProblem> items = getParseErrors(text);
		assertEquals(0, items.size());
	}

	@Test
	public void testMissingSemicolonReportsWarning() throws CoreException
	{
		// @formatter:off
		String text = "var USPostalReg = /^\\d{5}(-\\d{4})?$/\n" +
					"if (!USPostalReg.test(textFields_array[i].value)) {\n" +
					"    inValidValue = true;\n" +
					"}";
		// @formatter:on

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());
		IProblem problem = items.get(0);
		assertEquals("Error was not found on expected line", 1, problem.getLineNumber());
		assertEquals("Error message did not match expected error message", "Missing semicolon", problem.getMessage());
		assertEquals(IProblem.Severity.WARNING, problem.getSeverity());
	}

	@Test
	public void testMissingSemicolonSetToIgnoreReportsNoWarning() throws Exception
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(JSCorePlugin.PLUGIN_ID);
		prefs.put(IPreferenceConstants.PREF_MISSING_SEMICOLON_SEVERITY, IProblem.Severity.IGNORE.id());
		prefs.flush();

		try
		{
			// @formatter:off
			String text = "var USPostalReg = /^\\d{5}(-\\d{4})?$/\n" +
						"if (!USPostalReg.test(textFields_array[i].value)) {\n" +
						"    inValidValue = true;\n" +
						"}";
			// @formatter:on

			List<IProblem> items = getParseErrors(text);
			assertEquals("Expected no warning about missing semicolons but received one", 0, items.size());
		}
		finally
		{
			prefs.remove(IPreferenceConstants.PREF_MISSING_SEMICOLON_SEVERITY);
			prefs.flush();
		}
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new JSParseState(source), IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}
}
