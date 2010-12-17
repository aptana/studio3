package com.aptana.editor.yaml.parsing;

import java.util.List;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

import com.aptana.parsing.ast.ParseNode;

public class SequenceParseNode extends ParseNode
{

	private SequenceNode node;

	public SequenceParseNode(SequenceNode node)
	{
		super(IYAMLParserConstants.LANGUAGE);
		setLocation(node.getStartMark().getIndex(), node.getEndMark().getIndex());
		this.node = node;
		traverse();
	}

	private void traverse()
	{
		List<Node> children = node.getValue();
		for (Node child : children)
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
