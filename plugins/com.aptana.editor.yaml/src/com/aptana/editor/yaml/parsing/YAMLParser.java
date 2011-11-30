package com.aptana.editor.yaml.parsing;

import java.io.Reader;
import java.io.StringReader;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseRootNode;

public class YAMLParser implements IParser
{

	private Yaml fParser;

	public YAMLParser()
	{
		fParser = new Yaml();
	}

	public IParseRootNode parse(IParseState parseState) throws Exception
	{
		Reader reader = new StringReader(parseState.getSource());
		Node root = getSourceParser().compose(reader);

		// Convert YAML nodes to our own!
		YAMLParseRootNode yamlRoot = new YAMLParseRootNode(root, parseState);

		parseState.setParseResult(yamlRoot);

		return yamlRoot;
	}

	public Yaml getSourceParser()
	{
		return fParser;
	}
}
