/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.preferences.IPreferenceConstants;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.IHTMLConstants;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseError.Severity;

/**
 * Attaches HTML Parser errors from our own parser to the build context.
 * 
 * @author cwilliams
 */
public class HTMLParseErrorValidator extends AbstractBuildParticipant
{

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null || !enableHTMLParseErrors())
		{
			return;
		}

		List<IProblem> problems = new ArrayList<IProblem>();
		try
		{

			String source = context.getContents();
			if (!StringUtil.isEmpty(source))
			{
				URI path = context.getURI();
				String sourcePath = path.toString();

				context.getAST(); // Ensure a parse has happened

				// Add parse errors...
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
		}
		catch (CoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), "Failed to parse for HTML Parse Error Validation", e); //$NON-NLS-1$
		}

		context.putProblems(IHTMLConstants.HTML_PROBLEM, problems);
	}

	private boolean enableHTMLParseErrors()
	{
		IEclipsePreferences store = EclipseUtil.instanceScope().getNode(CommonEditorPlugin.PLUGIN_ID);
		return store.getBoolean(getEnableParseErrorPrefKey(IHTMLConstants.CONTENT_TYPE_HTML), true);
	}

	private String getEnableParseErrorPrefKey(String language)
	{
		return MessageFormat.format("{0}:{1}", language, IPreferenceConstants.PARSE_ERROR_ENABLED); //$NON-NLS-1$
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}
		context.removeProblems(IHTMLConstants.HTML_PROBLEM);
	}
}
