/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ast.IParseNode;

/**
 * It is recommended for instances of IBuildParticipant to subclass this class. This takes care of the getter/setter for
 * priority, as well as provides some helper methods for detecting tasks, generating task IValidationItems and
 * determining the line number of an offset in the document.
 * 
 * @author cwilliams
 */
public abstract class AbstractBuildParticipant implements IBuildParticipant
{

	private int priority;

	public int getPriority()
	{
		return priority;
	}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public void buildStarting(IProject project, int kind, IProgressMonitor monitor)
	{
		// no-op. Most impls won't do project level setup.
	}

	public void buildEnding(IProgressMonitor monitor)
	{
		// no-op. Most impls won't do project level setup.
	}

	public void clean(IProject project, IProgressMonitor monitor)
	{
		// no-op. Most impls won't do batch clean stuff
	}

	protected int getLineNumber(int start, String source)
	{
		if (start < 0 || start >= source.length())
		{
			return -1;
		}
		if (start == 0)
		{
			return 1;
		}

		Matcher m = StringUtil.LINE_SPLITTER.matcher(source.substring(0, start));
		int line = 1;
		while (m.find())
		{
			int offset = m.start();
			if (offset > start)
			{
				break;
			}
			line++;
		}
		return line;
	}

	/**
	 * Common code for detecting tasks in comment nodes from ASTs
	 **/
	protected Collection<IProblem> processCommentNode(String filePath, String source, int initialOffset,
			IParseNode commentNode, String commentEnding)
	{
		Collection<IProblem> tasks = new ArrayList<IProblem>();
		String text = commentNode.getText();
		if (text == null || text.length() == 0)
		{
			text = getText(source, commentNode);
		}

		if (!TaskTag.isCaseSensitive())
		{
			text = text.toLowerCase();
		}
		String[] lines = StringUtil.LINE_SPLITTER.split(text);
		for (String line : lines)
		{
			for (TaskTag entry : TaskTag.getTaskTags())
			{
				String tag = entry.getName();
				if (!TaskTag.isCaseSensitive())
				{
					tag = tag.toLowerCase();
				}
				int index = line.indexOf(tag);
				if (index == -1)
				{
					continue;
				}

				String message = new String(line.substring(index).trim());
				// Remove "*/" or whatever language specific comment ending from the end of the line!
				if (message.endsWith(commentEnding))
				{
					message = message.substring(0, message.length() - commentEnding.length()).trim();
				}
				// Start of comment + index of line + index of tag on line + initial offset
				int lineIndex = text.indexOf(line);
				int start = commentNode.getStartingOffset() + lineIndex + index + initialOffset;
				tasks.add(createTask(filePath, message, entry.getPriority(), getLineNumber(start, source), start, start
						+ message.length()));
			}
		}
		return tasks;
	}

	private String getText(String source, IParseNode commentNode)
	{
		return new String(source.substring(commentNode.getStartingOffset(), commentNode.getEndingOffset() + 1));
	}

	protected IProblem createTask(String sourcePath, String message, Integer priority, int lineNumber, int offset,
			int endOffset)
	{
		return new Problem(IMarker.SEVERITY_INFO, message, offset, endOffset - offset, lineNumber, sourcePath);
	}

	// Stuff from Validation...
	protected IProblem createWarning(String message, int lineNumber, int i, int j, String sourcePath)
	{
		return new Problem(IMarker.SEVERITY_WARNING, message, i, j, lineNumber, sourcePath);
	}

	protected IProblem createError(String message, int lineNumber, int i, int j, String sourcePath)
	{
		return new Problem(IMarker.SEVERITY_ERROR, message, i, j, lineNumber, sourcePath);
	}

	protected boolean hasErrorOrWarningOnLine(List<IProblem> items, int line)
	{
		if (items == null)
		{
			return false;
		}

		for (IProblem item : items)
		{
			if (item.getLineNumber() == line)
			{
				return true;
			}
		}

		return false;
	}
}
