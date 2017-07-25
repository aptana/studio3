package com.aptana.js.core.parsing.graal;

import com.aptana.core.build.IProblem.Severity;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.ParseError;
import com.oracle.js.parser.ErrorManager;
import com.oracle.js.parser.Parser;
import com.oracle.js.parser.ScriptEnvironment;
import com.oracle.js.parser.Source;
import com.oracle.js.parser.ir.FunctionNode;
import com.oracle.js.parser.ir.LexicalContext;

public class GraalJSParser implements IParser
{

	@Override
	public synchronized ParseResult parse(IParseState parseState) throws java.lang.Exception
	{
		WorkingParseResult working = new WorkingParseResult();
		parse(parseState, working);
		return working.getImmutableResult();
	}

	private void parse(IParseState parseState, final WorkingParseResult working)
	{
		String source = parseState.getSource();
		Source src = Source.sourceFor("filename.js", source);

		ScriptEnvironment env = ScriptEnvironment.builder().es6(true).build();
		ErrorManager errorManager = new ErrorManager()
		{
			@Override
			public void error(String message)
			{
				working.addError(new ParseError(IJSConstants.CONTENT_TYPE_JS, -1, -1, message, Severity.ERROR));
			}
		};
		Parser p = new Parser(env, src, errorManager);

		FunctionNode result = p.parseModule("");
		if (result != null)
		{
			try
			{
				GraalASTWalker astWalker = new GraalASTWalker(source, new LexicalContext());
				result.accept(astWalker);
				working.setParseResult(astWalker.getRootNode());
			}
			catch (Exception e)
			{
				// FIXME Sprint the stack traces to a string!
				e.printStackTrace();
				working.addError(new ParseError(IJSConstants.CONTENT_TYPE_JS, -1, -1, e.getMessage(), Severity.ERROR));
			}
		}
	}

}
