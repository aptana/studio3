/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.jface.text.Region;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.formatter.nodes.FormatterDefaultElementNode;
import com.aptana.editor.html.formatter.nodes.FormatterExcludedNode;
import com.aptana.editor.html.formatter.nodes.FormatterForeignElementNode;
import com.aptana.editor.html.formatter.nodes.FormatterHTMLCommentNode;
import com.aptana.editor.html.formatter.nodes.FormatterHTMLContentNode;
import com.aptana.editor.html.formatter.nodes.FormatterSpecialElementNode;
import com.aptana.editor.html.formatter.nodes.FormatterVoidElementNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.IHTMLNodeTypes;
import com.aptana.formatter.ExcludeRegionList.EXCLUDE_STRATEGY;
import com.aptana.formatter.FormatterDocument;
import com.aptana.formatter.FormatterWriter;
import com.aptana.formatter.nodes.AbstractFormatterNodeBuilder;
import com.aptana.formatter.nodes.FormatterBlockNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginEndNode;
import com.aptana.formatter.nodes.FormatterBlockWithBeginNode;
import com.aptana.formatter.nodes.FormatterCommentNode;
import com.aptana.formatter.nodes.FormatterTextNode;
import com.aptana.formatter.nodes.IFormatterContainerNode;
import com.aptana.formatter.nodes.IFormatterTextNode;
import com.aptana.parsing.ast.INameNode;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.lexer.IRange;

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
	public static final HashSet<String> VOID_ELEMENTS = new HashSet<String>(Arrays.asList("area", "base", "br", "col",
			"command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"));
	@SuppressWarnings("nls")
	// elements that will not format their internal content at all, to keep any whitespace characters intact.
	public static final HashSet<String> EXCLUDED_ELEMENTS = new HashSet<String>(Arrays.asList("textarea", "pre"));
	@SuppressWarnings("nls")
	// elements that may contains text that can force us to preserve one white-space character after formatting.
	public static final HashSet<String> SPACE_SENSITIVE_ELEMENTS = new HashSet<String>(Arrays.asList("a", "abbr",
			"acronym", "b", "bdo", "big", "cite", "code", "del", "dfn", "em", "font", "i", "ins", "kbd", "label", "q",
			"s", "samp", "small", "span", "strike", "strong", "sub", "sup", "tt", "u", "var"));

	@SuppressWarnings("nls")
	protected static final HashSet<String> OPTIONAL_ENDING_TAGS = new HashSet<String>(Arrays.asList(""));
	private static final String INLINE_TAG_CLOSING = "/>"; //$NON-NLS-1$
	private static final Object RUBY_LANGUAGE = "com.aptana.contenttype.ruby"; //$NON-NLS-1$
	private static final Object PHP_LANGUAGE = "com.aptana.contenttype.php"; //$NON-NLS-1$

	private FormatterDocument document;
	private FormatterWriter formatterWriter;

	/**
	 * Constructs a new HTML formatter node builder with a given {@link FormatterWriter} that may be used for region
	 * exclusions.
	 * 
	 * @param writer
	 *            A {@link FormatterWriter}
	 */
	public HTMLFormatterNodeBuilder(FormatterWriter writer)
	{
		this.formatterWriter = writer;
	}

	/**
	 * Constructs a new HTML formatter node builder.
	 */
	public HTMLFormatterNodeBuilder()
	{
	}

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
					HTMLFormatterConstants.FORMATTER_OFF_ON_ENABLED, HTMLFormatterConstants.FORMATTER_OFF,
					HTMLFormatterConstants.FORMATTER_ON));
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
		// Push the current node.
		if (node instanceof HTMLNode)
		{
			// DEBUG
			// System.out.println(elementNode.getName() + "[" + elementNode.getStartingOffset() + ", "
			// + elementNode.getEndingOffset() + "]");

			HTMLNode htmlNode = (HTMLNode) node;
			short nodeType = htmlNode.getNodeType();
			if (nodeType == IHTMLNodeTypes.COMMENT)
			{
				// We got a HTMLCommentNode
				FormatterCommentNode commentNode = new FormatterHTMLCommentNode(document, htmlNode.getStartingOffset(),
						htmlNode.getEndingOffset() + 1);
				// We just need to add a child here. We cannot 'push', since the comment node is not a container node.
				addChild(commentNode);
			}
			else if (nodeType == IHTMLNodeTypes.ELEMENT || nodeType == IHTMLNodeTypes.SPECIAL)
			{
				// Check if we need to create a formatter node with a begin and end node, or just begin node.
				HTMLElementNode elementNode = (HTMLElementNode) node;
				String name = elementNode.getName().toLowerCase();
				if (VOID_ELEMENTS.contains(name) || !hasInlineClosingTag(elementNode))
				{
					FormatterBlockWithBeginNode formatterNode = new FormatterVoidElementNode(document, name);
					formatterNode.setBegin(createTextNode(document, elementNode.getStartingOffset(),
							elementNode.getEndingOffset() + 1));
					push(formatterNode);
					checkedPop(formatterNode, -1);
				}
				else if (EXCLUDED_ELEMENTS.contains(name))
				{
					FormatterExcludedNode nonFormattedContent = new FormatterExcludedNode(document);
					nonFormattedContent.setBegin(createTextNode(document, elementNode.getStart(),
							elementNode.getEnd() + 1));
					push(nonFormattedContent);
					checkedPop(nonFormattedContent, -1);
				}
				else
				{
					pushFormatterNode(elementNode);
				}
			}
			else if (nodeType == IHTMLNodeTypes.TEXT)
			{
				FormatterTextNode contentFormatterNode = new FormatterHTMLContentNode(document, htmlNode.getParent(),
						htmlNode.getPreviousSibling(), htmlNode.getStart(), htmlNode.getEnd() + 1);
				addChild(contentFormatterNode);
			}
		}
		else
		{
			// it's a node that was generated from a foreign language parser, such as the RHTMLParser
			pushForeignSpecialNode(node);
		}
	}

	/**
	 * Push a special node that was generated by a foreign parser, such as RHTMLParser, PHTMLParser etc.
	 * 
	 * @param node
	 */
	private void pushForeignSpecialNode(IParseNode node)
	{
		int nodeStart = node.getStartingOffset();
		int nodeEnd = node.getEndingOffset() + 1;
		nodeEnd = Math.min(document.getLength(), nodeEnd);
		String text = document.get(nodeStart, nodeEnd);
		// create a default node by looking at edges
		FormatterForeignElementNode formatterNode = resolveForeignSpecialNode(text, node);
		if (formatterNode != null)
		{
			push(formatterNode);
			int startSpecial = formatterNode.getBegin()[0].getEndOffset();
			int endSpecial = formatterNode.getEnd().getStartOffset();
			if (endSpecial == document.getLength() - 1)
			{
				endSpecial++;
			}
			// push a special node
			FormatterSpecialElementNode specialNode = new FormatterSpecialElementNode(document, StringUtil.EMPTY);
			specialNode.setBegin(createTextNode(document, startSpecial, endSpecial));
			specialNode.setEnd(createTextNode(document, endSpecial, endSpecial)); // empty end
			push(specialNode);
			checkedPop(specialNode, -1);
			// pop the default node
			checkedPop(formatterNode, -1);
			if (formatterWriter != null)
			{
				// document.get(startSpecial, endSpecial);
				int excludeRegionStart = startSpecial + 1;
				IFormatterTextNode foreignEnd = formatterNode.getEnd();
				int excludeRegionLength = endSpecial - startSpecial
						- (foreignEnd.getEndOffset() - foreignEnd.getStartOffset());
				if (endSpecial == document.getLength())
				{
					excludeRegionLength--;
				}
				if (excludeRegionLength > excludeRegionStart)
				{
					formatterWriter.excludeRegion(new Region(excludeRegionStart, excludeRegionLength),
							EXCLUDE_STRATEGY.WRITE_AS_IS);
				}
			}
		}
		else
		{
			// A fall-back - add it as a single chunk of a special node only
			// push a special node
			FormatterSpecialElementNode specialNode = new FormatterSpecialElementNode(document, StringUtil.EMPTY);
			specialNode.setBegin(createTextNode(document, nodeStart, nodeEnd));
			specialNode.setEnd(createTextNode(document, nodeEnd, nodeEnd)); // empty end
			push(specialNode);
			checkedPop(specialNode, -1);
		}
	}

	/**
	 * @param text
	 * @param language
	 * @return
	 */
	private FormatterForeignElementNode resolveForeignSpecialNode(String text, IParseNode node)
	{
		int offset = node.getStartingOffset();
		String language = node.getLanguage();
		FormatterForeignElementNode elementNode = new FormatterForeignElementNode(document);
		int startLength = 2;
		if (RUBY_LANGUAGE.equals(language))
		{
			if (text.startsWith("<%=")) { //$NON-NLS-1$
				startLength = 3;
			}
		}
		else if (PHP_LANGUAGE.equals(language))
		{
			if (text.startsWith("<?php")) { //$NON-NLS-1$
				startLength = 5;
			}
			else if (text.startsWith("<?=")) { //$NON-NLS-1$
				startLength = 3;
			}
		}
		else
		{
			return null;
		}
		elementNode.setBegin(createTextNode(document, offset, offset + startLength));
		int end = offset + text.length();
		int endBegin = end;
		if (end == document.getLength())
		{
			endBegin = end - 2;
		}
		else
		{
			int endWithoutWhiteSpace = getEndWithoutWhiteSpaces(end, document);
			if (end != endWithoutWhiteSpace)
			{
				end = endWithoutWhiteSpace + 1;
			}
			endBegin = end;
		}

		String endStr = document.get(endBegin - 2, end);
		if (endStr.endsWith("?>") || endStr.endsWith("%>")) //$NON-NLS-1$ //$NON-NLS-2$
		{
			endBegin = end - 2;
		}
		else
		{
			endBegin = end;
		}
		elementNode.setEnd(createTextNode(document, endBegin, end));
		return elementNode;
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
	 * Determine the type of the node and return a formatter node that should represent it while rewriting the doc.<br>
	 * Ant HTMLElementNode is acceptable here, even the special nodes. These special node just represents the wrapping
	 * nodes around the 'foreign' nodes that exist as their children (nodes produced from the RHTML parser and JS
	 * parser, for example).<br>
	 * This behavior allows the inner child of these HTMLSpecialNodes to be processed in the
	 * {@link #addNode(IParseNode)} method and produce a FormatterSpecialElementNode.<br>
	 * 
	 * @param node
	 * @return FormatterBlockWithBeginEndNode sub-classing instance
	 */
	private FormatterBlockWithBeginEndNode pushFormatterNode(HTMLElementNode node)
	{
		String type = node.getName().toLowerCase();
		FormatterBlockWithBeginEndNode formatterNode;
		IRange beginNodeRange = node.getNameNode().getNameRange();
		INameNode endNode = node.getEndNode();
		int endOffset = node.getEndingOffset() + 1;
		IRange endNameRange = endNode.getNameRange();
		if (endNode != null && !endNameRange.isEmpty()
				&& endNameRange.getStartingOffset() != endNameRange.getEndingOffset())
		{
			IRange endNodeRange = endNode.getNameRange();
			endOffset = endNodeRange.getStartingOffset();
		}

		boolean createdContentNode = false;
		formatterNode = new FormatterDefaultElementNode(document, type, node.getChildren());
		formatterNode.setBegin(createTextNode(document, beginNodeRange.getStartingOffset(),
				beginNodeRange.getEndingOffset() + 1));
		push(formatterNode);
		if (node.getNodeType() == IHTMLNodeTypes.SPECIAL
				&& !IHTMLConstants.CONTENT_TYPE_HTML.equals(node.getLanguage()))
		{
			// Everything under this HTMLSpecialNode should be wrapped with a
			// FormatterSpecialElementNode, and no need to visit its children.
			// The assumption here is that the wrapping HTMLElementNode of this special node
			// always have start and end tags.
			FormatterSpecialElementNode specialNode = new FormatterSpecialElementNode(document, StringUtil.EMPTY);
			int beginSpecial = beginNodeRange.getEndingOffset() + 1;
			int endSpecial = endNode.getNameRange().getStartingOffset();
			specialNode.setBegin(createTextNode(document, beginSpecial, endSpecial));
			specialNode.setEnd(createTextNode(document, endSpecial, endSpecial)); // empty end
			push(specialNode);
			checkedPop(specialNode, -1);
		}
		else
		{
			// Recursively call this method till we are done with all the children under this node.
			addNodes(node.getChildren());

			int endNodeStartingOffset = endNode.getNameRange().getStartingOffset();
			// In case one or more spaces exist left or right to the text, we have to maintain them in order to
			// keep the HTML output the same. The browser will treat multiple spaces as one space, so we can trim down
			// to one.
			int textStartOffset = getBeginWithoutWhiteSpaces(beginNodeRange.getEndingOffset() + 1, document);
			int textEndOffset = getEndWithoutWhiteSpaces(endNodeStartingOffset - 1, document);
			char charAt = document.charAt(textStartOffset - 1);
			if (textStartOffset > 0 && (charAt == ' ' || charAt == '\t'))
			{
				textStartOffset--;
			}
			charAt = document.charAt(textEndOffset + 1);
			if (textEndOffset < document.getLength() - 1 && (charAt == ' ' || charAt == '\t'))
			{
				textEndOffset++;
			}
			// Create content node when the HTMLElementNode does not have any children
			if (!node.hasChildren())
			{
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
						textStartOffset = beginNodeRange.getEndingOffset() + 1;
						textEndOffset = endOffset - 1;
					}
				}
				FormatterTextNode contentFormatterNode = new FormatterHTMLContentNode(document, node,
						node.getPreviousSibling(), textStartOffset, textEndOffset + 1);
				formatterNode.addChild(contentFormatterNode);
				createdContentNode = true;
			}
		}

		if (createdContentNode)
		{
			checkedPop(formatterNode, -1);
		}
		else
		{
			checkedPop(formatterNode, getEndWithoutWhiteSpaces(endOffset - 1, document) + 1);
		}
		formatterNode.setEnd(createTextNode(document, endOffset, node.getEndingOffset() + 1));
		return formatterNode;
	}

	/**
	 * @param i
	 * @param document2
	 * @return
	 */
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

	/**
	 * @param startingOffset
	 * @param document2
	 * @return
	 */
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
