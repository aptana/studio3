package com.aptana.editor.ruby.parsing;

import com.aptana.editor.ruby.parsing.ast.RubyScript;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ast.IParseNode;

public class RubyParser implements IParser
{

	private RubySourceParser fParser;

	public RubyParser()
	{
		fParser = new RubySourceParser();
	}

	@Override
	public IParseNode parse(IParseState parseState) throws Exception
	{
		String source = new String(parseState.getSource());
		RubyScript root = new RubyScript(parseState.getStartingOffset(), parseState.getStartingOffset()
				+ source.length());
		RubyStructureBuilder builder = new RubyStructureBuilder(root);
		SourceElementVisitor visitor = new SourceElementVisitor(builder);
		visitor.acceptNode(getSourceParser().parse(source).getAST());
		parseState.setParseResult(root);

		return root;
	}

	public RubySourceParser getSourceParser()
	{
		return fParser;
	}
}
