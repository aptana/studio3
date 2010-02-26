package com.aptana.editor.html.parsing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import beaver.Symbol;
import beaver.Scanner.Exception;

import com.aptana.editor.css.parsing.CSSParserFactory;
import com.aptana.editor.css.parsing.ICSSParserConstants;
import com.aptana.editor.html.parsing.ast.HTMLElementNode;
import com.aptana.editor.html.parsing.ast.HTMLNode;
import com.aptana.editor.html.parsing.ast.HTMLSpecialNode;
import com.aptana.editor.html.parsing.lexer.HTMLTokens;
import com.aptana.editor.js.parsing.IJSParserConstants;
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

	private Map<String, IParser> fLanguageParsers;

	public HTMLParser()
	{
		this(new HTMLParserScanner());
	}

	protected HTMLParser(HTMLParserScanner scanner)
	{
		fScanner = scanner;
		fElementStack = new Stack<IParseNode>();
		fLanguageParsers = new HashMap<String, IParser>();
		fLanguageParsers.put(ICSSParserConstants.LANGUAGE, CSSParserFactory.getInstance().getParser());
		fLanguageParsers.put(IJSParserConstants.LANGUAGE, JSParserFactory.getInstance().getParser());
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

	protected void processSymbol(Symbol symbol) throws IOException, Exception
	{
		switch (symbol.getId())
		{
			case HTMLTokens.START_TAG:
				processStartTag();
				break;
			case HTMLTokens.END_TAG:
				processEndTag();
				break;
			case HTMLTokens.STYLE:
				processLanguage(ICSSParserConstants.LANGUAGE, HTMLTokens.STYLE_END);
				break;
			case HTMLTokens.SCRIPT:
				processLanguage(IJSParserConstants.LANGUAGE, HTMLTokens.SCRIPT_END);
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

		IParseNode[] nested = getParseResult(fLanguageParsers.get(language), start, end);
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(new HTMLSpecialNode(startTag, nested, startTag.getStart(), fCurrentSymbol.getEnd()));
		}
	}

	protected void addLanguageParser(String language, IParser parser)
	{
		fLanguageParsers.put(language, parser);
	}

	private void parseAll(IParseNode root) throws IOException, Exception
	{
		fElementStack.clear();
		fCurrentElement = root;

		advance();
		while (fCurrentSymbol.getId() != HTMLTokens.EOF)
		{
			processSymbol(fCurrentSymbol);
			advance();
		}
	}

	private void advance() throws IOException, Exception
	{
		fCurrentSymbol = fScanner.nextToken();
	}

	private IParseNode[] getParseResult(IParser parser, int start, int end)
	{
		try
		{
			String text = fScanner.getSource().get(start, end - start + 1);
			ParseState parseState = new ParseState();
			parseState.setEditState(text, text, 0, 0);
			IParseNode node = parser.parse(parseState);
			addOffset(node, start);
			return new IParseNode[] { node };
		}
		catch (java.lang.Exception e)
		{
		}
		return new IParseNode[0];
	}

	private void processStartTag() throws IOException, Exception
	{
		HTMLElementNode element = new HTMLElementNode(fCurrentSymbol, fCurrentSymbol.getStart(), fCurrentSymbol
				.getEnd());
		// pushes the element onto the stack
		openElement(element);
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
				// adjusts the ending offset of current element to include the entire block
				((HTMLElementNode) fCurrentElement).setLocation(fCurrentElement.getStartingOffset(), fCurrentSymbol.getEnd());
				closeElement();
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
