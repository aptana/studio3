package com.aptana.editor.yaml.parsing;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import beaver.Symbol;

import com.aptana.editor.yaml.IYAMLConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class YAMLParseRootNode extends ParseRootNode
{

	public YAMLParseRootNode(Node yamlRoot, IParseState parseState)
	{
		super(new Symbol[0], parseState.getStartingOffset(), parseState.getStartingOffset()
				+ parseState.getSource().length());
		traverse(yamlRoot, parseState);
	}

	public String getLanguage()
	{
		return IYAMLConstants.CONTENT_TYPE_YAML;
	}

	private void traverse(Node yamlRoot, IParseState parseState)
	{
		addChild(createNode(yamlRoot, parseState));
	}

	static IParseNode createNode(Node yamlNode, IParseState parseState)
	{
		if (yamlNode instanceof MappingNode)
		{
			MappingNode mapping = (MappingNode) yamlNode;
			return new MapParseNode(mapping, parseState);
		}
		else if (yamlNode instanceof SequenceNode)
		{
			SequenceNode mapping = (SequenceNode) yamlNode;
			return new SequenceParseNode(mapping, parseState);
		}
		return new ScalarParseNode((ScalarNode) yamlNode, parseState);
	}

	public static IParseNode createNode(NodeTuple child, IParseState parseState)
	{
		return new NodeTupleNode(child, parseState);
	}

	static int getStart(Node node, IParseState parseState)
	{
		Mark startMark = node.getStartMark();
		int lineOffset = 0;
		if (parseState != null)
		{
			lineOffset = offsetOfLine(startMark.getLine(), parseState.getSource());
		}
		return lineOffset + startMark.getColumn();
	}

	private static int offsetOfLine(int line, String source)
	{

		try
		{
			return new Document(source).getLineOffset(line);
		}
		catch (BadLocationException e)
		{
			// ignore
		}
		// cheat for first line
		if (line == 0)
		{
			return 0;
		}
		int curLine = 0;
		for (int i = 0; i < source.length(); i++)
		{
			if (curLine == line)
			{
				return i;
			}
			char c = source.charAt(i);
			if (c == '\r')
			{
				// peek to see if next is '\n'
				if (((i + 1) < source.length()) && source.charAt(i + 1) == '\n')
				{
					i += 1;
				}
				curLine++;
			}
			else if (c == '\n')
			{
				curLine++;
			}
		}
		return 0;
	}

	static int getEnd(Node node, IParseState parseState)
	{
		Mark endMark = node.getEndMark();
		int lineOffset = 0;
		if (parseState != null)
		{
			lineOffset = offsetOfLine(endMark.getLine(), parseState.getSource());
			return Math.min(lineOffset + endMark.getColumn() - 1, parseState.getSource().length());
		}
		return lineOffset + endMark.getColumn() - 1;
	}

}
