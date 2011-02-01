/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import beaver.Symbol;
import beaver.Scanner.Exception;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
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
		fScanner.getTokenScanner().reset();
		String source = new String(parseState.getSource());
		fScanner.setSource(source);
		fCurrentSymbol = null;

		// first processes the embedded language
		fEmbeddedlanguageRoot = processEmbeddedlanguage(parseState);

		// then processes the source as normal, but skips the nodes returned from embedded language parsing
		IParseNode[] embeddedNodes = null;
		if (fEmbeddedlanguageRoot != null)
		{
			embeddedNodes = fEmbeddedlanguageRoot.getChildren();
			if (embeddedNodes.length == 0)
			{
				embeddedNodes = new IParseNode[] { fEmbeddedlanguageRoot };
			}
			((ParseState) parseState).setSkippedRanges(embeddedNodes);
		}
		IParseRootNode result = primaryParse(parseState);
		if (embeddedNodes != null)
		{
			((ParseState) parseState).setSkippedRanges(null);
		}

		// merges the tree for the embedded language into the result
		if (fEmbeddedlanguageRoot != null)
		{
			List<IParseNode> list = new LinkedList<IParseNode>();
			getAllNodes(result, list);

			IParseNode parent;
			for (IParseNode embeddedNode : embeddedNodes)
			{
				parent = findNode(embeddedNode, list);
				if (parent == null)
				{
					// the node is at the end of the source
					result.addChild(embeddedNode);
				}
				else
				{
					// inserts the node into the right position
					List<IParseNode> newList = new ArrayList<IParseNode>();
					IParseNode[] children = parent.getChildren();
					boolean found = false;
					int embeddedStart = embeddedNode.getStartingOffset();
					int embeddedEnd = embeddedNode.getEndingOffset();
					for (IParseNode primaryNodeChild : children)
					{
						if (!found && primaryNodeChild.getStartingOffset() > embeddedStart)
						{
							found = true;
							newList.add(embeddedNode);
						}
						if (primaryNodeChild.getStartingOffset() > embeddedEnd)
						{
							newList.add(primaryNodeChild);
						}
						else if (primaryNodeChild.getStartingOffset() < embeddedStart
								&& (primaryNodeChild.getEndingOffset() < embeddedStart || primaryNodeChild
										.getEndingOffset() > embeddedEnd))
						{
							newList.add(primaryNodeChild);
						}

					}
					if (!found)
					{
						// the node locates at the end of the parent node
						newList.add(embeddedNode);
					}
					((ParseNode) parent).setChildren(newList.toArray(new IParseNode[newList.size()]));
				}
			}
		}

		return result;
	}

	/**
	 * primaryParse
	 * 
	 * @param parseState
	 * @return
	 * @throws java.lang.Exception
	 */
	private IParseRootNode primaryParse(IParseState parseState) throws java.lang.Exception
	{
		return ParserPoolFactory.parse(fParserLanguage, parseState);
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

	protected IParseRootNode getParseResult(String language, int start, int end)
	{
		try
		{
			String text = fScanner.getSource().get(start, end - start + 1);
			IParseRootNode node = ParserPoolFactory.parse(language, text);
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
