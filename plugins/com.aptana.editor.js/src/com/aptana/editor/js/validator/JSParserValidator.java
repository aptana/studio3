/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.JSPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseError.Severity;

public class JSParserValidator extends AbstractBuildParticipant
{
	public static final String ID = "com.aptana.editor.js.validator.JSParserValidator"; //$NON-NLS-1$

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

		try
		{
			context.getAST(); // Ensure a parse happened
		}
		catch (CoreException e)
		{
			// ignores the parser exception
		}

		List<IProblem> problems = new ArrayList<IProblem>();
		try
		{
			// Add parse errors...
			if (!CollectionsUtil.isEmpty(context.getParseErrors()))
			{
				String source = context.getContents();
				URI uri = context.getURI();
				String sourcePath = uri.toString();
				IDocument doc = null;
				if (source != null)
				{
					doc = new Document(source);
				}
				for (IParseError parseError : context.getParseErrors())
				{
					int severity = (parseError.getSeverity() == Severity.ERROR) ? IMarker.SEVERITY_ERROR
							: IMarker.SEVERITY_WARNING;
					int line = -1;
					try
					{
						if (doc != null)
						{
							line = doc.getLineOfOffset(parseError.getOffset()) + 1;
						}
					}
					catch (BadLocationException e)
					{
						// ignore
					}
					problems.add(new Problem(severity, parseError.getMessage(), parseError.getOffset(), parseError
							.getLength(), line, sourcePath));
				}
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(JSPlugin.getDefault(), "Failed to parse for JS Parser Validation", e); //$NON-NLS-1$
		}

		context.putProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE, problems);
	}
}
