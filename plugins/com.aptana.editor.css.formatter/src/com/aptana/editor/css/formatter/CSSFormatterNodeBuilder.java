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
package com.aptana.editor.css.formatter;

import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSBlockNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSDeclarationNode;
import com.aptana.editor.css.formatter.nodes.FormatterCSSSelectorNode;
import com.aptana.editor.css.parsing.ast.*;

/**
 * CSS formatter node builder.<br>
 * This builder generates the formatter nodes that will then be processed by the {@link CSSFormatterNodeRewriter} to
 * produce the output for the code formatting process.
 */
public class CSSFormatterNodeBuilder extends AbstractFormatterNodeBuilder
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
		CSSNode cssNode = (CSSNode) node;

		if (cssNode.getNodeType() == CSSNodeTypes.RULE)
		{
			pushFormatterNode(cssNode);
		}

	}

	/**
	 * Accepts a CSSRuleNode and breaks down the node into different formatter nodes which should represent it while
	 * rewriting the doc.<br>
	 * The CSSRuleNode will be broken down into several nodes of type FormatterCSSRuleNode, FormatterCSSBlockNode, and
	 * FormatterCSSDeclarationNode (if declarations are present).
	 * 
	 * @param node
	 * @return FormatterBlockWithBeginEndNode sub-classing instance
	 */
	private void pushFormatterNode(CSSNode node)
	{
		CSSRuleNode ruleNode = (CSSRuleNode) node;

		CSSSelectorNode[] selectors = ruleNode.getSelectors();
		CSSDeclarationNode[] declarations = ruleNode.getDeclarations();
		int blockStartOffset = getBlockStartOffset(selectors[selectors.length - 1].getEndingOffset() + 1, document);

		for (int i = 0; i < selectors.length; i++)
		{
			CSSSelectorNode selectorNode = selectors[i];
			FormatterBlockWithBeginNode formatterSelectorNode = new FormatterCSSSelectorNode(document, i == 0);
			formatterSelectorNode.setBegin(createTextNode(document,
					getSelectorNodeBegin(selectorNode.getStartingOffset(), document),
					getSelectorNodeEnd(selectorNode.getEndingOffset() + 1, document) + 1));
			push(formatterSelectorNode);

			checkedPop(formatterSelectorNode, -1);
		}

		FormatterBlockWithBeginEndNode formatterBlockNode = new FormatterCSSBlockNode(document);
		formatterBlockNode.setBegin(createTextNode(document, blockStartOffset, blockStartOffset + 1));
		formatterBlockNode.setEnd(createTextNode(document, ruleNode.getEndingOffset(), ruleNode.getEndingOffset() + 1));
		push(formatterBlockNode);

		if (declarations != null && declarations.length != 0)
		{
			formatterBlockNode.addChild(createTextNode(document, blockStartOffset + 1,
					declarations[0].getStartingOffset()));
		}
		else
		{
			formatterBlockNode.addChild(createTextNode(document, blockStartOffset + 1, ruleNode.getEndingOffset()));
		}

		for (int i = 0; i < declarations.length; ++i)
		{

			CSSDeclarationNode declarationNode = declarations[i];
			FormatterBlockWithBeginNode formatterDeclarationNode = new FormatterCSSDeclarationNode(document);

			formatterDeclarationNode.setBegin(createTextNode(document, declarationNode.getStartingOffset(),
					declarationNode.getEndingOffset() + 1));
			push(formatterDeclarationNode);

			checkedPop(formatterDeclarationNode, -1);

			if (i + 1 < declarations.length)
			{
				formatterBlockNode.addChild(createTextNode(document, declarations[i].getEndingOffset() + 1,
						declarations[i + 1].getStartingOffset()));
			}
			else
			{
				formatterBlockNode.addChild(createTextNode(document, declarations[i].getEndingOffset() + 1,
						ruleNode.getEndingOffset()));
			}
		}

		checkedPop(formatterBlockNode, -1);

	}

	private int getBlockStartOffset(int offset, FormatterDocument document)
	{
		int length = document.getLength();
		while (offset < length)
		{
			if (document.charAt(offset) == '{')
			{
				break;
			}
			offset++;
		}
		return offset;
	}

	/**
	 * @param i
	 * @param document2
	 * @return
	 */
	private int getSelectorNodeBegin(int offset, FormatterDocument document)
	{
		int length = document.getLength();
		while (offset < length)
		{
			if (!Character.isWhitespace(document.charAt(offset)) && (document.charAt(offset) != '\n'))
			{
				break;
			}
			offset++;
		}
		return offset;
	}

	/**
	 * @param startingOffset
	 * @param document2
	 * @return
	 */
	private int getSelectorNodeEnd(int offset, FormatterDocument document)
	{
		int original = offset;

		while (Character.isWhitespace(document.charAt(offset)))
		{
			offset++;
		}

		if (document.charAt(offset) == ',')
		{
			return offset;
		}

		offset = original;
		while (offset > 0)
		{
			if (!Character.isWhitespace(document.charAt(offset)) && (document.charAt(offset) != '\n')
					&& (document.charAt(offset) != '{'))
			{
				break;
			}
			offset--;
		}
		return offset;
	}

}
