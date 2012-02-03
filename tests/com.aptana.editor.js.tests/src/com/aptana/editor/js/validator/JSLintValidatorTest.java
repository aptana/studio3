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

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.editor.common.validation.AbstractValidatorTestCase;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.JSParseState;

public class JSLintValidatorTest extends AbstractValidatorTestCase
{

	@Override
	protected AbstractBuildParticipant createValidator()
	{
		return new JSLintValidator()
		{
			@Override
			protected String getPreferenceNode()
			{
				return JSPlugin.PLUGIN_ID;
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
		return "js";
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new JSParseState(), IJSConstants.JSLINT_PROBLEM_MARKER_TYPE);
	}

	public void testJSLintValidator() throws CoreException
	{
		String text = "var foo = function() {\nhello();\n};";

		List<IProblem> items = getParseErrors(text);
		assertEquals(1, items.size());

		IProblem item = items.get(0);
		assertEquals(2, item.getLineNumber());
		assertEquals("'hello' is not defined.", item.getMessage());
		assertEquals(IMarker.SEVERITY_WARNING, item.getSeverity());
		assertEquals(24, item.getOffset());
	}
}
