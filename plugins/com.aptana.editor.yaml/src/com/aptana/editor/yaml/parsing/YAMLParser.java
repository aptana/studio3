package com.aptana.editor.yaml.parsing;

import java.io.Reader;
import java.io.StringReader;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;

import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseRootNode;

public class YAMLParser extends AbstractParser
{

	private Yaml fParser;

	public YAMLParser()
	{
		fParser = new Yaml();
	}

	protected void parse(IParseState parseState, WorkingParseResult working) throws Exception
	{
		Reader reader = new StringReader(parseState.getSource());
		Node root = getSourceParser().compose(reader);

		// Convert YAML nodes to our own!
		YAMLParseRootNode yamlRoot = new YAMLParseRootNode(root, parseState);

		working.setParseResult(yamlRoot);
	}

	public Yaml getSourceParser()
	{
		return fParser;
	}
}
