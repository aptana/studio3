/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.xml.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;

import com.aptana.editor.xml.parsing.ast.XMLCDATANode;
import com.aptana.editor.xml.parsing.ast.XMLCommentNode;
import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.editor.xml.parsing.ast.XMLNode;
import com.aptana.editor.xml.parsing.lexer.XMLTokenType;
import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;
import com.aptana.parsing.lexer.Lexeme;

public class XMLParser extends AbstractParser
{
	public static final XMLNode[] NO_XML_NODES = new XMLNode[0];
	private XMLParserScanner fScanner;
	private XMLAttributeScanner fAttributeScanner;
	private Stack<IParseNode> fElementStack;
	private IProgressMonitor fMonitor;

	protected IParseNode fCurrentElement;
	protected Lexeme<XMLTokenType> fCurrentLexeme;

	private List<IParseNode> fCommentNodes;

	public XMLParser()
	{
		fCommentNodes = new ArrayList<IParseNode>();
	}

	/**
	 * advance
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	protected void advance() throws Exception
	{
		fCurrentLexeme = fScanner.nextLexeme();
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
	 * getSource
	 * 
	 * @param offset
	 * @param length
	 * @return
	 */
	protected String getSource(int offset, int length)
	{
		return fScanner.getSource(offset, length);
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
		fScanner = new XMLParserScanner();
		fAttributeScanner = new XMLAttributeScanner();
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
			fScanner = null;
			fAttributeScanner = null;
			fElementStack = null;
			fCurrentElement = null;
			fCurrentLexeme = null;
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

		while (fCurrentLexeme.getType() != XMLTokenType.EOF && !fMonitor.isCanceled())
		{
			processStatement();
			advance();
		}
	}

	/**
	 * parseAttributes
	 * 
	 * @param element
	 */
	protected void parseAttributes(XMLElementNode element)
	{
		String rawText = fCurrentLexeme.getText();

		fAttributeScanner.setRange(new Document(rawText), 0, rawText.length());

		IToken token = fAttributeScanner.nextToken();
		String name = null;
		String value = null;

		while (!token.isEOF())
		{
			Object data = token.getData();

			if (data instanceof XMLTokenType)
			{
				switch ((XMLTokenType) data)
				{
					case ATTRIBUTE:
						name = fAttributeScanner.getText();
						break;

					case VALUE:
						if (name != null)
						{
							value = fAttributeScanner.getText();
							value = value.substring(1, value.length() - 1);

							element.setAttribute(name, value);

							name = null;
							value = null;
						}
						break;
				}
			}

			token = fAttributeScanner.nextToken();
		}
	}

	/**
	 * processComment
	 */
	protected void processComment()
	{
		XMLCommentNode comment = new XMLCommentNode(fCurrentLexeme.getText(), fCurrentLexeme.getStartingOffset(),
				fCurrentLexeme.getEndingOffset());
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
			XMLCDATANode comment = new XMLCDATANode(fCurrentLexeme.getText(), fCurrentLexeme.getStartingOffset(),
					fCurrentLexeme.getEndingOffset());
			fCurrentElement.addChild(comment);
		}
	}

	/**
	 * processEndTag
	 */
	protected void processEndTag()
	{
		// adjusts the ending offset of current element to include the entire block
		((ParseNode) fCurrentElement).setLocation( //
				fCurrentElement.getStartingOffset(), //
				fCurrentLexeme.getEndingOffset() //
				);

		this.closeElement();
	}

	/**
	 * processStartTag
	 * 
	 * @param symbol
	 */
	protected void processStartTag()
	{
		XMLElementNode element = new XMLElementNode( //
				fCurrentLexeme.getText(), //
				fCurrentLexeme.getStartingOffset(), //
				fCurrentLexeme.getEndingOffset() //
		);

		this.parseAttributes(element);

		// pushes the element onto the stack
		this.openElement(element);

		if (element.isSelfClosing())
		{
			this.closeElement();
		}
	}

	/**
	 * processStatement
	 */
	protected void processStatement()
	{
		switch (fCurrentLexeme.getType())
		{
			case COMMENT:
				processComment();
				break;

			case CDATA:
				processCDATA();
				break;

			case START_TAG:
				processStartTag();
				break;

			case END_TAG:
				processEndTag();
				break;
		}
	}
}
