/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.ruby;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jrubyparser.CompatVersion;
import org.jrubyparser.SourcePosition;
import org.jrubyparser.Parser.NullWarnings;
import org.jrubyparser.ast.CommentNode;
import org.jrubyparser.ast.Node;
import org.jrubyparser.lexer.Lexer;
import org.jrubyparser.lexer.LexerSource;
import org.jrubyparser.lexer.SyntaxException;
import org.jrubyparser.lexer.Lexer.LexState;
import org.jrubyparser.lexer.SyntaxException.PID;
import org.jrubyparser.parser.ParserConfiguration;
import org.jrubyparser.parser.ParserResult;
import org.jrubyparser.parser.ParserSupport;
import org.jrubyparser.parser.Tokens;

public class RubySourcePartitionScanner implements IPartitionTokenScanner
{

	private static final String INDENTED_HEREDOC_MARKER_PREFIX = "<<-"; //$NON-NLS-1$
	private static final String HEREDOC_MARKER_PREFIX = "<<"; //$NON-NLS-1$
	private static final String DEFAULT_FILENAME = "filename"; //$NON-NLS-1$
	private static final String BEGIN = "=begin"; //$NON-NLS-1$

	private Lexer lexer;
	private ParserSupport parserSupport;
	private ParserResult result;
	private String fContents;
	private LexerSource lexerSource;
	private int origOffset;
	private int origLength;
	private int fLength;
	private int fOffset;

	private List<QueuedToken> fQueue = new ArrayList<QueuedToken>();
	private String fContentType = RubySourceConfiguration.DEFAULT;
	private boolean inSingleQuote;
	private String fOpeningString;

	public RubySourcePartitionScanner()
	{
		lexer = new Lexer();
		parserSupport = new ParserSupport();
		ParserConfiguration config = new ParserConfiguration(0, CompatVersion.RUBY1_8);
		parserSupport.setConfiguration(config);
		result = new ParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		lexer.setWarnings(new NullWarnings());
	}

	public void setPartialRange(IDocument document, int offset, int length, String contentType, int partitionOffset)
	{
		reset();
		int myOffset = offset;
		if (contentType != null)
		{
			int diff = offset - partitionOffset;
			// backtrack to beginning of partition so we don't get in weird
			// state
			myOffset = partitionOffset;
			length += diff;
			this.fContentType = contentType;
			if (this.fContentType.equals(RubySourceConfiguration.SINGLE_LINE_COMMENT) || this.fContentType.equals(IDocument.DEFAULT_CONTENT_TYPE))
				this.fContentType = RubySourceConfiguration.DEFAULT;
			// FIXME What if a heredoc with dynamic code inside is broken? contents will start with "}" rather than
			// expected
		}
		if (myOffset == -1)
			myOffset = 0;
		ParserConfiguration config = new ParserConfiguration(0, CompatVersion.RUBY1_8);
		try
		{
			fContents = document.get(myOffset, length);
			lexerSource = LexerSource.getSource(DEFAULT_FILENAME, new StringReader(fContents), config);
			lexer.setSource(lexerSource);
		}
		catch (BadLocationException e)
		{
			lexerSource = LexerSource.getSource(DEFAULT_FILENAME, new StringReader(""), config); //$NON-NLS-1$
			lexer.setSource(lexerSource);
		}
		origOffset = myOffset;
		origLength = length;
	}

	public int getTokenLength()
	{
		return fLength;
	}

	public int getTokenOffset()
	{
		return fOffset;
	}

