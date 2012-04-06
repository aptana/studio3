/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.parsing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseNode;

/**
 * NOTE: This class exists purely to compare the original merging logic to the new logic to verify that we are getting
 * the same behavior. If the new logic begins to diverge from the old behavior, then this class should be removed.
 */
public class OldCompositeParserTests extends CompositeParserTestBase
{
	private static class MergingParser extends CompositeParser implements IMerger
	{
		public MergingParser()
		{
			super(null, null);
		}

		public void merge(IParseRootNode ast, IParseNode[] embeddedNodes)
		{
			List<IParseNode> list = new LinkedList<IParseNode>();
			getAllNodes(ast, list);

			IParseNode parent;
			for (IParseNode embeddedNode : embeddedNodes)
			{
				parent = findNode(embeddedNode, list);
				if (parent == null)
				{
					// the node is at the end of the source
					ast.addChild(embeddedNode);
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

		protected IParseNode findNode(IParseNode node, List<IParseNode> list)
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

		protected void getAllNodes(IParseNode node, List<IParseNode> list)
		{
			IParseNode[] children = node.getChildren();
			for (IParseNode child : children)
			{
				getAllNodes(child, list);
			}
			list.add(node);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.parsing.CompositeParserTestBase#createMerger()
	 */
	@Override
	protected IMerger createMerger()
	{
		return new MergingParser();
	}
}
