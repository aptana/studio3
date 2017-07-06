package com.aptana.js.core.parsing.antlr;

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.DiagnosticErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
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

	private final class ErrorListener extends DiagnosticErrorListener
	{
		private final WorkingParseResult working;

		private ErrorListener(WorkingParseResult working)
		{
			super(false);
			this.working = working;
		}

		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e)
		{
			// FIXME Convert the arg1 object here into a Symbol?
			System.out.println(msg);
			working.addError(new ParseError(IJSConstants.CONTENT_TYPE_JS, null, msg, IProblem.Severity.WARNING));
			// super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
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
		ProgramContext pc;
		fParser.setProfile(true);
		// This is the fastest settings. We're supposed to fall back to DefaultErrorStrategy and LL if this fails!
		// (i.e. a ParseCancellationException)
		fParser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		fParser.setErrorHandler(new BailErrorStrategy());
		try
		{
			pc = fParser.program();
		}
		catch (ParseCancellationException e)
		{
			fParser.getInterpreter().setPredictionMode(PredictionMode.LL);
			fParser.setErrorHandler(new DefaultErrorStrategy());
			pc = fParser.program();
		}

		JSParseRootNode rootNode = convertAST(pc);
		ParseInfo pi = fParser.getParseInfo();
		System.out.println("DFA size: " + pi.getDFASize());
		System.out.println("ATN lookahead ops: " + pi.getTotalATNLookaheadOps());
		System.out.println("LLATN lookahead ops: " + pi.getTotalLLATNLookaheadOps());
		System.out.println("LL lookahead ops: " + pi.getTotalLLLookaheadOps());
		System.out.println("SLLATN lookahead ops: " + pi.getTotalSLLATNLookaheadOps());
		System.out.println("SLL lookahead ops: " + pi.getTotalSLLLookaheadOps());
		System.out.println("Total time in prediction: " + pi.getTotalTimeInPrediction());
		System.out.println("LL Decisions: " + pi.getLLDecisions());
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
