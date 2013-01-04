/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.internal.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.IFilter;
import com.aptana.core.build.IProblem;
import com.aptana.core.build.RequiredBuildParticipant;
import com.aptana.core.resources.IMarkerConstants;
import com.aptana.core.util.ArrayUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.css.core.build.CSSTaskDetector;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.build.JSTaskDetector;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.util.ParseUtil;

/**
 * Detects task tags in HTML comments. Also traverses into JS and CSS and delegates detecting tasks in those
 * sub-languages to the {@link JSTaskDetector} and {@link CSSTaskDetector}
 * 
 * @author cwilliams
 */
public class HTMLTaskDetector extends RequiredBuildParticipant
{

	private static final String COMMENT_ENDING = "-->"; //$NON-NLS-1$
	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

	public void buildFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		Collection<IProblem> tasks = detectTasks(context, monitor);
		context.putProblems(IMarkerConstants.TASK_MARKER, tasks);
	}

	public void deleteFile(BuildContext context, IProgressMonitor monitor)
	{
		if (context == null)
		{
			return;
		}

		context.removeProblems(IMarkerConstants.TASK_MARKER);
	}

	private Collection<IProblem> detectTasks(BuildContext context, IProgressMonitor monitor)
	{
		Collection<IProblem> tasks = new ArrayList<IProblem>();

		SubMonitor sub = SubMonitor.convert(monitor, 2);
		IParseRootNode rootNode = null;
		try
		{
			rootNode = context.getAST();
		}
		catch (CoreException e)
		{
			// ignores the parser exception
		}
		if (rootNode != null)
		{
			tasks.addAll(walkAST(context, rootNode, sub.newChild(1)));
			tasks.addAll(processComments(context, rootNode, sub.newChild(1)));
		}
		sub.done();

		return tasks;
	}

	private Collection<IProblem> processComments(BuildContext context, IParseRootNode rootNode, IProgressMonitor monitor)
	{
		IParseNode[] comments = rootNode.getCommentNodes();
		if (ArrayUtil.isEmpty(comments))
		{
			return Collections.emptyList();
		}

		SubMonitor sub = SubMonitor.convert(monitor, comments.length);
		Collection<IProblem> tasks = new ArrayList<IProblem>(comments.length);
		try
		{
			String source = context.getContents();
			String filePath = context.getURI().toString();
			for (IParseNode commentNode : comments)
			{
				if (commentNode instanceof HTMLCommentNode)
				{
					tasks.addAll(processCommentNode(filePath, source, 0, commentNode, COMMENT_ENDING));
				}
				sub.worked(1);
			}
		}
		finally
		{
			sub.done();
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
		IParseNode child = htmlSpecialNode.getChild(0);
		if (child != null)
		{
			String language = child.getLanguage();

			if (ICSSConstants.CONTENT_TYPE_CSS.equals(language))
			{
				// process inline code
				CSSTaskDetector detector = new CSSTaskDetector();
				return detector.detectTasks((IParseRootNode) child, context, null);
			}
		}
		if (ELEMENT_SCRIPT.equalsIgnoreCase(htmlSpecialNode.getName()))
		{
			String jsSource = htmlSpecialNode.getAttributeValue(ATTRIBUTE_SRC);
			if (jsSource == null && child != null && IJSConstants.CONTENT_TYPE_JS.equals(child.getLanguage()))
			{
				// process inline code
				JSTaskDetector detector = new JSTaskDetector();
				return detector.detectTasks((IParseRootNode) child, context, null);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * walkAST
	 * 
	 * @param index
	 * @param file
	 * @param parent
	 * @param monitor
	 */
	private Collection<IProblem> walkAST(final BuildContext context, IParseNode parent, IProgressMonitor monitor)
	{
		// TODO Provide progress somehow?
		final Collection<IProblem> tasks = new ArrayList<IProblem>();
		if (parent != null)
		{

			ParseUtil.treeApply(parent, new IFilter<IParseNode>()
			{
				public boolean include(IParseNode item)
				{
					if (item instanceof HTMLSpecialNode)
					{
						tasks.addAll(processHTMLSpecialNode(context, (HTMLSpecialNode) item));
					}
					return true;
				}
			});

		}
		return tasks;
	}

}
