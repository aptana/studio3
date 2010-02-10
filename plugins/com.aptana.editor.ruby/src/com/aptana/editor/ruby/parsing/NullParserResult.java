package com.aptana.editor.ruby.parsing;

import java.util.Collections;
import java.util.List;

import org.jrubyparser.ast.Node;
import org.jrubyparser.parser.ParserResult;
import org.jrubyparser.StaticScope;

public class NullParserResult extends ParserResult
{

	@Override
	public Node getAST()
	{
		return null;
	}

	@Override
	public List<Node> getBeginNodes()
	{
		return Collections.emptyList();
	}

	@Override
	public int getEndOffset()
	{
		return 0;
	}

	@Override
	public StaticScope getScope()
	{
		return null;
	}
}
