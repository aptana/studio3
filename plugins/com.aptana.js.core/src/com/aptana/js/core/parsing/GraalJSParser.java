package com.aptana.js.core.parsing;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import com.aptana.core.build.IProblem.Severity;
import com.aptana.js.core.IJSConstants;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseRootNode;
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
		String filename = parseState.getFilename();
		if (filename == null)
		{
			filename = "filename.js"; //$NON-NLS-1$
		}

		try
		{
			IParseRootNode ast = convertAST(source, parse(filename, source, working));
			working.setParseResult(ast);
		}
		catch (Exception e)
		{
			// print the stack traces to a string!
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(out);
			e.printStackTrace(pw);
			working.addError(new ParseError(IJSConstants.CONTENT_TYPE_JS, -1, -1,
					e.getMessage() + "\n" + out.toString(), Severity.ERROR));
		}

	}

	private IParseRootNode convertAST(final String source, final FunctionNode result)
	{
		if (result == null)
		{
			return null;
		}

		GraalASTWalker astWalker = new GraalASTWalker(source, new LexicalContext());
		result.accept(astWalker);
		return astWalker.getRootNode();
	}

	private FunctionNode parse(final String filename, final String source, final WorkingParseResult working)
	{
		Source src = Source.sourceFor(filename, source);

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

		// We try to parse as 'script' first, since that's much more common
		// FIXME Based on file extensions, choose module goal explicitly for *.mjs files!
		FunctionNode result = p.parse();
		if (result == null || errorManager.getParserException() != null)
		{
			// FIXME I assume if we get no result or had a parser exception we should retry as module. Sniff the
			// exception for import/export?
			// Reset errors on working result
			working.getErrors().clear();
			env = ScriptEnvironment.builder().es6(true).build();
			p = new Parser(env, src, errorManager);

			// parse as module
			result = p.parseModule("modulename");
		}
		return result;
	}

}
