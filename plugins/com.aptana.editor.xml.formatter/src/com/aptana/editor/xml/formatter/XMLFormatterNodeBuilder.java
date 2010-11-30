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
package com.aptana.editor.xml.formatter;

import com.aptana.editor.xml.formatter.nodes.FormatterXMLContentNode;
import com.aptana.editor.xml.formatter.nodes.FormatterXMLElementNode;
import com.aptana.editor.xml.formatter.nodes.FormatterXMLVoidElementNode;
import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.editor.xml.parsing.ast.XMLNode;
import com.aptana.editor.xml.parsing.ast.XMLNodeType;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.FormatterTextNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.lexer.IRange;

/**
 * XML formatter node builder.<br>
 * This builder generates the formatter nodes that will then be processed by the {@link XMLFormatterNodeRewriter} to
 * produce the output for the code formatting process.
 */
public class XMLFormatterNodeBuilder extends AbstractFormatterNodeBuilder
{

	private FormatterDocument document;

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
		addNodes(children);
		checkedPop(rootNode, document.getLength());
		return rootNode;
	}

	/**
	 * @param children
	 * @param rootNode
	 */
	private void addNodes(IParseNode[] children)
	{
		if (children == null || children.length == 0)
		{
			return;
		}
		for (IParseNode child : children)
		{
			addNode(child);
		}
	}

	/**
	 * @param node
	 * @param rootNode
	 */
	private void addNode(IParseNode node)
	{
		XMLNode xmlNode = (XMLNode) node;

		if (xmlNode.getNodeType() == XMLNodeType.ELEMENT.getIndex())
		{
			if (((XMLElementNode) xmlNode).isSelfClosing())
			{
				pushFormatterVoidNode((XMLElementNode) xmlNode);
			}
			else
			{
				pushFormatterElementNode((XMLElementNode) xmlNode);
			}
		}
		else if (xmlNode.getNodeType() == XMLNodeType.DECLARATION.getIndex())
		{
			// TODO account for declaration nodes when they are recognized in AST
		}
		else
		{
			// TODO account for comment/error nodes when they are recognized in AST
		}

	}

	private FormatterBlockWithBeginNode pushFormatterVoidNode(XMLElementNode node)
	{

		String type = node.getName().toLowerCase();
		FormatterBlockWithBeginNode formatterNode = new FormatterXMLVoidElementNode(document, type);
		formatterNode.setBegin(createTextNode(document, node.getStartingOffset(), node.getEndingOffset() + 1));
		push(formatterNode);
		checkedPop(formatterNode, -1);
		return formatterNode;

	}

	/**
	 * Accepts an XMLElementNode creates a corresponding formatter node for it. This also traverses the node's children
	 * and creates formatters nodes for them.
	 * 
	 * @param node
	 */
	private FormatterBlockWithBeginEndNode pushFormatterElementNode(XMLElementNode node)
	{
		String type = node.getName().toLowerCase();
		FormatterBlockWithBeginEndNode formatterNode;
		IRange beginNodeRange = node.getNameNode().getNameRange();
		int endOffset = getOpenTagOffset(node.getEndingOffset(), document);

		formatterNode = new FormatterXMLElementNode(document, type, node.hasChildren());
		formatterNode.setBegin(createTextNode(document, beginNodeRange.getStartingOffset(),
				beginNodeRange.getEndingOffset() + 1));
		push(formatterNode);

		// create a content node so we can move it to a single line for exclusions (only when it doesn't have child
		// nodes and there is content)

		int previousCloseTagOffset = getPreviousCloseTagOffset(endOffset, document);

		if (node.getChildCount() == 0)
		{

			int textStartOffset = getBeginWithoutWhiteSpaces(previousCloseTagOffset + 1, document);
			int textEndOffset = getEndWithoutWhiteSpaces(endOffset - 1, document);

			if (textStartOffset >= textEndOffset)
			{
				if (textStartOffset == endOffset)
				{
					// Set offset to create a blank text node when there is nothing so we can use
					// shouldConsumePreviousWhiteSpaces to remove new line
					textEndOffset = textStartOffset - 1;
				}
				else
				{
					// Case where nodes have only contain white spaces
					textStartOffset = previousCloseTagOffset + 1;
					textEndOffset = endOffset - 1;
				}
			}

			FormatterTextNode contentFormatterNode = new FormatterXMLContentNode(document, type, textStartOffset,
					textEndOffset + 1);
			formatterNode.addChild(contentFormatterNode);

		}

		// Recursively call this method till we are done with all the children under this node.
		addNodes(node.getChildren());

		checkedPop(formatterNode, -1);
		formatterNode.setEnd(createTextNode(document, endOffset, node.getEndingOffset() + 1));
		return formatterNode;
	}

	private int getOpenTagOffset(int offset, FormatterDocument document)
	{
		while (offset > 0)
		{
			if (document.charAt(offset) == '<')
			{
				break;
			}
			offset--;
		}
		return offset;
	}

	private int getPreviousCloseTagOffset(int offset, FormatterDocument document)
	{
		while (offset > 0)
		{
			if (document.charAt(offset) == '>')
			{
				break;
			}
			offset--;
		}
		return offset;
	}

	private int getBeginWithoutWhiteSpaces(int offset, FormatterDocument document)
	{
		int length = document.getLength();
		while (offset < length)
		{
			if (!Character.isWhitespace(document.charAt(offset)))
			{
				break;
			}
			offset++;
		}
		return offset;
	}

	private int getEndWithoutWhiteSpaces(int offset, FormatterDocument document)
	{
		while (offset > 0)
		{
			if (!Character.isWhitespace(document.charAt(offset)))
			{
				break;
			}
			offset--;
		}
		return offset;
	}
}
