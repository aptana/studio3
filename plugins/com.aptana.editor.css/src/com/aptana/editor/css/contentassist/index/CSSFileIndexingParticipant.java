/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.index;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.CSSPlugin;
import com.aptana.editor.css.ICSSConstants;
import com.aptana.editor.css.parsing.ast.CSSAttributeSelectorNode;
import com.aptana.editor.css.parsing.ast.CSSCommentNode;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.editor.css.parsing.ast.CSSTermNode;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;

public class CSSFileIndexingParticipant extends AbstractFileIndexingParticipant
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.AbstractFileIndexingParticipant#indexFileStore(com.aptana.index.core.Index,
	 * org.eclipse.core.filesystem.IFileStore, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void indexFileStore(Index index, IFileStore file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		try
		{
			if (file != null)
			{
				sub.subTask(index.getRelativeDocumentPath(file.toURI()).toString());

				removeTasks(file, sub.newChild(10));

				// grab the source of the file we're going to parse
				String fileContents = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(20)), getCharset(file));

				// minor optimization when creating a new empty file
				if (fileContents != null && fileContents.trim().length() > 0)
				{
					IParseNode ast = null;
					try
					{
						ast = ParserPoolFactory.parse(ICSSConstants.CONTENT_TYPE_CSS, fileContents, sub.newChild(50));
					}
					catch (Exception e)
					{
						// ignores parser exception
					}
					if (ast != null)
					{
						this.processParseResults(file, index, fileContents, ast, sub.newChild(20));
					}
				}
			}
		}
		catch (Throwable e)
		{
			IdeLog.logError(CSSPlugin.getDefault(), e);
		}
		finally
		{
			sub.done();
		}
	}

	public void processParseResults(IFileStore file, Index index, String source, IParseNode ast,
			IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		walkNode(index, file, ast);
		sub.worked(70);
		if (ast instanceof IParseRootNode)
		{
			processComments(file, source, ast, sub.newChild(30));
		}
		sub.done();
	}

	private void processComments(IFileStore file, String source, IParseNode parseResult, IProgressMonitor monitor)
	{
		if (parseResult instanceof IParseRootNode)
		{
			IParseRootNode rootNode = (IParseRootNode) parseResult;
			IParseNode[] comments = rootNode.getCommentNodes();
			SubMonitor sub = SubMonitor.convert(monitor, comments.length);
			for (IParseNode commentNode : comments)
			{
				if (commentNode instanceof CSSCommentNode)
				{
					processCommentNode(file, source, (CSSCommentNode) commentNode);
				}
				sub.worked(1);
			}
			sub.done();
		}
	}

	private void walkNode(Index index, IFileStore file, IParseNode parent)
	{
		if (parent == null)
			return;

		if (parent instanceof CSSAttributeSelectorNode)
		{
			CSSAttributeSelectorNode cssAttributeSelectorNode = (CSSAttributeSelectorNode) parent;
			String text = cssAttributeSelectorNode.getText();
			if (!StringUtil.isEmpty(text) && text.charAt(0) == '.')
			{
				addIndex(index, file, ICSSIndexConstants.CLASS, text.substring(1));
			}
			else if (!StringUtil.isEmpty(text) && text.charAt(0) == '#')
			{
				addIndex(index, file, ICSSIndexConstants.IDENTIFIER, text.substring(1));
			}
		}

		if (parent instanceof CSSTermNode)
		{
			CSSTermNode term = (CSSTermNode) parent;
			String value = term.getText();
			if (isColor(value))
			{
				addIndex(index, file, ICSSIndexConstants.COLOR, CSSColors.to6CharHexWithLeadingHash(value.trim()));
			}
		}

		if (parent instanceof CSSRuleNode)
		{
			CSSRuleNode cssRuleNode = (CSSRuleNode) parent;
			for (IParseNode child : cssRuleNode.getSelectors())
			{
				walkNode(index, file, child);
			}
			for (IParseNode child : cssRuleNode.getDeclarations())
			{
				walkNode(index, file, child);
			}
		}
		else
		{
			for (IParseNode child : parent.getChildren())
			{
				walkNode(index, file, child);
			}
		}

	}

	private void processCommentNode(IFileStore store, String source, CSSCommentNode commentNode)
	{
		String text = commentNode.getText();
		if (!TaskTag.isCaseSensitive())
		{
			text = text.toLowerCase();
		}
		int lastOffset = 0;
		String[] lines = StringUtil.LINE_SPLITTER.split(text);
		for (String line : lines)
		{
			int offset = text.indexOf(line, lastOffset);

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

				String message = line.substring(index).trim();
				// Remove "*/" from the end of the line!
				if (message.endsWith("*/")) //$NON-NLS-1$
				{
					message = message.substring(0, message.length() - 2).trim();
				}
				int start = commentNode.getStartingOffset() + offset + index;
				createTask(store, message, entry.getPriority(), getLineNumber(start, source), start,
						start + message.length());
			}

			lastOffset = offset;
		}
	}

	private static boolean isColor(String value)
	{
		if (StringUtil.isEmpty(value))
			return false;
		if (CSSColors.namedColorExists(value))
			return true;
		if (value.charAt(0) == '#' && (value.length() == 4 || value.length() == 7))
			return true; // FIXME Check to make sure it's hex values!
		return false;
	}
}
