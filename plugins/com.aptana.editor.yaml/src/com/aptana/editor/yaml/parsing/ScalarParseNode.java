package com.aptana.editor.yaml.parsing;

import org.yaml.snakeyaml.nodes.ScalarNode;

import com.aptana.parsing.ast.ParseNode;

public class ScalarParseNode extends ParseNode
{

	private ScalarNode node;

	public ScalarParseNode(ScalarNode node)
	{
		super(IYAMLParserConstants.LANGUAGE);
		setLocation(node.getStartMark().getIndex(), node.getEndMark().getIndex() - 1);
		this.node = node;
	}

	@Override
	public String getText()
	{
		return node.getValue();
	}

}
