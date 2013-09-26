/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.rules.ITokenScanner;

import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.core.util.StringUtil;
import com.aptana.css.core.ICSSConstants;
import com.aptana.css.core.parsing.ast.CSSDeclarationNode;
import com.aptana.css.core.parsing.ast.CSSRuleNode;
import com.aptana.editor.html.core.IHTMLConstants;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.html.parsing.ast.HTMLTextNode;
import com.aptana.editor.html.parsing.ast.IHTMLNodeTypes;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseError;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Range;
import com.aptana.parsing.util.ParseUtil;

public class HTMLParser extends AbstractParser
{
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_LANG = "language"; //$NON-NLS-1$

	private static final String[] CSS_VALID_TYPE_ATTR = new String[] { "text/css" }; //$NON-NLS-1$

	private static final String[] JS_VALID_TYPE_ATTR = new String[] { "application/javascript", //$NON-NLS-1$
			"application/ecmascript", "application/x-javascript", "application/x-ecmascript", "text/javascript", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"text/ecmascript", "text/jscript" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String[] JS_VALID_LANG_ATTR = new String[] { "JavaScript" }; //$NON-NLS-1$

	private HTMLParserScanner fScanner;
	private HTMLParseState fParseState;
	private Stack<IParseNode> fElementStack;
	private static final Pattern attributes = Pattern
			.compile("\\s+(\\w[\\w\\-:]*)\\s*?(=\\s*(('[^']*')|(\"[^\"]*\")))?"); //$NON-NLS-1$

	private IParseNode fCurrentElement;
	private Symbol fCurrentSymbol;
	private IProgressMonitor fMonitor;

	private List<IParseNode> fCommentNodes;
	private boolean previousSymbolSkipped;
	private WorkingParseResult fWorkingParseResult;

	/**
	 * parse
	 */
	protected synchronized void parse(IParseState parseState, WorkingParseResult working) throws java.lang.Exception
	{
		fMonitor = parseState.getProgressMonitor();
		fScanner = new HTMLParserScanner();
		fElementStack = new Stack<IParseNode>();
		fCommentNodes = new ArrayList<IParseNode>();
		fWorkingParseResult = working;

		String source = parseState.getSource();
		if (parseState instanceof HTMLParseState)
		{
			fParseState = (HTMLParseState) parseState;
		}
		else
		{
			fParseState = new HTMLParseState(source, parseState.getStartingOffset(), parseState.getSkippedRanges());
			fParseState.setProgressMonitor(parseState.getProgressMonitor());
		}

		fScanner.setSource(source);

		int startingOffset = fParseState.getStartingOffset();
		ParseRootNode root = new HTMLParseRootNode(startingOffset, startingOffset + source.length() - 1);
		try
		{
			fCurrentElement = root;

			parseAll(source);
			root.setCommentNodes(fCommentNodes.toArray(new IParseNode[fCommentNodes.size()]));
		}
		finally
		{
			// clear for garbage collection
			fWorkingParseResult = null;
			fMonitor = null;
			fScanner = null;
			fElementStack = null;
			fCurrentElement = null;
			fCurrentSymbol = null;
			fParseState = null;
			fCommentNodes = null;
		}

		// trim the tree and set the result only after clearing for garbage collection.
		ParseUtil.trimToSize(root);
		working.setParseResult(root);
	}

	protected void processSymbol(Symbol symbol, String source) throws IOException, Exception
	{
		switch (symbol.getId())
		{
			case HTMLTokens.COMMENT:
				processComment();
				break;
			case HTMLTokens.START_TAG:
				processStartTag();
				break;
			case HTMLTokens.END_TAG:
				processEndTag();
				break;
			case HTMLTokens.STYLE:
				processStyleTag();
				break;
			case HTMLTokens.SCRIPT:
				processScriptTag();
				break;
			case HTMLTokens.TEXT:
				processText(source);
		}
	}

	protected void processLanguage(String language, short endToken) throws IOException, Exception
	{
		ITokenScanner tokenScanner = fScanner.getTokenScanner().getPrimaryTokenScanner();
		if (tokenScanner instanceof HTMLTokenScanner)
		{
			((HTMLTokenScanner) tokenScanner).setInsideSpecialTag(true);
		}

		Symbol startTag = fCurrentSymbol;
		advance();

		int start = fCurrentSymbol.getStart();
		int end = start - 1;
		short id = fCurrentSymbol.getId();
		while (id != endToken && id != HTMLTokens.EOF)
		{
			end = fCurrentSymbol.getEnd();
			advance();
			id = fCurrentSymbol.getId();
		}

		if (tokenScanner instanceof HTMLTokenScanner)
		{
			((HTMLTokenScanner) tokenScanner).setInsideSpecialTag(false);
		}

		IParseNode[] nested = getParseResult(language, start, end);
		if (fCurrentElement != null)
		{
			HTMLSpecialNode node = new HTMLSpecialNode(startTag, nested, startTag.getStart(), fCurrentSymbol.getEnd());
			node.setEndNode(fCurrentSymbol.getStart(), fCurrentSymbol.getEnd());
			parseAttribute(node, startTag);
			fCurrentElement.addChild(node);
		}
	}

	protected HTMLElementNode processCurrentTag()
	{
		HTMLElementNode element = new HTMLElementNode(fCurrentSymbol, fCurrentSymbol.getStart(),
				fCurrentSymbol.getEnd());
		parseAttribute(element, fCurrentSymbol);
		if (element.isSelfClosing() && !HTMLParseState.isEndForbiddenOrEmptyTag(element.getName()))
		{
			fWorkingParseResult.addError(new ParseError(IHTMLConstants.CONTENT_TYPE_HTML, element.getStartingOffset(),
					element.getLength(), Messages.HTMLParser_self_closing_syntax_on_non_void_element_error,
					IParseError.Severity.ERROR));
		}
		return element;
	}

	private void parseAll(String source) throws IOException, Exception
	{
		advance();
		while (fCurrentSymbol.getId() != HTMLTokens.EOF && !fMonitor.isCanceled())
		{
			if (isSkipped(fCurrentSymbol.getStart(), fCurrentSymbol.getEnd()))
			{
				previousSymbolSkipped = true;
			}
			else
			{
				processSymbol(fCurrentSymbol, source);
				previousSymbolSkipped = false;
			}
			advance();
		}

		// if there are unclosed tags remaining, close them all
		List<HTMLElementNode> elementsToClose = new ArrayList<HTMLElementNode>();
		if (fCurrentElement instanceof HTMLElementNode)
		{
			elementsToClose.add((HTMLElementNode) fCurrentElement);
			addMissingEndTagError((HTMLElementNode) fCurrentElement);
		}
		IParseNode node;
		for (int i = fElementStack.size() - 1; i >= 0; --i)
		{
			node = fElementStack.get(i);
			if (node instanceof HTMLElementNode)
			{
				elementsToClose.add((HTMLElementNode) node);
				addMissingEndTagError((HTMLElementNode) node);
			}
		}
		int end = fCurrentSymbol.getStart() - 1;
		for (HTMLElementNode element : elementsToClose)
		{
			element.setLocation(element.getStartingOffset(), end);
			element.setEndNode(end, end);
		}
	}

	private void advance() throws IOException, Exception
	{
		fCurrentSymbol = fScanner.nextToken();
	}

	private boolean isSkipped(int start, int end)
	{
		IRange[] ranges = fParseState.getSkippedRanges();
		if (ranges != null)
		{
			int length = ranges.length;
			for (int i = 0; i < length; i++)
			{
				IRange range = ranges[i];
				if (start >= range.getStartingOffset() && end <= range.getEndingOffset())
				{
					return true;
				}
			}
		}
		return false;
	}

	private IParseNode[] getParseResult(String language, int start, int end)
	{
		if (start <= end)
		{
			try
			{
				String text = fScanner.getSource().get(start, end - start + 1);
				ParseState subParseState = new ParseState(text, start);
				// FIXME We need to propagate options down to sub-languages, i.e. JS's attach/collect comments
				ParseResult subParseResult = ParserPoolFactory.parse(language, subParseState);
				IParseNode node = subParseResult.getRootNode();
				for (IParseError subError : subParseResult.getErrors())
				{
					// Shift the line/offsets based on the starting offset/line of the sub-language!
					fWorkingParseResult.addError(new ParseError(language, start + subError.getOffset(), subError
							.getLength(), subError.getMessage(), subError.getSeverity()));
				}
				if (node == null)
				{
					node = new HTMLTextNode(text, start, end);
				}
				return new IParseNode[] { node };
			}
			catch (java.lang.Exception e)
			{
			}
		}
		return HTMLParserConstants.NO_PARSE_NODES;
	}

	private void processComment()
	{
		HTMLCommentNode comment = new HTMLCommentNode(fCurrentSymbol.value.toString(), fCurrentSymbol.getStart(),
				fCurrentSymbol.getEnd());
		fCommentNodes.add(comment);
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(comment);
		}
	}

