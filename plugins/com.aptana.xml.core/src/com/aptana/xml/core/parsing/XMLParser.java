/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;

import beaver.Symbol;

import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.xml.core.parsing.ast.XMLCDATANode;
import com.aptana.xml.core.parsing.ast.XMLCommentNode;
import com.aptana.xml.core.parsing.ast.XMLElementNode;
import com.aptana.xml.core.parsing.ast.XMLNode;
import com.aptana.xml.core.parsing.ast.XMLParseRootNode;

public class XMLParser extends AbstractParser
{
	public static final XMLNode[] NO_XML_NODES = new XMLNode[0];
	private Stack<IParseNode> fElementStack;
	private IProgressMonitor fMonitor;
	private XMLScanner fScanner;

	protected IParseNode fCurrentElement;

	private List<IParseNode> fCommentNodes;
	protected Symbol fCurrentLexeme;

	public XMLParser()
	{
		fCommentNodes = new ArrayList<IParseNode>();
		fScanner = new XMLScanner();
	}

	/**
	 * Closes the element that is on the top of the stack.
	 */
	protected void closeElement()
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

	/**
	 * Pushes the currently active element onto the stack and sets the specified element as the new active element.
	 * 
	 * @param element
	 */
	protected void openElement(XMLElementNode element)
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
	 * parse
	 * 
	 * @throws Exception
	 */
	protected void parse(IParseState parseState, WorkingParseResult working) throws Exception
	{
		fMonitor = parseState.getProgressMonitor();
		fElementStack = new Stack<IParseNode>();

		// create scanner and apply source
		String source = parseState.getSource();
		fScanner.setSource(source);

		int startingOffset = parseState.getStartingOffset();

		// creates the root node
		ParseRootNode root = new XMLParseRootNode(startingOffset, startingOffset + source.length() - 1);
		try
		{
			fCurrentElement = root;

			parseAll(root);
			root.setCommentNodes(fCommentNodes.toArray(new IParseNode[fCommentNodes.size()]));

			working.setParseResult(root);
		}
		finally
		{
			fMonitor = null;
			fElementStack = null;
			fCurrentElement = null;
			fCommentNodes.clear();
		}
	}

	/**
	 * parseAll
	 * 
	 * @param root
	 * @throws Exception
	 * @throws IOException
	 */
	protected void parseAll(IParseNode root) throws IOException, Exception
	{
		advance();

		while (fCurrentLexeme.getId() != Terminals.EOF && !fMonitor.isCanceled())
		{
			processStatement();
			advance();
		}
	}

	protected void advance() throws beaver.Scanner.Exception, IOException
	{
		fCurrentLexeme = fScanner.nextToken();
	}

	/**
	 * parseAttributes
	 * 
	 * @param element
	 * @throws IOException
	 * @throws beaver.Scanner.Exception
	 */
	protected Map<String, String> parseAttributes() throws beaver.Scanner.Exception, IOException
	{
		Map<String, String> result = new HashMap<String, String>();
		String name = null;
		// Keep advancing until we hit EOF or GREATER or SLASH_GREATER
		while (true)
		{
			advance();
			switch (fCurrentLexeme.getId())
			{
				case Terminals.EOF:
				case Terminals.GREATER:
				case Terminals.SLASH_GREATER:
					return result;

				case Terminals.IDENTIFIER:
				case Terminals.TEXT:
					name = (String) fCurrentLexeme.value;
					break;

				case Terminals.STRING:
					result.put(name, (String) fCurrentLexeme.value);
					name = null;
					break;

				default:
					break;
			}
		}
	}

	/**
	 * processComment
	 */
	protected void processComment()
	{
		XMLCommentNode comment = new XMLCommentNode((String) fCurrentLexeme.value, fCurrentLexeme.getStart(),
				fCurrentLexeme.getEnd());
		fCommentNodes.add(comment);
		if (fCurrentElement != null)
		{
			fCurrentElement.addChild(comment);
		}
	}

	/**
	 * processCDATA
	 */
	protected void processCDATA()
	{
		if (fCurrentElement != null)
		{
			XMLCDATANode comment = new XMLCDATANode((String) fCurrentLexeme.value, fCurrentLexeme.getStart(),
					fCurrentLexeme.getEnd());
			fCurrentElement.addChild(comment);
		}
	}

	/**
	 * processEndTag
	 * 
	 * @throws IOException
	 * @throws beaver.Scanner.Exception
	 */
	protected void processEndTag() throws beaver.Scanner.Exception, IOException
	{
		// read element name
		advance();
		// read '>'
		advance();

		// adjusts the ending offset of current element to include the entire block
		((ParseNode) fCurrentElement).setLocation( //
				fCurrentElement.getStartingOffset(), //
				fCurrentLexeme.getEnd() //
				);

		closeElement();
	}

	/**
	 * processStartTag
	 * 
	 * @param symbol
	 * @throws IOException
	 * @throws beaver.Scanner.Exception
	 */
	protected void processStartTag() throws beaver.Scanner.Exception, IOException
	{

		int start = fCurrentLexeme.getStart();
		// grab the element name
		advance();
		String tagName = (String) fCurrentLexeme.value;

		Map<String, String> attrs = parseAttributes();

		XMLElementNode element = new XMLElementNode(tagName, start, fCurrentLexeme);
		for (Map.Entry<String, String> pair : attrs.entrySet())
		{
			element.setAttribute(pair.getKey(), pair.getValue());
		}
		// pushes the element onto the stack
		openElement(element);

		if (element.isSelfClosing())
		{
			closeElement();
		}
	}

	/**
	 * processStatement
	 * 
	 * @throws IOException
	 * @throws beaver.Scanner.Exception
	 */
	protected void processStatement() throws beaver.Scanner.Exception, IOException
	{
		switch (fCurrentLexeme.getId())
		{
			case Terminals.COMMENT:
				processComment();
				break;

			case Terminals.CDATA:
				processCDATA();
				break;

			case Terminals.LESS:
				processStartTag();
				break;

			case Terminals.LESS_SLASH:
				processEndTag();
				break;
		}
	}
}
