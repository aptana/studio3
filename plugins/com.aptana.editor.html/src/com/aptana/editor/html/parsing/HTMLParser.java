/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;

import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.css.parsing.ast.CSSDeclarationNode;
import com.aptana.editor.css.parsing.ast.CSSRuleNode;
import com.aptana.editor.html.parsing.HTMLTagScanner.TokenType;
import com.aptana.editor.html.parsing.ast.HTMLCommentNode;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;
import com.aptana.editor.js.parsing.IJSParserConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.parsing.lexer.IRange;

public class HTMLParser implements IParser
{
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_LANG = "language"; //$NON-NLS-1$

	@SuppressWarnings("nls")
	private static final String[] CSS_VALID_TYPE_ATTR = new String[] { "text/css" };
	@SuppressWarnings("nls")
	private static final String[] JS_VALID_TYPE_ATTR = new String[] { "application/javascript",
			"application/ecmascript", "application/x-javascript", "application/x-ecmascript", "text/javascript",
			"text/ecmascript", "text/jscript" };
	@SuppressWarnings("nls")
	private static final String[] JS_VALID_LANG_ATTR = new String[] { "JavaScript" };

	@SuppressWarnings("nls")
	private static final String[] CSS_VALID_ATTR = { "style" };
	@SuppressWarnings("nls")
	private static final String[] JS_VALID_ATTR = { "onabort", "onactivate", "onafterprint", "onafterupdate",
			"onbeforeactivate", "onbeforecopy", "onbeforecut", "onbeforedeactivate", "onbeforeeditfocus",
			"onbeforepaste", "onbeforeprint", "onbeforeunload", "onbeforeupdate", "onblur", "onbounce", "oncellchange",
			"onchange", "onclick", "oncontextmenu", "oncontrolselect", "oncopy", "oncut", "ondataavailable",
			"ondatasetchanged", "ondatasetcomplete", "ondblclick", "ondeactivate", "ondrag", "ondragend",
			"ondragenter", "ondragleave", "ondragover", "ondrop", "onerror", "onerrorupdate", "onfilterchange",
			"onfinish", "onfocus", "onfocusin", "onfocusout", "onhelp", "onkeydown", "onkeypress", "onkeyup",
			"onlayoutcomplete", "onload", "onlosecapture", "onmousedown", "onmouseenter", "onmouseleave",
			"onmousemove", "onmouseout", "onmouseover", "onmouseup", "onmousewheel", "onmove", "onmoveend",
			"onmovestart", "onpaste", "onpropertychange", "onreadystatechange", "onreset", "onresize", "onresizeend",
			"onresizestart", "onrowenter", "onrowexit", "onrowsdelete", "onrowsinserted", "onscroll", "onselect",
			"onselectstart", "onstart", "onsubmit", "ontimeerror", "onunload" };

	private static Set<String> cssAttributes = new HashSet<String>();
	static
	{
		for (String attribute : CSS_VALID_ATTR)
		{
			cssAttributes.add(attribute);
		}
	}
	private static Set<String> jsAttributes = new HashSet<String>();
	static
	{
		for (String attribute : JS_VALID_ATTR)
		{
			jsAttributes.add(attribute);
		}
	}

	private HTMLParserScanner fScanner;
	private HTMLParseState fParseState;
	private Stack<IParseNode> fElementStack;
	private HTMLTagScanner fTagScanner;

	private IParseNode fCurrentElement;
	private Symbol fCurrentSymbol;

	/**
	 * HTMLParser
	 */
	public HTMLParser()
	{
	}

	/**
	 * parse
	 */
	public synchronized IParseRootNode parse(IParseState parseState) throws java.lang.Exception
	{
		fScanner = new HTMLParserScanner();
		fTagScanner = new HTMLTagScanner();
		fElementStack = new Stack<IParseNode>();

		fParseState = (HTMLParseState) parseState;
		String source = new String(parseState.getSource());
		fScanner.setSource(source);

		int startingOffset = parseState.getStartingOffset();

		IParseRootNode root = new ParseRootNode( //
				IHTMLParserConstants.LANGUAGE, //
				new HTMLNode[0], //
				startingOffset, //
				startingOffset + source.length() //
		);

		try
		{
			fCurrentElement = root;

			this.parseAll();

			parseState.setParseResult(root);
		}
		finally
		{
			// clear for garbage collection
			fScanner = null;
			fTagScanner = null;
			fElementStack = null;
			fCurrentElement = null;
			fCurrentSymbol = null;
			fParseState = null;
		}

		return root;
	}