	public IToken nextToken()
	{
		if (!fQueue.isEmpty())
		{
			return popTokenOffQueue();
		}
		setOffset(getAdjustedOffset());
		setLength(0);
		IToken returnValue = new Token(fContentType);
		boolean isEOF = false;
		try
		{
			isEOF = !lexer.advance();
			if (isEOF)
			{
				returnValue = Token.EOF;
			}
			else
			{
				int lexerToken = lexer.token();
				if (isSingleVariableStringInterpolation(lexerToken))
				{
					return handleSingleVariableStringInterpolation();
				}
				else if (isStringInterpolation(lexerToken))
				{
					return handleStringInterpolation();
				}
				else if (lexerToken == Tokens.tSTRING_BEG)
				{
					IToken heredoc = handleHeredocInMiddleOfLine();
					if (heredoc != null)
						return heredoc;
				}
				returnValue = getToken(lexerToken);
			}
			// TODO Are there ever comment nodes anymore? Do we need this code?!
			List<CommentNode> comments = result.getCommentNodes();
			if (comments != null && !comments.isEmpty())
			{
				parseOutComments(comments);
				// Queue the normal token we just ate up
				addQueuedToken(returnValue);
				comments.clear();
				return popTokenOffQueue();
			}
		}
		catch (SyntaxException se)
		{
			if (se.getMessage().equals("embedded document meets end of file")) { //$NON-NLS-1$
				return handleUnterminedMultilineComment(se);
			}
			else if (se.getPid().equals(PID.STRING_MARKER_MISSING) || se.getPid().equals(PID.STRING_HITS_EOF)) {
				return handleUnterminatedString(se);
			}

			if (lexerSource.getOffset() - origLength == 0)
			{
				// return eof if we hit a problem found at end of parsing
				return Token.EOF;
			}
			setLength(getAdjustedOffset() - fOffset);
			return new Token(RubySourceConfiguration.DEFAULT);
		}
		catch (IOException e)
		{
			RubyEditorPlugin.log(e);
		}
		if (!isEOF)
		{
			setLength(getAdjustedOffset() - fOffset);
			// HACK End of heredocs are returning a zero length token for end of string that hoses us
			if (fLength == 0
					&& (returnValue.getData().equals(RubySourceConfiguration.STRING_DOUBLE) || returnValue.getData()
							.equals(RubySourceConfiguration.STRING_SINGLE)))
				return nextToken();
		}
		return returnValue;
	}

	private boolean isSingleVariableStringInterpolation(int lexerToken)
	{
		return !inSingleQuote && lexerToken == Tokens.tSTRING_DVAR;
	}

	private boolean isStringInterpolation(int lexerToken)
	{
		return !inSingleQuote && lexerToken == Tokens.tSTRING_DBEG;
	}

	private IToken handleHeredocInMiddleOfLine() throws IOException
	{
		String opening = getOpeningStringToEndOfLine();
		int endOfMarker = indexOf(opening.trim(), "., +()"); //$NON-NLS-1$
		if (opening.trim().startsWith(HEREDOC_MARKER_PREFIX) && endOfMarker != -1)
		{
			adjustOffset(opening);
			addHereDocStartToken(endOfMarker);
			addCommaToken(endOfMarker);
			scanRestOfLineAfterHeredocBegins(opening.trim(), endOfMarker);
			setLexerPastHeredocBeginning(opening.trim());
			return popTokenOffQueue();
		}
		return null;
	}

	private String getOpeningStringToEndOfLine()
	{
		int start = fOffset - origOffset;
		// TODO Are there ever comment nodes anymore? Do we need this code?!
		List<CommentNode> comments = result.getCommentNodes();
		if (comments != null && !comments.isEmpty())
		{
			Node comment = comments.get(comments.size() - 1);
			int end = comment.getPosition().getEndOffset();
			start = end;
		}
		// Need to grab until newline or EOF!
		String untilEnd = new String(fContents.substring(start));
		int index = indexOf(untilEnd, "\r\n"); //$NON-NLS-1$
		if (index != -1)
			untilEnd = new String(untilEnd.substring(0, index + 1));
		return untilEnd;
	}

	private void setLength(int newLength)
	{
		fLength = newLength;
		Assert.isTrue(fLength >= 0);
	}

	private IToken handleUnterminedMultilineComment(SyntaxException se)
	{
		return handleUnterminatedPartition(se.getPosition().getStartOffset(),
				RubySourceConfiguration.MULTI_LINE_COMMENT);
	}

	private IToken handleUnterminatedString(SyntaxException se)
	{
		return handleUnterminatedPartition(se.getPosition().getStartOffset(), fContentType);
	}

