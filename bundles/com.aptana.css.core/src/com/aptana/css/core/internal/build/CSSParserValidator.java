/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.internal.build;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

import com.aptana.core.IMap;
import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.Problem;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.css.core.CSSCorePlugin;
import com.aptana.css.core.ICSSConstants;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseError;

/**
 * Grabs the errors/warnings from our CSS Parser and attaches them to the build context.
 * 
 * @author cwilliams
 */
public class CSSParserValidator extends AbstractBuildParticipant
{
	public static final String ID = "com.aptana.css.core.CSSParserValidator"; //$NON-NLS-1$

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		try
		{
			context.getAST(); // make sure a parse has happened...
		}
		catch (CoreException e)
		{
			// ignores the parser exception
		}

		final String path = context.getURI().toString();
		List<IProblem> problems = Collections.emptyList();
		try
		{
			// Add parse errors...
			if (!CollectionsUtil.isEmpty(context.getParseErrors()))
			{
				String source = context.getContents();
				final IDocument doc = new Document(source);

				problems = CollectionsUtil.map(context.getParseErrors(), new IMap<IParseError, IProblem>()
				{

					public IProblem map(IParseError parseError)
					{
						int severity = parseError.getSeverity().intValue();
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
						return new Problem(severity, parseError.getMessage(), parseError.getOffset(), parseError
								.getLength(), line, path);
					}
				});
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(CSSCorePlugin.getDefault(),
					MessageFormat.format("Failed to parse {0} for CSS Parser Validation", path), e); //$NON-NLS-1$
		}

		context.putProblems(ICSSConstants.CSS_PROBLEM, problems);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(ICSSConstants.CSS_PROBLEM);
	}
}