	private void processStartTag()
	{
		HTMLElementNode element = processCurrentTag();
		// pushes the element onto the stack
		openElement(element);
	}

	private void processEndTag()
	{
		boolean addErrors = false;
		String tagName = HTMLUtils.stripTagEndings(fCurrentSymbol.value.toString());
		List<HTMLElementNode> elementsToClose = new ArrayList<HTMLElementNode>();
		if (fCurrentElement instanceof HTMLElementNode
				&& ((HTMLElementNode) fCurrentElement).getName().equalsIgnoreCase(tagName))
		{
			elementsToClose.add((HTMLElementNode) fCurrentElement);
		}
		else
		{
			// finds the closest opened tag of the same name
			IParseNode node;
			int i;
			boolean hasOpenTag = false;
			for (i = fElementStack.size() - 1; i >= 0; --i)
			{
				node = fElementStack.get(i);
				if (node instanceof HTMLElementNode && ((HTMLElementNode) node).getName().equalsIgnoreCase(tagName))
				{
					hasOpenTag = true;
					break;
				}
			}

			if (hasOpenTag)
			{
				addErrors = true;
				// found the match, so closes it as well as all the open elements above
				if (fCurrentElement instanceof HTMLElementNode)
				{
					elementsToClose.add((HTMLElementNode) fCurrentElement);
				}

				// Work our way from back of stack to front, marking unclosed elements
				for (int j = fElementStack.size() - 1; j >= i; --j)
				{
					node = fElementStack.get(j);
					if (node instanceof HTMLElementNode)
					{
						HTMLElementNode elementNode = (HTMLElementNode) node;
						elementsToClose.add(elementNode);
					}
				}
			}
			else
			{
				fWorkingParseResult.addError(new ParseError(IHTMLConstants.CONTENT_TYPE_HTML, fCurrentSymbol,
						Messages.HTMLParser_unexpected_error + fCurrentSymbol.value, IParseError.Severity.WARNING));
			}
		}

		int currentStart = fCurrentSymbol.getStart();
		int currentEnd = fCurrentSymbol.getEnd();
		int size = elementsToClose.size();
		Collections.reverse(elementsToClose);
		Set<String> missingEndTagNames = new HashSet<String>(size);
		for (int i = 0; i < size; ++i)
		{
			HTMLElementNode element = elementsToClose.get(i);

			if (addErrors)
			{
				String elementName = element.getName();
				// Only add error if it's not the opening tag for the current tagName
				if (!elementName.equalsIgnoreCase(tagName))
				{
					if (missingEndTagNames.contains(elementName))
					{
						addMissingLikelyMistypedEndTagError(element);
					}
					else
					{
						addMissingEndTagError(element);
						missingEndTagNames.add(elementName);
					}
				}
			}
			// adjusts the ending offset of the element to include the entire block
			if (i == 0)
			{
				element.setLocation(element.getStartingOffset(), currentEnd);
				element.setEndNode(currentStart, currentEnd);
			}
			else
			{
				element.setLocation(element.getStartingOffset(), currentStart - 1);
				element.setEndNode(currentStart - 1, currentStart - 1);
			}
			closeElement();
		}
	}

