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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jrubyparser.CompatVersion;
import org.jrubyparser.Parser.NullWarnings;
import org.jrubyparser.lexer.Lexer;
import org.jrubyparser.lexer.Lexer.LexState;
import org.jrubyparser.lexer.LexerSource;
import org.jrubyparser.lexer.SyntaxException;
import org.jrubyparser.parser.ParserConfiguration;
import org.jrubyparser.parser.ParserResult;
import org.jrubyparser.parser.ParserSupport;
import org.jrubyparser.parser.Tokens;

/**
 * A token scanner which returns integers for ruby tokens. These can later be mapped to colors. Does some smoothing on
 * the tokens to add additional token types that the JRuby parser ignores.
 * 
 * @author Chris Williams
 */
public class RubyTokenScanner implements ITokenScanner
{

	private static final int COMMA = 44;
	public static final int COLON = 58;
	public static final int ASSIGNMENT = 61;
	public static final int QUESTION = 63;
	public static final int NEWLINE = 10;
	public static final int CHARACTER = 128;
	static final int MIN_KEYWORD = 257;
	static final int MAX_KEYWORD = 305;
	public static final int SPACE = 32;
	private static final int LBRACK = 91;
	public static final int SEMICOLON = 59;

	private Lexer lexer;
	private LexerSource lexerSource;
	private ParserSupport parserSupport;

	private int fTokenLength;
	private int fOffset;

	private boolean isInSymbol;
	private boolean inAlias;
	private ParserResult result;
	private int origOffset;
	private int origLength;
	private String fContents;

	public RubyTokenScanner()
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

	public int getTokenLength()
	{
		return fTokenLength;
	}

	public int getTokenOffset()
	{
		return fOffset;
	}

	public IToken nextToken()
	{
		fOffset = getOffset();
		fTokenLength = 0;
		IToken returnValue = new Token(Tokens.tIDENTIFIER);
		boolean isEOF = false;
		try
		{
			isEOF = !lexer.advance(); // FIXME if we're assigning a string to a
			// variable we may get a
			// NumberFormatException here!
			if (isEOF)
			{
				returnValue = Token.EOF;
			}
			else
			{
				fTokenLength = getOffset() - fOffset;
				returnValue = token(lexer.token());
			}
		}
		catch (SyntaxException se)
		{
			if (lexerSource.getOffset() - origLength == 0)
				return Token.EOF; // return eof if we hit a problem found at
			// end of parsing
			fTokenLength = getOffset() - fOffset;
			return token(Tokens.yyErrorCode); // FIXME This should return a
			// special error token!
		}
		catch (NumberFormatException nfe)
		{
			fTokenLength = getOffset() - fOffset;
			return returnValue;
		}
		catch (IOException e)
		{
			RubyEditorPlugin.log(e);
		}

		return returnValue;
	}

	private int getOffset()
	{
		return lexerSource.getOffset() + origOffset;
	}

	private IToken token(int i)
	{

		if (isInSymbol)
		{
			if (isSymbolTerminator(i))
			{
				isInSymbol = false; // we're at the end of the symbol
				if (shouldReturnDefault(i))
					return new Token(i);
			}
			return new Token(Tokens.tSYMBEG);
		}
		// The next two conditionals work around a JRuby parsing bug
		// JRuby returns the number for ':' on second symbol's beginning in
		// alias calls
		if (i == Tokens.kALIAS)
		{
			inAlias = true;
		}
		if (i == COLON && inAlias)
		{
			isInSymbol = true;
			inAlias = false;
			return new Token(Tokens.tSYMBEG);
		} // end JRuby parsing hack for alias

		switch (i)
		{
			case LBRACK:
				return new Token(Tokens.tLBRACK);
			case Tokens.tSYMBEG:
				if (looksLikeTertiaryConditionalWithNoSpaces())
				{
					return new Token(Tokens.tCOLON2);
				}
				isInSymbol = true;
				// FIXME Set up a token for symbols
				return new Token(Tokens.tSYMBEG);
			case Tokens.tGVAR:
			case Tokens.tBACK_REF:
				return new Token(Tokens.tGVAR);
			case Tokens.tFLOAT:
			case Tokens.tINTEGER:
				// A character is marked as an integer, lets check for that special
				// case...
				if ((((fOffset - origOffset) + 1) < fContents.length())
						&& (fContents.charAt((fOffset - origOffset) + 1) == '?'))
					return new Token(CHARACTER);
				return new Token(i);
			default:
				return new Token(i);
		}
	}

	private boolean looksLikeTertiaryConditionalWithNoSpaces()
	{
		if (fTokenLength > 1)
			return false;
		int index = (fOffset - origOffset) - 1;
		if (index < 0)
			return false;
		try
		{
			char c = fContents.charAt(index);
			return !Character.isWhitespace(c) && Character.isUnicodeIdentifierPart(c);
		}
		catch (RuntimeException e)
		{
			return false;
		}
	}

	private boolean shouldReturnDefault(int i)
	{
		switch (i)
		{
			case NEWLINE:
			case COMMA:
			case Tokens.tASSOC:
			case Tokens.tRPAREN:
			case Tokens.tWHITESPACE:
				return true;
			default:
				return false;
		}
	}

	private boolean isSymbolTerminator(int i)
	{
		if (isRealKeyword(i))
			return true;
		switch (i)
		{
			case Tokens.tAREF:
			case Tokens.tCVAR:
			case Tokens.tMINUS:
			case Tokens.tPLUS:
			case Tokens.tPIPE:
			case Tokens.tCARET:
			case Tokens.tLT:
			case Tokens.tGT:
			case Tokens.tAMPER:
			case Tokens.tSTAR2:
			case Tokens.tDIVIDE:
			case Tokens.tPERCENT:
			case Tokens.tBACK_REF2:
			case Tokens.tTILDE:
			case Tokens.tCONSTANT:
			case Tokens.tFID:
			case Tokens.tASET:
			case Tokens.tIDENTIFIER:
			case Tokens.tIVAR:
			case Tokens.tGVAR:
			case Tokens.tASSOC:
			case Tokens.tLSHFT:
			case Tokens.tRPAREN:
			case Tokens.tWHITESPACE:
			case COMMA:
			case NEWLINE:
				return true;
			default:
				return false;
		}
	}

	private boolean isRealKeyword(int i)
	{
		if (i >= MIN_KEYWORD && i <= MAX_KEYWORD)
			return true;
		return false;
	}

	public void setRange(IDocument document, int offset, int length)
	{
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		lexer.setPreserveSpaces(true);
		parserSupport.initTopLocalVariables();
		isInSymbol = false;
		ParserConfiguration config = new ParserConfiguration(0, CompatVersion.RUBY1_8);
		try
		{
			fContents = document.get(offset, length);
			lexerSource = LexerSource.getSource("filename", new StringReader(fContents), //$NON-NLS-1$
					config);
			lexer.setSource(lexerSource);
		}
		catch (BadLocationException e)
		{
			lexerSource = LexerSource.getSource("filename", new StringReader(""), config); //$NON-NLS-1$ //$NON-NLS-2$
			lexer.setSource(lexerSource);
		}
		origOffset = offset;
		origLength = length;
	}

	String getSource(int offset, int length)
	{
		if (fContents == null || offset < 0 || (offset + length) > fContents.length())
			return null;
		return new String(fContents.substring(offset, offset + length));
	}
}
