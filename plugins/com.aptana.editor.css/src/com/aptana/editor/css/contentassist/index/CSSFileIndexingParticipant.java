/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist.index;

import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.tasks.TaskTag;
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.parsing.ICSSParserConstants;
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

	private void indexFileStore(Index index, IFileStore file, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		
		try
		{
			if (file != null)
			{
				sub.subTask(file.getName());
	
				removeTasks(file, sub.newChild(10));
	
				// grab the source of the file we're going to parse
				String fileContents = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(20)));
				
				// minor optimization when creating a new empty file
				if (fileContents != null && fileContents.trim().length() > 0)
				{
					IParseNode ast = ParserPoolFactory.parse(ICSSParserConstants.LANGUAGE, fileContents);
					sub.worked(50);
					
					if (ast != null)
					{
						this.processParseResults(file, index, ast, sub.newChild(20));
					}
				}
			}
		}
		catch (Throwable e)
		{
			Activator.logError(e.getMessage(), e);
		}
		finally
		{
			sub.done();
		}
	}

	public void processParseResults(IFileStore file, Index index, IParseNode ast, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		walkNode(index, file, ast);
		sub.worked(70);
		if (ast instanceof IParseRootNode)
		{
			processComments(file, ast, sub.newChild(30));
		}
		sub.done();
	}

	private void processComments(IFileStore file, IParseNode parseResult, IProgressMonitor monitor)
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
					processCommentNode(file, rootNode.getStartingOffset(), (CSSCommentNode) commentNode);
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
			if (text != null && text.startsWith(".")) //$NON-NLS-1$
			{
				addIndex(index, file, CSSIndexConstants.CLASS, text.substring(1));
			}
			else if (text != null && text.startsWith("#")) //$NON-NLS-1$
			{
				addIndex(index, file, CSSIndexConstants.IDENTIFIER, text.substring(1));
			}
		}

		if (parent instanceof CSSTermNode)
		{
			CSSTermNode term = (CSSTermNode) parent;
			String value = term.getText();
			if (isColor(value))
			{
				addIndex(index, file, CSSIndexConstants.COLOR, CSSColors.to6CharHexWithLeadingHash(value.trim()));
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

	private void processCommentNode(IFileStore store, int initialOffset, CSSCommentNode commentNode)
	{
		String text = commentNode.getText();
		if (!TaskTag.isCaseSensitive())
		{
			text = text.toLowerCase();
		}
		int offset = initialOffset;
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
				// Remove "*/" from the end of the line!
				if (message.endsWith("*/")) //$NON-NLS-1$
				{
					message = message.substring(0, message.length() - 2).trim();
				}
				int start = commentNode.getStartingOffset() + offset + index;
				createTask(store, message, entry.getPriority(), -1, start, start + message.length());
			}
			// FIXME This doesn't take the newline into account from split!
			offset += line.length();
		}
	}

	private static boolean isColor(String value)
	{
		if (value == null || value.trim().length() == 0)
			return false;
		if (CSSColors.namedColorExists(value))
			return true;
		if (value.startsWith("#") && (value.length() == 4 || value.length() == 7)) //$NON-NLS-1$
			return true; // FIXME Check to make sure it's hex values!
		return false;
	}
}