	private void addMissingEndTagError(HTMLElementNode node)
	{
		if (fParseState.getCloseTagType(node.getName()) != IHTMLTagInfo.END_OPTIONAL)
		{
			fWorkingParseResult.addError(new ParseError(IHTMLConstants.CONTENT_TYPE_HTML, node.getStartingOffset(),
					node.getLength(), MessageFormat.format(Messages.HTMLParser_missing_end_tag_error, node.getName()),
					IParseError.Severity.WARNING));
		}
	}

	private void addMissingLikelyMistypedEndTagError(HTMLElementNode node)
	{
		if (fParseState.getCloseTagType(node.getName()) != IHTMLTagInfo.END_OPTIONAL)
		{
			fWorkingParseResult.addError(new ParseError(IHTMLConstants.CONTENT_TYPE_HTML, node.getStartingOffset(),
					node.getLength(),
					MessageFormat.format(Messages.HTMLParser_OpenTagIntendedAsClosed, node.getName()),
					IParseError.Severity.WARNING));
		}
	}

	private void processStyleTag() throws IOException, Exception
	{
		HTMLElementNode node = processCurrentTag();
		String language = null;
		String type = node.getAttributeValue(ATTR_TYPE);
		if (type == null || isInArray(type, CSS_VALID_TYPE_ATTR))
		{
			language = ICSSConstants.CONTENT_TYPE_CSS;
		}
		else if (isJavaScript(node))
		{
			language = IJSConstants.CONTENT_TYPE_JS;
		}
		processLanguage(language, HTMLTokens.STYLE_END);
	}

