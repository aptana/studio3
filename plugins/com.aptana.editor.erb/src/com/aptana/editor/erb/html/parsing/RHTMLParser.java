package com.aptana.editor.erb.html.parsing;

import java.io.IOException;

import com.aptana.editor.common.parsing.CompositeParser;
import com.aptana.editor.erb.parsing.lexer.ERBTokens;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.editor.ruby.parsing.RubyParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseBaseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class RHTMLParser extends CompositeParser
{

	public RHTMLParser()
	{
		super(new RHTMLParserScanner(), new HTMLParser());
	}

	@Override
	protected IParseNode processEmbeddedlanguage(IParseState parseState) throws Exception
	{
		String source = new String(parseState.getSource());
		int startingOffset = parseState.getStartingOffset();
		IParseNode root = new ParseRootNode(IRubyParserConstants.LANGUAGE, new ParseBaseNode[0], startingOffset,
				startingOffset + source.length());

		advance();
		short id = getCurrentSymbol().getId();
		while (id != ERBTokens.EOF)
		{
			// only cares about ruby tokens
			switch (id)
			{
				case ERBTokens.RUBY:
					processRubyBlock(root);
					break;
			}
			advance();
			id = getCurrentSymbol().getId();
		}
		return root;
	}

	private void processRubyBlock(IParseNode root) throws IOException, Exception
	{
		advance();

		// finds the entire ruby block
		int start = getCurrentSymbol().getStart();
		int end = start;
		while (getCurrentSymbol().getId() != ERBTokens.RUBY_END)
		{
			end = getCurrentSymbol().getEnd();
			advance();
		}

		IParseNode result = getParseResult(new RubyParser(), start, end);
		if (result != null)
		{
			root.addChild(result);
		}
	}
}
