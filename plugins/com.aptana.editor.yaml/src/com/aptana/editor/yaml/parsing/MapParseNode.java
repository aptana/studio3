package com.aptana.editor.yaml.parsing;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;

public class MapParseNode extends ParseNode
{

	private MappingNode node;

	public MapParseNode(MappingNode node, IParseState parseState)
	{
		super(IYAMLParserConstants.LANGUAGE);
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
