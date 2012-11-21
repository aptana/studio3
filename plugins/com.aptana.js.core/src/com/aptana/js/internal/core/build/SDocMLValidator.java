/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.build;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.internal.core.index.JSMetadataReader;
import com.aptana.sax.IValidatingReaderLogger;

public class SDocMLValidator extends AbstractBuildParticipant
{
	
	public static final String ID = "com.aptana.js.core.SDocMLValidator"; //$NON-NLS-1$

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IJSConstants.SDOCML_PROBLEM_MARKER_TYPE);
	}

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		final List<IProblem> problems = new ArrayList<IProblem>();
		final String path = context.getURI().toString();
		// TODO Can we re-use this reader? If so, instantiate in buildStarting()
		JSMetadataReader reader = new JSMetadataReader();
		IValidatingReaderLogger collector = new IValidatingReaderLogger()
		{
			public void logError(String message, int line, int column)
			{
				problems.add(createError(message, line, column, 0, path));
			}

			public void logInfo(String message, int line, int column)
			{
				problems.add(createInfo(message, line, column, 0, path));
			}

			public void logWarning(String message, int line, int column)
			{
				problems.add(createWarning(message, line, column, 0, path));
			}
		};
		reader.setLogger(collector);

		try
		{
			InputStream input = context.openInputStream(monitor); // $codepro.audit.disable closeWhereCreated
			reader.loadXML(input, context.getURI().toString());
		}
		catch (Exception e)
		{
			problems.add(createError(e.getMessage(), 0, 0, 0, path.toString()));
		}

		context.putProblems(IJSConstants.SDOCML_PROBLEM_MARKER_TYPE, problems);
	}
}
