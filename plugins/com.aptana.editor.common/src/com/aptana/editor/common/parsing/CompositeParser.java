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
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;

public class CompositeParser implements IParser
{

	protected CompositeParserScanner fScanner;
	protected IParser fParser;

	protected IParseNode fEmbeddedlanguageRoot;
	protected Symbol fCurrentSymbol;

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