	private void processScriptTag() throws IOException, Exception
	{
		HTMLElementNode node = processCurrentTag();
		String language = null;
		String type = node.getAttributeValue(ATTR_TYPE);
		if (type == null || isJavaScript(node))
		{
			language = IJSConstants.CONTENT_TYPE_JS;
		}
		processLanguage(language, HTMLTokens.SCRIPT_END);
	}

	private void parseAttribute(HTMLElementNode element, Symbol tagSymbol)
	{
		String tag = tagSymbol.value.toString();

		String tagName = element.getElementName();
		int index = tag.indexOf(tagName);
		tag = tag.substring(index + tagName.length());

		int startOftagText = tagSymbol.getStart() + index + tagName.length();

		Matcher m = attributes.matcher(tag);
		while (m.find())
		{
			String name = m.group(1);
			String value = m.group(3);

			IRange nameRange = new Range(startOftagText + m.start(1), startOftagText + m.end(1));
			IRange valueRange = new Range(startOftagText + m.start(3), startOftagText + m.end(3));
			int absoluteOffset = startOftagText + m.start(3);

			if (value != null)
			{
				// trim off the quotes
				value = value.substring(1, value.length() - 1).trim();
			}
			element.setAttribute(name, value == null ? StringUtil.EMPTY : value, nameRange, valueRange);

			if (!StringUtil.isEmpty(value))
			{
				// checks if we need to process the value as CSS
				if (HTMLUtils.isCSSAttribute(name))
				{
					String text = tagName + " {" + value + "}"; //$NON-NLS-1$ //$NON-NLS-2$
					try
					{

						int startingOffset = absoluteOffset - (tagName.length() + 1);
						IParseNode node = ParserPoolFactory.parse(ICSSConstants.CONTENT_TYPE_CSS, text, startingOffset)
								.getRootNode();

						// should always have a rule node
						if (node.hasChildren())
						{
							IParseNode rule = node.getChild(0);
							if (rule instanceof CSSRuleNode)
							{
								CSSDeclarationNode[] declarations = ((CSSRuleNode) rule).getDeclarations();
								for (CSSDeclarationNode declaration : declarations)
								{
									element.addCSSStyleNode(declaration);
								}
							}
						}
					}
					catch (java.lang.Exception e)
					{
					}
				}
				// checks if we need to process the value as JS
				else if (HTMLUtils.isJSAttribute(tagName, name))
				{
					try
					{
						int startingOffset = absoluteOffset + 1;
						IParseNode node = ParserPoolFactory.parse(IJSConstants.CONTENT_TYPE_JS, value, startingOffset)
								.getRootNode();

						for (IParseNode child : node)
						{
							element.addJSAttributeNode(child);
						}
					}
					catch (java.lang.Exception e)
					{
					}
				}
			}
		}
	}

