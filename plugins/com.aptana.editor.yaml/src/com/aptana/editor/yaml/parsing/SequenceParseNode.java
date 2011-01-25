package com.aptana.editor.yaml.parsing;

import java.util.List;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.ParseNode;

public class SequenceParseNode extends ParseNode
{

	private SequenceNode node;

	public SequenceParseNode(SequenceNode node, IParseState parseState)
	{
		super(IYAMLParserConstants.LANGUAGE);
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
