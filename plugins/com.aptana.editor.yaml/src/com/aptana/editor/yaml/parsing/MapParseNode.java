package com.aptana.editor.yaml.parsing;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import com.aptana.parsing.ast.ParseNode;

public class MapParseNode extends ParseNode
{

	private MappingNode node;

	public MapParseNode(MappingNode node)
	{
		super(IYAMLParserConstants.LANGUAGE);
		setLocation(node.getStartMark().getIndex(), node.getEndMark().getIndex() - 1);
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
		Tag tag = node.getTag();
		if (tag.startsWith(Tag.PREFIX))
		{
			return tag.getClassName();
		}
		return tag.getValue();
	}
}
