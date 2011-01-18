package com.aptana.editor.yaml.parsing;

import org.yaml.snakeyaml.nodes.NodeTuple;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;

public class NodeTupleNode extends ParseNode
{

	private NodeTuple tuple;

	public NodeTupleNode(NodeTuple tuple, IParseState parseState)
	{
		super(IYAMLParserConstants.LANGUAGE);
		setLocation(YAMLParseRootNode.getStart(tuple.getKeyNode(), parseState), YAMLParseRootNode.getEnd(tuple.getValueNode(), parseState));
		this.tuple = tuple;
		traverse(parseState);
		if (getChildCount() > 0)
		{
			IParseNode lastChild = getChild(getChildCount() - 1);
			setLocation(getStartingOffset(), lastChild.getEndingOffset());
		}
	}

	private void traverse(IParseState parseState)
	{
		addChild(YAMLParseRootNode.createNode(tuple.getValueNode(), parseState));
	}

	@Override
	public String getText()
	{
		return YAMLParseRootNode.createNode(tuple.getKeyNode(), null).getText();
	}

}
