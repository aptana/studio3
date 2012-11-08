/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.parsing;

import org.yaml.snakeyaml.nodes.NodeTuple;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;

public class NodeTupleNode extends YAMLNode
{

	private NodeTuple tuple;

	public NodeTupleNode(NodeTuple tuple, IParseState parseState)
	{
		super();
		int start = YAMLParseRootNode.getStart(tuple.getKeyNode(), parseState);
		int end = YAMLParseRootNode.getEnd(tuple.getValueNode(), parseState);
		if (end < start)
		{
			end = YAMLParseRootNode.getEnd(tuple.getKeyNode(), parseState);
		}
		setLocation(start, end);
		this.tuple = tuple;
		traverse(parseState);
		if (getChildCount() > 0)
		{
			IParseNode lastChild = getChild(getChildCount() - 1);
			end = lastChild.getEndingOffset();
			if (end > start)
			{
				setLocation(start, end);
			}
		}
	}

	private void traverse(IParseState parseState)
	{
		addChild(YAMLParseRootNode.createNode(tuple.getValueNode(), parseState));
	}

	@Override
	public String getText()
	{
		return YAMLParseRootNode.createNode(tuple.getKeyNode(), null).getText();
	}

}