	protected void processSymbol(Symbol symbol) throws IOException, Exception
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
		}
	}

	protected void processLanguage(String language, short endToken) throws IOException, Exception
	{
		Symbol startTag = fCurrentSymbol;
		advance();

		int start = fCurrentSymbol.getStart();
		int end = start;
		short id = fCurrentSymbol.getId();
		while (id != endToken && id != HTMLTokens.EOF)
		{
			end = fCurrentSymbol.getEnd();
			advance();
			id = fCurrentSymbol.getId();
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
		return element;
	}

	private void parseAll() throws IOException, Exception
	{
		advance();
		while (fCurrentSymbol.getId() != HTMLTokens.EOF)
		{
			if (!isSkipped(fCurrentSymbol.getStart(), fCurrentSymbol.getEnd()))
			{
				processSymbol(fCurrentSymbol);
			}
			advance();
		}

		// if there are unclosed tags remaining, close them all
		List<HTMLElementNode> elementsToClose = new ArrayList<HTMLElementNode>();
		if (fCurrentElement instanceof HTMLElementNode)
		{
			elementsToClose.add((HTMLElementNode) fCurrentElement);
		}
		IParseNode node;
		for (int i = fElementStack.size() - 1; i >= 0; --i)
		{
			node = fElementStack.get(i);
			if (node instanceof HTMLElementNode)
			{
				elementsToClose.add((HTMLElementNode) node);
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
			for (IRange range : ranges)
			{
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
		try
		{
			String text = fScanner.getSource().get(start, end - start + 1);
			IParseNode node = ParserPoolFactory.parse(language, text);
			addOffset(node, start);
			return new IParseNode[] { node };
		}
		catch (java.lang.Exception e)
		{
		}
		return new IParseNode[0];
	}

	private void processComment()
	{
		HTMLCommentNode comment = new HTMLCommentNode(fCurrentSymbol.value.toString(), fCurrentSymbol.getStart(),
				fCurrentSymbol.getEnd());
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
			for (i = fElementStack.size() - 1; i >= 0; --i)
			{
				node = fElementStack.get(i);
				if (node instanceof HTMLElementNode && ((HTMLElementNode) node).getName().equalsIgnoreCase(tagName))
				{
					break;
				}
			}

			if (i >= 0)
			{
				// found the match, so closes it as well as all the open elements above
				if (fCurrentElement instanceof HTMLElementNode)
				{
					elementsToClose.add((HTMLElementNode) fCurrentElement);
				}

				for (int j = fElementStack.size() - 1; j >= i; --j)
				{
					node = fElementStack.get(j);
					if (node instanceof HTMLElementNode)
					{
						elementsToClose.add((HTMLElementNode) node);
					}
				}
			}
		}

		HTMLElementNode element;
		int currentStart = fCurrentSymbol.getStart();
		int currentEnd = fCurrentSymbol.getEnd();
		int size = elementsToClose.size();
		for (int i = 0; i < size; ++i)
		{
			element = elementsToClose.get(i);
			// adjusts the ending offset of the element to include the entire block
			if (i < size - 1)
			{
				element.setLocation(element.getStartingOffset(), currentStart - 1);
				element.setEndNode(currentStart - 1, currentStart - 1);
			}
			else
			{
				element.setLocation(element.getStartingOffset(), currentEnd);
				element.setEndNode(currentStart, currentEnd);
			}
			closeElement();
		}
	}

	private void processStyleTag() throws IOException, Exception
	{
		HTMLElementNode node = processCurrentTag();
		String language = null;
		String type = node.getAttributeValue(ATTR_TYPE);
		if (type == null || isInArray(type, CSS_VALID_TYPE_ATTR))
		{
			language = ICSSParserConstants.LANGUAGE;
		}
		else if (isJavaScript(node))
		{
			language = IJSParserConstants.LANGUAGE;
		}
		if (language != null)
		{
			processLanguage(language, HTMLTokens.STYLE_END);
		}
	}

	private void processScriptTag() throws IOException, Exception
	{
		HTMLElementNode node = processCurrentTag();
		String language = null;
		String type = node.getAttributeValue(ATTR_TYPE);
		if (type == null || isJavaScript(node))
		{
			language = IJSParserConstants.LANGUAGE;
		}
		if (language != null)
		{
			processLanguage(language, HTMLTokens.SCRIPT_END);
		}
	}

	private void parseAttribute(HTMLElementNode element, Symbol tagSymbol)
	{
		String tag = tagSymbol.value.toString();
		fTagScanner.setRange(new Document(tag), 0, tag.length());
		IToken token;
		Object data;
		String name = null, value = null;
		while (!(token = fTagScanner.nextToken()).isEOF())
		{
			data = token.getData();
			if (data == null)
			{
				continue;
			}

			if (data == TokenType.ATTR_NAME)
			{
				name = tag.substring(fTagScanner.getTokenOffset(),
						fTagScanner.getTokenOffset() + fTagScanner.getTokenLength());
			}
			else if (data == TokenType.ATTR_VALUE)
			{
				// found a pair
				int start = fTagScanner.getTokenOffset();
				value = tag.substring(start, start + fTagScanner.getTokenLength());
				// strips the quotation marks and any surrounding whitespaces
				value = value.substring(1, value.length() - 1).trim();
				element.setAttribute(name, value);

				// checks if we need to process the value as CSS
				if (isCSSAttribute(name))
				{
					String text = element.getName() + " {" + value + "}"; //$NON-NLS-1$ //$NON-NLS-2$
					IParseNode node = ParserPoolFactory.parse(ICSSParserConstants.LANGUAGE, text);
					// should always have a rule node
					if (node.hasChildren())
					{
						IParseNode rule = node.getChild(0);
						if (rule instanceof CSSRuleNode)
						{
							CSSDeclarationNode[] declarations = ((CSSRuleNode) rule).getDeclarations();
							for (CSSDeclarationNode declaration : declarations)
							{
								addOffset(declaration, tagSymbol.getStart() + start - (element.getName().length() + 1));
								element.addCSSStyleNode(declaration);
							}
						}
					}
				}
				// checks if we need to process the value as JS
				else if (isJSAttribute(name))
				{
					IParseNode node = ParserPoolFactory.parse(IJSParserConstants.LANGUAGE, value);
					IParseNode[] children = node.getChildren();
					for (IParseNode child : children)
					{
						addOffset(child, tagSymbol.getStart() + start + 1);
						element.addJSAttributeNode(child);
					}
				}
			}
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
		if (closeTagType == HTMLTagInfo.END_OPTIONAL && fCurrentElement != null
				&& tagName.equals(fCurrentElement.getNameNode().getName()))
		{
			// adjusts the ending offset of current element to include the entire block up to the start of the new tag
			((HTMLNode) fCurrentElement)
					.setLocation(fCurrentElement.getStartingOffset(), fCurrentSymbol.getStart() - 1);
			closeElement();
		}

		// adds the new parent as a child of the current parent
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(element);
		}

		if (closeTagType != HTMLTagInfo.END_FORBIDDEN)
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

	private void addOffset(IParseNode node, int offset)
	{
		if (node instanceof ParseNode)
		{
			ParseNode parseNode = (ParseNode) node;
			parseNode.addOffset(offset);
		}
		IParseNode[] children = node.getChildren();
		for (IParseNode child : children)
		{
			addOffset(child, offset);
		}
	}

	private static boolean isCSSAttribute(String name)
	{
		return cssAttributes.contains(name.toLowerCase());
	}

	private static boolean isJSAttribute(String name)
	{
		return jsAttributes.contains(name.toLowerCase());
	}

	private static boolean isJavaScript(HTMLElementNode node)
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
