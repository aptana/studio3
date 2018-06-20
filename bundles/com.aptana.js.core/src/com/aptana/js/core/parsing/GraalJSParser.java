package com.aptana.js.core.parsing;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.aptana.core.build.IProblem.Severity;
import com.aptana.js.core.IJSConstants;
import com.aptana.js.core.parsing.ast.IJSNodeTypes;
import com.aptana.js.core.parsing.ast.JSCommentNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.parsing.AbstractParser;
import com.aptana.parsing.IParseState;
import com.aptana.parsing.WorkingParseResult;
import com.aptana.parsing.ast.IParseError;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.ast.IParseRootNode;
import com.aptana.parsing.ast.ParseError;
import com.aptana.parsing.util.ParseUtil;
import com.oracle.js.parser.ErrorManager;
import com.oracle.js.parser.Parser;
import com.oracle.js.parser.ScriptEnvironment;
import com.oracle.js.parser.Source;
import com.oracle.js.parser.TokenType;
import com.oracle.js.parser.ir.FunctionNode;
import com.oracle.js.parser.ir.LexicalContext;

public class GraalJSParser extends AbstractParser
{

	private CommentCollectingParser fParser;

	protected void parse(IParseState parseState, final WorkingParseResult working) throws Exception
	{
		String source = parseState.getSource();
		String filename = parseState.getFilename();
		if (filename == null)
		{
			filename = "filename.js"; //$NON-NLS-1$
		}

		try
		{
			FunctionNode graalAST = parse(filename, 0, source, working);
			JSParseRootNode ast = (JSParseRootNode) convertAST(source, graalAST);
			if (ast != null)
			{
				ast.setCommentNodes(fParser.getCommentNodes());
			}

			// update node offsets
			int start = parseState.getStartingOffset();
			int length = source.length();

			// align root with zero-based offset
			ast.setLocation(0, length - 1);

			if (start != 0)
			{
				// shift all offsets to the correct position
				ParseUtil.addOffset(ast, start);
			}
			working.setParseResult(ast);
		}
		catch (Exception e)
		{
			System.err.println(e);
			e.printStackTrace();
			IParseError error = handleError(e);
			working.addError(error);
			throw e;
		}

	}

	private IParseError handleError(Exception e)
	{
		// print the stack traces to a string!
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);
		e.printStackTrace(pw);
		return new ParseError(IJSConstants.CONTENT_TYPE_JS, -1, -1, e.getMessage() + "\n" + out.toString(),
				Severity.ERROR);
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

	private FunctionNode parse(final String filename, int startOffset, final String source,
			final WorkingParseResult working)
	{
		Source src = Source.sourceFor(filename, source);

		ScriptEnvironment env = ScriptEnvironment.builder().es6(true).strict(false).emptyStatements(true).build();
		ErrorManager errorManager = new ErrorManager()
		{
			@Override
			public void error(String message)
			{
				working.addError(new ParseError(IJSConstants.CONTENT_TYPE_JS, -1, -1, message, Severity.ERROR));
			}
		};
		// Subclass and collect comments too
		fParser = new CommentCollectingParser(env, src, errorManager);

		// TODO Can we do a quick peek/guess based on a regexp to find imports/exports at top-level?
		// FIXME Based on file extensions, choose module goal explicitly for *.mjs files!

		// First try as module. This also implicitly does strict mode.
		FunctionNode result = fParser.parseModule(filename, startOffset, source.length() - startOffset);
		if (result == null || errorManager.getParserException() != null)
		{
			// Reset state and fall back to non-strict script as our goal
			working.getErrors().clear();
			fParser = new CommentCollectingParser(env, src, errorManager);
			result = fParser.parse(filename, startOffset, source.length() - startOffset, false);
		}
		return result;
	}

	private static class CommentCollectingParser extends Parser
	{
		private static final int DIDNT_SEE_COMMENT = -1;
		private final List<IParseNode> comments = new ArrayList<IParseNode>();
		private int fLastCommentStart = DIDNT_SEE_COMMENT;

		public CommentCollectingParser(ScriptEnvironment env, Source src, ErrorManager errorManager)
		{
			super(env, src, errorManager);
		}

		@Override
		protected TokenType nextToken()
		{
			TokenType tt = super.nextToken();
			if (sawCommentLastTime())
			{
				// we saw a comment last time, so grab where it ended now and record it!
				recordComment(tt);
			}
			if (tt == TokenType.COMMENT)
			{
				// Record the comment start
				fLastCommentStart = start;
			}
			return tt;
		}

		private void recordComment(TokenType curTokenType)
		{
			// FIXME finish can *still* be wrong. If finish is less than fLastCommentStart, use start - 1?
			int commentEndOffset = finish - 1; // we use inclusive end, graal uses exclusive, so we need to subtract
												// one!
			// Looks like because eol holds different info in the token (line number/offset) overloading the token
			// length, finish doesn't get updated!
			if (curTokenType == TokenType.EOL)
			{
				commentEndOffset = linePosition - 2; // linePosition points at offset of next line, subtract 1 to go
														// back to end of previous line (at newline char), another to
														// remove newline char
			}
			if (commentEndOffset < fLastCommentStart)
			{
				commentEndOffset = start - 1;
			}
			char secondCharacter = source.getContent().charAt(fLastCommentStart + 1); // is the second char of comment a
																						// '*' or '/'?
			short commentType = secondCharacter == '*' ? IJSNodeTypes.MULTI_LINE_COMMENT
					: IJSNodeTypes.SINGLE_LINE_COMMENT;
			comments.add(new JSCommentNode(commentType, fLastCommentStart, commentEndOffset));
			fLastCommentStart = DIDNT_SEE_COMMENT; // reset
		}

		private boolean sawCommentLastTime()
		{
			return fLastCommentStart != DIDNT_SEE_COMMENT;
		}

		public IParseNode[] getCommentNodes()
		{
			return this.comments.toArray(new IParseNode[comments.size()]);
		}

	}

}
