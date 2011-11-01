package com.aptana.editor.js.sdoc.parsing;

import java.io.Reader;
import java.io.StringReader;

import beaver.Symbol;
import beaver.Scanner;

import com.aptana.editor.js.sdoc.lexer.SDocTokenType;

%%

%class SDocFlexScanner
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

	public SDocFlexScanner()
	{
		this((Reader) null);
	}

	private Symbol newToken(SDocTokenType type, Object value)
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

			result = new Symbol(SDocTokenType.EOF.getIndex(), yychar, end, text);
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

Identifier = ([:jletter:]|\$)([:jletterdigit:]|[$.])*

%state TYPES

%%

<YYINITIAL> {
	// comment prefix and other whitespace
	^[ \t]* "*" [ \t]* /[^/]	{ /* ignore */ }
	{Whitespace}+				{ /* ignore */ }
	{LineTerminator}+			{ /* ignore */ }

	// keywords
	"@advanced"			{ return newToken(SDocTokenType.ADVANCED, yytext()); }
	"@alias"			{ return newToken(SDocTokenType.ALIAS, yytext()); }
	"@author"			{ return newToken(SDocTokenType.AUTHOR, yytext()); }
	"@classDescription"	{ return newToken(SDocTokenType.CLASS_DESCRIPTION, yytext()); }
	"@constructor"		{ return newToken(SDocTokenType.CONSTRUCTOR, yytext()); }
	"@example"			{ return newToken(SDocTokenType.EXAMPLE, yytext()); }
	"@exception"		{ return newToken(SDocTokenType.EXCEPTION, yytext()); }
	"@extends"			{ return newToken(SDocTokenType.EXTENDS, yytext()); }
	"@internal"			{ return newToken(SDocTokenType.INTERNAL, yytext()); }
	"@method"			{ return newToken(SDocTokenType.METHOD, yytext()); }
	"@namespace"		{ return newToken(SDocTokenType.NAMESPACE, yytext()); }
	"@overview"			{ return newToken(SDocTokenType.OVERVIEW, yytext()); }
	"@param"			{ return newToken(SDocTokenType.PARAM, yytext()); }
	"@private"			{ return newToken(SDocTokenType.PRIVATE, yytext()); }
	"@property"			{ return newToken(SDocTokenType.PROPERTY, yytext()); }
	"@return"			{ return newToken(SDocTokenType.RETURN, yytext()); }
	"@see"				{ return newToken(SDocTokenType.SEE, yytext()); }
	"@type"				{ return newToken(SDocTokenType.TYPE, yytext()); }

	"@"[:letter:]*		{ return newToken(SDocTokenType.UNKNOWN, yytext()); }

	// operators and punctuators
	"#"					{ return newToken(SDocTokenType.POUND, yytext()); }
	"["					{ return newToken(SDocTokenType.LBRACKET, yytext()); }
	"]"					{ return newToken(SDocTokenType.RBRACKET, yytext()); }
	"{"					{ yybegin(TYPES); return newToken(SDocTokenType.LCURLY, yytext()); }
	"}"					{ return newToken(SDocTokenType.RCURLY, yytext()); }
	"/**"				{ return newToken(SDocTokenType.START_DOCUMENTATION, yytext()); }
	"*/"				{ return newToken(SDocTokenType.END_DOCUMENTATION, yytext()); }

	// text
	[^ \t\r\n{\[\]#]+	{ return newToken(SDocTokenType.TEXT, yytext()); }
}

<TYPES> {
	// whitespace
	{Whitespace}+		{ /* ignore */ }
	{LineTerminator}+	{ /* ignore */ }

	// keywords
	Array			{ return newToken(SDocTokenType.ARRAY, yytext()); }
	Function		{ return newToken(SDocTokenType.FUNCTION, yytext()); }
	Class			{ return newToken(SDocTokenType.CLASS, yytext()); }

	// identifiers
	{Identifier}	{ return newToken(SDocTokenType.IDENTIFIER, yytext()); }

	// operators and punctuation
	"("				{ return newToken(SDocTokenType.LPAREN, yytext()); }
	")"				{ return newToken(SDocTokenType.RPAREN, yytext()); }
	"{"				{ return newToken(SDocTokenType.LCURLY, yytext()); }
	"}"				{ yybegin(YYINITIAL); return newToken(SDocTokenType.RCURLY, yytext()); }
	"["				{ return newToken(SDocTokenType.LBRACKET, yytext()); }
	"]"				{ return newToken(SDocTokenType.RBRACKET, yytext()); }
	"<"				{ return newToken(SDocTokenType.LESS_THAN, yytext()); }
	">"				{ return newToken(SDocTokenType.GREATER_THAN, yytext()); }
	":"				{ return newToken(SDocTokenType.COLON, yytext()); }
	","				{ return newToken(SDocTokenType.COMMA, yytext()); }
	"|"				{ return newToken(SDocTokenType.PIPE, yytext()); }
	"..."			{ return newToken(SDocTokenType.ELLIPSIS, yytext()); }
	"->"			{ return newToken(SDocTokenType.ARROW, yytext()); }
}

.|\n			{ return newToken(SDocTokenType.ERROR, yytext()); }
