/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.internal.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.parsing.ast.JSCommentNode;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

/**
 * Detects task tags in JS comments.
 * 
 * @author cwilliams
 */
public class JSTaskDetector extends AbstractBuildParticipant
{

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		Collection<IProblem> tasks = detectTasks(context, monitor);
		context.putProblems(IMarker.TASK, tasks);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		context.removeProblems(IMarker.TASK);
	}

	private Collection<IProblem> detectTasks(BuildContext context, IProgressMonitor monitor)
	{
		try
		{
			return detectTasks(context.getAST(), context, monitor);
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
		}
		return Collections.emptyList();
	}

	/**
	 * Method for detecting tasks in the root of a JS AST. This is public for the HTML task detector to be able to
	 * delegate to this for JS as a sub-language.
	 */
	public Collection<IProblem> detectTasks(IParseRootNode rootNode, BuildContext context, IProgressMonitor monitor)
	{
		Collection<IProblem> tasks = new ArrayList<IProblem>();
		try
		{
			IParseNode[] comments = rootNode.getCommentNodes();
			if (comments == null || comments.length == 0)
			{
				return Collections.emptyList();
			}

			SubMonitor sub = SubMonitor.convert(monitor, comments.length);
			String source = context.getContents();
			String filePath = context.getURI().toString();
			for (IParseNode commentNode : comments)
			{
				if (commentNode instanceof JSCommentNode)
				{
					tasks.addAll(processCommentNode(filePath, source, 0, commentNode, "*/")); //$NON-NLS-1$
				}
				sub.worked(1);
			}
			sub.done();
		}
		catch (CoreException e)
		{
			IdeLog.logError(JSPlugin.getDefault(), e);
		}
		return tasks;
	}

}
