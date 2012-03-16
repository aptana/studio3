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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.index.core.build.BuildContext;

public class MozillaJsValidator extends AbstractBuildParticipant
{
	public static final String ID = "com.aptana.editor.js.validator.MozillaValidator"; //$NON-NLS-1$

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.MOZILLA_PROBLEM_MARKER_TYPE);
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		List<IProblem> problems = new ArrayList<IProblem>();
		Context cx = Context.enter();
		DefaultErrorReporter reporter = new DefaultErrorReporter();
		URI path = context.getURI();
		String sourcePath = context.getName();
		if (path != null)
		{
			sourcePath = path.toString();
		}
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

		// Filter the problems...
		List<String> filters = getFilters();
		List<IProblem> errors = reporter.getItems();
		for (IProblem error : errors)
		{
			String message = error.getMessage();
			if (!isIgnored(message, filters))
			{
				// Don't attempt to add errors or warnings if there are already errors on this line
				if (hasErrorOrWarningOnLine(problems, error.getLineNumber()))
				{
					continue;
				}
				problems.add(error);
			}
		}

		context.putProblems(IJSConstants.MOZILLA_PROBLEM_MARKER_TYPE, problems);
	}

	private boolean isIgnored(String message, List<String> expressions)
	{
		for (String expression : expressions)
		{
			if (message.matches(expression))
			{
				return true;
			}
		}

		return false;
	}

	private class DefaultErrorReporter implements ErrorReporter
	{
		private List<IProblem> items = new ArrayList<IProblem>();

		public DefaultErrorReporter()
		{
			items = new ArrayList<IProblem>();
		}

		public void error(String message, String sourceURI, int line, String lineText, int lineOffset)
		{
			items.add(createError(message, line, lineOffset, 0, sourceURI));
		}

		public void warning(String message, String sourceURI, int line, String lineText, int lineOffset)
		{
			items.add(createWarning(message, line, lineOffset, 0, sourceURI));
		}

		public EvaluatorException runtimeError(String message, String sourceURI, int line, String lineText,
				int lineOffset)
		{
			return new EvaluatorException(message, sourceURI, line, lineText, lineOffset);
		}

		public List<IProblem> getItems()
		{
			return Collections.unmodifiableList(items);
		}
	}
}
