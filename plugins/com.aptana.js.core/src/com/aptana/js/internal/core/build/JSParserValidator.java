/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.parsing.ast.IParseError;

public class JSParserValidator extends AbstractBuildParticipant
{

	public static final String ID = "com.aptana.js.core.JSParserValidator"; //$NON-NLS-1$

	/**
	 * Temporary problem collector used during {@link #buildFile(BuildContext, IProgressMonitor)}.
	 */
	private List<IProblem> fProblems;

	/**
	 * A re-used {@link IDocument} object wrapping the current {@link BuildContext}s source. Useful for asking for line
	 * numbers in a performant way. Temporary lifecycle, should get instantiated on-demand, and cleaned up at the end of
	 * {@link #buildFile(BuildContext, IProgressMonitor)}
	 */
	private Document fDocument;

	private URI fLocation;
	private String fPath;
	private BuildContext fContext;

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE);
	}

	public void buildFile(final BuildContext context, IProgressMonitor monitor)
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

		// Set our temp fields up
		this.fContext = context;
		this.fProblems = new ArrayList<IProblem>();

		try
		{
			// Add parse errors...
			Collection<IParseError> parseErrors = context.getParseErrors();
			if (!CollectionsUtil.isEmpty(parseErrors))
			{
				fLocation = context.getURI();
				fPath = fLocation.toString();

				fProblems.addAll(CollectionsUtil.map(parseErrors, new IMap<IParseError, IProblem>()
				{

					public IProblem map(IParseError parseError)
					{
						return new Problem(parseError.getSeverity().intValue(), parseError.getMessage(), parseError
								.getOffset(), parseError.getLength(), getLine(parseError.getOffset()), fPath);
					}
				}));
			}
		}
		catch (Exception e)
		{
			IdeLog.logError(JSCorePlugin.getDefault(),
					MessageFormat.format("Failed to parse {0} for JS Parser Validation", fPath), e); //$NON-NLS-1$
		}

		context.putProblems(IJSConstants.JS_PROBLEM_MARKER_TYPE, fProblems);

		// Clean up the temporary fields
		this.fDocument = null;
		this.fPath = null;
		this.fLocation = null;
		this.fContext = null;
	}

	/**
	 * Determine the line number for the offset.
	 * 
	 * @param offset
	 * @return
	 */
	private int getLine(int offset)
	{
		try
		{
			return getDocument(fContext).getLineOfOffset(offset) + 1;
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		return -1;
	}

	/**
	 * Lazily instantiate an {@link IDocument} to wrap the source for querying line numbers. See
	 * {@link #getLine(BuildContext, int)}
	 * 
	 * @param context
	 * @return
	 */
	private IDocument getDocument(BuildContext context)
	{
		if (this.fDocument == null)
		{
			String source = context.getContents();
			this.fDocument = new Document(source);
		}
		return this.fDocument;
	}
}
