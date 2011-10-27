/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.coffee.internal.index;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.coffee.CoffeeScriptEditorPlugin;
import com.aptana.editor.coffee.ICoffeeConstants;
import com.aptana.editor.coffee.parsing.ast.CoffeeCommentNode;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class CoffeeFileIndexingParticipant extends AbstractFileIndexingParticipant
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.AbstractFileIndexingParticipant#indexFileStore(com.aptana.index.core.Index,
	 * org.eclipse.core.filesystem.IFileStore, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void indexFileStore(final Index index, IFileStore store, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			if (store == null)
			{
				return;
			}

			sub.subTask(getIndexingMessage(index, store));

			removeTasks(store, sub.newChild(10));

			// grab the source of the file we're going to parse
			String source = IOUtil.read(store.openInputStream(EFS.NONE, sub.newChild(20)), getCharset(store));

			// minor optimization when creating a new empty file
			if (StringUtil.isEmpty(source))
			{
				return;
			}

			indexSource(index, source, store, sub.newChild(70));
		}
		catch (Throwable e)
		{
			IdeLog.logError(CoffeeScriptEditorPlugin.getDefault(), e);
		}
		finally
		{
			sub.done();
		}
	}

	public void indexSource(final Index index, String source, IFileStore store, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 70);
		try
		{
			IParseRootNode root = ParserPoolFactory.parse(ICoffeeConstants.CONTENT_TYPE_COFFEE, source, sub.newChild(60));
			sub.setWorkRemaining(10);
			detectTasks(store, source, root.getCommentNodes(), sub.newChild(10));
		}
		catch (Exception e)
		{
			IdeLog.logError(CoffeeScriptEditorPlugin.getDefault(), e);
		}
		finally
		{
			sub.done();
		}
	}

	private void detectTasks(IFileStore store, String source, IParseNode[] comments, IProgressMonitor monitor)
	{
		if (comments == null || comments.length == 0)
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, comments.length);
		for (IParseNode commentNode : comments)
		{
			if (commentNode instanceof CoffeeCommentNode)
			{
				processCommentNode(store, source, 0, (CoffeeCommentNode) commentNode);
			}
			sub.worked(1);
		}
		sub.done();
	}

	private void processCommentNode(IFileStore store, String source, int initialOffset, CoffeeCommentNode commentNode)
	{
		String text = commentNode.getText();
		if (!TaskTag.isCaseSensitive())
		{
			text = text.toLowerCase();
		}
		int offset = initialOffset;
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
				if (message.endsWith("###")) //$NON-NLS-1$
				{
					message = message.substring(0, message.length() - 2).trim();
				}
				int start = commentNode.getStartingOffset() + offset + index;
				createTask(store, message, entry.getPriority(), getLineNumber(start, source), start,
						start + message.length());
			}
			// FIXME This doesn't take the newline into account from split!
			offset += line.length();
		}
	}
}