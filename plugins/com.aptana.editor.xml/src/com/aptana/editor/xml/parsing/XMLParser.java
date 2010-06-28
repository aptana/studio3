package com.aptana.editor.xml.parsing;

import java.io.IOException;
import java.util.Stack;

import beaver.Symbol;
import beaver.Scanner.Exception;

import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.editor.xml.parsing.ast.XMLNode;
import com.aptana.editor.xml.parsing.lexer.XMLToken;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class XMLParser implements IParser
{

	private XMLParserScanner fScanner;
	private Stack<IParseNode> fElementStack;

	private IParseNode fCurrentElement;

	public XMLParser()
	{
		this(new XMLParserScanner());
	}

	protected XMLParser(XMLParserScanner scanner)
	{
		fScanner = scanner;
		fElementStack = new Stack<IParseNode>();
	}

	@Override
	public IParseNode parse(IParseState parseState) throws java.lang.Exception
	{
		String source = new String(parseState.getSource());
		fScanner.setSource(source);

		int startingOffset = parseState.getStartingOffset();
		// creates the root node
		IParseNode root = new ParseRootNode(IXMLParserConstants.LANGUAGE, new XMLNode[0], startingOffset,
				startingOffset + source.length());
		parseAll(root);
		// stores the result
		parseState.setParseResult(root);

		return root;
	}

	private void parseAll(IParseNode root) throws IOException, Exception
	{
		fElementStack.clear();
		fCurrentElement = root;

		Symbol symbol;
		while (XMLToken.getToken((symbol = fScanner.nextToken()).getId()) != XMLToken.EOF)
		{
			switch (XMLToken.getToken(symbol.getId()))
			{
				case START_TAG:
					processStartTag(symbol);
					break;
				case END_TAG:
					processEndTag(symbol);
					break;
			}
		}
	}

	private void processStartTag(Symbol symbol)
	{
		XMLElementNode element = new XMLElementNode(symbol.value.toString(), symbol.getStart(), symbol.getEnd());
		// pushes the element onto the stack
		openElement(element);
		if (element.isSelfClosing())
		{
			closeElement();
		}
	}

	private void processEndTag(Symbol symbol)
	{
		// adjusts the ending offset of current element to include the entire block
		((XMLElementNode) fCurrentElement).setLocation(fCurrentElement.getStartingOffset(), symbol.getEnd());
		closeElement();
	}

	/**
	 * Pushes the currently active element onto the stack and sets the specified element as the new active element.
	 * 
	 * @param element
	 */
	private void openElement(XMLElementNode element)
	{
		// adds the new parent as a child of the current parent
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(element);
		}

		fElementStack.push(fCurrentElement);
		fCurrentElement = element;
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
}
