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
package com.aptana.editor.common.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;

public class CompositeParser implements IParser
{

	protected CompositeParserScanner fScanner;
	private String fParserLanguage;

	protected IParseNode fEmbeddedlanguageRoot;
	protected Symbol fCurrentSymbol;

	public CompositeParser(CompositeParserScanner defaultScanner, String primaryParserLanguage)
	{
		fScanner = defaultScanner;
		fParserLanguage = primaryParserLanguage;
	}

	public IParseRootNode parse(IParseState parseState) throws java.lang.Exception
	{
		String source = new String(parseState.getSource());
		fScanner.setSource(source);
		fCurrentSymbol = null;

		// first processes the embedded language
		fEmbeddedlanguageRoot = processEmbeddedlanguage(parseState);
		// then processes the source as normal
		IParseRootNode result = primaryParse(parseState);

		if (fEmbeddedlanguageRoot != null)
		{
			// merges the tree for the embedded language into the result
			List<IParseNode> list = new LinkedList<IParseNode>();
			getAllNodes(result, list);

			IParseNode[] embeddedNodes = fEmbeddedlanguageRoot.getChildren();
			IParseNode parent;
			for (IParseNode node : embeddedNodes)
			{
				parent = findNode(node, list);
				if (parent == null)
				{
					// the node is at the end of the source
					result.addChild(node);
				}
				else
				{
					// inserts the node into the right position
					List<IParseNode> newList = new ArrayList<IParseNode>();
					IParseNode[] children = parent.getChildren();
					boolean found = false;
					for (IParseNode child : children)
					{
						if (!found && child.getStartingOffset() > node.getStartingOffset())
						{
							found = true;
							newList.add(node);
						}
						newList.add(child);
					}
					if (!found)
					{
						// the node locates at the end of the parent node
						newList.add(node);
					}
					((ParseNode) parent).setChildren(newList.toArray(new IParseNode[newList.size()]));
				}
			}
		}

		return result;
	}

	private IParseRootNode primaryParse(IParseState parseState) throws java.lang.Exception
	{
		IParserPool pool = ParserPoolFactory.getInstance().getParserPool(fParserLanguage);
		if (pool != null)
		{
			IParser parser = null;
			try
			{
				parser = pool.checkOut();
				return parser.parse(parseState);
			}
			finally
			{
				if (parser != null)
				{
					pool.checkIn(parser);
				}
			}
		}
		return null;
	}

	/**
	 * The method is for finding and processing embedded language inside the primary one and stores them in a separate
	 * node. The subclass should override.
	 */
	protected IParseNode processEmbeddedlanguage(IParseState parseState) throws java.lang.Exception
	{
		return null;
	}

	protected void advance() throws IOException, Exception
	{
		fCurrentSymbol = fScanner.nextToken();
	}

	protected Symbol getCurrentSymbol()
	{
		return fCurrentSymbol;
	}

	protected IParseRootNode getParseResult(IParser parser, int start, int end)
	{
		try
		{
			String text = fScanner.getSource().get(start, end - start + 1);
			ParseState parseState = new ParseState();
			parseState.setEditState(text, text, 0, 0);
			IParseRootNode node = parser.parse(parseState);
			addOffset(node, start);
			return node;
		}
		catch (java.lang.Exception e)
		{
		}
		return null;
	}

	protected static void getAllNodes(IParseNode node, List<IParseNode> list)
	{
		IParseNode[] children = node.getChildren();
		for (IParseNode child : children)
		{
			getAllNodes(child, list);
		}
		list.add(node);
	}

	protected static IParseNode findNode(IParseNode node, List<IParseNode> list)
	{
		for (IParseNode element : list)
		{
			if (element.getStartingOffset() <= node.getStartingOffset()
					&& element.getEndingOffset() >= node.getEndingOffset())
			{
				return element;
			}
		}
		return null;
	}

	protected static void addOffset(IParseNode node, int offset)
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
}
