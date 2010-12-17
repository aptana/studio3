package com.aptana.editor.yaml.parsing;

import org.yaml.snakeyaml.nodes.NodeTuple;

import com.aptana.parsing.ast.ParseNode;

public class NodeTupleNode extends ParseNode
{

	private NodeTuple tuple;

	public NodeTupleNode(NodeTuple tuple)
	{
		super(IYAMLParserConstants.LANGUAGE);
		setLocation(tuple.getKeyNode().getStartMark().getIndex(), tuple.getValueNode().getEndMark().getIndex() - 1);
		this.tuple = tuple;
		traverse();
	}

	private void traverse()
	{
		addChild(YAMLParseRootNode.createNode(tuple.getValueNode()));
	}

	@Override
	public String getText()
	{
		return YAMLParseRootNode.createNode(tuple.getKeyNode()).getText();
	}

}
