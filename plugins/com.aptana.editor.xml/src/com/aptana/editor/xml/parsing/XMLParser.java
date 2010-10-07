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
package com.aptana.editor.xml.parsing;

import java.io.IOException;
import java.util.Stack;

import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.editor.xml.parsing.ast.XMLElementNode;
import com.aptana.editor.xml.parsing.ast.XMLNode;
import com.aptana.editor.xml.parsing.lexer.XMLToken;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
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

	public IParseRootNode parse(IParseState parseState) throws java.lang.Exception
	{
		String source = new String(parseState.getSource());
		fScanner.setSource(source);

		int startingOffset = parseState.getStartingOffset();
		// creates the root node
		IParseRootNode root = new ParseRootNode(IXMLParserConstants.LANGUAGE, new XMLNode[0], startingOffset,
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
