package com.aptana.editor.yaml.parsing;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import beaver.Symbol;

import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class YAMLParseRootNode extends ParseRootNode
{

	public YAMLParseRootNode(Node yamlRoot)
	{
		// FIXME The start and end are busted! Need to determine the real offset!
		super(IYAMLParserConstants.LANGUAGE, new Symbol[0], yamlRoot.getStartMark().getIndex(), yamlRoot.getEndMark()
				.getIndex());
		traverse(yamlRoot);
	}

	private void traverse(Node yamlRoot)
	{
		addChild(createNode(yamlRoot));
	}

	static IParseNode createNode(Node yamlNode)
	{
		if (yamlNode instanceof MappingNode)
		{
			MappingNode mapping = (MappingNode) yamlNode;
			return new MapParseNode(mapping);
		}
		else if (yamlNode instanceof SequenceNode)
		{
			SequenceNode mapping = (SequenceNode) yamlNode;
			return new SequenceParseNode(mapping);
		}
		return new ScalarParseNode((ScalarNode) yamlNode);
	}

	public static IParseNode createNode(NodeTuple child)
	{
		return new NodeTupleNode(child);
	}

}
