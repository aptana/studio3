/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.validator;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.ast.IParseError;

/**
 * Attaches HTML Parser errors from our own parser to the build context.
 * 
 * @author cwilliams
 */
public class HTMLParserValidator extends AbstractBuildParticipant
{
	public static final String ID = "com.aptana.editor.html.validator.HTMLParseErrorValidator"; //$NON-NLS-1$

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		// Set up our problems per language map
		Map<String, List<IProblem>> problems = new HashMap<String, List<IProblem>>();
		problems.put(IHTMLConstants.CONTENT_TYPE_HTML, new ArrayList<IProblem>());
		problems.put(IJSConstants.CONTENT_TYPE_JS, new ArrayList<IProblem>());
		problems.put(ICSSConstants.CONTENT_TYPE_CSS, new ArrayList<IProblem>());

		String source = context.getContents();
		if (!StringUtil.isEmpty(source))
		{
			try
			{
				context.getAST(); // Ensure a parse has happened
			}
			catch (CoreException e)
			{
				// ignores the parser exception
			}

			// Add parse errors...
			if (!CollectionsUtil.isEmpty(context.getParseErrors()))
			{
				URI path = context.getURI();
				String sourcePath = path.toString();
				IDocument doc = new Document(source);
				for (IParseError parseError : context.getParseErrors())
				{
					int severity = parseError.getSeverity().intValue();
					int line = -1;
					try
					{
						line = doc.getLineOfOffset(parseError.getOffset()) + 1;
					}
					catch (BadLocationException e)
					{
						// ignore
					}

					String language = parseError.getLangauge();
					List<IProblem> langProblems = problems.get(language);
					langProblems.add(new Problem(severity, parseError.getMessage(), parseError.getOffset(), parseError
							.getLength(), line, sourcePath));
					problems.put(language, langProblems);
				}
			}
		}

		context.putProblems(IHTMLConstants.HTML_PROBLEM, problems.get(IHTMLConstants.CONTENT_TYPE_HTML));
		context.putProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE, problems.get(IJSConstants.CONTENT_TYPE_JS));
		context.putProblems(ICSSConstants.CSS_PROBLEM, problems.get(ICSSConstants.CONTENT_TYPE_CSS));
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}
		context.removeProblems(IHTMLConstants.HTML_PROBLEM);
		context.removeProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE);
		context.removeProblems(ICSSConstants.CSS_PROBLEM);
	}
}
