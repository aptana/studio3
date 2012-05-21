package com.aptana.scope.parsing;

import java.io.Reader;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Scanner;

import com.aptana.scope.parsing.ScopeTokenType;

%%

%class ScopeFlexScanner
%extends Scanner
%type Symbol
%yylexthrow Scanner.Exception
%eofval{
	return newToken(Terminals.EOF, "end-of-file");
%eofval}
%unicode
%char

%{
	private int _offset;

	public ScopeFlexScanner()
	{
		this((Reader) null);
	}

	private Symbol newToken(ScopeTokenType type, Object value)
	{
		//System.out.println(type + ":~" + yytext() + "~");
		return newToken(type.getIndex(), value);
	}

	private Symbol newToken(short id, Object value)
	{
		int start = yychar + _offset;
		int end = start + yylength() - 1;

		return new Symbol(id, start, end, value);
	}

	public Symbol nextToken() throws java.io.IOException, Scanner.Exception
	{
		Symbol result;

		try
		{
			// get next token
			result = yylex();
		} 
		catch (Scanner.Exception e)
		{
			// create default token type
			String text = yytext();
			int end = yychar + text.length() - 1;

			result = new Symbol(ScopeTokenType.EOF.getIndex(), yychar, end, text);
		}

		return result;
	}

	public void setOffset(int offset)
	{
		_offset = offset;
	}

	public void setSource(String source)
	{
		yyreset(new StringReader(source));
	}
%}

LineTerminator = \r|\n|\r\n
Whitespace = [ \t\f]

StartLetter = [_a-zA-Z]
BodyLetter = [-_a-zA-Z0-9]
Word = {StartLetter}{BodyLetter}*
Identifier = {Word}(\.{Word})*

%%

<YYINITIAL> {
	// comment prefix and other whitespace
	{Whitespace}+		{ /* ignore */ }
	{LineTerminator}+	{ /* ignore */ }

	// operators and punctuators
	","					{ return newToken(ScopeTokenType.COMMA,     yytext()); }
	"|"					{ return newToken(ScopeTokenType.PIPE,      yytext()); }
	"&"					{ return newToken(ScopeTokenType.AMPERSAND, yytext()); }
	"("					{ return newToken(ScopeTokenType.LPAREN,    yytext()); }
	")"					{ return newToken(ScopeTokenType.RPAREN,    yytext()); }
	"-"					{ return newToken(ScopeTokenType.MINUS,     yytext()); }

	// identifiers
	{Identifier}		{ return newToken(ScopeTokenType.IDENTIFIER, yytext()); }
}

.|\n	{ return newToken(ScopeTokenType.ERROR, yytext()); }
