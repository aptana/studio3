/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.yaml.parsing;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import com.aptana.editor.yaml.IYAMLConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;

public class MapParseNode extends YAMLNode
{

	private MappingNode node;

	public MapParseNode(MappingNode node, IParseState parseState)
	{
		super();
		setLocation(YAMLParseRootNode.getStart(node, parseState), YAMLParseRootNode.getEnd(node, parseState));
		this.node = node;
		traverse(parseState);
		if (getChildCount() > 0)
		{
			IParseNode lastChild = getChild(getChildCount() - 1);
			setLocation(getStartingOffset(), lastChild.getEndingOffset());
		}
	}

	private void traverse(IParseState parseState)
	{
		List<NodeTuple> children = node.getValue();
		for (NodeTuple child : children)
		{
			addChild(YAMLParseRootNode.createNode(child, parseState));
		}
	}

	@Override
	public String getText()
	{
		Tag tag = node.getTag();
		if (tag.startsWith(Tag.PREFIX))
		{
			return tag.getClassName();
		}
		return tag.getValue();
	}
}
