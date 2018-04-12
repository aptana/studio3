/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.formatter;

import com.aptana.editor.xml.formatter.nodes.FormatterXMLContentNode;
import com.aptana.editor.xml.formatter.nodes.FormatterXMLElementNode;
import com.aptana.editor.xml.formatter.nodes.FormatterXMLVoidElementNode;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.FormatterTextNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.xml.core.parsing.ast.XMLCDATANode;
import com.aptana.xml.core.parsing.ast.XMLElementNode;
import com.aptana.xml.core.parsing.ast.XMLNode;
import com.aptana.xml.core.parsing.ast.XMLNodeType;

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
		// Collect Off/On tags
		if (parseResult instanceof IParseRootNode)
		{
			setOffOnRegions(resolveOffOnRegions((IParseRootNode) parseResult, document,
					XMLFormatterConstants.FORMATTER_OFF_ON_ENABLED, XMLFormatterConstants.FORMATTER_OFF,
					XMLFormatterConstants.FORMATTER_ON));
		}
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

		boolean hasNonTextChildren = false;
		IParseNode[] children = node.getChildren();
		for (IParseNode child : children)
		{
			if (!(child instanceof XMLCDATANode))
			{
				hasNonTextChildren = true;
			}
		}

		FormatterBlockWithBeginEndNode formatterNode = new FormatterXMLElementNode(document, type, hasNonTextChildren);
		formatterNode.setBegin(createTextNode(document, node.getStartingOffset(), node.getStartTagEndOffset() + 1));
		push(formatterNode);

		// create a content node so we can move it to a single line for exclusions (only when it doesn't have child
		// nodes and there is content)
		int endOffset = getOpenTagOffset(node.getEndingOffset(), document);
		int previousCloseTagOffset = getPreviousCloseTagOffset(endOffset, document);

		if (node.getChildCount() <= 1) // single child means text content, zero means no text
		{

			int textStartOffset = getBeginWithoutWhiteSpaces(previousCloseTagOffset + 1, document);
			int textEndOffset = getEndWithoutWhiteSpaces(endOffset - 1, document);

			if (textStartOffset > textEndOffset)
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

		if (node.getChildCount() <= 1) // single child means text content, zero means no text
		{
			checkedPop(formatterNode, -1);
		}
		else
		{
			checkedPop(formatterNode, endOffset);
		}

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
