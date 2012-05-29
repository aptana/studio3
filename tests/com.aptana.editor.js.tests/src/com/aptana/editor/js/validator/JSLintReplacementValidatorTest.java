/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.JSParseState;

public class JSLintReplacementValidatorTest extends JSLintValidatorTest
{

	@Override
	protected AbstractBuildParticipant createValidator()
	{
		return new JSLintReplacementValidator()
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

			@Override
			// Don't filter anything out
			public List<String> getFilters()
			{
				return Collections.emptyList();
			}

			@Override
			// Don't skip errors/warnings just because they're on same line!
			protected boolean hasErrorOrWarningOnLine(List<IProblem> items, int line)
			{
				return false;
			}
		};
	}

	protected List<IProblem> getParseErrors(String source) throws CoreException
	{
		return getParseErrors(source, new JSParseState(), IJSConstants.JSLINT_PROBLEM_MARKER_TYPE);
	}
}
