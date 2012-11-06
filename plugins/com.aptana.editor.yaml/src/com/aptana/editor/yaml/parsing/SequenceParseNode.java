/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.parsing;

import java.util.List;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

import com.aptana.parsing.IParseState;

public class SequenceParseNode extends YAMLNode
{

	private SequenceNode node;

	public SequenceParseNode(SequenceNode node, IParseState parseState)
	{
		super();
		setLocation(YAMLParseRootNode.getStart(node, parseState), YAMLParseRootNode.getEnd(node, parseState));
		this.node = node;
		traverse(parseState);
	}

	private void traverse(IParseState parseState)
	{
		List<Node> children = node.getValue();
		for (Node child : children)
		{
			addChild(YAMLParseRootNode.createNode(child, parseState));
		}
	}

	@Override
	public String getText()
	{
		return node.getTag().getValue();
	}

}
