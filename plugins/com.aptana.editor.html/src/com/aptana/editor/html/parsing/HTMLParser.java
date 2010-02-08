package com.aptana.editor.html.parsing;

import java.io.IOException;
import java.util.Stack;

import beaver.Symbol;
import beaver.Scanner.Exception;

import com.aptana.editor.css.parsing.CSSParserFactory;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;
import com.aptana.editor.js.parsing.JSParserFactory;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class HTMLParser implements IParser
{

	private HTMLParserScanner fScanner;
	private HTMLParseState fParseState;
	private Stack<IParseNode> fElementStack;

	private IParseNode fCurrentElement;
	private Symbol fCurrentSymbol;

	public HTMLParser()
	{
		fScanner = new HTMLParserScanner();
		fElementStack = new Stack<IParseNode>();
	}

	@Override
	public IParseNode parse(IParseState parseState) throws java.lang.Exception
	{
		fParseState = (HTMLParseState) parseState;
		String source = new String(parseState.getSource());
		fScanner.setSource(source);

		int startingOffset = parseState.getStartingOffset();
		IParseNode root = new ParseRootNode(HTMLNode.LANGUAGE, new HTMLNode[0], startingOffset, startingOffset
				+ source.length());
		parseAll(root);
		parseState.setParseResult(root);

		return root;
	}

	private void parseAll(IParseNode root) throws IOException, Exception
	{
		fElementStack.clear();
		fCurrentElement = root;

		advance();
		while (fCurrentSymbol.getId() != HTMLTokens.EOF)
		{
			switch (fCurrentSymbol.getId())
			{
				case HTMLTokens.START_TAG:
					processStartTag();
					break;
				case HTMLTokens.END_TAG:
					processEndTag();
					break;
				case HTMLTokens.STYLE:
					processCSSStyle();
					break;
				case HTMLTokens.SCRIPT:
					processJSScript();
					break;
				default:
					advance();
			}
		}
	}

	private void advance() throws IOException, Exception
	{
		fCurrentSymbol = fScanner.nextToken();
	}

	private void processStartTag() throws IOException, Exception
	{
		HTMLElementNode element = new HTMLElementNode(fCurrentSymbol.value.toString(), fCurrentSymbol.getStart(),
				fCurrentSymbol.getEnd());
		// pushes the element onto the stack
		openElement(element);

		advance();
	}

	private void processEndTag() throws IOException, Exception
	{
		// only closes current element if current lexeme and element have the same tag name
		if (fCurrentElement != null)
		{
			String tagName = HTMLUtils.stripTagEndings(fCurrentSymbol.value.toString());
			if ((fCurrentElement instanceof HTMLElementNode)
					&& ((HTMLElementNode) fCurrentElement).getName().equalsIgnoreCase(tagName))
			{
				closeElement();
			}
		}
		advance();
	}

	private void processCSSStyle() throws IOException, Exception
	{
		Symbol styleTag = fCurrentSymbol;
		advance();

		int start = fCurrentSymbol.getStart();
		int end = start;
		while (fCurrentSymbol.getId() == HTMLTokens.STYLE)
		{
			end = fCurrentSymbol.getEnd();
			advance();
		}

		IParseNode[] nested = new IParseNode[0];
		if (start != end)
		{
			// has CSS content
			try
			{
				String text = fScanner.getSource().get(start, end - start + 1);
				ParseState parseState = new ParseState();
				parseState.setEditState(text, text, 0, 0);
				IParseNode node = CSSParserFactory.getInstance().getParser().parse(parseState);
				addOffset(node, start);
				nested = new IParseNode[] { node };
			}
			catch (java.lang.Exception e)
			{
			}
		}
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(new HTMLSpecialNode(HTMLSpecialNode.CSS, nested, styleTag.getStart(), styleTag
					.getEnd()));
		}
		advance();
	}

	private void processJSScript() throws IOException, Exception
	{
		Symbol scriptTag = fCurrentSymbol;
		advance();

		int start = fCurrentSymbol.getStart();
		int end = start;
		while (fCurrentSymbol.getId() == HTMLTokens.SCRIPT)
		{
			end = fCurrentSymbol.getEnd();
			advance();
		}

		IParseNode[] nested = new IParseNode[0];
		if (start != end)
		{
			// has JS content
			try
			{
				String text = fScanner.getSource().get(start, end - start + 1);
				ParseState parseState = new ParseState();
				parseState.setEditState(text, text, 0, 0);
				IParseNode node = JSParserFactory.getInstance().getParser().parse(parseState);
				addOffset(node, start);
				nested = new IParseNode[] { node };
			}
			catch (java.lang.Exception e)
			{
			}
		}
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(new HTMLSpecialNode(HTMLSpecialNode.JS, nested, scriptTag.getStart(), scriptTag
					.getEnd()));
		}
		advance();
	}

	/**
	 * Pushes the currently active element onto the stack and sets the specified element as the new active element.
	 * 
	 * @param element
	 */
	private void openElement(HTMLElementNode element)
	{
		// adds the new parent as a child of the current parent
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(element);
		}

		if (fParseState.getCloseTagType(element.getName()) != HTMLTagInfo.END_FORBIDDEN)
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
		if (node instanceof ParseBaseNode)
		{
			ParseBaseNode parseNode = (ParseBaseNode) node;
			parseNode.addOffset(offset);
		}
		IParseNode[] children = node.getChildren();
		for (IParseNode child : children)
		{
			addOffset(child, offset);
		}
	}
}
