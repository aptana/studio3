package com.aptana.js.core.parsing.antlr;

import java.util.BitSet;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.IterativeParseTreeWalker;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.aptana.core.build.IProblem;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.parsing.JSParseState;
import com.aptana.js.core.parsing.antlr.JSParser.ProgramContext;
import com.aptana.js.core.parsing.antlr.ast.JSASTWalker;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseResult;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.ParseError;

public class JSWrappingParser implements IParser
{

	private final class ErrorListener implements ANTLRErrorListener
	{
		private final WorkingParseResult working;

		private ErrorListener(WorkingParseResult working)
		{
			this.working = working;
		}

		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e)
		{
			// FIXME Convert the arg1 object here into a Symbol?
			working.addError(new ParseError(IJSConstants.CONTENT_TYPE_JS, null, msg, IProblem.Severity.WARNING));
		}

		public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5)
		{
			// TODO Auto-generated method stub

		}

		public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4,
				ATNConfigSet arg5)
		{
			// TODO Auto-generated method stub

		}

		public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5,
				ATNConfigSet arg6)
		{
			// TODO Auto-generated method stub

		}
	}

	private JSParser fParser;

	@Override
	public synchronized ParseResult parse(IParseState parseState) throws java.lang.Exception
	{
		WorkingParseResult working = new WorkingParseResult();
		parse(parseState, working);
		return working.getImmutableResult();
	}

	private void parse(IParseState parseState, final WorkingParseResult working)
	{
		// re-use the same CharStream object per-parse state
		CharStream stream;
		if (parseState instanceof JSParseState)
		{
			stream = ((JSParseState) parseState).getCharStream();
		}
		else
		{
			String source = parseState.getSource();
			stream = CharStreams.fromString(source);
		}
		
		// Can we re-use the lexer/token stream?
		JSLexer lexer = new JSLexer(stream);
		CommonTokenStream input = new CommonTokenStream(lexer);
		if (fParser == null)
		{
			fParser = new JSParser(input);
			fParser.addErrorListener(new ErrorListener(working));
			// This is the fastest settings. We're supposed to fall back to DefaultErrorStrategy and LL if this fails! (i.e. a ParseCancellationException)
			fParser.getInterpreter().setPredictionMode(PredictionMode.SLL);
			fParser.setErrorHandler(new BailErrorStrategy());
		}
		else
		{
			fParser.setInputStream(input);
		}
		// TODO We had written some custom error recovery code for our JS parser before. Can we replicate that here?
		// See JS.grammar#379-440
		// - try inserting ";"
		// - try inserting ");"
		// - try inserting empty identifier and ";" after: '.', 'new', or '='
		// - try inserting empty identifier after: '.', 'new', or '='
		// - try inserting empty identifier after '('
		// - try inserting empty identifier ':' after: '.'
		// - try inserting "{}" after: ')'
		// - seems to try and recover from trailing commas (inside parens)
		// - try inserting empty identifier if current token is (i.e. before) '}'
		// - try inserting ':' and empty identifier if current token is (i.e. before) '}'
		// fParser.setErrorHandler(new DefaultErrorStrategy());
		
		// FIXME This is still much slower than the beaver parser. How can we speed it up?
		JSParseRootNode rootNode = convertAST(fParser.program());
		working.setParseResult(rootNode);
	}

	private JSParseRootNode convertAST(ProgramContext pc)
	{
		ParseTreeWalker walker = new IterativeParseTreeWalker();
		JSASTWalker listener = new JSASTWalker();
		walker.walk(listener, pc);
		return listener.getRootNode();
	}
}
