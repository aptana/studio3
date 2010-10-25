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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.editor.common.resolver.URIResolver;
import com.aptana.editor.css.contentassist.index.CSSFileIndexingParticipant;
import com.aptana.editor.css.contentassist.index.CSSIndexConstants;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.Activator;
import com.aptana.editor.html.parsing.HTMLParseState;
import com.aptana.editor.html.parsing.IHTMLParserConstants;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.js.contentassist.index.JSFileIndexingParticipant;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.index.core.IFileStoreIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;

public class HTMLFileIndexingParticipant implements IFileStoreIndexingParticipant
{
	private static final String ELEMENT_LINK = "link"; //$NON-NLS-1$
	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

	/**
	 * addIndex
	 * 
	 * @param index
	 * @param file
	 * @param category
	 * @param word
	 */
	private static void addIndex(Index index, IFileStore file, String category, String word)
	{
		index.addEntry(category, word, file.toURI());
	}

	/**
	 * processHTMLElementNode
	 * 
	 * @param index
	 * @param file
	 * @param element
	 */
	private static void processHTMLElementNode(Index index, IFileStore file, HTMLElementNode element)
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
	private static void processHTMLSpecialNode(Index index, IFileStore file, HTMLSpecialNode htmlSpecialNode)
	{
		IParseNode child = htmlSpecialNode.getChild(0);

		if (child != null)
		{
			String language = child.getLanguage();

			if (ICSSParserConstants.LANGUAGE.equals(language))
			{
				CSSFileIndexingParticipant.walkNode(index, file, child);
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
				
				jsIndex.processParseResults(index, child, file.toURI());
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
	private static void processNode(Index index, IFileStore file, IParseNode current)
	{
		if (current instanceof HTMLSpecialNode)
		{
			processHTMLSpecialNode(index, file, (HTMLSpecialNode) current);
		}
		else if (current instanceof HTMLElementNode)
		{
			processHTMLElementNode(index, file, (HTMLElementNode) current);
		}
	}

	/**
	 * walkAST
	 * 
	 * @param index
	 * @param file
	 * @param parent
	 */
	public static void walkAST(Index index, IFileStore file, IParseNode parent)
	{
		if (parent != null)
		{
			Queue<IParseNode> queue = new LinkedList<IParseNode>();

			// prime queue
			queue.offer(parent);

			while (queue.isEmpty() == false)
			{
				IParseNode current = queue.poll();

				processNode(index, file, current);

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
		if (file == null)
		{
			return;
		}
		try
		{
			sub.subTask(file.getName());

			IParserPool pool = ParserPoolFactory.getInstance().getParserPool(IHTMLParserConstants.LANGUAGE);
			if (pool != null)
			{
				String fileContents = IOUtil.read(file.openInputStream(EFS.NONE, sub.newChild(20)));
				if (fileContents != null && fileContents.trim().length() > 0)
				{
					IParser htmlParser = pool.checkOut();
					if (htmlParser != null)
					{

						HTMLParseState parseState = new HTMLParseState();
						parseState.setEditState(fileContents, "", 0, 0); //$NON-NLS-1$
						IParseNode parseNode = htmlParser.parse(parseState);
						pool.checkIn(htmlParser);
						sub.worked(50);
						walkAST(index, file, parseNode);
					}
				}
			}
		}
		catch (beaver.Parser.Exception e)
		{
			// just like in FileServer ... "not logging the parsing error here since
			// the source could be in an intermediate state of being edited by the user"
		}
		catch (Throwable e)
		{
			Activator.logError(MessageFormat.format(Messages.HTMLFileIndexingParticipant_Error_During_Indexing, file.getName()), e);
		}
		finally
		{
			sub.done();
		}
	}
}
