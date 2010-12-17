package com.aptana.editor.yaml.parsing;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;

import com.aptana.parsing.ast.ParseNode;

public class MapParseNode extends ParseNode
{

	private MappingNode node;

	public MapParseNode(MappingNode node)
	{
		super(IYAMLParserConstants.LANGUAGE);
		this.node = node;
		traverse();
	}

	private void traverse()
	{
		List<NodeTuple> children = node.getValue();
		for (NodeTuple child : children)
		{
			addChild(YAMLParseRootNode.createNode(child));
		}
	}

	@Override
	public String getText()
	{
		return node.getTag().getValue();
	}
}
