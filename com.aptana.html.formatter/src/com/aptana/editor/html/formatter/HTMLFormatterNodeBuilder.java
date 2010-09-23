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
package com.aptana.editor.html.formatter;

import java.util.Arrays;
import java.util.HashSet;

import com.aptana.editor.html.formatter.nodes.FormatterBodyNode;
import com.aptana.editor.html.formatter.nodes.FormatterDefaultElementNode;
import com.aptana.editor.html.formatter.nodes.FormatterHeadNode;
import com.aptana.editor.html.formatter.nodes.FormatterHtmlNode;
import com.aptana.editor.html.formatter.nodes.FormatterLINode;
import com.aptana.editor.html.formatter.nodes.FormatterMetaNode;
import com.aptana.editor.html.formatter.nodes.FormatterTableNode;
import com.aptana.editor.html.formatter.nodes.FormatterTableTDNode;
import com.aptana.editor.html.formatter.nodes.FormatterTableTHNode;
import com.aptana.editor.html.formatter.nodes.FormatterTableTRNode;
import com.aptana.editor.html.formatter.nodes.FormatterULNode;
import com.aptana.editor.html.formatter.nodes.NonIndentingFormatterNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.parsing.ast.IParseNode;

/**
 * HTML formatter node builder.<br>
 * This builder generates the formatter nodes that will then be processed by the {@link HTMLFormatterNodeRewriter} to
 * produce the output for the code formatting process.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{

	/**
	 * Void Elements are elements that can <b>only</b> have a start tag.<br>
	 * 
	 * @see http://dev.w3.org/html5/spec/Overview.html#void-elements
	 */
	@SuppressWarnings("nls")
	protected static final HashSet<String> VOID_ELEMENTS = new HashSet<String>(Arrays.asList("area", "base", "br",
			"col", "command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track",
			"wbr"));
	private static final String INLINE_TAG_CLOSING = "/>"; //$NON-NLS-1$

	private FormatterDocument document;

	// TODO - Take a look at http://dev.w3.org/html5/spec/Overview.html#syntax-tag-omission
	// We might need to identify those elements and treat them a little differently in case they omit the ending tags.

	/**
	 * @param parseResult
	 * @param document
	 * @return
	 */
	public IFormatterContainerNode build(IParseNode parseResult, FormatterDocument document)
	{
		this.document = document;
		final IFormatterContainerNode rootNode = new FormatterBlockNode(document);
		start(rootNode);
		IParseNode[] children = parseResult.getChildren();
		addNodes(children, rootNode);
		checkedPop(rootNode, document.getLength());
		return rootNode;
	}

	/**
	 * @param children
	 * @param rootNode
	 */
	private void addNodes(IParseNode[] children, IFormatterContainerNode rootNode)
	{
		if (children == null || children.length == 0)
		{
			return;
		}
		for (IParseNode child : children)
		{
			addNode(child, rootNode);
		}
	}

	/**
	 * @param node
	 * @param rootNode
	 */
	private void addNode(IParseNode node, IFormatterContainerNode rootNode)
	{
		if (node instanceof HTMLElementNode)
		{
			HTMLElementNode elementNode = (HTMLElementNode) node;
			System.out.println(elementNode.getName() + "[" + elementNode.getStartingOffset() + ", "
					+ elementNode.getEndingOffset() + "]");
			// Check if we need to create a formatter node with a begin and end node, or just begin node.
			String name = elementNode.getName();
			if (VOID_ELEMENTS.contains(name.toLowerCase()) || !hasInlineClosingTag(elementNode))
			{
				FormatterBlockWithBeginNode formatterNode = new NonIndentingFormatterNode(document);
				formatterNode.setBegin(createTextNode(document, elementNode.getStartingOffset(), elementNode
						.getEndingOffset()));
				push(formatterNode);
				checkedPop(formatterNode, -1);
			}
			else
			{
				FormatterBlockWithBeginEndNode formatterNode = getFormatterNode(elementNode);
				push(formatterNode);
				// Recursively call this method till we are done with all the children under this node.
				addNodes(node.getChildren(), rootNode);
				checkedPop(formatterNode, -1);
			}
		}
		else if (node instanceof HTMLSpecialNode)
		{
			// it's an embedded JS or CSS
			HTMLSpecialNode specialNode = (HTMLSpecialNode) node;
			System.out.println(specialNode.getName() + "[" + specialNode.getStartingOffset() + ", "
					+ specialNode.getEndingOffset() + "]");
			// specialNode.get
		}
	}

	/**
	 * @param elementNode
	 * @return
	 */
	private boolean hasInlineClosingTag(HTMLElementNode elementNode)
	{
		int startingOffset = elementNode.getStartingOffset();
		int endingOffset = elementNode.getEndingOffset();
		if (endingOffset - startingOffset > 1 && document.getLength() >= endingOffset + 1)
		{
			String text = document.get(endingOffset - 1, endingOffset + 1);
			if (INLINE_TAG_CLOSING.equals(text))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine the type of the node and return a formatter node that should represent it while rewriting the doc.
	 * 
	 * @param node
	 * @return FormatterBlockWithBeginEndNode sub-classing instance
	 */
	private FormatterBlockWithBeginEndNode getFormatterNode(HTMLElementNode node)
	{
		FormatterBlockWithBeginEndNode formatterNode;
		String type = node.getName().toLowerCase();
		if ("html".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterHtmlNode(document);
		}
		else if ("head".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterHeadNode(document);
		}
		else if ("meta".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterMetaNode(document);
		}
		else if ("body".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterBodyNode(document);
		}
		else if ("table".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterTableNode(document);
		}
		else if ("tr".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterTableTRNode(document);
		}
		else if ("th".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterTableTHNode(document);
		}
		else if ("td".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterTableTDNode(document);
		}
		else if ("ul".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterULNode(document);
		}
		else if ("li".equals(type)) //$NON-NLS-1$
		{
			formatterNode = new FormatterLINode(document);
		}
		else
		{
			formatterNode = new FormatterDefaultElementNode(document);
		}
		int startingOffset = node.getStartingOffset();
		int endingOffset = node.getEndingOffset();
		formatterNode.setBegin(createTextNode(document, startingOffset, startingOffset + type.length()));
		formatterNode.setEnd(createTextNode(document, endingOffset - type.length(), endingOffset));
		return formatterNode;
	}
}