	private void processText(String source)
	{
		// checks text node that starts with "<"
		String text = fCurrentSymbol.value.toString().trim();
		if (text.length() > 0 && text.charAt(0) == '<')
		{
			// this means we have an open < tag that doesn't have a closing >
			fWorkingParseResult.addError(new ParseError(IHTMLConstants.CONTENT_TYPE_HTML, fCurrentSymbol.getStart(),
					fCurrentSymbol.getEnd() - fCurrentSymbol.getStart() + 1, MessageFormat.format(
							Messages.HTMLParser_ERR_TagMissingEnd, text), IParseError.Severity.ERROR));
		}

		// checks if the last child of the current node is also a HTML text node. If so, we should unify both to one
		// node with a larger offset.
		if (!previousSymbolSkipped
				&& (fCurrentElement.getChildCount() > 0 && fCurrentElement.getLastChild().getNodeType() == IHTMLNodeTypes.TEXT))
		{
			HTMLTextNode node = (HTMLTextNode) fCurrentElement.getLastChild();
			int start = node.getStartingOffset(), end = fCurrentSymbol.getEnd();
			node.setLocation(start, end);
			node.setText(source.substring(start, end + 1));
		}
		else
		{
			fCurrentElement.addChild(new HTMLTextNode(fCurrentSymbol.value.toString(), fCurrentSymbol.getStart(),
					fCurrentSymbol.getEnd()));
		}
	}

	/**
	 * Pushes the currently active element onto the stack and sets the specified element as the new active element.
	 * 
	 * @param element
	 */
	private void openElement(HTMLElementNode element)
	{
		String tagName = element.getName();
		int closeTagType = fParseState.getCloseTagType(tagName);
		// tag with optional end could not be nested, so if we see another instance of the same start tag, close the
		// previous one
		if (closeTagType == IHTMLTagInfo.END_OPTIONAL && fCurrentElement != null
				&& tagName.equals(fCurrentElement.getNameNode().getName()))
		{
			// adjusts the ending offset of current element to include the entire block up to the start of the new tag
			int end = fCurrentSymbol.getStart() - 1;
			((HTMLNode) fCurrentElement).setLocation(fCurrentElement.getStartingOffset(), end);
			if (fCurrentElement instanceof HTMLElementNode)
			{
				((HTMLElementNode) fCurrentElement).setEndNode(end, end);
			}
			closeElement();
		}

		// adds the new parent as a child of the current parent
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(element);
		}

		if (closeTagType != IHTMLTagInfo.END_FORBIDDEN && !element.isSelfClosing())
		{
			fElementStack.push(fCurrentElement);
			fCurrentElement = element;
		}
	}

	/**
	 * Closes the element that is on the top of the stack.
	 */
	private void closeElement()
	{
		if (fElementStack.size() > 0)
		{
			fCurrentElement = fElementStack.pop();
		}
		else
		{
			fCurrentElement = null;
		}
	}

	public static boolean isJavaScript(HTMLElementNode node)
	{
		String type = node.getAttributeValue(ATTR_TYPE);
		if (isInArray(type, JS_VALID_TYPE_ATTR))
		{
			return true;
		}
		String langAttr = node.getAttributeValue(ATTR_LANG);
		if (langAttr != null && isInArray(langAttr, JS_VALID_LANG_ATTR))
		{
			return true;
		}
		return false;
	}

	private static boolean isInArray(String element, String[] array)
	{
		for (String arrayElement : array)
		{
			if (element.startsWith(arrayElement))
			{
				return true;
			}
		}
		return false;
	}
}
