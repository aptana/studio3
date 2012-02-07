/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.validator;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.build.RequiredBuildParticipant;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseError.Severity;

public class JSParserValidator extends RequiredBuildParticipant
{

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		List<IProblem> problems = new ArrayList<IProblem>();
		if (enableJSParseErrors())
		{
			try
			{
				context.getAST(); // Ensure a parse happened

				String source = context.getContents();
				URI uri = context.getURI();
				String sourcePath = uri.toString();

				// Add parse errors... FIXME Move this out of here!
				for (IParseError parseError : context.getParseErrors())
				{
					int severity = (parseError.getSeverity() == Severity.ERROR) ? IMarker.SEVERITY_ERROR
							: IMarker.SEVERITY_WARNING;
					int line = -1;
					if (source != null)
					{
						line = getLineNumber(parseError.getOffset(), source);
					}
					problems.add(new Problem(severity, parseError.getMessage(), parseError.getOffset(), parseError
							.getLength(), line, sourcePath));
				}

			}
			catch (Exception e)
			{
				IdeLog.logError(JSPlugin.getDefault(), "Failed to parse for JS Parser Validation", e); //$NON-NLS-1$
			}
		}

		context.putProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE, problems);
	}

	private boolean enableJSParseErrors()
	{
		IEclipsePreferences store = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		return store.getBoolean(getEnableParseErrorPrefKey(IJSConstants.CONTENT_TYPE_JS), true);
	}

	private String getEnableParseErrorPrefKey(String language)
	{
		return MessageFormat.format("{0}:{1}", language, IPreferenceConstants.PARSE_ERROR_ENABLED); //$NON-NLS-1$
	}

}
