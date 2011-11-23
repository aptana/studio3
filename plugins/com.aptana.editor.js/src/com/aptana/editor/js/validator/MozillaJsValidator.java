/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.index.core.build.BuildContext;

public class MozillaJsValidator extends AbstractBuildParticipant
{

	// FIXME Create sub-markers of Problem marker just for Mozilla JS!

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		context.removeProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		List<IProblem> problems = new ArrayList<IProblem>();
		Context cx = Context.enter();
		DefaultErrorReporter reporter = new DefaultErrorReporter();
		URI path = context.getURI();
		String sourcePath = path.toString();
		try
		{
			String source = context.getContents();

			cx.setErrorReporter(reporter);

			CompilerEnvirons compilerEnv = new CompilerEnvirons();
			compilerEnv.initFromContext(cx);

			Parser p = new Parser(compilerEnv, reporter);
			try
			{
				p.parse(source, sourcePath, 1);
			}
			catch (EvaluatorException e)
			{
				// ignores the exception here
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), "Failed to parse for Mozilla JS Validation", e); //$NON-NLS-1$
		}
		finally
		{
			Context.exit();
		}

		// converts the items from mozilla's error reporter to the ones stored in validation manager
		List<ErrorItem> errors = reporter.getItems();
		String message;
		int severity;
		for (ErrorItem error : errors)
		{
			message = error.getMessage();
			if (!isIgnored(message, IJSConstants.CONTENT_TYPE_JS))
			{
				// Don't attempt to add errors or warnings if there are already errors on this line
				if (hasErrorOrWarningOnLine(problems, error.getLine()))
				{
					continue;
				}

				severity = error.getSeverity();
				if (severity == IMarker.SEVERITY_ERROR)
				{
					problems.add(createError(message, error.getLine(), error.getLineOffset(), 0, sourcePath));
				}
				else if (severity == IMarker.SEVERITY_WARNING)
				{
					problems.add(createWarning(message, error.getLine(), error.getLineOffset(), 0, sourcePath));
				}
			}
		}

		context.putProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE, problems);
	}

	private String getFilterExpressionsPrefKey(String language)
	{
		return language + ":" + IPreferenceConstants.FILTER_EXPRESSIONS; //$NON-NLS-1$
	}

	private boolean isIgnored(String message, String language)
	{
		String list = CommonEditorPlugin.getDefault().getPreferenceStore()
				.getString(getFilterExpressionsPrefKey(language));
		if (!StringUtil.isEmpty(list))
		{
			String[] expressions = list.split("####"); //$NON-NLS-1$
			for (String expression : expressions)
			{
				if (message.matches(expression))
				{
					return true;
				}
			}
		}
		return false;
	}
}