	private IToken handleUnterminatedPartition(int start, String contentType)
	{
		// Add to the queue (at end), then try to just do the rest of
		// the file...
		// TODO recover somehow by removing this chunk out of the
		// fContents?
		int length = fContents.length() - start;
		QueuedToken qtoken = new QueuedToken(new Token(contentType), start + origOffset, length);
		if (fOffset == origOffset)
		{
			// If we never got to read in beginning contents
			RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
			String possible = new String(fContents.substring(0, start));
			IDocument document = new Document(possible);
			scanner.setRange(document, origOffset, possible.length());
			IToken token;
			while (!(token = scanner.nextToken()).isEOF())
			{
				push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner.getTokenLength()));
			}
		}
		push(qtoken);
		push(new QueuedToken(Token.EOF, start + origOffset + length, 0));
		return popTokenOffQueue();
	}

	private IToken handleSingleVariableStringInterpolation() throws IOException
	{
		addPoundToken();
		scanDynamicVariable();
		setLexerPastDynamicSectionOfString();
		return popTokenOffQueue();
	}

	private IToken handleStringInterpolation() throws IOException
	{
		addPoundBraceToken();
		scanTokensInsideDynamicPortion();
		addClosingBraceToken();
		setLexerPastDynamicSectionOfString();
		return popTokenOffQueue();
	}

	public void setRange(IDocument document, int offset, int length)
	{
		setPartialRange(document, offset, length, RubySourceConfiguration.DEFAULT, 0);
	}

	private void reset()
	{
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		lexer.setPreserveSpaces(true);
		parserSupport.initTopLocalVariables();
		fQueue.clear();
		inSingleQuote = false;
		fContentType = RubySourceConfiguration.DEFAULT;
	}

	private void adjustOffset(String opening)
	{
		int index = opening.indexOf(HEREDOC_MARKER_PREFIX);
		if (index > 0)
			setOffset(fOffset + index);
	}

	private int indexOf(String opening, String string)
	{
		String trimmed = opening.trim();
		int diff;
		if (trimmed.length() == 0)
		{
			diff = opening.length();
		}
		else
		{
			// Count leading whitespace
			diff = opening.indexOf(trimmed.charAt(0));
		}
		int lowest = -1;
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			int value = trimmed.indexOf(c);
			if (value == -1)
				continue;
			value += diff;
			if (lowest == -1)
			{
				lowest = value;
				continue;
			}
			if (value < lowest)
				lowest = value;
		}
		return lowest;
	}

	private void scanRestOfLineAfterHeredocBegins(String opening, int index)
	{
		String possible = new String(opening.substring(index + 1));
		RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF())
		{
			push(new QueuedToken(token, scanner.getTokenOffset() + fOffset + index + 1, scanner.getTokenLength()));
		}
		setOffset(fOffset + index + 1 + possible.length());
		if (scanner.fOpeningString != null && scanner.fOpeningString.endsWith("\n")) { //$NON-NLS-1$
			fOpeningString = scanner.fOpeningString;
		}
		else
		{
			String marker = new String(opening.substring(0, index).trim());
			fOpeningString = generateOpeningStringForHeredocMarker(marker);
		}
		fContentType = RubySourceConfiguration.STRING_DOUBLE;
	}

	private void addCommaToken(int index)
	{
		push(new QueuedToken(new Token(RubySourceConfiguration.DEFAULT), fOffset + index, 1));
	}

	private void addHereDocStartToken(int index)
	{
		push(new QueuedToken(new Token(RubySourceConfiguration.STRING_DOUBLE), fOffset, index));
	}

	private void setOffset(int offset)
	{
		fOffset = offset;
	}

	private void addPoundToken()
	{
		addStringToken(1);// add token for the #
	}

	private void scanDynamicVariable()
	{
		// read until whitespace or '"'
		int whitespace = fContents.indexOf(' ', fOffset - origOffset);
		if (whitespace == -1)
			whitespace = Integer.MAX_VALUE;
		int doubleQuote = fContents.indexOf('"', fOffset - origOffset);
		if (doubleQuote == -1)
			doubleQuote = Integer.MAX_VALUE;
		int end = Math.min(whitespace, doubleQuote);
		// FIXME If we can't find whitespace or doubleQuote, we are pretty
		// screwed.
		String possible = null;
		if (end == -1)
		{
			possible = new String(fContents.substring(fOffset - origOffset));
		}
		else
		{
			possible = new String(fContents.substring(fOffset - origOffset, end));
		}
		RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF())
		{
			push(new QueuedToken(token, scanner.getTokenOffset() + (fOffset), scanner.getTokenLength()));
		}
		setOffset(fOffset + possible.length());
	}

	private void scanTokensInsideDynamicPortion()
	{
		String possible = new String(fContents.substring(fOffset - origOffset));
		int end = findEnd(possible);
		if (end != -1)
		{
			possible = new String(possible.substring(0, end));
		}
		RubySourcePartitionScanner scanner = new RubySourcePartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF())
		{
			push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner.getTokenLength()));
		}
		setOffset(fOffset + possible.length());
	}

	private int findEnd(String possible)
	{
		int end = new EndBraceFinder(possible).find();
		if (this.insideHeredoc())
		{
			String marker = fOpeningString.trim();
			int offset = possible.indexOf(marker);
			if (offset != -1)
			{
				int endingOffset = offset + marker.length();
				boolean allowLeadingWhitespace = true; // TODO: set based on existence of '-' operator
				while (offset > 0)
				{
					char c = possible.charAt(offset - 1);
					if (c == '\r' || c == '\n')
					{
						break;
					}
					else if (allowLeadingWhitespace && (c == ' ' || c == '\t'))
					{
						offset--;
					}
					else
					{
						// try to advance to the next potential end marker. Note
						// that if this fails, offset will be -1 and then we'll
						// exit the while loop
						offset = possible.indexOf(marker, endingOffset);
						endingOffset = offset + marker.length();
					}
				}
				// if we found an end marker, use it if it comes before the
				// ending brace
				if (end == -1 || (offset != -1 && offset < end))
				{
					end = offset - 1;
				}
			}
		}
		return end;
	}

	private void addPoundBraceToken()
	{
		addStringToken(2); // add token for the #{
	}

	private void addStringToken(int length)
	{
		push(new QueuedToken(new Token(fContentType), fOffset, length));
		setOffset(fOffset + length); // move past token
	}

	private void addClosingBraceToken()
	{
		addStringToken(1);
	}

	private void setLexerPastDynamicSectionOfString() throws IOException
	{
		String opening = fOpeningString;
		if (opening.endsWith("\n")) { //$NON-NLS-1$
			// What about When it should remain <<-!
			// try searching backwards from fOffset in fContents for <<-opening
			// or <<opening and take whichever we find
			// first. If we fail to find, assume <<
			String heredocStart = HEREDOC_MARKER_PREFIX;
			int lastIndent = fContents.lastIndexOf(INDENTED_HEREDOC_MARKER_PREFIX + opening, fOffset);
			if (lastIndent != -1)
			{
				if (lastIndent > fContents.lastIndexOf(HEREDOC_MARKER_PREFIX + opening, fOffset))
					heredocStart = INDENTED_HEREDOC_MARKER_PREFIX;
			}
			opening = heredocStart + opening;
		}
		String oldContentType = fContentType;
		String oldOpening = fOpeningString;
		generateHackedSource(opening);
		fContentType = oldContentType;
		fOpeningString = oldOpening;
	}

	private void setLexerPastHeredocBeginning(String rawBeginning) throws IOException
	{
		String heredocMarker = HEREDOC_MARKER_PREFIX;
		if (rawBeginning.startsWith(INDENTED_HEREDOC_MARKER_PREFIX))
		{
			heredocMarker = INDENTED_HEREDOC_MARKER_PREFIX;
		}
		heredocMarker += fOpeningString.trim();

		generateHackedSource(heredocMarker);

		// Add a token for the heredoc string we just ate up!
		fContentType = RubySourceConfiguration.STRING_DOUBLE;
		int afterHeredoc = fOffset;
		push(new QueuedToken(new Token(RubySourceConfiguration.STRING_DOUBLE), afterHeredoc, getAdjustedOffset()
				- afterHeredoc));
	}

	private void generateHackedSource(String beginning) throws IOException
	{
		StringBuffer fakeContents = new StringBuffer();
		int start = fOffset - beginning.length();
		for (int i = 0; i < start; i++)
		{
			fakeContents.append(" "); //$NON-NLS-1$
		}
		fakeContents.append(beginning);
		if ((fOffset - origOffset) < origLength)
		{
			fakeContents.append(new String(fContents.substring((fOffset - origOffset))));
		}

		IDocument document = new Document(fakeContents.toString());
		List<QueuedToken> queueCopy = new ArrayList<QueuedToken>(fQueue);
		setPartialRange(document, start, fakeContents.length() - start, RubySourceConfiguration.DEFAULT, start);
		fQueue = new ArrayList<QueuedToken>(queueCopy);
		lexer.advance();
	}

	private void parseOutComments(List<CommentNode> comments)
	{
		for (CommentNode comment : comments)
		{
			int offset = correctOffset(comment);
			int length = comment.getContent().length();
			if (isCommentMultiLine(comment))
			{
				length = (origOffset + comment.getPosition().getEndOffset()) - offset;
				if (comment.getContent().charAt(0) != '=')
				{
					length++;
				}
			}
			Token token = new Token(getContentType(comment));
			push(new QueuedToken(token, offset, length));
		}
	}

	private IToken popTokenOffQueue()
	{
		QueuedToken token = fQueue.remove(0);
		setOffset(token.getOffset());
		setLength(token.getLength());
		return token.getToken();
	}

	private IToken getToken(int i)
	{
		// We have an unresolved heredoc
		if (fContentType.equals(RubySourceConfiguration.STRING_DOUBLE) && insideHeredoc())
		{
			if (reachedEndOfHeredoc())
			{
				fContentType = RubySourceConfiguration.DEFAULT;
				inSingleQuote = false;
				return new Token(RubySourceConfiguration.STRING_DOUBLE);
			}
		}
		if (fContentType.equals(RubySourceConfiguration.MULTI_LINE_COMMENT) && i != Tokens.tWHITESPACE)
		{
			fContentType = RubySourceConfiguration.DEFAULT;
		}

		switch (i)
		{
			case RubyTokenScanner.SPACE:
				return new Token(fContentType);
			case Tokens.tCOMMENT:
				return new Token(RubySourceConfiguration.SINGLE_LINE_COMMENT);
			case Tokens.tDOCUMENTATION:
				fContentType = RubySourceConfiguration.MULTI_LINE_COMMENT;
				return new Token(RubySourceConfiguration.MULTI_LINE_COMMENT);
			case Tokens.tSTRING_CONTENT:
				return new Token(fContentType);
			case Tokens.tSTRING_BEG:
				fOpeningString = getOpeningString();
				fContentType = RubySourceConfiguration.STRING_DOUBLE;
				if (fOpeningString.equals("'") || fOpeningString.startsWith("%q")) { //$NON-NLS-1$//$NON-NLS-2$
					inSingleQuote = true;
					fContentType = RubySourceConfiguration.STRING_SINGLE;
				}
				else if (fOpeningString.startsWith(HEREDOC_MARKER_PREFIX))
				{ // here-doc
					fOpeningString = generateOpeningStringForHeredocMarker(fOpeningString);
					if (fOpeningString.startsWith("'")) //$NON-NLS-1$
					{
						inSingleQuote = true;
						fContentType = RubySourceConfiguration.STRING_SINGLE;
					}
				}
				return new Token(fContentType);
			case Tokens.tXSTRING_BEG:
				fOpeningString = getOpeningString();
				fContentType = RubySourceConfiguration.COMMAND;
				return new Token(RubySourceConfiguration.COMMAND);
			case Tokens.tQWORDS_BEG:
			case Tokens.tWORDS_BEG:
				fOpeningString = getOpeningString();
				fContentType = RubySourceConfiguration.STRING_SINGLE;
				if (fOpeningString.startsWith("%") && fOpeningString.length() > 1 //$NON-NLS-1$
						&& Character.isUpperCase(fOpeningString.charAt(1)))
				{
					fContentType = RubySourceConfiguration.STRING_DOUBLE;
				}
				return new Token(fContentType);
			case Tokens.tSTRING_END:
				// If we're ending a heredoc, make sure we're not nested and ending one of the earlier ones!
				if (insideHeredoc() && !reachedEndOfHeredoc())
				{
					String contentTypeToReturn = RubySourceConfiguration.STRING_DOUBLE;
					if (fOpeningString.startsWith("'")) //$NON-NLS-1$
						contentTypeToReturn = RubySourceConfiguration.STRING_SINGLE;
					return new Token(contentTypeToReturn);
				}

				String oldContentType = fContentType;
				fContentType = RubySourceConfiguration.DEFAULT;
				inSingleQuote = false;
				return new Token(oldContentType);
			case Tokens.tREGEXP_BEG:
				fOpeningString = getOpeningString();
				fContentType = RubySourceConfiguration.REGULAR_EXPRESSION;
				return new Token(RubySourceConfiguration.REGULAR_EXPRESSION);
			case Tokens.tREGEXP_END:
				fContentType = RubySourceConfiguration.DEFAULT;
				return new Token(RubySourceConfiguration.REGULAR_EXPRESSION);
			case Tokens.tSYMBEG:
				// Sometimes we need to add 1, sometimes two. Depends on if there's
				// a space preceding the ':'
				int charAt = fOffset - origOffset;
				char c = fContents.charAt(charAt);
				int nextCharOffset = (fOffset + 1);
				while (c == ' ') // skip past space if it's there
				{
					nextCharOffset++;
					c = fContents.charAt(++charAt);
				}				
				if (fContents.length() <= charAt + 1)
				{
					return new Token(RubySourceConfiguration.DEFAULT);
				}
				if (c == '%') // %s syntax
				{
					fOpeningString = getOpeningString();
					fContentType = RubySourceConfiguration.STRING_SINGLE;
				}
				else if (c == ':') // normal syntax (i.e. ":symbol")
				{
					if (fContents.length() <= charAt + 1)
					{
						return new Token(RubySourceConfiguration.DEFAULT);
					}
					nextCharOffset++;
					c = fContents.charAt(++charAt);
					if (c == '"') // Check for :"symbol" syntax
					{
						fOpeningString = "\""; //$NON-NLS-1$
						push(new QueuedToken(new Token(RubySourceConfiguration.STRING_DOUBLE), nextCharOffset - 1, 1));
						fContentType = RubySourceConfiguration.STRING_DOUBLE;
					}
				}				
				return new Token(RubySourceConfiguration.DEFAULT);
			default:
				return new Token(fContentType);
		}
	}

	private boolean insideHeredoc()
	{
		return fOpeningString != null && fOpeningString.endsWith("\n"); //$NON-NLS-1$
	}

	private boolean reachedEndOfHeredoc()
	{
		return fContents.startsWith(fOpeningString.trim(), (fOffset - origOffset));
	}

	private String generateOpeningStringForHeredocMarker(String marker)
	{
		if (marker.startsWith(INDENTED_HEREDOC_MARKER_PREFIX))
		{
			marker = marker.substring(3);
		}
		else if (marker.startsWith(HEREDOC_MARKER_PREFIX))
		{
			marker = marker.substring(2);
		}
		return marker + "\n"; //$NON-NLS-1$
	}

	private String getOpeningString()
	{
		return getUntrimmedOpeningString().trim();
	}

	private String getUntrimmedOpeningString()
	{
		int start = fOffset - origOffset;
		List<CommentNode> comments = result.getCommentNodes();
		if (comments != null && !comments.isEmpty())
		{
			Node comment = comments.get(comments.size() - 1);
			int end = comment.getPosition().getEndOffset();
			start = end;
		}
		return new String(fContents.substring(start, lexerSource.getOffset()));
	}

	/**
	 * correct start offset, since when a line with nothing but spaces on it appears before comment, we get messed up
	 * positions
	 */
	private int correctOffset(CommentNode comment)
	{
		return origOffset + comment.getPosition().getStartOffset();
	}

	private boolean isCommentMultiLine(CommentNode comment)
	{
		String src = getSource(fContents, comment);
		return (src != null && src.startsWith(BEGIN));
	}

	private String getContentType(CommentNode comment)
	{
		if (isCommentMultiLine(comment))
			return RubySourceConfiguration.MULTI_LINE_COMMENT;
		return RubySourceConfiguration.SINGLE_LINE_COMMENT;
	}

	private void addQueuedToken(IToken returnValue)
	{
		// grab end of last comment (last thing in queue)
		QueuedToken token = peek();
		setOffset(token.getOffset() + token.getLength());
		int length = getAdjustedOffset() - fOffset;
		if (length < 0)
		{
			length = 0;
		}
		push(new QueuedToken(returnValue, fOffset, length));
	}

	private QueuedToken peek()
	{
		return fQueue.get(fQueue.size() - 1);
	}

	private void push(QueuedToken token)
	{
		Assert.isTrue(token.getLength() >= 0);
		fQueue.add(token);
	}

	private int getAdjustedOffset()
	{
		return lexerSource.getOffset() + origOffset;
	}

	private static String getSource(String contents, Node node)
	{
		if (node == null || contents == null)
			return null;
		SourcePosition pos = node.getPosition();
		if (pos == null)
			return null;
		if (pos.getStartOffset() >= contents.length())
			return null; // position is past end of our source
		if (pos.getEndOffset() > contents.length())
			return null; // end is past end of source
		return new String(contents.substring(pos.getStartOffset(), pos.getEndOffset()));
	}

	/**
	 * Used to find the end of string interpolation (the '}'). Uses a stack to maintain nesting of strings/regexp, and
	 * knowledge of escape chars.
	 * 
	 * @author cwilliams
	 */
	public static class EndBraceFinder
	{
		private String input;
		private List<String> stack;

		public EndBraceFinder(String possible)
		{
			this.input = possible;
			stack = new ArrayList<String>();
		}

		/**
		 * Return index of the end brace. -1 if not found.
		 * 
		 * @return
		 */
		public int find()
		{
			int lastEndBrace = -1;
			for (int i = 0; i < input.length(); i++)
			{
				char c = input.charAt(i);
				switch (c)
				{
					case '$':
						// don't skip next char if we're in a regexp
						if (!topEquals("/")) //$NON-NLS-1$
						{
							i++;
						}
						break;
					case '\\':
						i++;
						break;
					case '"':
						if (topEquals("\"")) { //$NON-NLS-1$
							pop();
						}
						else
						{
							// if we hit an '"' with an open '/' we assume we're done. Ticket #372
							if (stack.contains("/") && !stack.contains("\"") && lastEndBrace != -1) //$NON-NLS-1$ //$NON-NLS-2$
							{
								return lastEndBrace;
							}
							if (!topEquals("'")) //$NON-NLS-1$
								push("\""); //$NON-NLS-1$
						}
						break;
					case '/':
						if (topEquals("/")) { //$NON-NLS-1$
							pop();
							// found a regex
							lastEndBrace = -1;
						}
						else
						{
							// Only if we're not inside a string
							if (!topEquals("'") && !topEquals("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
								push("/"); //$NON-NLS-1$
							}
						}
						break;
					case '\'':
						if (topEquals("'")) { //$NON-NLS-1$
							pop();
						}
						else if (!topEquals("\"") && !topEquals("/")) { //$NON-NLS-1$ //$NON-NLS-2$
							// not inside a double quoted string or a regex
							push("'"); //$NON-NLS-1$
						}
						break;
					case '{':
						// Only if we're not inside a string
						if (!topEquals("'") && !topEquals("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
							push("{"); //$NON-NLS-1$
						}
						break;
					case '#':
						// Only add if we're inside a double quote string
						if (topEquals("\"")) { //$NON-NLS-1$
							c = input.charAt(i + 1);
							if (c == '{')
							{
								push("#{"); //$NON-NLS-1$
								i++;
							}
						}
						break;
					case '}':
						if (stack.isEmpty())
						{ // if not in open state
							return i;
						}
						if (topEquals("#{") || topEquals("{")) { //$NON-NLS-1$ //$NON-NLS-2$
							pop();
						}
						if (topEquals("/")) { //$NON-NLS-1$
							// assumes '/' is for division until we find a matching '/' to make it a regex
							lastEndBrace = i;
						}
						break;
					default:
						break;
				}
			}
			return lastEndBrace < 0 ? -1 : lastEndBrace;
		}

		private boolean topEquals(String string)
		{
			String open = peek();
			return open != null && open.equals(string);
		}

		private boolean push(String string)
		{
			return stack.add(string);
		}

		private String pop()
		{
			return stack.remove(stack.size() - 1);
		}

		private String peek()
		{
			if (stack.isEmpty())
				return null;
			return stack.get(stack.size() - 1);
		}
	}
}
