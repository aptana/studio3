/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.core.util.ArrayUtil;
import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ParserPoolFactory;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;

public class CompositeParser extends AbstractParser
{
	private CompositeParserScanner fScanner;
	private String fParserLanguage;
	private IParseNode fEmbeddedlanguageRoot;
	private Symbol fCurrentSymbol;

	public CompositeParser(CompositeParserScanner defaultScanner, String primaryParserLanguage)
	{
		fScanner = defaultScanner;
		fParserLanguage = primaryParserLanguage;
	}

	protected void parse(IParseState parseState, WorkingParseResult working) throws java.lang.Exception
	{
		fScanner.getTokenScanner().reset();
		fScanner.setSource(parseState.getSource());
		fCurrentSymbol = null;

		// first process the embedded language
		fEmbeddedlanguageRoot = processEmbeddedlanguage(parseState, working);

		// setup to skip the embedded language nodes before doing the primary parse
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

		// process source as normal
		ParseResult parseResult = primaryParse(parseState);
		working.addAllErrors(parseResult.getErrors());
		IParseRootNode result = parseResult.getRootNode();

		// reset skip regions now that they're no longer needed
		if (embeddedNodes != null)
		{
			((ParseState) parseState).setSkippedRanges(null);
		}

		// merge the embedded language nodes into the primary AST
		if (fEmbeddedlanguageRoot != null)
		{
			mergeEmbeddedNodes(result, embeddedNodes);
		}
		working.setParseResult(result);
	}

	/**
	 * mergeEmbeddedNodes
	 * 
	 * @param ast
	 * @param embeddedNodes
	 */
	protected void mergeEmbeddedNodes(IParseRootNode ast, IParseNode[] embeddedNodes)
	{
		if (ArrayUtil.isEmpty(embeddedNodes) || ast == null)
		{
			return;
		}

		for (IParseNode embeddedNode : embeddedNodes)
		{
			IParseNode parent = ast.getNodeAtOffset(embeddedNode.getStartingOffset());

			// fix-up results to preserve behavior of old "getAllNodes" method. Basically move back up the AST until we
			// find a node that completely encloses the embedded node
			// @formatter:off
			while (
					parent != null
				&& !(parent instanceof IParseRootNode)
				&& (embeddedNode.getStartingOffset() <= parent.getStartingOffset() || embeddedNode.getEndingOffset() >= parent.getEndingOffset())
			)
			{
				parent = parent.getParent();
			}
			// @formatter:on

			if (parent == null)
			{
				// the node is at the end of the source
				ast.addChild(embeddedNode);
			}
			else
			{
				// inserts the node into the right position
				List<IParseNode> newList = new ArrayList<IParseNode>();
				boolean found = false;
				int embeddedStart = embeddedNode.getStartingOffset();
				int embeddedEnd = embeddedNode.getEndingOffset();

				for (IParseNode primaryNodeChild : parent)
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

	/**
	 * primaryParse
	 * 
	 * @param parseState
	 * @return
	 * @throws java.lang.Exception
	 */
	private ParseResult primaryParse(IParseState parseState) throws java.lang.Exception
	{
		return ParserPoolFactory.parse(fParserLanguage, parseState);
	}

	/**
	 * The method is for finding and processing embedded language inside the primary one and stores them in a separate
	 * node. The subclass should override.
	 */
	protected IParseNode processEmbeddedlanguage(IParseState parseState, WorkingParseResult working) throws java.lang.Exception
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

	protected ParseResult getParseResult(String language, int start, int end)
	{
		try
		{
			String text = fScanner.getSource().get(start, end - start + 1);
			return ParserPoolFactory.parse(language, text, start);
		}
		catch (java.lang.Exception e)
		{
		}
		return null;
	}
}
