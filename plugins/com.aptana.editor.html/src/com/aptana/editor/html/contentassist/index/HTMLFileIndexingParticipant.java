/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.index;

import java.net.URI;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.editor.common.resolver.URIResolver;
import com.aptana.editor.common.tasks.TaskTag;
import com.aptana.editor.css.contentassist.index.CSSFileIndexingParticipant;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.IHTMLParserConstants;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

public class HTMLFileIndexingParticipant extends AbstractFileIndexingParticipant
{
	private static final String ELEMENT_LINK = "link"; //$NON-NLS-1$
	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

	/**
	 * processHTMLElementNode
	 * 
	 * @param index
	 * @param file
	 * @param element
	 */
	private void processHTMLElementNode(Index index, IFileStore file, HTMLElementNode element)
	{
		String cssClass = element.getCSSClass();

		if (cssClass != null && cssClass.trim().length() > 0)
		{
			StringTokenizer tokenizer = new StringTokenizer(cssClass);

			while (tokenizer.hasMoreTokens())
			{
				addIndex(index, file, CSSIndexConstants.CLASS, tokenizer.nextToken());
			}
		}

		String id = element.getID();

		if (id != null && id.trim().length() > 0)
		{
			addIndex(index, file, CSSIndexConstants.IDENTIFIER, id);
		}

		if (element.getName().equalsIgnoreCase(ELEMENT_LINK))
		{
			String cssLink = element.getAttributeValue(ATTRIBUTE_HREF);

			if (cssLink != null)
			{
				IPathResolver resolver = new URIResolver(file.toURI());
				URI resolved = resolver.resolveURI(cssLink);

				if (resolved != null)
				{
					addIndex(index, file, HTMLIndexConstants.RESOURCE_CSS, resolved.toString());
				}
			}
		}
	}

	/**
	 * processHTMLSpecialNode
	 * 
	 * @param index
	 * @param file
	 * @param htmlSpecialNode
	 */
	private void processHTMLSpecialNode(Index index, IFileStore file, String source, HTMLSpecialNode htmlSpecialNode)
	{
		IParseNode child = htmlSpecialNode.getChild(0);

		if (child != null)
		{
			String language = child.getLanguage();

			if (ICSSParserConstants.LANGUAGE.equals(language))
			{
				// process inline code
				CSSFileIndexingParticipant cssIndex = new CSSFileIndexingParticipant();
				cssIndex.processParseResults(file, index, child, new NullProgressMonitor());
			}
		}

		if (htmlSpecialNode.getName().equalsIgnoreCase(ELEMENT_SCRIPT))
		{
			String jsSource = htmlSpecialNode.getAttributeValue(ATTRIBUTE_SRC);

			if (jsSource != null)
			{
				IPathResolver resolver = new URIResolver(file.toURI());
				URI resolved = resolver.resolveURI(jsSource);

				if (resolved != null)
				{
					addIndex(index, file, HTMLIndexConstants.RESOURCE_JS, resolved.toString());
				}
			}
			else if (child != null && IJSParserConstants.LANGUAGE.equals(child.getLanguage()))
			{
				// process inline code
				JSFileIndexingParticipant jsIndex = new JSFileIndexingParticipant();
				jsIndex.processParseResults(file, source, index, child, new NullProgressMonitor());
			}
		}
	}

	/**
	 * processNode
	 * 
	 * @param index
	 * @param file
	 * @param current
	 */
	protected void processNode(Index index, IFileStore file, String source, IParseNode current)
	{
		if (current instanceof HTMLSpecialNode)
		{
			processHTMLSpecialNode(index, file, source, (HTMLSpecialNode) current);
		}
		else if (current instanceof HTMLElementNode)
		{
			processHTMLElementNode(index, file, (HTMLElementNode) current);
		}
		else if (current instanceof HTMLCommentNode)
		{
			processHTMLCommentNode(file, (HTMLCommentNode) current);
		}
	}

	private void processHTMLCommentNode(IFileStore store, HTMLCommentNode commentNode)
	{
		String text = commentNode.getText();
		if (!TaskTag.isCaseSensitive())
		{
			text = text.toLowerCase();
		}
		int offset = 0;
		String[] lines = text.split("\r\n|\r|\n"); //$NON-NLS-1$
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

				String message = line.substring(index).trim();
				// Remove "-->" from the end of the line!
				if (message.endsWith("-->")) //$NON-NLS-1$
				{
					message = message.substring(0, message.length() - 3).trim();
				}
				int start = commentNode.getStartingOffset() + offset + index;
				createTask(store, message, entry.getPriority(), -1, start, start + message.length());
			}
			// FIXME This doesn't take the newline into account from split!
			offset += line.length();
		}
	}

	/**
	 * walkAST
	 * 
	 * @param index
	 * @param file
	 * @param parent
	 * @param monitor
	 */
	public void walkAST(Index index, IFileStore file, String source, IParseNode parent, IProgressMonitor monitor)
	{
		if (parent != null)
		{
			Queue<IParseNode> queue = new LinkedList<IParseNode>();

			// prime queue
			queue.offer(parent);

			while (queue.isEmpty() == false)
			{
				IParseNode current = queue.poll();

				processNode(index, file, source, current);

				for (IParseNode child : current)
				{
					queue.offer(child);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.index.core.IFileStoreIndexingParticipant#index(java.util.Set, com.aptana.index.core.Index,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void index(Set<IFileStore> files, Index index, IProgressMonitor monitor) throws CoreException
	{
		SubMonitor sub = SubMonitor.convert(monitor, files.size() * 100);
		for (IFileStore file : files)
		{
			if (sub.isCanceled())
			{
				throw new CoreException(Status.CANCEL_STATUS);
			}
			Thread.yield(); // be nice to other threads, let them get in before each file...
			indexFileStore(index, file, sub.newChild(100));
		}
		sub.done();
	}

	/**
	 * indexFileStore
	 * 
	 * @param index
	 * @param file
	 * @param monitor
	 */
	private void indexFileStore(Index index, IFileStore file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);

		try
		{
			if (file != null)
			{
				sub.subTask(getIndexingMessage(index, file));

				removeTasks(file, sub.newChild(10));

				// grab the source of the file we're going to parse
				String fileContents = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(20)));

				// minor optimization when creating a new empty file
				if (fileContents != null && fileContents.trim().length() > 0)
				{
					HTMLParseState parseState = new HTMLParseState();
					parseState.setEditState(fileContents, null, 0, 0);

					IParseNode parseNode = ParserPoolFactory.parse(IHTMLParserConstants.LANGUAGE, parseState);
					sub.worked(50);

					walkAST(index, file, fileContents, parseNode, sub.newChild(20));
				}
			}
		}
		catch (Throwable e)
		{
			HTMLPlugin
					.logError(
							MessageFormat.format(Messages.HTMLFileIndexingParticipant_Error_During_Indexing,
									file.getName()), e);
		}
		finally
		{
			sub.done();
		}
	}
}
