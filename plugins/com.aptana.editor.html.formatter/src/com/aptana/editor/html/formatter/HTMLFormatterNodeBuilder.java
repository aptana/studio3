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

import com.aptana.editor.html.formatter.nodes.FormatterDefaultElementNode;
import com.aptana.editor.html.formatter.nodes.FormatterVoidElementNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLNodeTypes;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.FormatterCommentNode;
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
	@SuppressWarnings("nls")
	protected static final HashSet<String> OPTIONAL_ENDING_TAGS = new HashSet<String>(Arrays.asList(""));
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
		long start = System.currentTimeMillis();
		this.document = document;
		final IFormatterContainerNode rootNode = new FormatterBlockNode(document);
		start(rootNode);
		IParseNode[] children = parseResult.getChildren();
		addNodes(children);
		checkedPop(rootNode, document.getLength());
		System.out.println((System.currentTimeMillis() - start));
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
		if (node instanceof HTMLNode)
		{
			// DEBUG
			// System.out.println(elementNode.getName() + "[" + elementNode.getStartingOffset() + ", "
			// + elementNode.getEndingOffset() + "]");

			HTMLElementNode elementNode = (HTMLElementNode) node;
			if (elementNode.getNodeType() == HTMLTokens.COMMENT)
			{
				// We got a HTMLCommentNode
				FormatterCommentNode commentNode = new FormatterCommentNode(document, elementNode.getStartingOffset(),
						elementNode.getEndingOffset());
				// We just need to add a child here. We cannot 'push', since the comment node is not a container node.
				addChild(commentNode);
			}
			else if (elementNode.getNodeType() == HTMLNodeTypes.ELEMENT)
			{
				// Check if we need to create a formatter node with a begin and end node, or just begin node.
				String name = elementNode.getName().toLowerCase();
				if (VOID_ELEMENTS.contains(name) || !hasInlineClosingTag(elementNode)
						|| (node instanceof HTMLSpecialNode))
				{
					FormatterBlockWithBeginNode formatterNode = new FormatterVoidElementNode(document, name);
					formatterNode.setBegin(createTextNode(document, elementNode.getStartingOffset(), elementNode
							.getEndingOffset() + 1));
					push(formatterNode);
					checkedPop(formatterNode, -1);
				}
				else
				{
					pushFormatterNode(elementNode);
				}
			}
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
	private FormatterBlockWithBeginEndNode pushFormatterNode(HTMLElementNode node)
	{
		String type = node.getName().toLowerCase();
		FormatterBlockWithBeginEndNode formatterNode = new FormatterDefaultElementNode(document, type);
		int startingOffset = node.getStartingOffset();
		int endingOffset = node.getEndingOffset();
		int tagCloser = findRightChar('>', startingOffset + type.length() + 1, true);
		formatterNode.setBegin(createTextNode(document, startingOffset, tagCloser + 1));
		push(formatterNode);

		// Recursively call this method till we are done with all the children under this node.
		addNodes(node.getChildren());

		int end;
		if (node.hasChildren())
		{
			IParseNode firstChild = node.getFirstChild();
			IParseNode lastChild = node.getLastChild();
			end = lastChild.getEndingOffset() + 1;
			checkedPop(formatterNode, firstChild.getStartingOffset());
		}
		else
		{
			end = findLeftChar('<', endingOffset - type.length(), true);
			checkedPop(formatterNode, end);
		}
		int tagEnd = findRightChar('>', end, true);
		formatterNode.setEnd(createTextNode(document, end, tagEnd + 1));
		return formatterNode;
	}

	/**
	 * @param c
	 *            The char to find left to the start position.
	 * @param position
	 *            Start position
	 * @return The char position. If not found or out of bounds, we return the initial position.
	 */
	private int findLeftChar(char c, int position, boolean skipComments)
	{
		if (position > -1 && position <= document.getLength())
		{
			CommentScanner scanner = skipComments ? new CommentScanner(false) : null;
			for (int i = position; i > 0; i--)
			{
				char nextChar = document.charAt(i);
				if (skipComments)
				{
					scanner.feed(nextChar);
				}
				if (nextChar == c)
				{
					// Skip any comments openers
					if (skipComments && scanner.isInComment())
					{
						continue;
					}
					position = i;
					break;
				}
			}
		}
		return position;
	}

	/**
	 * @param c
	 *            The char to find right to the start position.
	 * @param position
	 *            Start position
	 * @return The char position. If not found or out of bounds, we return the initial position.
	 */
	private int findRightChar(char c, int position, boolean skipComments)
	{
		if (position > -1 && position <= document.getLength())
		{
			CommentScanner scanner = skipComments ? new CommentScanner(true) : null;
			for (int i = position; i < document.getLength(); i++)
			{
				char nextChar = document.charAt(i);
				if (skipComments)
				{
					scanner.feed(nextChar);
				}
				if (nextChar == c)
				{
					// Skip any comments openers
					if (skipComments && scanner.isInComment())
					{
						continue;
					}
					position = i;
					break;
				}
			}
		}
		return position;
	}

	/**
	 * A simple scanner that is fed with chars and keeps track whether we are in an HTML comment or not. A comment can
	 * appear in two different forms: <br>
	 * 
	 * <pre>
	 * 1. <! > 
	 * 2. <!-- -->
	 * </pre>
	 */
	private class CommentScanner
	{
		private String startComment;
		private String endComment;
		private StringBuilder buffer;
		private final boolean leftToRight;

		/**
		 * Constructs a new comments scanner with the direction of the scanning.
		 * 
		 * @param leftToRight
		 */
		public CommentScanner(boolean leftToRight)
		{
			this.leftToRight = leftToRight;
			buffer = new StringBuilder();
			if (leftToRight)
			{
				startComment = "<!"; //$NON-NLS-1$
				endComment = ">"; //$NON-NLS-1$
			}
			else
			{
				// The buffer will hold those characters in a reverse order, so we have to make sure the string are
				// revered as well
				startComment = ">"; //$NON-NLS-1$
				endComment = "!<"; //$NON-NLS-1$
			}
		}

		/**
		 * @param nextChar
		 */
		public void feed(char nextChar)
		{
			buffer.append(nextChar);
		}

		/**
		 * @return if we are currently in a comment
		 */
		public boolean isInComment()
		{
			String string = buffer.toString();
			String hyphens = "--"; //$NON-NLS-1$
			boolean hasHyphens = true;
			String startWithHyphens = (leftToRight) ? startComment + hyphens : hyphens + startComment;
			int startIndex = string.lastIndexOf(startWithHyphens);
			if (startIndex < 0)
			{
				startIndex = string.lastIndexOf(startComment);
				hasHyphens = false;
			}
			if (startIndex < 0)
			{
				return false;
			}
			// else, we check where is the matching end tag.
			String ending = endComment;
			if (hasHyphens)
			{
				ending = (leftToRight) ? hyphens + endComment : endComment + hyphens;
			}
			int endIndex = string.lastIndexOf(ending);
			if (endIndex > 0 && startIndex > endIndex)
			{
				return true;
			}
			return endIndex < 0 || endIndex == string.length() - ending.length();
		}
	}
}
