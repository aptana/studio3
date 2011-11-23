/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.internal.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.build.AbstractBuildParticipant;
import com.aptana.core.build.IProblem;
import com.aptana.core.logging.IdeLog;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.css.internal.build.CSSTaskDetector;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.IJSConstants;
import com.aptana.editor.js.internal.build.JSTaskDetector;
import com.aptana.index.core.build.BuildContext;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

/**
 * Detects task tags in HTML comments. Also traverses into JS and CSS and delegates detecting tasks in those
 * sub-languages to the {@link JSTaskDetector} and {@link CSSTaskDetector}
 * 
 * @author cwilliams
 */
public class HTMLTaskDetector extends AbstractBuildParticipant
{

	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

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
		Collection<IProblem> tasks = new ArrayList<IProblem>();

		SubMonitor sub = SubMonitor.convert(monitor, 2);
		try
		{
			IParseRootNode rootNode = context.getAST();
			tasks.addAll(walkAST(context, rootNode, sub.newChild(1)));
			tasks.addAll(processComments(rootNode, context, sub.newChild(1)));
			sub.done();
		}
		catch (CoreException e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e);
		}
		return tasks;
	}

	private Collection<IProblem> processComments(IParseRootNode rootNode, BuildContext context, IProgressMonitor monitor)
	{
		Collection<IProblem> tasks = new ArrayList<IProblem>();
		IParseNode[] comments = rootNode.getCommentNodes();
		if (comments == null || comments.length == 0)
		{
			return Collections.emptyList();
		}

		try
		{
			SubMonitor sub = SubMonitor.convert(monitor, comments.length);
			String source = context.getContents();
			String filePath = context.getURI().toString();
			for (IParseNode commentNode : comments)
			{
				if (commentNode instanceof HTMLCommentNode)
				{
					tasks.addAll(processCommentNode(filePath, source, 0, commentNode, "-->")); //$NON-NLS-1$
				}
				sub.worked(1);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e);
		}
		return tasks;
	}

	/**
	 * processHTMLSpecialNode
	 * 
	 * @param index
	 * @param file
	 * @param htmlSpecialNode
	 */
	private Collection<IProblem> processHTMLSpecialNode(BuildContext context, HTMLSpecialNode htmlSpecialNode)
	{
		Collection<IProblem> tasks = new ArrayList<IProblem>();
		IParseNode child = htmlSpecialNode.getChild(0);

		if (child != null)
		{
			String language = child.getLanguage();

			if (ICSSConstants.CONTENT_TYPE_CSS.equals(language))
			{
				// process inline code
				CSSTaskDetector detector = new CSSTaskDetector();
				tasks.addAll(detector.detectTasks((IParseRootNode) child, context, null));
			}
		}
		if (ELEMENT_SCRIPT.equalsIgnoreCase(htmlSpecialNode.getName()))
		{
			String jsSource = htmlSpecialNode.getAttributeValue(ATTRIBUTE_SRC);
			if (jsSource == null && child != null && IJSConstants.CONTENT_TYPE_JS.equals(child.getLanguage()))
			{
				// process inline code
				JSTaskDetector detector = new JSTaskDetector();
				tasks.addAll(detector.detectTasks((IParseRootNode) child, context, null));
			}
		}
		return tasks;
	}

	/**
	 * walkAST
	 * 
	 * @param index
	 * @param file
	 * @param parent
	 * @param monitor
	 */
	private Collection<IProblem> walkAST(BuildContext context, IParseNode parent, IProgressMonitor monitor)
	{
		// TODO Provide progress somehow?
		Collection<IProblem> tasks = new ArrayList<IProblem>();
		if (parent != null)
		{
			Queue<IParseNode> queue = new LinkedList<IParseNode>();

			// prime queue
			queue.offer(parent);

			while (!queue.isEmpty())
			{
				IParseNode current = queue.poll();

				if (current instanceof HTMLSpecialNode)
				{
					tasks.addAll(processHTMLSpecialNode(context, (HTMLSpecialNode) current));
				}

				for (IParseNode child : current)
				{
					queue.offer(child);
				}
			}
		}
		return tasks;
	}

}
