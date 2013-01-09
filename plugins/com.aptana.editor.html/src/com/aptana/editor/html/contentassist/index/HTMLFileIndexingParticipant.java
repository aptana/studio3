/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist.index;

import java.net.URI;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import com.aptana.core.IFilter;
import com.aptana.core.util.StringUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.css.core.index.CSSFileIndexingParticipant;
import com.aptana.css.core.index.ICSSIndexConstants;
import com.aptana.editor.common.resolver.IPathResolver;
import com.aptana.editor.common.resolver.URIResolver;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.index.core.AbstractFileIndexingParticipant;
import com.aptana.index.core.Index;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.index.JSFileIndexingParticipant;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.util.ParseUtil;

public class HTMLFileIndexingParticipant extends AbstractFileIndexingParticipant
{
	private static final String ELEMENT_LINK = "link"; //$NON-NLS-1$
	private static final String ELEMENT_SCRIPT = "script"; //$NON-NLS-1$
	private static final String ATTRIBUTE_HREF = "href"; //$NON-NLS-1$
	private static final String ATTRIBUTE_SRC = "src"; //$NON-NLS-1$

	public void index(BuildContext context, Index index, IProgressMonitor monitor) throws CoreException
	{
		if (context == null || index == null)
		{
			return;
		}

		SubMonitor sub = SubMonitor.convert(monitor, 100);
		sub.subTask(getIndexingMessage(index, context.getURI()));
		IParseNode ast = null;
		try
		{
			ast = context.getAST();
		}
		catch (CoreException e)
		{
			// ignores the parser exception
		}
		if (ast != null)
		{
			walkAST(context, index, ast, monitor);
		}
		sub.done();
	}

	/**
	 * processHTMLElementNode
	 * 
	 * @param index
	 * @param file
	 * @param element
	 */
	private void processHTMLElementNode(Index index, URI uri, HTMLElementNode element)
	{
		String cssClass = element.getCSSClass();
		if (!StringUtil.isEmpty(cssClass))
		{
			StringTokenizer tokenizer = new StringTokenizer(cssClass);

			while (tokenizer.hasMoreTokens())
			{
				addIndex(index, uri, ICSSIndexConstants.CLASS, tokenizer.nextToken());
			}
		}

		String id = element.getID();
		if (!StringUtil.isEmpty(id))
		{
			addIndex(index, uri, ICSSIndexConstants.IDENTIFIER, id);
		}

		if (element.getName().equalsIgnoreCase(ELEMENT_LINK))
		{
			String cssLink = element.getAttributeValue(ATTRIBUTE_HREF);
			if (!StringUtil.isEmpty(cssLink))
			{
				// TODO Fire off a thread to run this, since it could be slow or hit the network
				IPathResolver resolver = new URIResolver(uri);
				URI resolved = resolver.resolveURI(cssLink);

				if (resolved != null)
				{
					addIndex(index, uri, IHTMLIndexConstants.RESOURCE_CSS, resolved.toString());
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
	private void processHTMLSpecialNode(Index index, BuildContext context, HTMLSpecialNode htmlSpecialNode)
	{
		IParseNode child = htmlSpecialNode.getChild(0);

		if (child != null)
		{
			String language = child.getLanguage();

			if (ICSSConstants.CONTENT_TYPE_CSS.equals(language))
			{
				// process inline code
				CSSFileIndexingParticipant cssIndex = createCSSIndexer();
				cssIndex.walkNode(index, context.getURI(), child);
			}
		}

		if (htmlSpecialNode.getName().equalsIgnoreCase(ELEMENT_SCRIPT))
		{
			String jsSource = htmlSpecialNode.getAttributeValue(ATTRIBUTE_SRC);

			if (jsSource != null)
			{
				IPathResolver resolver = new URIResolver(context.getURI());
				URI resolved = resolver.resolveURI(jsSource);

				if (resolved != null)
				{
					addIndex(index, context.getURI(), IHTMLIndexConstants.RESOURCE_JS, resolved.toString());
				}
			}
			else if (child != null && IJSConstants.CONTENT_TYPE_JS.equals(child.getLanguage()))
			{
				// process inline code
				JSFileIndexingParticipant jsIndex = createJSIndexer();
				jsIndex.processParseResults(context, index, child, new NullProgressMonitor());
			}
		}
	}

	protected JSFileIndexingParticipant createJSIndexer()
	{
		return new JSFileIndexingParticipant();
	}

	protected CSSFileIndexingParticipant createCSSIndexer()
	{
		return new CSSFileIndexingParticipant();
	}

	/**
	 * processNode
	 * 
	 * @param index
	 * @param file
	 * @param current
	 */
	protected void processNode(Index index, BuildContext context, URI uri, IParseNode current)
	{
		if (current instanceof HTMLSpecialNode)
		{
			processHTMLSpecialNode(index, context, (HTMLSpecialNode) current);
		}
		else if (current instanceof HTMLElementNode)
		{
			processHTMLElementNode(index, uri, (HTMLElementNode) current);
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
	public void walkAST(final BuildContext context, final Index index, IParseNode parent, IProgressMonitor monitor)
	{
		if (context == null || index == null || parent == null)
		{
			return;
		}
		final URI uri = context.getURI();
		ParseUtil.treeApply(parent, new IFilter<IParseNode>()
		{

			public boolean include(IParseNode item)
			{
				processNode(index, context, uri, item);
				return true;
			}
		});
	}
}
