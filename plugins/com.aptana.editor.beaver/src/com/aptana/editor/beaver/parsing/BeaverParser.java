package com.aptana.editor.beaver.parsing;

import java.io.StringReader;

import beaver.spec.ast.GrammarTreeRoot;
import beaver.spec.parser.GrammarParser;
import beaver.spec.parser.GrammarScanner;

import com.aptana.editor.beaver.parsing.ast.BeaverParseRootNode;
import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseRootNode;

/**
 * This is a wrapper/adapter for the Beaver grammar parser so we can use it within our parsing framework
 */
public class BeaverParser extends AbstractParser
{
	protected void parse(IParseState parseState, WorkingParseResult working) throws Exception
	{
		GrammarScanner scanner = new GrammarScanner(new StringReader(parseState.getSource()));
		GrammarParser parser = new GrammarParser();
		IParseRootNode result = null;

		Object parseResult = parser.parse(scanner);

		if (parseResult instanceof GrammarTreeRoot)
		{
			GrammarTreeRoot grammarRoot = (GrammarTreeRoot) parseResult;

			result = new BeaverParseRootNode(grammarRoot);

			working.setParseResult(result);
		}
	}
}
