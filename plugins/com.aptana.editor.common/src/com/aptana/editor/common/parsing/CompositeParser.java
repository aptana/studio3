package com.aptana.editor.common.parsing;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import beaver.Symbol;
import beaver.Scanner.Exception;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;

public class CompositeParser implements IParser
{

	private CompositeParserScanner fScanner;
	private IParser fParser;

	private IParseNode fEmbeddedlanguageRoot;
	private Symbol fCurrentSymbol;

	public CompositeParser(CompositeParserScanner defaultScanner, IParser primaryParser)
	{
		fScanner = defaultScanner;
		fParser = primaryParser;
	}

	@Override
	public IParseNode parse(IParseState parseState) throws java.lang.Exception
	{
		String source = new String(parseState.getSource());
		fScanner.setSource(source);
		fCurrentSymbol = null;

		// first processes the embedded language
		fEmbeddedlanguageRoot = processEmbeddedlanguage(parseState);
		// then processes the source as normal
		IParseNode result = fParser.parse(parseState);

		if (fEmbeddedlanguageRoot != null)
		{
			// merges the tree for the embedded language into the result
			SortedMap<Integer, IParseNode> list = new TreeMap<Integer, IParseNode>();
			getAllNodes(result, list);

			IParseNode[] embeddedNodes = fEmbeddedlanguageRoot.getChildren();
			IParseNode parent;
			for (IParseNode node : embeddedNodes)
			{
				parent = findNode(list, node.getEndingOffset());
				if (parent != null)
				{
					parent.addChild(node);
				}
			}
		}

		return result;
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

	protected IParseNode getParseResult(IParser parser, int start, int end)
	{
		try
		{
			String text = fScanner.getSource().get(start, end - start + 1);
			ParseState parseState = new ParseState();
			parseState.setEditState(text, text, 0, 0);
			IParseNode node = parser.parse(parseState);
			addOffset(node, start);
			return node;
		}
		catch (java.lang.Exception e)
		{
		}
		return null;
	}

	private static void getAllNodes(IParseNode node, Map<Integer, IParseNode> list)
	{
		IParseNode[] children = node.getChildren();
		for (IParseNode child : children)
		{
			getAllNodes(child, list);
		}
		list.put(node.getStartingOffset(), node);
	}

	private static IParseNode findNode(SortedMap<Integer, IParseNode> list, int endOffset)
	{
		// the map is sorted by starting offset, and we want to find the node closest to the desired ending offset
		// without exceeding it as that will be the one which includes the embedded text
		list = list.headMap(endOffset);
		return list.get(list.lastKey());
	}

	private static void addOffset(IParseNode node, int offset)
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
