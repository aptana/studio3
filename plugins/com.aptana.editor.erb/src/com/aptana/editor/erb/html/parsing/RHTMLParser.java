package com.aptana.editor.erb.html.parsing;

import java.io.IOException;

import beaver.Symbol;

import com.aptana.editor.common.parsing.CompositeParser;
import com.aptana.editor.erb.parsing.lexer.ERBTokens;
import com.aptana.editor.html.parsing.HTMLParser;
import com.aptana.editor.ruby.core.IRubyScript;
import com.aptana.editor.ruby.parsing.IRubyParserConstants;
import com.aptana.editor.ruby.parsing.RubyParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.ParseNode;
import com.aptana.parsing.ast.ParseRootNode;

public class RHTMLParser extends CompositeParser
{

	public RHTMLParser()
	{
		// FIXME keep a reference to language and check out parser on demand?
		super(new RHTMLParserScanner(), new HTMLParser());
	}

	@Override
	protected IParseNode processEmbeddedlanguage(IParseState parseState) throws Exception
	{
		String source = new String(parseState.getSource());
		int startingOffset = parseState.getStartingOffset();
		IParseNode root = new ParseRootNode(IRubyParserConstants.LANGUAGE, new ParseNode[0], startingOffset,
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
		Symbol startTag = getCurrentSymbol();
		advance();

		// finds the entire ruby block
		int start = getCurrentSymbol().getStart();
		int end = start;
		short id = getCurrentSymbol().getId();
		while (id != ERBTokens.RUBY_END && id != ERBTokens.EOF)
		{
			end = getCurrentSymbol().getEnd();
			advance();
			id = getCurrentSymbol().getId();
		}

		IParseNode result = getParseResult(new RubyParser(), start, end);
		if (result != null)
		{
			Symbol endTag = getCurrentSymbol();
			ERBScript erb = new ERBScript((IRubyScript) result, startTag.value.toString(), endTag.value.toString());
			erb.setLocation(startTag.getStart(), endTag.getEnd());
			root.addChild(erb);
		}
	}
}
